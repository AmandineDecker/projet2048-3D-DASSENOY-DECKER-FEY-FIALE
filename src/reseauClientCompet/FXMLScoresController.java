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
 * FXML Controller class
 *
 * @author Amandine
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
    
    
    @FXML
    private void newPartie(ActionEvent event) {
        clientController.gare.shareInfos("newGame?");
    }
    
    @FXML
    private void deconnect(ActionEvent event) {
        clientController.gare.disconnect();
        documentController.reactiverMenuCompet();
        // Fermer score
        fond.getScene().getWindow().hide();
        // Fermer Client
        clientController.close();
        // Relancer une partie solo
        documentController.fermerReseau();
        documentController.nouvellePartie(SOLO);
    }
    

    
    // Recevoir le style de l'autre page
    public void transferStyle(Style s) {
        perso = s;
        if (perso.styleActuel.equals("css/perso.css")){
            perso.applyCSS(fond);
        } else {
            fond.getStylesheets().clear();
            fond.getStylesheets().add(perso.styleActuel);
        }
    }
    
    // Recuperer le texte
    public void getScores(String scores) {
        txtScores.setText(scores);
    }
    
    // Recuperer le controller
    public void giveObjects(FXMLClientCompetController c, FXMLDocumentController d) {
        this.clientController = c;
        this.documentController = d;
    }
    
    public void giveRights(){
        buttonNewPartie.setDisable(false);
    }
    
    public void close() {
        fond.getScene().getWindow().hide();
    }
    
    /**
     * Initializes the controller class.
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
