/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.scene.layout.VBox;
import model.Case;
import model.Glissement;
import model.Grille;
import model.Parametres;

/**
 *
 * @author Valou
 */
public class FXMLDocumentController implements Parametres, Initializable {
    
    
    // Les objets graphiques
    @FXML
    private AnchorPane fond, ap1, ap2, ap3;
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
    private VBox commandes;
    
    
    // Les événements
    
    @FXML
    public void quitter(ActionEvent event) {
        System.out.println("Au revoir!");
        System.exit(0);
    }

    @FXML
    private void nouveauJeu(ActionEvent event) {
        System.out.println("\n\n\nNouvelle partie!");
        nouvellePartie();
    }
    
    @FXML
    public void toucheClavier(KeyEvent ke) {
        if (!modelGrille.partieFinie()){
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
                    if (!b) {
                        modelGrille.defaite();
                        resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
                    }
                }
                afficheGrille(modelGrille);
                System.out.println(modelGrille);
                if (modelGrille.getValeurMax()>=OBJECTIF){
                    modelGrille.victoire();
                    resultat.setText("Bravo ! Vous avez atteint " + modelGrille.getValeurMax() + "\nVotre score est " + modelGrille.getScore() + ".");
                }
            }
        }
        else {
            resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
        }
    }
    
    
    // Les méthodes
    
    
    // Place la case sur la fenêtre graphique
    public void placeCase(Case c){
        StackPane p = new StackPane();
        p.getStyleClass().add("tuile" + c.getVal());
//        switch (c.getVal()){
//            case 2:
//             p.getStyleClass().add("tuile2");
//             break;
//            case 4:
//             p.getStyleClass().add("tuile4");
//             break;
//            case 8:
//             p.getStyleClass().add("tuile8");
//             break;
//            case 16:
//             p.getStyleClass().add("tuile16");
//             break;
//            case 32:
//             p.getStyleClass().add("tuile32");
//             break;
//            case 64:
//             p.getStyleClass().add("tuile64");
//             break;
//            case 128:
//             p.getStyleClass().add("tuile128");
//             break;
//            case 256:
//             p.getStyleClass().add("tuile256");
//             break;
//            case 512:
//             p.getStyleClass().add("tuile512");
//             break;
//            case 1024:
//             p.getStyleClass().add("tuile1024");
//             break;
//            case 2048:
//             p.getStyleClass().add("tuile2048");
//             break;
//        }
        Label l = new Label(String.valueOf(c.getVal()));
        l.getStyleClass().add("valeurTuile");
        p.getChildren().add(l);
        if (c.getGlisseX0() == -1 && c.getGlisseY0() == -1){
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
        }
        else {
            Thread th;
            Glissement gl;
            switch (c.getZ()){
                case 0:
                    // Ajouter le glissement ici
                    gl = new Glissement(ap1, gr1, p, c.getGlisseX0(), c.getGlisseY0(), c.getX(), c.getY());
                    th = new Thread(gl);
                    th.setDaemon(true);
                    th.start();
                    break;
                case 1:
                    // Ajouter le glissement ici
                    gl = new Glissement(ap2, gr2, p, c.getGlisseX0(), c.getGlisseY0(), c.getX(), c.getY());
                    th = new Thread(gl);
                    th.setDaemon(true);
                    th.start();
                    break;
                case 2:
                    // Ajouter le glissement ici
                    gl = new Glissement(ap3, gr3, p, c.getGlisseX0(), c.getGlisseY0(), c.getX(), c.getY());
                    th = new Thread(gl);
                    th.setDaemon(true);
                    th.start();
                    break;
            }
        }
        c.setGlisseX0(-1);
        c.setGlisseY0(-1);
        p.setVisible(true);
        l.setVisible(true);
    }
    
    // Affiche la grille de jeu (les 3 sous-grilles)
    public void afficheGrille(Grille gr){
        Node node1 = gr1.getChildren().get(0);
        Node node2 = gr2.getChildren().get(0);
        Node node3 = gr3.getChildren().get(0);

        gr1.getChildren().clear();
        gr1.getChildren().add(0,node1);
        gr2.getChildren().clear();
        gr2.getChildren().add(0,node2);
        gr3.getChildren().clear();
        gr3.getChildren().add(0,node3);

        for (Case c : gr.getGr()){
            placeCase(c);
        }
        score.setText(String.valueOf(gr.getScore()));
        //gr1.getStyleClass().add("gridpane");
        //gr2.getStyleClass().add("gridpane");
        //gr3.getStyleClass().add("gridpane");
    }
    
    // Commence une nouvelle partie
    public void nouvellePartie(){
        // On efface les grilles
        Node node1 = gr1.getChildren().get(0);
        Node node2 = gr2.getChildren().get(0);
        Node node3 = gr3.getChildren().get(0);

        gr1.getChildren().clear();
        gr1.getChildren().add(0,node1);
        gr2.getChildren().clear();
        gr2.getChildren().add(0,node2);
        gr3.getChildren().clear();
        gr3.getChildren().add(0,node3);
        
        //
        modelGrille = new Grille();
        
        //Initialisation de la partie avec les deux premières cases aux hasard
        boolean b = modelGrille.nouvelleCase();
        b = modelGrille.nouvelleCase();
        System.out.println(modelGrille);
        afficheGrille(modelGrille);
    }

    
    //////////////////////////////////////////////////////////////////////
    
    private Grille modelGrille = new Grille();
//    private HashSet<Case> grille;
    
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
//        grille = modelGrille.getGr();
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
