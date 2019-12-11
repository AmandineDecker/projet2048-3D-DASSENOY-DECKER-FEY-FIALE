/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClientCoop;

import application.FXMLDocumentController;
import css.Style;
import java.io.*;
import java.net.*;
import java.util.Random;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import model.Grille;
import model.Joueur;
import static model.Parametres.SOLO;

/**
 * Gère la vue liée à la connexion côté client en coopération.
 * FXML ClientCoopController class, fonctionne avec GestionClientCoop.
 */
public class FXMLClientCoopController implements Initializable {

    @FXML
    private VBox fond;
    @FXML
    private TextField txtHost, txtPort, txtPseudo;
    @FXML
    private Button buttonConnexion, buttonDeconnexion, buttonStart;
    @FXML
    private Label labelHost, labelPort, labelPseudo, joueur1, joueur2;   
    
    // Connexion
    Socket s = null;
    String serverhost;
    int port;
    // Partage de data
    public GestionClientCoop gare;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;
    // Le jeu
    Grille grilleAPartager;
    Joueur joueur;
    // Controlleur
    FXMLDocumentController controlleur;
    // Style
    Style perso;
    
    /**
     * Lance la connexion au serveur.
     * @param event 
     */
    @FXML
    private void connect(ActionEvent event) throws UnknownHostException, IOException, ClassNotFoundException {
        if (txtHost.getText().length() > 0 && txtPort.getText().length() > 0 && txtPseudo.getText().length() > 0){
            // On crée le joueur
            joueur = new Joueur(txtPseudo.getText());
            Random rd = new Random();
            joueur.setID(rd.nextInt());
            // On se connecte au serveur
            System.out.println("Client en cours de connexion");
            // On recupere les informations de connexion
            serverhost = txtHost.getText();
            port = Integer.parseInt(txtPort.getText());
            // On se connecte
            gare = new GestionClientCoop(grilleAPartager, this, this.controlleur);
            gare.connect();
            if (gare.isConnected()){
                System.out.println("Connexion effectuée, échanges en attente");
                buttonDeconnexion.setDisable(false);
                buttonConnexion.setDisable(true);
                txtHost.setEditable(false);
                txtPort.setEditable(false);
                txtPseudo.setEditable(false);
            }
            joueur = gare.joueur;
//            System.out.println("Joueur du controller: " + joueur.isAdmin());
        }
    }
    
    /**
     *Déconnecte le client et réactive les champs/boutons nécessaires. 
     * @param event 
     */
    @FXML
    private void disconnect(ActionEvent event) {
        arreterClient();
        showAlertGameClosed();
    }
    
    /**
     * Partage "Start" au serveur. Sert à lancer une partie en multijoueur compétitif.
     * @param event 
     */
    @FXML
    private void lancerPartie(ActionEvent event) {
        // Play
        gare.shareInfos("Start");
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
     * Active le bouton start. 
     */
    public void giveRights(){
        buttonStart.setDisable(false);
    }
    
    /**
     * Affiche les données de connexion sur le client.
     * @param p 
     * paramètre de type int.
     */
    public void setConnexion(int p) {
        try {
            txtHost.setText(InetAddress.getLocalHost().getHostAddress());
            txtPort.setText(Integer.toString(p));
        } catch (UnknownHostException ex) {
            Logger.getLogger(FXMLClientCoopController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Vérifie si le client est connecté à un serveur. 
     * @return 
     * Renvoie True s'il l'est.
     */
    public boolean isConnected() {
        if (gare == null) {
            return false;
        } else {
            return gare.isConnected();
        }
    }
    
    /**
     * Récupère le DocumentController.
     * @param c 
     * Paramètre de type FXMLDocumentController.
     */
    public void giveObjects(FXMLDocumentController c) {
        this.controlleur = c;
    }
    
    public void setTxtJoueur(Joueur j) {
        if (j.isAdmin()) {
            joueur1.setText(j.getPseudo());
        } else {
            joueur2.setText(j.getPseudo());
        }
    }
    
    /**
     * Met la grille à jour. 
     * @param gr
     * paramètre de type Grille.
     */
    public void updateGrille(Grille gr) {
        grilleAPartager = Grille.setInstance(gr);
    }
    
    /**
     * Déconnecte le client et réactive les champs/boutons nécessaires. 
     */
    public void arreterClient() {
        gare.disconnect(true);
        buttonConnexion.setDisable(false);
        buttonDeconnexion.setDisable(true);
        buttonStart.setDisable(true);
        txtHost.setEditable(true);
        txtPort.setEditable(true);
        txtPseudo.setEditable(true);
        joueur1.setText("");
        joueur2.setText("");
    }
    
    /**
     * Affiche une alerte signifiant que la connexion a été coupée. Relance 
     * une partie SOLO.
     */
    public void showAlertGameClosed() {
        fond.getScene().getWindow().requestFocus();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fin du jeu");
        alert.setHeaderText("La connexion a été coupée.");
        alert.setContentText("Retour au jeu en solo.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                controlleur.activerMenuCompet(false);
                controlleur.activerMenuCoop(false);
                fond.getScene().getWindow().hide();
                if (controlleur.getModeJeu() != SOLO){
                    controlleur.nouvellePartie(SOLO);
                } else {
                    controlleur.focus();
                }
            }
        });
    }
    
    /**
     * Affiche une alerte signifiant que la partie est terminée. Le joueur peut
     * alors proposer de lancer une nouvelle partie.
     * @param finJeu
     * Paramètre de type String. Le texte qui sera affiché.
     */
    public void showPartieFinie(String finJeu) {
        fond.getScene().getWindow().requestFocus();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fin du jeu");
        alert.setHeaderText(finJeu);
        alert.setContentText("Voulez-vous lancer une nouvelle partie ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().requestFocus();
        // Les boutons
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        
        final Button btnOUI = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        btnOUI.setOnAction( event -> {
            // Proposer Nouvelle partie
            gare.shareInfos("NouvellePartie");
        } );
        
        final Button btnNON = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        btnNON.setOnAction( event -> {
            // Déconnecter et informer
            gare.shareInfos("FinJeu");
            gare.disconnect(false);
            // Retour au jeu solo
            controlleur.activerMenuCompet(false);
            controlleur.activerMenuCoop(false);
            fond.getScene().getWindow().hide();
            controlleur.nouvellePartie(SOLO);
        } );
        
        // Le choix de l'utilisateur
        alert.show();
    }
    
    /**
     * Affiche une alerte signifiant que la partie est terminée. Le joueur peut
     * alors accepter de participer à une autre partie ou pas.
     * @param finJeu
     * Paramètre de type String. Le texte qui sera affiché.
     */
    public void showAlertNouvellePartie(String finJeu) {
        System.out.println("AFFICHAGE ALERTE");
        fond.getScene().getWindow().requestFocus();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fin de la partie");
        alert.setHeaderText(finJeu);
        alert.setContentText("Voulez-vous participer à une nouvelle partie ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().requestFocus();
        // Les boutons
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        
        final Button btnOUI = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        btnOUI.setOnAction( event -> {
            // Effacer la grille actuelle
            // Lancer Nouvelle partie
            gare.shareInfos("Start");
        } );
        
        final Button btnNON = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        btnNON.setOnAction( event -> {
            // Déconnecter et informer
            gare.shareInfos("FinJeu");
            gare.disconnect(false);
            // Retour au jeu solo
            controlleur.activerMenuCompet(false);
            controlleur.activerMenuCoop(false);
            fond.getScene().getWindow().hide();
            controlleur.nouvellePartie(SOLO);
        } );
        
        // Le choix de l'utilisateur
        alert.show();
    }
    
    /**
     * Affiche une alerte signifiant que l'autre joueur a refusé la 
     * nouvelle partie.
     * @param finJeu
     * Paramètre de type String. Le texte qui sera affiché.
     */
    public void showAlertFinJeu(String finJeu) {
        fond.getScene().getWindow().requestFocus();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fin du jeu");
        alert.setHeaderText(finJeu);
        alert.setContentText("L'autre joueur ne souhaite pas participer à une nouvelle partie. \nRetour au jeu en solo.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                controlleur.activerMenuCompet(false);
                controlleur.activerMenuCoop(false);
                fond.getScene().getWindow().hide();
                if (controlleur.getModeJeu() != SOLO){
                    controlleur.nouvellePartie(SOLO);
                } else {
                    controlleur.focus();
                }
            }
        });
    }
    
    
    /**
     * Initialise la classe controller.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fond.getStyleClass().add("style-client-coop-fond");
        buttonConnexion.getStyleClass().add("style-client-coop-bouton");
        buttonDeconnexion.getStyleClass().add("style-client-coop-bouton");
        buttonStart.getStyleClass().add("style-client-coop-bouton");
        txtHost.getStyleClass().add("style-client-coop-textField");
        txtPort.getStyleClass().add("style-client-coop-textField");
        txtPseudo.getStyleClass().add("style-client-coop-textField");
        labelHost.getStyleClass().add("style-client-coop-label");
        labelPort.getStyleClass().add("style-client-coop-label");
        labelPseudo.getStyleClass().add("style-client-coop-label");
        joueur1.getStyleClass().add("style-client-coop-label");
        joueur2.getStyleClass().add("style-client-coop-label");
    }    
    
}
