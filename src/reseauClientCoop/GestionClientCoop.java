/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClientCoop;

import application.FXMLDocumentController;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import javafx.application.Platform;
import model.*;
import static model.Parametres.COOPERATION;
import static model.Parametres.OBJECTIF;

/**
 *
 * @author Amandine
 */
public class GestionClientCoop {
    
    // Le controller
    private final FXMLClientCoopController clientController;
    private final FXMLDocumentController docController;
    // La connexion
    private final String host;
    private final int port;
    private Socket s = null;
    
    Grille aPartager = null;
    Joueur joueur;
    boolean main = false;
    
    InputStream in = null;
    ObjectInputStream objIn;
    OutputStream out = null;
    ObjectOutputStream objOut;
    
    
    
    public GestionClientCoop(Grille gr, FXMLClientCoopController c, FXMLDocumentController d){
        // GestionClientCompet
        this.aPartager = null;
        this.clientController = c;
        this.docController = d;
        // Connexion
        this.host = c.serverhost;
        this.port = c.port;
        // Le jeu
        this.joueur = c.joueur;
    }
    
    /**
     * Lance la connexion au serveur.
     */
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
            // On récupère le jeu et le boolen admin
            receive();
            receive();
            System.out.println("Client connecté: " + aPartager);
            shareJoueur(joueur);
            WaitForData wait = new WaitForData();
            Thread th = new Thread(wait);
            th.setDaemon(true);
            th.start();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    /**
     * Vérifie si le client est connecté à un serveur.
     * @return 
     */
    public boolean isConnected(){
        if (s == null){
            return false;
        } else {
            return s.isConnected();
        }
    }
    
    /**
     * Déconnecte le client du serveur et partage éventuellement "Stop" avec 
     * le serveur. 
     * @param stopper
     * paramètre de type boolean.
     */
    public void disconnect(boolean stopper){
        System.out.println("Disconnect");
        if (stopper) {
            shareInfos("Stop");
        }
    }
    
    /**
     * Met la grille à jour.
     * @param gr
     * paramètre de type Grille.
     */
    public void updateGrille(Grille gr) {
//        System.out.println("Gr: \n" + gr);
        aPartager = Grille.setInstance(gr);
//        System.out.println("A Partager: \n" + aPartager);
//        System.out.println("Instance: \n" + Grille.getInstance());
        clientController.updateGrille(aPartager);
    }
    
    /**
     * Récupère les données du serveur.
     */
    public void receive(){
        InputStream in = null;
        Object colis = null;
        try {
            colis = objIn.readObject();
            System.out.println("Objet recu: " + colis);
            if (colis instanceof Joueur){
                Joueur j = (Joueur) colis;
               // Le deuxième joueur est connecté
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        clientController.setTxtJoueur(j);
                        if (joueur.isAdmin()) {
                            clientController.giveRights();
                        }
                    }
                });
            } else if (colis instanceof Grille) {
//                System.out.println(aPartager);
                if (aPartager != null) {
//                    if (aPartager.getValeurMax() > 0) {
                        System.out.println("Je prends la main!");
                        main = true;
//                    }
                }
                aPartager = Grille.setInstance((Grille) colis);
                docController.updateGrille(aPartager);
                System.out.println(aPartager.getValeurMax());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        docController.afficheGrille(aPartager);
                        if (main) {
                            if (aPartager.partieFinie()) {
                                String txtFinPartie = "";
                                if (aPartager.getValeurMax() < OBJECTIF) {
                                    txtFinPartie = "La partie est finie. Votre score est " + aPartager.getScore() + ".\nBravo à tous les deux !";
                                } else {
                                    txtFinPartie = "Bravo à tous les deux ! Vous avez atteint " + aPartager.getValeurMax() + "\nVotre score est " + aPartager.getScore() + ".";
                                }
                                docController.setInfos(txtFinPartie);
                            } else {
                                docController.setInfos("A vous de jouer !");
                            }
                        }
                    }
                });
            } else if (colis instanceof Boolean){
//                System.out.println(colis);
                if (!((Boolean) colis)){
                    joueur.setAdmin();
                }
                clientController.setTxtJoueur(joueur);
            } else if (colis instanceof String){
                if (colis.equals("Start")) {
                    main = joueur.isAdmin();
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (main) {
                                docController.nouvellePartie(COOPERATION);
                                docController.setInfos("A vous de jouer !");
                            } else {
                                docController.effacerGrille();
                                docController.setInfos("Une nouvelle partie va commencer.");
                            }
                            docController.focus();
                        }
                    });
//                    share();
                } else if (colis.equals("Stop")) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            clientController.showAlertGameClosed();
                        }
                    });
                } else if (colis.equals("PartieFinie")) {
                    String txtFinPartie;
                    if (aPartager.getValeurMax() < OBJECTIF) {
                        txtFinPartie = "La partie est finie. Votre score est " + aPartager.getScore() + ".\nBravo à tous les deux !";
                    } else {
                        txtFinPartie = "Bravo à tous les deux ! Vous avez atteint " + aPartager.getValeurMax() + "\nVotre score est " + aPartager.getScore() + ".";
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            clientController.showPartieFinie(txtFinPartie);
                        }
                    });
                } else if (colis.equals("FinJeu")) {
                    String txtFinPartie;
                    if (aPartager.getValeurMax() < OBJECTIF) {
                        txtFinPartie = "La partie est finie. Votre score est " + aPartager.getScore() + ".\nBravo à tous les deux !";
                    } else {
                        txtFinPartie = "Bravo à tous les deux ! Vous avez atteint " + aPartager.getValeurMax() + "\nVotre score est " + aPartager.getScore() + ".";
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            clientController.showAlertFinJeu(txtFinPartie);
                        }
                    });
                } else if (colis.equals("NouvellePartie")) {
                    String txtFinPartie;
                    if (aPartager.getValeurMax() < OBJECTIF) {
                        txtFinPartie = "La partie est finie. Votre score est " + aPartager.getScore() + ".\nBravo à tous les deux !";
                    } else {
                        txtFinPartie = "Bravo à tous les deux ! Vous avez atteint " + aPartager.getValeurMax() + "\nVotre score est " + aPartager.getScore() + ".";
                    }
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            clientController.showAlertNouvellePartie(txtFinPartie);
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
    
    /**
     * Partage la grille avec le serveur.
     */
    public void share() {
        OutputStream out = null;
        try {
            // On envoie l'objet
            objOut.writeObject(aPartager);
            // On update
            objOut.flush();
            System.out.println("Objet envoyé: \n" + aPartager);
            System.out.println();
            System.out.println();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
                System.out.println("Client: fermeture de " + s);
                // Fermer jeu
                
            } catch (IOException ex) {
                ex.printStackTrace();
                in = null;
            }
            System.err.println(e.getMessage());  
        } catch (Exception e) 
            { System.err.println("Autre exception");
        } 
    }
    
    /**
     * Partage le joueur avec le serveur. A utiliser lors de la connexion des 
     * deux joueurs.
     */
    public void shareJoueur(Joueur j) {
        OutputStream out = null;
        try {
            // On envoie l'objet
            objOut.writeObject(j);
            // On update
            objOut.flush();
            System.out.println("Objet envoyé: " + j);
            System.out.println();
            System.out.println();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
                System.out.println("Client: fermeture de " + s);
                // Fermer jeu
            } catch (IOException ex) {
                ex.printStackTrace();
                in = null;
            }
            System.err.println(e.getMessage());  
        } catch (Exception e) 
            { System.err.println("Autre exception");
        } 
    }
    
    /**
     * Partage des informations sous forme de String avec le serveur. 
     * Peut partager "Start", "newGame?", "PartieFinie, "Stop", "FinJeu" ou 
     * "NouvellePartie".
     * @param str
     */
    public void shareInfos(String str) {
        OutputStream out = null;
        try {
            // On envoie l'objet
            objOut.writeObject(str);
            // On update
            objOut.flush();
            System.out.println("Objet envoyé: " + str);
            System.out.println();
            System.out.println();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
                System.out.println("Client: fermeture de " + s);
                // Fermer jeu
            } catch (IOException ex) {
                ex.printStackTrace();
                in = null;
            }
            System.err.println(e.getMessage());  
        } catch (Exception e) 
            { System.err.println("Autre exception");
        } 
    }
    
    /**
     * Le joueur prend la main. Il peut donc jouer.
     */
    public void prendreMain() {
        main = true;
    }
    
    /**
     * Le joueur donne la main. Il ne peut donc plus jouer.
     */
    public void donnerMain() {
        main = false;
    }
    
    /**
     * Vérifie si le joueur a la main.
     * @return 
     */
    public boolean aLaMain() {
        return main;
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
