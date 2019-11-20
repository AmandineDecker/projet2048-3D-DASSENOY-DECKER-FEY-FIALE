/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClient;

import css.Style;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

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
    
    
    @FXML
    private void newPartie(ActionEvent event) {
        
    }
    
    @FXML
    private void deconnect(ActionEvent event) {
        
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
