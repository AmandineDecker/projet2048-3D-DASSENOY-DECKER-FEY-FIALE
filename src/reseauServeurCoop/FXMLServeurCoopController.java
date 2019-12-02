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
 * FXML Controller class
 *
 * @author Amandine
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
    
    public void lancerServeur() {
        System.out.println("Serveur en attente de connexion");
        listeConnexions  = new GestionServeurCoop[2];
        // On lance le thread de connexion (pour ne pas bloquer le serveur)
        WaitForConnection wait = new WaitForConnection(this);
        Thread th = new Thread(wait);
        th.start();
        // On empêche l'utilisateur d'ouvrir deux serveurs en même temps
        stopButton.setDisable(false);
        startButton.setDisable(true);
    }
    
    public void arreterServeur(boolean fermer) {
        try {
            if (listeConnexions != null) {
                listeConnexions[0].shareInfos("disconnected");
                listeConnexions[1].shareInfos("disconnected");
                listeConnexions[0] = null;
                listeConnexions[1] = null;
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
    
    public int getConnexion() {
        return port;
    }
    
    public boolean isConnected() {
        System.out.println(listeConnexions);
        if (ecoute == null || listeConnexions == null) {
            return false;
        } else {
            return listeConnexions[0] == null && listeConnexions[1] == null;
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
    
    
    /**
     * Initializes the controller class.
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
