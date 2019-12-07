/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClientCompet;

import application.FXMLDocumentController;
import model.*;
import java.io.*;
import java.io.OutputStream;
import java.net.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import static model.Parametres.COMPETITION;

/**
 * Gère la connexion côté client en compétition.
 * Fonctionne avec FXMLClientCompetController.
 */
public class GestionClientCompet {
    
    // Le controller
    private final FXMLClientCompetController clientController;
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
    
    
    public GestionClientCompet(ListeJoueurs o, FXMLClientCompetController c, FXMLDocumentController d){
        // GestionClientCompet
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
    
    /**
     * Met le joueur à jour.
     * @param p
     * paramètre de type Joueur.
     */
    public void update(Joueur p){
        this.joueur.setScore(p.getScore());
        this.joueur.setTuileMax(p.getTuileMax());
        this.joueur.setFini(p.getFini());
        this.joueur.setTemps(p.getTemps());
        this.joueur.setTempsIni(p.getTempsIni());
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
            // On récupère le jeu et le booleen Admin
            receive();
            receive();
//            System.out.println("Client connecté: " + aPartager);
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
    
    /**
     * Vérifie si le client est connecté à un serveur.
     * @return 
     * Renvoie True s'il l'est.
     */
    public boolean isConnected(){
        if (s == null){
            return false;
        } else {
            return s.isConnected();
        }
    }
    
    /**
     * Déconnecte le client du serveur.
     */
    public void disconnect(){
        System.out.println("Disconnect");
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
    
    /**
     * Partage l'état du joueur avec le serveur.
     */
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
//            System.out.println("Objet envoyé: " + aPartager);
//            System.out.println();
//            System.out.println();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
//                System.out.println("Client: fermeture de " + s);
                
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
    
    /**
     * Récupère les données du serveur.
     */
    public void receive(){
        InputStream in = null;
        Object colis = null;
        try {
            colis = objIn.readObject();
//            System.out.println("Objet recu: " + colis);
            if (colis instanceof ListeJoueurs){
                aPartager = (ListeJoueurs) colis;
                Joueur pRecu = aPartager.getJoueur(joueur.getID());
                if (pRecu != null){
                    if (pRecu.getTempsIni() == null && joueur.getTempsIni() != null) {
                        pRecu.setTempsIni(joueur.getTempsIni());
                    }
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
//                                    System.out.println("Compétition terminée");
                                    clientController.afficherScores(aPartager.afficherScore(), joueur.isAdmin());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } else if (aPartager.pretsAJouer() && joueur.getTempsIni() == null){
                        // Relancer une partie
//                        System.out.println("Relancer partie");
//                        System.out.println(joueur.getTempsIni());
                        if (joueur.isAdmin()) {
                            shareInfos("Start");
                        }
                    }
//                    System.out.println("Reception client: " + joueur);
//                    System.out.println();
//                    System.out.println();
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
                } else if (colis.equals("newGame?")){
                    if (joueur.isAdmin()) {
                        joueur.reinitialiser();
                        aPartager.setCompetFinie(false);
                        aPartager.updateJoueur(joueur);
                        share();
                    } else {
                        // Afficher la boite de dialogue OUI / NON, NON déconnecte
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                showAlertNewGame();
                            }
                        });
                        
                    }
                }
            } else if (colis == null){
                try {
                    in.close();
//                    System.out.println("Client: fermeture de " + s);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    in = null;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            if (s != null){
                try {
                    s.close();
//                    System.out.println("Client: fermeture de " + s);
                } catch (IOException e) {
                    e.printStackTrace();
                    in = null;
                }
                System.out.println(ex.getMessage());
            }
        } 
    }
    
    /**
     * Affiche une alerte signifiant qu'une nouvelle partie va débuter. Le 
     * joueur peut choisir d'y participer ou pas.
     */
    public void showAlertNewGame() {
        System.out.println("New alert");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setOnCloseRequest(e -> {
//            pour ne pas pouvoir fermer avec la croix
//        });
        alert.setTitle("Nouvelle partie ?");
        alert.setHeaderText("Une nouvelle partie va débuter.");
        alert.setContentText("Voulez-vous participer ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().requestFocus();
        // Les boutons
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        
        final Button btnOUI = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        btnOUI.setOnAction( event -> {
            joueur.reinitialiser();
            aPartager.setCompetFinie(false);
            aPartager.updateJoueur(joueur);
            share();
            System.out.println(joueur.getFini());
            alert.close();
            clientController.fermerScores();
        } );
        
        final Button btnNON = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        btnNON.setOnAction( event -> {
            disconnect();
            alert.close();
        } );
        
        // Le choix de l'utilisateur
        alert.show();
    }
    
    /**
     * Affiche la liste de joueurs. 
     */
    public void affiche() {
        String listeJoueurs = "";
        for (Joueur p : aPartager.getListe()){
            listeJoueurs = listeJoueurs + p.toString() + "\n";
        }
        clientController.setTextAffichage(listeJoueurs);
    }
    
    /**
     * Partage des informations sous forme de String avec le serveur. 
     * Peut partager "Start" ou "newGame?".
     * @param str
     * Paramètre de type String. Seuls "Start" ou "newGame?" auront un effet sur
     * le serveur.
     */
    public void shareInfos(String str) {
        OutputStream out = null;
        try {
            // On envoie l'objet
            objOut.writeObject(str);
            // On update
            objOut.flush();
        } catch (NullPointerException | IOException e) { 
            try {
                s.close();
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
    
    /**
     * Lance la partie. 
     */
    public void lancerPartie() {
        aPartager.setCompetFinie(false);
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
