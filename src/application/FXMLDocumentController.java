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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import model.Case;
import model.Grille;
import model.Parametres;

/**
 *
 * @author Valou
 */
public class FXMLDocumentController implements Parametres, Initializable {
    
    
    // Les objets graphiques
    @FXML
    private AnchorPane fond;
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
    private Label txtScore, score, logo, resultat;
    @FXML
    private TextArea commandes;
    
    
    // Les événements
    
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
        int direction = 0;
        switch(ke.getText()){
            case "z" :
                System.out.println("Vous vous déplacez vers le haut");
                direction = HAUT;
                break;
            case "q":
                System.out.println("Vous vous déplacez vers la gauche");
                direction = GAUCHE;
                break;
             case "s":
                System.out.println("Vous vous déplacez vers le bas");
                direction = BAS;
                break;
             case "d":
                System.out.println("Vous vous déplacez vers la droite");
                direction = DROITE;
                break; 
             case "r":
                System.out.println("Vous vous déplacez sur la grille supérieure");
                direction = SUPERIEUR;
                break;
            case "f":
                System.out.println("Vous vous déplacez sur la grille inférieur");
                direction = INFERIEUR;
                break;
        }
        if (direction != 0) {
            boolean b2 = modelGrille.initialiserDeplacement(direction);
            if (b2) {
                boolean b = modelGrille.nouvelleCase();
                if (!b) modelGrille.defaite();
            }
            afficheGrille(modelGrille);
            System.out.println(modelGrille);
            if (modelGrille.getValeurMax()>=OBJECTIF) modelGrille.victoire();
        }
    }
    
    
    // Les méthodes
    
    
    // Place la case sur la fenêtre graphique
    public void placeCase(Case c){
        StackPane p = new StackPane();
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
    
    // Affiche la grille de jeu (les 3 sous-grilles)
    public void afficheGrille(Grille gr){
        gr1.getChildren().clear();
        gr2.getChildren().clear();
        gr3.getChildren().clear();
        for (Case c : gr.getGr()){
            placeCase(c);
        }
        score.setText(String.valueOf(gr.getScore()));
        gr1.getStyleClass().add("gridpane");
        gr2.getStyleClass().add("gridpane");
        gr3.getStyleClass().add("gridpane");
    }

    
    //////////////////////////////////////////////////////////////////////
    
    private Grille modelGrille = new Grille();
    private HashSet<Case> grille;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("le contrôleur initialise la vue");
        fond.getStyleClass().add("pane");
        gr1.getStyleClass().add("gridpane");
        gr2.getStyleClass().add("gridpane");
        gr3.getStyleClass().add("gridpane");
        
        //Initialisation de la partie avec les deux premières cases aux hasard 
        boolean b = modelGrille.nouvelleCase();
        b = modelGrille.nouvelleCase();
        grille = modelGrille.getGr();
        System.out.println(modelGrille);
//        for (Case c : grille){
//            StackPane p = new StackPane();
//            p.getStyleClass().add("tuile");
//            Label l = new Label(String.valueOf(c.getVal()));
//            l.getStyleClass().add("valeurTuile");
//            p.getChildren().add(l);    
//            switch (c.getZ()){
//                case 0:
//                    gr1.add(p, c.getX(), c.getY());
//                    break;
//                case 1:
//                    gr2.add(p, c.getX(), c.getY());
//                    break;
//                case 2:
//                    gr3.add(p, c.getX(), c.getY());
//                    break;
//                  
//          }
//          p.setVisible(true);
//          l.setVisible(true);
//      }
        afficheGrille(modelGrille);
        
        
          
        
        
        
    }
    
}
