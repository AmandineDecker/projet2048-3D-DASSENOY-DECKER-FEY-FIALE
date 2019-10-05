/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 *
 * @author Valou
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;
    @FXML 
    private Pane fond;
    @FXML
    private HBox hbox;
    @FXML
    private GridPane gr1, gr2, gr3;
    @FXML
    private MenuBar barreMenu;
    @FXML
    private Menu menuFic, menuAide, menuEdit;
    @FXML
    private MenuItem quitter, nouveauJeu, changerStyle, aPropos;
    @FXML
    private Label score, logo, commandes, resultat;
    
    
    @FXML
    public void quitter(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void nouveauJeu(ActionEvent event) {
        System.out.println("You clicked me!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("le contrôleur initialise la vue");
        fond.getStyleClass().add("pane");
        gr1.getStyleClass().add("gridpane");

    }    
    
    
    @FXML
    public void toucheClavier(KeyEvent ke) {
        System.out.println("touche clavier appuyée");
    }
    
}
