/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import css.Style;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.io.ObjectInputStream;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import threads.*;
import reseauClient.*;
import reseauServeur.*;

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
    private Menu menuFic, menuAide, menuEdit, menuCompet;
    @FXML
    private MenuItem quitter, nouveauJeu, changerStyle, aPropos, backMove, avancerUnCoup, newCompetMenu, joinCompetMenu;
    @FXML
    private RadioMenuItem themeClassique, themeNuit, themeWanda, themeAmandine, themeAmelie, themePerso, themeAme2, themeWVert;
    @FXML
    private ToggleGroup grStyle;
    @FXML
    private Label txtScore, score, logo, resultat, commande0, commande1, commande2, commande3, commande4, commande5, commande6;
    @FXML
    private VBox commandes;
    @FXML
    private Button bHaut, bBas, bGauche, bDroite, bSup, bInf;
    
    private Grille modelGrille = Grille.getInstance();
    public Style style = new Style();
    
    Caretaker caretaker= new Caretaker();
    Originator originator=new Originator();
    
    private Joueur joueur = new Joueur("");
    FXMLClientController clientController;
    FXMLServeurController serveurController;
    
    // Les événements
    
    @FXML
    public void quitter(ActionEvent event) {
        System.out.println("Au revoir!");
        ObjectOutputStream oosGrille = null;
        ObjectOutputStream oosStyle = null;
        
        if (modelGrille.getModeJeu() == SOLO) {
            if (modelGrille.partieFinie()){
                nouvellePartie(SOLO);
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
        } else {
            if (serveurController != null) {
                serveurController.arreterServeur();
            }
            if (clientController != null) {
                clientController.arreterClient();
            }
        }
            
        System.exit(0);
    }


    @FXML
    private void nouveauJeu(ActionEvent event) {
        System.out.println("\n\n\nNouvelle partie!");
        if (modelGrille.getModeJeu() == SOLO){
            nouvellePartie(SOLO);
        } else {
            if (clientController.gare.isConnected()){
                nouvellePartie(joueur, modelGrille.getModeJeu());
                this.joueur.setScore(modelGrille.getScore());
                this.joueur.setTuileMax(modelGrille.getValeurMax());
                clientController.gare.update(joueur);
                clientController.gare.share();
            } else {
                nouvellePartie(SOLO);
            }
        }
        
    }
    
    @FXML
    private void revenirUnCoup(ActionEvent event) {
        int index = caretaker.getIndex();
        modelGrille.update(originator.restoreFromMemento(caretaker.getMemento(index - 1)));
//        System.out.println("On récupère " + (index - 1));
//        for (Case c : modelGrille.getGr()){
//            System.out.println(c);
//        }
        caretaker.setIndex(index - 1);
        this.joueur.setScore(modelGrille.getScore());
        this.joueur.setTuileMax(modelGrille.getValeurMax());
        clientController.gare.update(joueur);
        clientController.gare.share();
        afficheGrille(modelGrille);
        System.out.println(modelGrille);
    }
    
    @FXML
    private void avancerUnCoup(ActionEvent event) {
        int index = caretaker.getIndex();
        modelGrille.update(originator.restoreFromMemento(caretaker.getMemento(index + 1)));
        caretaker.setIndex(index + 1);
        originator.set(modelGrille.clone());
        caretaker.addMemento(originator.saveToMemento());
        this.joueur.setScore(modelGrille.getScore());
        this.joueur.setTuileMax(modelGrille.getValeurMax());
        clientController.gare.update(joueur);
        clientController.gare.share();
        afficheGrille(modelGrille);
        System.out.println(modelGrille);
    }
    
    @FXML
    private void newCompet(ActionEvent event) throws IOException {
        System.out.println("\n\n\nCréation d'une partie en mode compétitf");
        
        menuCompet.setDisable(true);
        
        // Le serveur
        
        // Load fenetre d'ouverture serveur
        FXMLLoader loaderServeur = new FXMLLoader(getClass().getResource("FXMLServeur.fxml"));
        Parent rootServeur = loaderServeur.load();
        // Recupérer le controller
        serveurController = loaderServeur.getController();
        // Transmettre ce qu'on veut
        serveurController.transferStyle(style);
        // Afficher la fenetre
        Stage stageServeur = new Stage();
        Scene sceneServeur = new Scene(rootServeur);
        
        // Fermeture propre du serveur
        stageServeur.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (serveurController.isConnected()){
                e.consume();
                showAlertCloseServeur(stageServeur);
            } else {
                serveurController.arreterServeur();
                serveurController = null;
                menuCompet.setDisable(false);
            }
        });
        
        // On positionne la page du serveur
        stageServeur.setX(0);
        stageServeur.setX(0);
        stageServeur.setScene(sceneServeur);
//        stage.initModality(Modality.WINDOW_MODAL);
        sceneServeur.getStylesheets().add(style.styleActuel);
        stageServeur.show();
        
        // Le client
        clientController = this.joinCompet(event);
        clientController.setConnexion(serveurController.getConnexion());
        clientController.giveObjects(this);
        
      
    }
    
    @FXML
    private FXMLClientController joinCompet(ActionEvent event) throws IOException {
        System.out.println("\n\n\nRejoindre une partie en mode compétitif");
        
        newCompetMenu.setDisable(true);
        joinCompetMenu.setDisable(true);
        
        // Load fenetre d'ouverture serveur
        FXMLLoader loaderClient = new FXMLLoader(getClass().getResource("FXMLClient.fxml"));
        Parent rootClient = loaderClient.load();
        // Recupérer le controller
        clientController = loaderClient.getController();
        clientController.giveObjects(this);
        // Transmettre ce qu'on veut
        clientController.transferStyle(style);
        // Afficher la fenetre
        Stage stageClient = new Stage();
        Scene sceneClient = new Scene(rootClient);
        
        // Fermeture propre du serveur
        stageClient.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (clientController.isConnected()){
                e.consume();
                showAlertCloseClient(stageClient);
            } else {
                clientController = null;
                newCompetMenu.setDisable(false);
                joinCompetMenu.setDisable(false);
            }
        });
        
        stageClient.setScene(sceneClient);
        stageClient.initModality(Modality.NONE);
        sceneClient.getStylesheets().add(style.styleActuel);
        stageClient.show();
        return clientController;
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
                fond.getStylesheets().add("css/wvert.css");
                style.styleActuel = "css/wvert.css";
                break;
            case 7 :
                style.applyCSS(fond);
                break;
            default:
                break;
        }
    }

    
    @FXML 
    private void fenetrePersonnalisation(ActionEvent event) throws IOException {
        // Load fenetre de personnalisation
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLColorPicker.fxml"));
        Parent root = loader.load();
        // Recupérer le controller
        FXMLColorPickerController personnalisationController = loader.getController();
        // Transmettre ce qu'on veut
        personnalisationController.transferStyle(style, fond);
        //System.out.println(style.styleActuel);
        // Afficher la fenetre
        //Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLColorPicker.fxml")));
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Fenetre de personnalisation");
        stage.initModality(Modality.WINDOW_MODAL);
        scene.getStylesheets().add(style.styleActuel);
        stage.show();
    }
    
    
    
    
    
    
    @FXML
    private void clicHaut(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(HAUT);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
        }
    }
    
    @FXML
    private void clicBas(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(BAS);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
        }
    }
    
    @FXML
    private void clicGauche(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(GAUCHE);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
        }
    }
    
    @FXML
    private void clicDroite(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(DROITE);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
        }
    }
    
    @FXML
    private void clicSup(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(SUPERIEUR);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
        }
    }
    
    @FXML
    private void clicInf(MouseEvent m){
        if (!modelGrille.partieFinie()){
            joue(INFERIEUR);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
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
            if (modelGrille.getModeJeu() == SOLO) {
                resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientController.gare.update(joueur);
                    clientController.gare.share();
                }
            }
        }
    }
    
    
    // Les méthodes
    
    public int getModeJeu() {
        return modelGrille.getModeJeu();
    }
    
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
    public void nouvellePartie(int modeJeu){
//        fond.getScene().getWindow().centerOnScreen();
        fond.getScene().getWindow().requestFocus();
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
        modelGrille = modelGrille.newGame();
        modelGrille.setModeJeu(modeJeu);
        
        //Initialisation de la partie avec les deux premières cases aux hasard
        boolean b = modelGrille.nouvelleCase();
        b = modelGrille.nouvelleCase();
        System.out.println(modelGrille);
        afficheGrille(modelGrille);
        originator=new Originator();
        caretaker=new Caretaker();
        originator.set(modelGrille.clone());
        caretaker.addMemento(originator.saveToMemento());
    }
    
    public void nouvellePartie(Joueur j, int modeJeu){
//        fond.getScene().getWindow().centerOnScreen();
        fond.getScene().getWindow().requestFocus();
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
        modelGrille = modelGrille.newGame();
        joueur = j;
        if (modeJeu == COMPETITION){
            modelGrille.setModeJeu(modeJeu);
        }
        
        //Initialisation de la partie avec les deux premières cases aux hasard
        boolean b = modelGrille.nouvelleCase();
        b = modelGrille.nouvelleCase();
        System.out.println(modelGrille);
        afficheGrille(modelGrille);
        originator=new Originator();
        caretaker=new Caretaker();
        originator.set(modelGrille.clone());
        caretaker.addMemento(originator.saveToMemento());
    }

    
    public void joue(int direction){
        System.out.println();
        if (direction != 0) {
            boolean b2 = modelGrille.initialiserDeplacement(direction);
            
            if (b2) {
                boolean b = modelGrille.nouvelleCase();
                if (!b) {
                    modelGrille.defaite();
                    resultat.setText("La partie est finie. Votre score est " + modelGrille.getScore() + ".");
                    if (modelGrille.getModeJeu() == COMPETITION) {
                        this.joueur.setFini(true);
                        this.joueur.stopTemps();
                    }
                }
            }
            originator.set(modelGrille.clone());
            caretaker.addMemento(originator.saveToMemento());
            afficheGrille(modelGrille);
            System.out.println(modelGrille);
            if (modelGrille.getValeurMax()>=OBJECTIF){
                modelGrille.victoire();
                resultat.setText("Bravo ! Vous avez atteint " + modelGrille.getValeurMax() + "\nVotre score est " + modelGrille.getScore() + ".");
                if (modelGrille.getModeJeu() == COMPETITION) {
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                }
            }
        }
        if (modelGrille.getModeJeu() == COMPETITION) {
            this.joueur.setScore(modelGrille.getScore());
            this.joueur.setTuileMax(modelGrille.getValeurMax());
            clientController.gare.update(joueur);
            clientController.gare.share();
        }
    }
    
    public void afficherListeJoueurs(ListeJoueurs liste) {
        String listeJoueurs = "";
        for (Joueur p : liste.getListe()){
            listeJoueurs = listeJoueurs + p.toString() + "\n";
        }
        resultat.setText(listeJoueurs);
    }
    
    public void showAlertCloseServeur(Stage s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez couper la connexion avec d'autres joueurs.");
        alert.setContentText("Fermer quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                serveurController.arreterServeur();
                fond.getScene().getWindow().requestFocus();
                menuCompet.setDisable(false);
                s.close();
                serveurController = null;
            }
        });
    }
    
    public void showAlertCloseClient(Stage s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez vous déconnecter du serveur.");
        alert.setContentText("Fermer quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                clientController.arreterClient();
                newCompetMenu.setDisable(false);
                joinCompetMenu.setDisable(false);
                fond.getScene().getWindow().requestFocus();
                s.close();
                clientController = null;
            }
        });
    }
    
    public void reactiverMenuCompet() {
        newCompetMenu.setDisable(false);
        joinCompetMenu.setDisable(false);
    }
    
    public void focus() {
        fond.getScene().getWindow().requestFocus();
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
            style = st;
            try{
                // On met le bon style
                fond.getStylesheets().clear();
                grStyle.getSelectedToggle().setSelected(false);
                // On coche le bon style dans le menu
                switch(st.styleActuel){
                    case "css/styles.css":
                        fond.getStylesheets().add("css/styles.css");
                        themeClassique.setSelected(true);
                        break;
                    case "css/modeNuit.css":
                        fond.getStylesheets().add("css/modeNuit.css");
                        themeNuit.setSelected(true);
                        break;
                    case "css/wanda.css":
                        fond.getStylesheets().add("css/wanda.css");
                        themeWanda.setSelected(true);
                        break;
                    case "css/amandine.css":
                        fond.getStylesheets().add("css/amandine.css");
                        themeAmandine.setSelected(true);
                        break;
                    case "css/amelie.css":
                        fond.getStylesheets().add("css/amelie.css");
                        themeAmelie.setSelected(true);
                        break;
                    case "css/perso.css":
//                        fond.getStylesheets().add("css/basePerso.css");
//                        fond.getStylesheets().add("css/perso.css");
                        style.applyCSS(fond);
                        themePerso.setSelected(true);
                        break;
                    case "css/ame.css":
                        fond.getStylesheets().add("css/ame.css");
                        themeAme2.setSelected(true);
                        break;
                    case "css/wvert.css":
                        fond.getStylesheets().add("css/wvert.css");
                        themePerso.setSelected(true);
                        break;
                    default:
                        break;
                }
            } catch (Exception e){
                System.out.println("HAHAH");
            }
        }
        
        System.out.println(modelGrille);
        afficheGrille(modelGrille);     

        originator.set(modelGrille.clone());
        caretaker.addMemento(originator.saveToMemento());  
        
    }
    
    
}
