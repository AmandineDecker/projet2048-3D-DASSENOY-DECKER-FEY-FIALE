/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauServeurCoop;

import css.Style;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import model.*;
import static model.Parametres.COOPERATION;

/**
 * Gère la connexion avec les joueurs en compétition.
 * FXML ServeurCoopController class
 */
public class FXMLServeurCoopController implements Initializable {
    @FXML
    BorderPane fond;
    @FXML
    private Label labelHost, labelPort, labelTxtHost, labelTxtPort;
    @FXML
    private Button startButton, stopButton; 
    
    Style perso;
    
    // Connexion
    int port = 0;
    ServerSocket ecoute = null;
    GestionServeurCoop[] listeConnexions; // La liste des sockets à MAJ
    // Le jeu
    private Grille gr;
    private Joueur joueur1;
    private Joueur joueur2;
    
    
    @FXML
    private void startServeur(ActionEvent event) throws UnknownHostException, IOException, ClassNotFoundException {
        lancerServeur();
    }
    
    @FXML
    private void stopServeur(ActionEvent event) {
        arreterServeur(false);
    }
    
    
    
    /* Méthodes */
    /**
     * Vérifie si la partie a un admin.
     * @return 
     * Renvoie True si c'est le cas.
     */
    public boolean hasAdmin() {
        return joueur1 != null;
    }
    
    public Joueur getJoueur1() {
        return joueur1;
    }
    
    public Joueur getJoueur2() {
        return joueur2;
    }
    
    public void setJoueur1(Joueur j) {
        joueur1 = j;
    }
    
    public void setJoueur2(Joueur j) {
        joueur2 = j;
    }
    
    /**
     * Lance le serveur. Les joueurs peuvent donc se connecter. 
     */
    public void lancerServeur() {
//        System.out.println("Serveur en attente de connexion");
        listeConnexions  = new GestionServeurCoop[2];
        // On lance le thread de connexion (pour ne pas bloquer le serveur)
        WaitForConnection wait = new WaitForConnection(this);
        Thread th = new Thread(wait);
        th.start();
        // On empêche l'utilisateur d'ouvrir deux serveurs en même temps
        stopButton.setDisable(false);
        startButton.setDisable(true);
    }
    
    /**
     * Arrête le serveur. L'utilisateur spécifie si la page serveur doit être 
     * fermée.
     * @param fermer
     * paramètre de type boolean.
     */
    public void arreterServeur(boolean fermer) {
        try {
            if (listeConnexions != null) {
                if (listeConnexions[0] != null) {
                    listeConnexions[0].shareInfos("disconnected");
                    listeConnexions[0] = null;
                }
                if (listeConnexions[1] != null) {
                    listeConnexions[1].shareInfos("disconnected");
                    listeConnexions[1] = null;
                }
            }
            ecoute.close();
            ecoute = new ServerSocket(port);
        } catch (IOException e){
            System.out.println(e.getMessage());
        } finally {
            stopButton.setDisable(true);
            startButton.setDisable(false);
            if (fermer) {
                fond.getScene().getWindow().hide();
            }
        }
    }
    
    /**
     * Renvoie le port dédié aux échanges réseaux.
     * @return 
     * Renvoie l'entier représentant le port.
     */
    public int getConnexion() {
        return port;
    }
    
    /**
     * Vérifie si le serveur est connecté à au moins un joueur. 
     * @return 
     * Renvoie True si c'est le cas.
     */
    public boolean isConnected() {
        if (ecoute == null || listeConnexions == null) {
            return false;
        } else {
//            System.out.println(listeConnexions[0]);
//            System.out.println(listeConnexions[1]);
            return !(listeConnexions[0] == null && listeConnexions[1] == null);
        }
    }
    
    /**
     * Récupère le style en cours d'utilisation et l'applique.
     * @param s
     * Paramètre de type Style. Celui qui sera appliqué.
     */
    public void transferStyle(Style s){
        perso = s;
        if (perso.styleActuel.equals("data/perso.css")){
            perso.applyCSS(fond);
        } else {
            fond.getStylesheets().clear();
            fond.getStylesheets().add(perso.styleActuel);
        }
    }
    
    
    /**
     * Initialise la classe controller.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        gr = Grille.getInstance();
        gr.newGame(COOPERATION);
        try {    
            InetAddress host = InetAddress.getLocalHost();
            labelHost.setText(host.getHostAddress());
            ecoute = new ServerSocket(0);
            port = ecoute.getLocalPort();
            labelPort.setText(Integer.toString(port));
        } catch (UnknownHostException ex) {
            Logger.getLogger(FXMLServeurCoopController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLServeurCoopController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.lancerServeur();
        
        fond.getStyleClass().add("style-serveur-coop-fond");
        startButton.getStyleClass().add("style-serveur-coop-bouton");
        stopButton.getStyleClass().add("style-serveur-coop-bouton");
        labelHost.getStyleClass().add("style-serveur-coop-label");
        labelPort.getStyleClass().add("style-serveur-coop-label");
        labelTxtHost.getStyleClass().add("style-serveur-coop-label");
        labelTxtPort.getStyleClass().add("style-serveur-coop-label");
    }    
    
    
    
    //////////////////////////////////////////////////////////////////////////
    
    private class WaitForConnection implements Runnable {
        
        FXMLServeurCoopController controller;
        
        public WaitForConnection(FXMLServeurCoopController c){
            this.controller = c;
        }

        @Override
        public void run() {
            try {
                Socket s = ecoute.accept();
                System.out.println(Arrays.toString(listeConnexions));
                System.out.println("Serveur atteint");
                if (!controller.hasAdmin()) {
                    System.out.println("Admin");
                    GestionServeurCoop c = new GestionServeurCoop(s, controller, 0);
                    System.out.println("nouvelle gestion");
                    listeConnexions[0] = c;
                    Thread processus_connexion = new Thread(c);
                    processus_connexion.start();
                } else if (listeConnexions[1] == null) {
                    System.out.println("Pas admin");
                    GestionServeurCoop c = new GestionServeurCoop(s, controller, 1);
                    System.out.println("nouvelle gestion");
                    listeConnexions[1] = c;
                    Thread processus_connexion = new Thread(c);
                    processus_connexion.start();
                }
                if (!s.isClosed() && (listeConnexions[0] == null || listeConnexions[1] == null)){
                    WaitForConnection wait = new WaitForConnection(controller);
                    Thread th = new Thread(wait);
                    th.start();
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
    
    
}
