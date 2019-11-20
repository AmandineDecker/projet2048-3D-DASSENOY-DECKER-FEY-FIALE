/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauServeur;

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
import javafx.stage.Stage;
import model.*;

/**
 * FXML Controller class
 *
 * @author Amandine
 */
public class FXMLServeurController implements Initializable {

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
    HashSet<GestionServeur> listeConnexions; // La liste des sockets à MAJ
    // Le jeu
    public ListeJoueurs listeJoueurs;
    
    
    @FXML
    private void startServeur(ActionEvent event) throws UnknownHostException, IOException, ClassNotFoundException {
        lancerServeur();
    }
    
    @FXML
    private void stopServeur(ActionEvent event) {
        if (this.isConnected()) {
            showAlertCloseServeur();
        } else {
            arreterServeur();
        }
    }
    
    
    
    /* Méthodes */
    
    public void showAlertCloseServeur() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez couper la connexion avec d'autres joueurs.");
        alert.setContentText("Arreter le serveur quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                this.arreterServeur();
            }
        });
    }
    
    public int getConnexion() {
        return port;
    }
    
    public boolean isConnected() {
        System.out.println(listeConnexions);
        if (ecoute == null) {
            return false;
        } else {
            return !listeConnexions.isEmpty();
        }
    }
    
    public void lancerServeur() {
        System.out.println("Serveur en attente de connexion");
        listeConnexions = new HashSet();
        // On lance le thread de connexion (pour ne pas bloquer le serveur)
        WaitForConnection wait = new WaitForConnection(this);
        Thread th = new Thread(wait);
        th.start();
        // On empêche l'utilisateur d'ouvrir deux serveurs en même temps
        stopButton.setDisable(false);
        startButton.setDisable(true);
    }
    
    public void arreterServeur() {
        try {
            for (GestionServeur quai : listeConnexions){
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
        }
    }
    
    
    // Recevoir le style de l'autre page
    public void transferStyle(Style s){
        perso = s;
        if (perso.styleActuel.equals("css/perso.css")){
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
     * @param rb
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
            Logger.getLogger(FXMLServeurController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLServeurController.class.getName()).log(Level.SEVERE, null, ex);
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
        
        FXMLServeurController controller;
        
        public WaitForConnection(FXMLServeurController c){
            this.controller = c;
        }

        @Override
        public void run() {
            try {
                Socket s = ecoute.accept();
                System.out.println("Serveur atteint");
                GestionServeur c = new GestionServeur(s, listeJoueurs, controller);
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
