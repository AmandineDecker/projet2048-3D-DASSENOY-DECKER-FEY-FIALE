/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.net.URL;
import java.util.HashSet;
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
import model.Case;
import model.Grille;

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
    
    private Grille modelGrille = new Grille();
    private HashSet<Case> grille;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("le contrôleur initialise la vue");
        fond.getStyleClass().add("pane");
        gr1.getStyleClass().add("gridpane");
        
        //Initialisation de la partie avec les deux premières cases aux hasard 
        boolean b = modelGrille.nouvelleCase();
        b = modelGrille.nouvelleCase();
        grille = modelGrille.getGr();
        System.out.println(grille);
        for (Case c : grille){
          Pane p = new Pane ();
          p.getStyleClass().add("tuile");
          Label l = new Label(String.valueOf(c.getVal()));
          l.getStyleClass().add("valeurTuile");
          p.getChildren().add(l);    
          switch (c.getZ()){
              case 0:
                  gr1.add(p, c.getX(), c.getY());
                  break;
              case 1:
                  gr2.add(p, c.getX(), c.getY());
                  break;
             case 2:
                  gr3.add(p, c.getX(), c.getY());
                  break;
                  
          }
          p.setVisible(true);
          l.setVisible(true);
          
        }
        
        
    }
       
    
     @FXML
    public void quitter(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void nouveauJeu(ActionEvent event) {
        System.out.println("You clicked me!");
    }
    
    @FXML
    public void toucheClavier(KeyEvent ke) {
        switch(ke.getText()){
            case "z" :
                System.out.println("Vous vous déplacez vers le haut");
                break;
            case "q":
                System.out.println("Vous vous déplacez vers la gauche");
                break;
             case "s":
                System.out.println("Vous vous déplacez vers le bas");
                break;
             case "d":
                System.out.println("Vous vous déplacez vers la droite");
                break; 
             case "r":
                System.out.println("Vous vous déplacez sur la grille supérieure");
                break;
            case "f":
                System.out.println("Vous vous déplacez sur la grille inférieur");
                break;
        }
    }
    
}
