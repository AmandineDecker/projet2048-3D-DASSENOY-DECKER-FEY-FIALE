/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClientCompet;

import application.FXMLDocumentController;
import css.Style;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import static model.Parametres.SOLO;

/**
 * Affichage des scores en fin de compétition.
 * FXML ScoresController class
 */
public class FXMLScoresController implements Initializable {
    
    @FXML
    private BorderPane fond;
    @FXML
    private Button buttonNewPartie, buttonDeconnect;
    @FXML
    private Label txtScores;    
    
    Style perso;
    FXMLDocumentController documentController;
    FXMLClientCompetController clientController;
    
    /**
     * Partage "newGame?" avec le serveur. Sert à proposer une nouvelle partie 
     * aux autres joueurs.
     * @param event 
     */
    @FXML
    private void newPartie(ActionEvent event) {
        clientController.gare.shareInfos("newGame?");
    }
    
    /**
     *Déconnecte le client et réactive les champs/boutons nécessaires. 
     * @param event 
     */
    @FXML
    private void deconnect(ActionEvent event) {
        clientController.gare.disconnect();
        documentController.activerMenuCompet(false);
        // Fermer score
        fond.getScene().getWindow().hide();
        // Fermer Client
        clientController.close();
        // Relancer une partie solo
        documentController.fermerReseau();
        documentController.nouvellePartie(SOLO);
    }
    

    /**
     * Récupère le style en cours d'utilisation et l'applique.
     * @param s
     * Paramètre de type Style. Celui qui sera appliqué.
     */
    public void transferStyle(Style s) {
        perso = s;
        if (perso.styleActuel.equals("data/perso.css")){
            perso.applyCSS(fond);
        } else {
            fond.getStylesheets().clear();
            fond.getStylesheets().add(perso.styleActuel);
        }
    }
    
    /**
     * Récupère le score de la partie en cours.
     * @param scores
     * paramètre de type String.
     */
    // Recuperer le texte
    public void getScores(String scores) {
        txtScores.setText(scores);
    }
    
    /**
     * Récupère le DocumentController et le ClientCompetController.
     * @param c
     * paramètre de type FXMLClientCompetController.
     * @param d
     * paramètre de type FXMLDocumentController.
     */
    public void giveObjects(FXMLClientCompetController c, FXMLDocumentController d) {
        this.clientController = c;
        this.documentController = d;
    }
    
    /**
     * Active le bouton nouvelle partie. 
     */
    public void giveRights(){
        buttonNewPartie.setDisable(false);
    }
    
    /**
     * Ferme la page client. 
     */
    public void close() {
        fond.getScene().getWindow().hide();
    }
    
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
        fond.getStyleClass().add("style-scores-compet-fond");
        buttonNewPartie.getStyleClass().add("style-scores-compet-bouton");
        buttonDeconnect.getStyleClass().add("style-scores-compet-bouton");
        txtScores.getStyleClass().add("style-scores-compet-label");
    }    
    
}
