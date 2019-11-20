/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClient;

import application.FXMLDocumentController;
import model.*;
import java.io.*;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import static model.Parametres.COMPETITION;

/**
 *
 * @author Amandine
 */
public class GestionClient {
    
    // Le controller
    private final FXMLClientController clientController;
    private final FXMLDocumentController docController;
    // La connexion
    private final String host;
    private final int port;
    private Socket s = null;
    
    ListeJoueurs aPartager;
    Joueur joueur;
    
    InputStream in = null;
    ObjectInputStream objIn;
    OutputStream out = null;
    ObjectOutputStream objOut;
    
    
    public GestionClient(ListeJoueurs o, FXMLClientController c, FXMLDocumentController d){
        // GestionClient
        this.aPartager = ListeJoueurs.getInstance();
        this.clientController = c;
        this.docController = d;
        System.out.println(docController);
        // Connexion
        this.host = c.serverhost;
        this.port = c.port;
        // Le jeu
        this.joueur = c.joueur;
    }
    
    public void update(Joueur p){
        this.joueur.setScore(p.getScore());
        this.joueur.setTuileMax(p.getTuileMax());
        this.joueur.setFini(p.getFini());
        this.joueur.setTemps(p.getTemps());
        this.joueur.setTempsIni(p.getTempsIni());
    }
    
    public void connect(){
        try {
            s = new Socket(host, port);
            
            // La sortie
            out = s.getOutputStream();
            //System.out.println("Client: rails sortants créés");
            objOut = new ObjectOutputStream(out);
            //System.out.println("Client: gare ouverte");
            
            // L'entrée
            in = s.getInputStream();
            //System.out.println("Client: rails entrants créés");
            objIn = new ObjectInputStream(in);
            //System.out.println("Client: gare ouverte");
            // On récupère le jeu et le booleen Admin
            receive();
            receive();
            System.out.println("Client connecté: " + aPartager);
            if (aPartager != null){
                aPartager.addJoueur(joueur);
                share();
            }
            WaitForData wait = new WaitForData();
            Thread th = new Thread(wait);
            th.setDaemon(true);
            th.start();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    public boolean isConnected(){
        if (s == null){
            return false;
        } else {
            return s.isConnected();
        }
    }
    
    public void disconnect(){
        aPartager.removeJoueur(joueur);
        share();
        if (s != null){
            try {
                s.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                s = null;
            }
        }
    }
    
    
    public void share() {
        OutputStream out = null;
        try {
            if (this.joueur.getFini()){
                aPartager.updateCompetFinie();
            }
            // On envoie l'objet
            objOut.writeObject(ListeJoueurs.getInstance());
            // On update
            objOut.flush();
            System.out.println("Objet envoyé: " + aPartager);
            System.out.println();
            System.out.println();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
                System.out.println("Client: fermeture de " + s);
                
                clientController.quit();
                
            } catch (IOException ex) {
                ex.printStackTrace();
                in = null;
            }
            System.err.println(e.getMessage());  
        } catch (Exception e) 
            { System.err.println("Autre exception");
        } 
    }
    
    public void receive(){
        InputStream in = null;
        Object colis = null;
        try {
            colis = objIn.readObject();
            System.out.println("Objet recu: " + colis);
            if (colis instanceof ListeJoueurs){
                aPartager = (ListeJoueurs) colis;
                Joueur pRecu = aPartager.getJoueur(joueur.getID());
                if (pRecu != null){
                    joueur = pRecu;
                    if (joueur.isAdmin()){
                        clientController.giveRights();
                    }
                }
                affiche();
                if (docController.getModeJeu() == COMPETITION){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            docController.afficherListeJoueurs(aPartager);
                        }
                    });
                    if (aPartager.getCompetFinie()){
                        // Page de résultats et nouvelle partie
                        Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("Compétition terminée");
                                clientController.afficherScores(aPartager.afficherScore());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                        
                    }
                    System.out.println("Reception client: " + joueur);
                    System.out.println();
                    System.out.println();
                }
            } else if (colis instanceof Boolean){
//                System.out.println(colis);
                if (!((Boolean) colis)){
                    joueur.setAdmin();
                    aPartager.setAdmin(joueur);
                }
            } else if (colis instanceof String){
                if (colis.equals("Start")){
                    // Lancer partie
                    this.lancerPartie();
                } else if (colis.equals("disconnected")){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            clientController.showAlertServeurClosed();
                        }
                    });
                }
            } else if (colis == null){
                try {
                    in.close();
                    System.out.println("Client: fermeture de " + s);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    in = null;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            if (s != null){
                try {
                    s.close();
                    System.out.println("Client: fermeture de " + s);
                } catch (IOException e) {
                    e.printStackTrace();
                    in = null;
                }
                System.out.println(ex.getMessage());
            }
        } 
    }
    
    
    
    
    public void affiche() {
        String listeJoueurs = "";
        for (Joueur p : aPartager.getListe()){
            listeJoueurs = listeJoueurs + p.toString() + "\n";
        }
        clientController.setTextAffichage(listeJoueurs);
    }
    
    public void start() {
        OutputStream out = null;
        try {
            // On envoie l'objet
            objOut.writeObject("Start");
            // On update
            objOut.flush();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
                System.out.println("Client: fermeture de " + s);
                
                clientController.quit();
                
            } catch (IOException ex) {
                ex.printStackTrace();
                in = null;
            }
            System.err.println(e.getMessage());  
        } catch (Exception e) 
            { System.err.println("Autre exception");
        } 
    }
    
    public void lancerPartie() {
        System.out.println(joueur);
        System.out.println(docController);
        joueur.startGame();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                docController.nouvellePartie(joueur, COMPETITION);
            }
        });
    }
    
    
    private class WaitForData implements Runnable {
        
        public WaitForData(){
        }

        @Override
        public void run() {
            receive();
            if (in != null && !s.isClosed()){
                WaitForData wait = new WaitForData();
                Thread th = new Thread(wait);
                th.setDaemon(true);
                th.start();
            }
            
        }
    }
    
}
