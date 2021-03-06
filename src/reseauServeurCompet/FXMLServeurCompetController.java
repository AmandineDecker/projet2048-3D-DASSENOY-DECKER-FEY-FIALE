/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauServeurCompet;

import css.Style;
import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import model.*;

/**
 * Gère la connexion côté serveur des joueurs en compétition.
 * Serveur multithread, fonctionne avec GestionServeurCompet.
 * FXML ServeurCompetController class
 */
public class FXMLServeurCompetController implements Initializable {

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
    HashSet<GestionServeurCompet> listeConnexions; // La liste des sockets à MAJ
    // Le jeu
    public ListeJoueurs listeJoueurs;
    
    
    @FXML
    private void startServeur(ActionEvent event) throws UnknownHostException, IOException, ClassNotFoundException {
        lancerServeur();
    }
    
    @FXML
    private void stopServeur(ActionEvent event) {
        if (this.isConnected()) {
            showAlertCloseServeur(false);
        } else {
            arreterServeur(false);
        }
    }
    
    
    
    /* Méthodes */
    
    /**
     * Affiche une alerte signifiant que la déconnexion du serveur va couper la
     * connexion avec les autres joueurs. L'utilisateur paut continuer quand 
     * même ou annuler.
     * @param fermer
     * Paramètre de type boolean. Spécifie s'il faut fermer la page du serveur.
     */
    public void showAlertCloseServeur(boolean fermer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez couper la connexion avec d'autres joueurs.");
        alert.setContentText("Arreter le serveur quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                this.arreterServeur(fermer);
            }
        });
    }
    
    /**
     * Renvoie le port dédié aux échanges réseaux.
     * @return
     * Renvoie l'entier désignant le port.
     */
    public int getConnexion() {
        return port;
    }
    
    /**
     * Vérifie si le serveur est connecté à au moins un joueur. 
     * @return 
     * Paramètre de type booleen. True si au moins un joueur est connecté.
     */
    public boolean isConnected() {
        System.out.println(listeConnexions);
        if (ecoute == null) {
            return false;
        } else {
            return !listeConnexions.isEmpty();
        }
    }
    
    /**
     * Lance le serveur. Les joueurs peuvent donc se connecter. 
     */
    public void lancerServeur() {
//        System.out.println("Serveur en attente de connexion");
        listeConnexions = new HashSet();
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
            for (GestionServeurCompet quai : listeConnexions){
                quai.shareInfos("disconnected");
            }
            ecoute.close();
            ecoute = new ServerSocket(port);
            listeConnexions.clear();
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
    
    
    
    //////////////////////////////////////////////////////////////////////////
    
    /**
     * Initializes the controller class.
     * @param url
     * Paramètre de type URL.
     * @param rb
     * Paramètre de type ResourceBundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        listeJoueurs = ListeJoueurs.getInstance();
        listeJoueurs.initGame();
        try {    
            InetAddress host = InetAddress.getLocalHost();
            labelHost.setText(host.getHostAddress());
            ecoute = new ServerSocket(0);
            port = ecoute.getLocalPort();
            labelPort.setText(Integer.toString(port));
        } catch (UnknownHostException ex) {
            Logger.getLogger(FXMLServeurCompetController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLServeurCompetController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.lancerServeur();
        
//        fond.getStylesheets().add("css/basePerso.css");
//        fond.getStylesheets().add(perso.styleActuel);
        fond.getStyleClass().add("style-serveur-compet-fond");
        startButton.getStyleClass().add("style-serveur-compet-bouton");
        stopButton.getStyleClass().add("style-serveur-compet-bouton");
        labelHost.getStyleClass().add("style-serveur-compet-label");
        labelPort.getStyleClass().add("style-serveur-compet-label");
        labelTxtHost.getStyleClass().add("style-serveur-compet-label");
        labelTxtPort.getStyleClass().add("style-serveur-compet-label");
    }    
    
    
    //////////////////////////////////////////////////////////////////////////
    
    private class WaitForConnection implements Runnable {
        
        FXMLServeurCompetController controller;
        
        public WaitForConnection(FXMLServeurCompetController c){
            this.controller = c;
        }

        @Override
        public void run() {
            try {
                Socket s = ecoute.accept();
                System.out.println("Serveur atteint");
                GestionServeurCompet c = new GestionServeurCompet(s, listeJoueurs, controller);
                listeConnexions.add(c);
                Thread processus_connexion = new Thread(c);
                processus_connexion.start();
                if (!s.isClosed()){
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
