/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import css.Style;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    private Label labelScore, instructions;  
    
    
    
    private static String URL = "jdbc:mysql://mysql-2048-dassenoyfialedeckerfey.alwaysdata.net:3306/2048-dassenoyfialedeckerfey_scores";
    private static String UTILISATEUR = "195812";
    private static String MDP = "juxje5-maQkum-pacrim";
    
    // Controlleur
    FXMLDocumentController documentController;
    // Style
    Style perso;
    // Donnees de jeu
    int score;
    int tuileMax;
    String aAfficher;
    
//    try {
//            // TODO code application logic here
//            Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP);
//            
//            String requete = "INSERT INTO scores VALUES ('TestMain', 1024, 29374)";
//            
//            Statement stmt = con.createStatement();
//            stmt.executeUpdate(requete);
//            
////            while (res.next()) {
////                String pseudo = res.getString("pseudo");
////                System.out.println(pseudo);
////            }
////            
////            res.close();
//            stmt.close();
//            con.close();
//            
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(ConsoleTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    
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
        instructions.getStyleClass().add("style-scores-compet-label");
        pseudo.getStyleClass().add("style-scores-compet-textfield");
    }    
    
}
