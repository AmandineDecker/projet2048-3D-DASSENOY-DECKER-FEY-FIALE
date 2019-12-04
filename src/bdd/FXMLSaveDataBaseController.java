/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd;

import application.FXMLDocumentController;
import css.Style;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

/**
 * FXML Controller class
 *
 * @author Amandine
 */
public class FXMLSaveDataBaseController implements Initializable {

    @FXML
    private VBox fond;
    @FXML
    private TextField pseudo;
    @FXML
    private Button btnValider, btnQuitter;
    @FXML
    private Label labelScore, instructions, labelPseudo;  
    
    
    
   
    // Controlleur
    FXMLDocumentController documentController;
    // Style
    Style perso;
    // Donnees de jeu
    int score;
    int tuileMax;
    String aAfficher;
    
//    
    
//     Recevoir le style de l'autre page
    public void transferStyle(Style s){
        perso = s;
        if (perso.styleActuel.equals("css/perso.css")){
            perso.applyCSS(fond);
        } else {
            fond.getStylesheets().clear();
            fond.getStylesheets().add(perso.styleActuel);
        }
    }
    
    // Recuperer le controller
    public void giveObjects(FXMLDocumentController d) {
        this.documentController = d;
    }
    
    // Recuperer le score et la tuileMax
    public void getData(int score, int tuileMax, String str) {
        this.score = score;
        this.tuileMax = tuileMax;
        this.aAfficher = str;
    }
    
    @FXML
    public void quit(ActionEvent event) {
        fond.getScene().getWindow().hide();
        documentController.focus();
    }
    
    @FXML
    public void valid(ActionEvent event) {
        if (!pseudo.getText().equals("")) {
            // Sauvegarder le score du joueur
            BDD reachBDD = new BDD();
            if (reachBDD.pseudoAlreadyExists(pseudo.getText())) {
                // Alert pseudo existe déjà
                showAlertPseudoExists();
            } else {
                reachBDD.save(pseudo.getText(), tuileMax, score);
                fond.getScene().getWindow().hide();
            }
        } else {
        }
    }
    
    public void showAlertPseudoExists() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setOnCloseRequest(e -> {
//            pour ne pas pouvoir fermer avec la croix
//        });
        alert.setTitle("");
        alert.setHeaderText("Ce pseudo existe déjà.");
        alert.setContentText("Est-ce bien vous ?");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().requestFocus();
        // Les boutons
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        
        final Button btnOUI = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        btnOUI.setOnAction( event -> {
            BDD reachBDD = new BDD();
            reachBDD.save(pseudo.getText(), tuileMax, score);
            alert.close();
            fond.getScene().getWindow().hide();
        } );
        
        final Button btnNON = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
        btnNON.setOnAction( event -> {
            alert.close();
        } );
        
        // Le choix de l'utilisateur
        alert.show();
    }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fond.getStyleClass().add("style-scores-compet-fond");
        btnValider.getStyleClass().add("style-scores-compet-bouton");
        btnQuitter.getStyleClass().add("style-scores-compet-bouton");
        labelScore.getStyleClass().add("style-scores-compet-label");
        labelPseudo.getStyleClass().add("style-scores-compet-label");
        instructions.getStyleClass().add("style-scores-compet-label");
        pseudo.getStyleClass().add("style-scores-compet-textfield");
        
        labelScore.setText(aAfficher);
        
    }    
    
}
