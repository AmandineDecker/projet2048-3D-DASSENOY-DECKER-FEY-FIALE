/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClientCompet;

import application.FXMLDocumentController;
import css.Style;
import model.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static model.Parametres.SOLO;

/**
 * FXML Controller class
 *
 * @author Amandine
 */
public class FXMLClientCompetController implements Initializable {

    @FXML
    private AnchorPane fond;
    @FXML
    private TextField txtHost, txtPort, txtPseudo;
    @FXML
    private Button buttonConnexion, buttonDeconnexion, buttonStart;
    @FXML
    private TextArea affichage;
    @FXML
    private Label labelHost, labelPort, labelPseudo;     
            
    // Connexion
    Socket s = null;
    String serverhost;
    int port;
    // Partage de data
    public GestionClientCompet gare;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;
    // Le jeu
    ListeJoueurs listeAPartager;
    Joueur joueur;
    // Controlleur
    FXMLDocumentController controlleur;
    FXMLScoresController scoresController;
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
//            System.out.println("Client en cours de connexion");
            // On recupere les informations de connexion
            serverhost = txtHost.getText();
            port = Integer.parseInt(txtPort.getText());
            // On se connecte
            gare = new GestionClientCompet(listeAPartager, this, this.controlleur);
            gare.connect();
            if (gare.isConnected()){
//                System.out.println("Connexion effectuée, échanges en attente");
                buttonDeconnexion.setDisable(false);
                buttonConnexion.setDisable(true);
                txtHost.setEditable(false);
                txtPort.setEditable(false);
                txtPseudo.setEditable(false);
            }
            joueur = gare.joueur;
        }
    }
    
    /**
     *Déconnecte le client et réactive les champs/boutons nécessaires. 
     * @param event 
     */
    @FXML
    private void disconnect(ActionEvent event) {
        arreterClient();
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
    
    /* Méthodes */
    
    /**
     * Vérifie si le client est connecté à un serveur. 
     * @return 
     */
    public boolean isConnected() {
        if (gare == null) {
            return false;
        } else {
            return gare.isConnected();
        }
    }
    
    /**
     * Désactive buttonDeconnexion, active buttonConnexion.
     */
    public void quit(){
        buttonConnexion.setDisable(false);
        buttonDeconnexion.setDisable(true);
    }
    
    /**
     * Affiche une alerte signifiant que le serveur a été déconnecté.
     */
    public void showAlertServeurClosed() {
        fond.getScene().getWindow().requestFocus();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Serveur déconnecté");
        alert.setHeaderText("Le serveur a été déconnecté.");
        alert.setContentText("Retour au jeu en solo.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                controlleur.reactiverMenuCompet();
                controlleur.reactiverMenuCoop();
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
     * Ferme la page client. 
     */
    public void close() {
        fond.getScene().getWindow().hide();
    }
    
    /**
     * Active le bouton start. 
     */
    public void giveRights(){
        buttonStart.setDisable(false);
    }
    
    /**
     * Modifie le texte de affichage.
     * @param str
     * paramètre de type String.
     */
    public void setTextAffichage(String str) {
        affichage.setText(str);
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
            Logger.getLogger(FXMLClientCompetController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Récupère le DocumentController.
     * @param c 
     * paramètre de type FXMLDocumentController.
     */
    public void giveObjects(FXMLDocumentController c) {
        this.controlleur = c;
    }
    
    /**
     * Déconnecte le client et réactive les champs/boutons nécessaires. 
     */
    public void arreterClient() {
        gare.disconnect();
        quit();
        buttonStart.setDisable(true);
        txtHost.setEditable(true);
        txtPort.setEditable(true);
        txtPseudo.setEditable(true);
        affichage.clear();
    }
    
    /**
     * Récupère le style en cours d'utilisation et l'applique.
     * @param s
     */
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
     * Affiche la page de scores de la partie multijoueur. Autorise l'admin à 
     * appuyer sur le bouton qui lance une nouvelle partie.
     * @param scores
     * paramètre de type String.
     * @param isAdmin
     * paramètre de type boolean.
     * @throws java.io.IOException
     */
    public void afficherScores(String scores, boolean isAdmin) throws IOException {
        // Load fenetre de personnalisation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLScores.fxml"));
        Parent root = loader.load();
        // Recupérer le controller
        scoresController = loader.getController();
        // Transmettre ce qu'on veut
        scoresController.transferStyle(perso);
        scoresController.getScores(scores);
        scoresController.giveObjects(this, controlleur);
        if (isAdmin) {
            scoresController.giveRights();
        }
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        
        // Fermeture propre du serveur
        stage.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (this.isConnected()){
                e.consume();
                controlleur.showAlertCloseClientCompet(stage);
            } else {
                controlleur.fermerReseau();
                fond.getScene().getWindow().hide();
                if (controlleur.getModeJeu() != SOLO){
                    controlleur.nouvellePartie(SOLO);
                } else {
                    controlleur.focus();
                }
            }
        });
        
        stage.setScene(scene);
        stage.setTitle("Partie terminée !");
        stage.initModality(Modality.WINDOW_MODAL);
        scene.getStylesheets().add(perso.styleActuel);
        stage.show();
    }
    
    /**
     * Ferme la page de scores de la partie multijoueur.
     */
    public void fermerScores() {
        if (scoresController != null) {
            scoresController.close();
            scoresController = null;
        }
    }
    
   
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fond.getStyleClass().add("style-client-compet-fond");
        buttonConnexion.getStyleClass().add("style-client-compet-bouton");
        buttonDeconnexion.getStyleClass().add("style-client-compet-bouton");
        buttonStart.getStyleClass().add("style-client-compet-bouton");
        affichage.getStyleClass().add("style-client-compet-textArea");
        txtHost.getStyleClass().add("style-client-compet-textField");
        txtPort.getStyleClass().add("style-client-compet-textField");
        txtPseudo.getStyleClass().add("style-client-compet-textField");
        labelHost.getStyleClass().add("style-client-compet-label");
        labelPort.getStyleClass().add("style-client-compet-label");
        labelPseudo.getStyleClass().add("style-client-compet-label");
    }    
    
}