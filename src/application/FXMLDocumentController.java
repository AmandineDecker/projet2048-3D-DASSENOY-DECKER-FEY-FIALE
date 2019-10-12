/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Fusion;
import model.Glissement;

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
    
    private Grille modelGrille = new Grille();
    
    // Les événements
    
    @FXML
    public void quitter(ActionEvent event) {
        System.out.println("Au revoir!");
        ObjectOutputStream oos = null;
        
        if (modelGrille.partieFinie()){
            nouvellePartie();
        }
        try{
            final FileOutputStream fichier = new FileOutputStream("../../model.ser");
            oos = new ObjectOutputStream(fichier);
            oos.writeObject(modelGrille);
            oos.flush();
        }catch (final IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(oos != null){
                    oos.flush();
                    oos.close();
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
        c.setGrimpe(false);
        p.setVisible(true);
        l.setVisible(true);
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
                    fus = new Fusion(ap1, gr1, cCopy);
                    th = new Thread(fus);
                }
                else {
                    gl = new Glissement(ap1, gr1, cCopy);
                    th = new Thread(gl);
                }
                th.setDaemon(true);
                th.start();
                break;
            case 1:
                // Ajouter le glissement ici
                if (c.getFusionneX0() != -1 && c.getFusionneY0() != -1){
                    fus = new Fusion(ap2, gr2, cCopy);
                    th = new Thread(fus);
                }
                else {
                    gl = new Glissement(ap2, gr2, cCopy);
                    th = new Thread(gl);
                }
                th.setDaemon(true);
                th.start();
                break;
            default:
                // Ajouter le glissement ici
                if (c.getFusionneX0() != -1 && c.getFusionneY0() != -1){
                    fus = new Fusion(ap3, gr3, cCopy);
                    th = new Thread(fus);
                }
                else {
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
        
        HashSet<Case> glisse = new HashSet();
        HashSet<Case> apparait = new HashSet();
        HashSet<Case> immobile = new HashSet();
        
        for (Case c : gr.getGr()){
            if (c.getGrimpe()){
                apparait.add(c);
            }
            else if ((c.getGlisseX0() != -1 && c.getGlisseY0() != -1) || (c.getFusionneX0() != -1 && c.getFusionneY0() != -1)){
                glisse.add(c);
            }
            else {
                immobile.add(c);
            }
            //placeCase(c);
        }
        
        for (Case c : immobile){
            placeCase(c);
        }
        
        Thread fin = null;
        //HashSet<Thread> threads = new HashSet();
        
        for (Case c : glisse){
            if (fin != null){
                glisseCase(c);
            }
            else {fin = glisseCase(c);}
        }
        if (fin != null){
            try { // Pour éviter que la nouvelle case n'apparaisse sous un glissement
                fin.join(TPSSLEEP*150);
            } catch (Exception ex) {
                System.out.println("Join killed");
            }
        }
        
        for (Case c : apparait){
            placeCase(c);
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
    }

    
    //////////////////////////////////////////////////////////////////////
    

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("le contrôleur initialise la vue");
        fond.getStyleClass().add("pane");
        gr1.getStyleClass().add("gridpane");
        gr2.getStyleClass().add("gridpane");
        gr3.getStyleClass().add("gridpane");
        
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Grille gr = null;
        try{
            final FileInputStream fichierln = new FileInputStream("../../model.ser");
            ois = new ObjectInputStream(fichierln);
            gr = (Grille)ois.readObject();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally{
            try{
                if(ois != null){
                    ois.close();
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
        
        System.out.println(modelGrille);
        afficheGrille(modelGrille);     
    }
    
}
