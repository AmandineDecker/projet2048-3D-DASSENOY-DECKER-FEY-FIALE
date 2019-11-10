/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import css.Style;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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
import model.Grille;
import model.Parametres;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Application.STYLESHEET_MODENA;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Caretaker;
import model.Originator;
import threads.Apparition;
import threads.Fusion;
import threads.Glissement;

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
    private MenuItem quitter, nouveauJeu, changerStyle, aPropos, backMove, avancerUnCoup;
    @FXML
    private RadioMenuItem themeClassique, themeNuit, themeWanda, themeAmandine, themeAmelie, themePerso, themeAme2;
    @FXML
    private ToggleGroup grStyle;
    @FXML
    private Label txtScore, score, logo, resultat, commande0, commande1, commande2, commande3, commande4, commande5, commande6;
    @FXML
    private VBox commandes;
    @FXML
    private Button bHaut, bBas, bGauche, bDroite, bSup, bInf;
    
    private Grille modelGrille = new Grille();
    public Style style = new Style();
    
    // Les événements
    
    @FXML
    public void quitter(ActionEvent event) {
        System.out.println("Au revoir!");
        ObjectOutputStream oosGrille = null;
        ObjectOutputStream oosStyle = null;
        
        if (modelGrille.partieFinie()){
            nouvellePartie();
        }
        try{
            final FileOutputStream fichierGrille = new FileOutputStream("../../model.ser");
            oosGrille = new ObjectOutputStream(fichierGrille);
            oosGrille.writeObject(modelGrille);
            oosGrille.flush();
            final FileOutputStream fichierStyle = new FileOutputStream("../../style.ser");
            oosStyle = new ObjectOutputStream(fichierStyle);
            oosStyle.writeObject(style);
            oosStyle.flush();
        }catch (final IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(oosGrille != null){
                    oosGrille.flush();
                    oosGrille.close();
                }
                if(oosStyle != null){
                    oosStyle.flush();
                    oosStyle.close();
                }
            }catch(final IOException ex){
                ex.printStackTrace();
            }
        }
        
        System.exit(0);
    }


    @FXML
    private void nouveauJeu(ActionEvent event) {
        System.out.println("\n\n\nNouvelle partie!");
        nouvellePartie();
    }
    
    @FXML
    private void changeTheme(ActionEvent event) {
        fond.getStylesheets().clear();
        switch(grStyle.getToggles().indexOf(grStyle.getSelectedToggle())){
            // Les nombres sont dans l'ordre du menu
            case 0 :
                fond.getStylesheets().add("css/styles.css");
                style.styleActuel = "css/styles.css";
                break;
            case 1 :
                fond.getStylesheets().add("css/modeNuit.css");
                style.styleActuel = "css/modeNuit.css";
                break;
            case 2 :
                fond.getStylesheets().add("css/wanda.css");
                style.styleActuel = "css/wanda.css";
                break;
            case 3 :
                fond.getStylesheets().add("css/amandine.css");
                style.styleActuel = "css/amandine.css";
                break;
            case 4 :
                fond.getStylesheets().add("css/amelie.css");
                style.styleActuel = "css/amelie.css";
                break;
            case 5 :
                fond.getStylesheets().add("css/ame.css");
                style.styleActuel = "css/ame.css";
                break;
            case 6 :
                try {
                    style.applyCSS(fond);
                } catch (URISyntaxException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            default:
                break;
        }
    }

    
    @FXML
    private void revenirUnCoup(ActionEvent event) {
        int index=caretaker.getIndex();
        modelGrille = originator.restoreFromMemento(caretaker.getMemento(index - 1));
        caretaker.setIndex(index - 1);
        afficheGrille(modelGrille);
        System.out.println(modelGrille);
    }
    
    @FXML
    private void avancerUnCoup(ActionEvent event) {
        int index = caretaker.getIndex();
        modelGrille = originator.restoreFromMemento(caretaker.getMemento(index + 1));
        caretaker.setIndex(index + 1);
        afficheGrille(modelGrille);
        System.out.println(modelGrille);
    }
    
    
    @FXML
    private void clicHaut(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(HAUT);
        }
        else {
        }
    }
    
    @FXML
    private void clicBas(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(BAS);
        }
        else {
        }
    }
    
    @FXML
    private void clicGauche(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(GAUCHE);
        }
        else {
        }
    }
    
    @FXML
    private void clicDroite(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(DROITE);
        }
        else {
        }
    }
    
    @FXML
    private void clicSup(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(SUPERIEUR);
        }
        else {
        }
    }
    
    @FXML
    private void clicInf(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(INFERIEUR);
        }
        else {
        }
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
            joue(direction);
        }
        else {
            resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");    
        }
    }
    
    
    // Les méthodes
    
               
    Caretaker caretaker= new Caretaker();
    Originator originator=new Originator();
    
    // Place la case sur la fenêtre graphique
    public void placeCase(Case c){
        //System.out.println(c + " a été placée!");
        StackPane p = new StackPane();
        p.getStyleClass().add("tuile" + c.getVal());
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
        c.setGlisseX0(-1);
        c.setGlisseY0(-1);
        c.setFusionneX0(-1);
        c.setFusionneY0(-1);
        c.setGrimpe(false);
        p.setVisible(true);
        l.setVisible(true);
    }
    
    // Place la case sur la fenêtre graphique
    public void afficheCase(Case c){
        Thread th;
        Apparition app;
        Case cCopy = (Case) c.clone();
        //System.out.println(c + " est apparue!");
        switch (c.getZ()){
            case 0:
                app = new Apparition(ap1, gr1, cCopy);
                th = new Thread(app);
                break;
            case 1:
                app = new Apparition(ap2, gr2, cCopy);
                th = new Thread(app);
                break;
            default:
                app = new Apparition(ap3, gr3, cCopy);
                th = new Thread(app);
                break;
        }
        th.setDaemon(true);
        th.start();
        
        c.setGlisseX0(-1);
        c.setGlisseY0(-1);
        c.setFusionneX0(-1);
        c.setFusionneY0(-1);
        c.setGrimpe(false);
        c.setApparue(true);
    }

    
    public Thread glisseCase(Case c){
        StackPane p = new StackPane();
        p.getStyleClass().add("tuile" + c.getVal());
        Label l = new Label(String.valueOf(c.getVal()));
        l.getStyleClass().add("valeurTuile");
        p.getChildren().add(l);
        
        Thread th;
        Glissement gl;
        Fusion fus;
        
        Case cCopy = (Case) c.clone();
        switch (c.getZ()){
            case 0:
                // Ajouter le glissement ici
                if (c.getFusionneX0() != -1 && c.getFusionneY0() != -1){
                    //System.out.println(cCopy + " a fusionné!");
                    fus = new Fusion(ap1, gr1, cCopy);
                    th = new Thread(fus);
                }
                else {
                    //System.out.println(cCopy + " a glissé!");
                    gl = new Glissement(ap1, gr1, cCopy);
                    th = new Thread(gl);
                }
                th.setDaemon(true);
                th.start();
                break;
            case 1:
                // Ajouter le glissement ici
                if (c.getFusionneX0() != -1 && c.getFusionneY0() != -1){
                    //System.out.println(cCopy + " a fusionné!");
                    fus = new Fusion(ap2, gr2, cCopy);
                    th = new Thread(fus);
                }
                else {
                    //System.out.println(cCopy + " a glissé!");
                    gl = new Glissement(ap2, gr2, cCopy);
                    th = new Thread(gl);
                }
                th.setDaemon(true);
                th.start();
                break;
            default:
                // Ajouter le glissement ici
                if (c.getFusionneX0() != -1 && c.getFusionneY0() != -1){
                    //System.out.println(cCopy + " a fusionné!");
                    fus = new Fusion(ap3, gr3, cCopy);
                    th = new Thread(fus);
                }
                else {
                    //System.out.println(cCopy + " a glissé!");
                    gl = new Glissement(ap3, gr3, cCopy);
                    th = new Thread(gl);
                }
                th.setDaemon(true);
                th.start();
                break;
        }
        c.setGlisseX0(-1);
        c.setGlisseY0(-1);
        c.setFusionneX0(-1);
        c.setFusionneY0(-1);
        c.setGrimpe(false);
        p.setVisible(true);
        l.setVisible(true);
        
        return th;
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
            if ((c.getGlisseX0() != -1 && c.getGlisseY0() != -1) || (c.getFusionneX0() != -1 && c.getFusionneY0() != -1)){
                glisseCase(c);
            }
//            else if (c.getGrimpe()){
//                placeCase(c); // On peut mettre affiche aussi, a discuter
//            }
            else if (!c.getApparue()){
                afficheCase(c);
            }
            else {
                placeCase(c);
            }
        }
        score.setText(String.valueOf(gr.getScore()));
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
        originator=new Originator();
        caretaker=new Caretaker();
    }

    
    public void joue(int direction){
        System.out.println();
        if (direction != 0) {
            boolean b2 = modelGrille.initialiserDeplacement(direction);
            originator.set(modelGrille.clone());
            caretaker.addMemento(originator.saveToMemento());
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
    
    //////////////////////////////////////////////////////////////////////
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        System.out.println("le contrôleur initialise la vue");
        try {
            fond.getStyleClass().add("pane");
            gr1.getStyleClass().add("gridpane");
            gr2.getStyleClass().add("gridpane");
            gr3.getStyleClass().add("gridpane");
            score.getStyleClass().add("score");
            txtScore.getStyleClass().add("score");
            resultat.getStyleClass().add("resultat");
            logo.getStyleClass().add("logo");
            commande0.getStyleClass().add("commandes");
            commande1.getStyleClass().add("commandes");
            commande2.getStyleClass().add("commandes");
            commande3.getStyleClass().add("commandes");
            commande4.getStyleClass().add("commandes");
            commande5.getStyleClass().add("commandes");
            commande6.getStyleClass().add("commandes");
        } catch (Exception e){}
        
        ObjectOutputStream oosGrille = null;
        ObjectInputStream oisGrille = null;
        Grille gr = null;
        ObjectOutputStream oosStyle = null;
        ObjectInputStream oisStyle = null;
        Style st = null;
        try{
            final FileInputStream fichierlnGrille = new FileInputStream("../../model.ser");
            oisGrille = new ObjectInputStream(fichierlnGrille);
            gr = (Grille)oisGrille.readObject();
            final FileInputStream fichierlnStyle = new FileInputStream("../../style.ser");
            oisStyle = new ObjectInputStream(fichierlnStyle);
            st = (Style)oisStyle.readObject();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally{
            try{
                if(oisGrille != null){
                    oisGrille.close();
                }
                if(oisStyle != null){
                    oisStyle.close();
                }
            }catch (final IOException exc){
                exc.printStackTrace();
            }
        }
        
        if(gr == null){
       
        //Initialisation de la partie avec les deux premières cases aux hasard 
        boolean b = modelGrille.nouvelleCase();
        b = modelGrille.nouvelleCase();
        }else{
            modelGrille = gr;
        }
        
        if (st != null){
            try{
            // On met le bon style
            fond.getStylesheets().clear();
            fond.getStylesheets().add(st.styleActuel);
            grStyle.getSelectedToggle().setSelected(false);
            // On coche le bon style dans le menu
            switch(st.styleActuel){
                case "css/styles.css":
                    themeClassique.setSelected(true);
                    break;
                case "css/modeNuit.css":
                    themeNuit.setSelected(true);
                    break;
                case "css/wanda.css":
                    themeWanda.setSelected(true);
                    break;
                case "css/amandine.css":
                    themeAmandine.setSelected(true);
                    break;
                case "css/amelie.css":
                    themeAmelie.setSelected(true);
                    break;
                case "css/perso.css":
                    themePerso.setSelected(true);
                    break;
                default:
                    break;
            }
            } catch (Exception e){System.out.println("HAHAH");}
            
        }
        
        System.out.println(modelGrille);
        afficheGrille(modelGrille);     

        originator.set(modelGrille);
        caretaker.addMemento(originator.saveToMemento());                
    }
    
    @FXML private void fenetrePersonnalisation(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        stage.setTitle("Fenetre de personnalisation");
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("FXMLColorPicker.fxml"))));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }
}
