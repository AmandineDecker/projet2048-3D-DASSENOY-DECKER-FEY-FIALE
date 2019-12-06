/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import bdd.FXMLSaveDataBaseController;
import bdd.FXMLShowDataBaseController;
import reseauServeurCompet.FXMLServeurCompetController;
import reseauClientCompet.FXMLClientCompetController;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.*;
import reseauClientCoop.FXMLClientCoopController;
import reseauServeurCoop.FXMLServeurCoopController;
import threads.*;

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
    private Menu menuFic, menuAide, menuEdit, menuCompet, menuCoop;
    @FXML
    private MenuItem quitter, nouveauJeu, menuScores, changerStyle, aPropos, backMove, avancerUnCoup, newCompetMenu, joinCompetMenu, newCoopMenu, joinCoopMenu;
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
    FXMLClientCompetController clientCompetController;
    FXMLServeurCompetController serveurCompetController;
    FXMLClientCoopController clientCoopController;
    FXMLServeurCoopController serveurCoopController;
    
    // Les événements
    
    /**
     *
     * Cette fonction permet de quitter le jeu tout en sauvegardant la partie.
     * @param event 
     */
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
            if (serveurCompetController != null) {
                serveurCompetController.arreterServeur(true);
            }
            if (clientCompetController != null) {
                clientCompetController.arreterClient();
            }
        }
            
        System.exit(0);
    }

    /**
     *
     * Cette fonction permet de lancer une nouvelle partie.
     * @param event 
     */
    @FXML
    private void nouveauJeu(ActionEvent event) {
        System.out.println("\n\n\nNouvelle partie!");
        if (modelGrille.getModeJeu() == SOLO){
            nouvellePartie(SOLO);
        } else {
            if (clientCompetController.gare.isConnected()){
                nouvellePartie(joueur, modelGrille.getModeJeu());
                this.joueur.setScore(modelGrille.getScore());
                this.joueur.setTuileMax(modelGrille.getValeurMax());
                clientCompetController.gare.update(joueur);
                clientCompetController.gare.share();
            } else {
                nouvellePartie(SOLO);
            }
        }
    }
    
    /**
    * Cette fonction permet d'afficher la page des scores.
    * @param event 
    */
    @FXML
    private void voirScores(ActionEvent event) throws IOException {
        FXMLLoader loaderServeur = new FXMLLoader(getClass().getResource("FXMLShowDataBase.fxml"));
        Parent rootServeur = loaderServeur.load();
        // Recupérer le controller
        FXMLShowDataBaseController showDataBaseController = loaderServeur.getController();
        // Transmettre ce qu'on veut
        showDataBaseController.transferStyle(style);
        // Afficher la fenetre
        Stage stageScores = new Stage();
        stageScores.setTitle("Scores du jeu en solo");
        Scene sceneScores = new Scene(rootServeur);
        stageScores.setScene(sceneScores);

        sceneScores.getStylesheets().add(style.styleActuel);
        stageScores.show();
    }
    
    /**
     * Cette fonction permet de faire jouer le prochain  coup par l'intelligence
     * artificielle.
     * @param event 
     */
    @FXML
    private void jouerUnCoupIA(ActionEvent event) throws CloneNotSupportedException{
        int dir = unCoupIA();
        System.out.println("IA joue dans la direction " + dir);
        if (!modelGrille.partieFinie()){
            joue(dir);
        }
        afficheGrille(modelGrille);
        System.out.println("IA joue" + "\n" + modelGrille);
    }
    
    /** 
     * Cette fonction permet de revenir un coup en arrière.
     * @param event 
     */
    @FXML
    private void revenirUnCoup(ActionEvent event) throws CloneNotSupportedException {
        int index = caretaker.getIndex();
        modelGrille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index - 1)));
//        System.out.println("On récupère " + (index - 1));
//        for (Case c : modelGrille.getGr()){
//            System.out.println(c);
//        }
        caretaker.setIndex(index - 1);
        if (modelGrille.getModeJeu() == COMPETITION) {
            this.joueur.setScore(modelGrille.getScore());
            this.joueur.setTuileMax(modelGrille.getValeurMax());
            clientCompetController.gare.update(joueur);
            clientCompetController.gare.share();
        }
        afficheGrille(modelGrille);
        index = caretaker.getIndex();
        if (index < 1){
            backMove.setDisable(true);
        }
        else if (index >= 1) {
            backMove.setDisable(false);
        }
        System.out.println(modelGrille);
    }
    
    /**
    * Cette fonction permet de refaire un coup annulé.
    * @param event 
    */ 
    @FXML
    private void avancerUnCoup(ActionEvent event) throws CloneNotSupportedException {
        int index = caretaker.getIndex();
        modelGrille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index + 1)));
        caretaker.setIndex(index + 1);
        if (modelGrille.getModeJeu() == COMPETITION) {
            this.joueur.setScore(modelGrille.getScore());
            this.joueur.setTuileMax(modelGrille.getValeurMax());
            clientCompetController.gare.update(joueur);
            clientCompetController.gare.share();
        }
        afficheGrille(modelGrille);
        System.out.println(modelGrille);
    }
    
    /**
     * Cette fonction permet de créer une nouvelle partie en mode compétition.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void newCompet(ActionEvent event) throws IOException {
        System.out.println("\n\n\nCréation d'une partie en mode compétitf");
        
        menuCompet.setDisable(true);
        menuCoop.setDisable(true);
        
        // Le serveur
        
        // Load fenetre d'ouverture serveur
        FXMLLoader loaderServeur = new FXMLLoader(getClass().getResource("FXMLServeurCompet.fxml"));
        Parent rootServeur = loaderServeur.load();
        // Recupérer le controller
        serveurCompetController = loaderServeur.getController();
        // Transmettre ce qu'on veut
        serveurCompetController.transferStyle(style);
        // Afficher la fenetre
        Stage stageServeur = new Stage();
        stageServeur.setTitle("Serveur compétiton");
        Scene sceneServeur = new Scene(rootServeur);
        
        // Fermeture propre du serveur
        stageServeur.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (serveurCompetController.isConnected()){
                e.consume();
                showAlertCloseServeurCompet(true);
            } else {
                serveurCompetController.arreterServeur(true);
                serveurCompetController = null;
                menuCompet.setDisable(false);
                menuCoop.setDisable(false);
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
        clientCompetController = this.joinCompet(event);
        clientCompetController.setConnexion(serveurCompetController.getConnexion());
        clientCompetController.giveObjects(this);
    }
    
    /**
     * Cette fonction permet de rejoinre une partie en mode compétition.
     * @param event
     * @return ClientController
     * @throws IOException 
     */
    @FXML
    private FXMLClientCompetController joinCompet(ActionEvent event) throws IOException {
        System.out.println("\n\n\nRejoindre une partie en mode compétitif");
        
        newCompetMenu.setDisable(true);
        joinCompetMenu.setDisable(true);
        newCoopMenu.setDisable(true);
        joinCoopMenu.setDisable(true);
        
        // Load fenetre d'ouverture serveur
        FXMLLoader loaderClient = new FXMLLoader(getClass().getResource("FXMLClientCompet.fxml"));
        Parent rootClient = loaderClient.load();
        // Recupérer le controller
        clientCompetController = loaderClient.getController();
        clientCompetController.giveObjects(this);
        // Transmettre ce qu'on veut
        clientCompetController.transferStyle(style);
        // Afficher la fenetre
        Stage stageClient = new Stage();
        stageClient.setTitle("Client compétiton");
        placeStageNE(stageClient);
        Scene sceneClient = new Scene(rootClient);
        
        // Fermeture propre du serveur
        stageClient.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (clientCompetController.isConnected()){
                e.consume();
                showAlertCloseClientCompet(stageClient);
            } else {
                clientCompetController = null;
                newCompetMenu.setDisable(false);
                joinCompetMenu.setDisable(false);
            }
        });
        
        stageClient.setScene(sceneClient);
        stageClient.initModality(Modality.NONE);
        sceneClient.getStylesheets().add(style.styleActuel);
        stageClient.show();
        return clientCompetController;
    }
    
    /**
     * Cette fonction permet la création d'une partie en mode coopératif.
     * @param event
     * @throws IOException 
     */
    @FXML
    private void newCoop(ActionEvent event) throws IOException {
        System.out.println("\n\n\nCréation d'une partie en mode coopératif");
        
        menuCompet.setDisable(true);
        menuCoop.setDisable(true);
        
        // Le serveur
        
        // Load fenetre d'ouverture serveur
        FXMLLoader loaderServeur = new FXMLLoader(getClass().getResource("FXMLServeurCoop.fxml"));
        Parent rootServeur = loaderServeur.load();
        // Recupérer le controller
        serveurCoopController = loaderServeur.getController();
        // Transmettre ce qu'on veut
        serveurCoopController.transferStyle(style);
        // Afficher la fenetre
        Stage stageServeur = new Stage();
        stageServeur.setTitle("Serveur coopération");
        Scene sceneServeur = new Scene(rootServeur);
        
        // Fermeture propre du serveur
        stageServeur.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (serveurCoopController.isConnected()){
                e.consume();
                showAlertCloseServeurCoop(true);
            } else {
                serveurCoopController.arreterServeur(true);
                serveurCoopController = null;
                menuCompet.setDisable(false);
                menuCoop.setDisable(false);
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
        clientCoopController = this.joinCoop(event);
        clientCoopController.setConnexion(serveurCoopController.getConnexion());
        clientCoopController.giveObjects(this);
    }
    
    /**
     * Cette fonction permet de rejoindre une partie en mode coopératif.
     * @param event
     * @return clientCoopController
     * @throws IOException 
     */
    @FXML
    private FXMLClientCoopController joinCoop(ActionEvent event) throws IOException {
        System.out.println("\n\n\nRejoindre une partie en mode coopératif");
        
        newCoopMenu.setDisable(true);
        joinCoopMenu.setDisable(true);
        newCompetMenu.setDisable(true);
        joinCompetMenu.setDisable(true);
        
        // Load fenetre d'ouverture serveur
        FXMLLoader loaderClient = new FXMLLoader(getClass().getResource("FXMLClientCoop.fxml"));
        Parent rootClient = loaderClient.load();
        // Recupérer le controller
        clientCoopController = loaderClient.getController();
        clientCoopController.giveObjects(this);
        // Transmettre ce qu'on veut
        clientCoopController.transferStyle(style);
        // Afficher la fenetre
        Stage stageClient = new Stage();
        stageClient.setTitle("Client coopération");
        placeStageNE(stageClient);
        Scene sceneClient = new Scene(rootClient);
        
        // Fermeture propre du serveur
        stageClient.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            if (clientCoopController.isConnected()){
                e.consume();
                showAlertCloseClientCoop(stageClient);
            } else {
                clientCoopController = null;
                newCoopMenu.setDisable(false);
                joinCoopMenu.setDisable(false);
                newCompetMenu.setDisable(false);
                joinCompetMenu.setDisable(false);
            }
        });
        
        stageClient.setScene(sceneClient);
        stageClient.initModality(Modality.NONE);
        sceneClient.getStylesheets().add(style.styleActuel);
        stageClient.show();
        return clientCoopController;
    }
    
   /**
     * Cette fonction permet de changer le thème du jeu.
     * @param event 
     */ 
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

    /**
     * Cette fonction permet au joueur du changer les couleurs du jeu.
     * @param event
     * @throws IOException 
     */
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
    
    /**
     * Cette fonction permet de déplacer les case vers le haut.
     * @param m 
     * Paramètre de type MouseEvent
     */
    @FXML
    private void clicHaut(MouseEvent m){
//        System.out.println(modelGrille.getModeJeu() == COOPERATION);
//        System.out.println(clientCoopController.gare.aLaMain());
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
            joue(HAUT);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
     /**
     * Cette fonction permet de déplacer les cases vers le bas.
     * @param m 
     * Paramètre de type MouseEvent
     */
    @FXML
    private void clicBas(MouseEvent m){
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
            joue(BAS);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
     /**
     * Cette fonction permet de déplcer les cases vers la gauche.
     * @param m 
     * Paramètre de type MouseEvent
     */
    @FXML
    private void clicGauche(MouseEvent m){
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
            joue(GAUCHE);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
    /**
     * Cette fonction permet de déplacer les cases vers la droite.
     * @param m 
     * Paramètre de type MouseEvent
     */
    @FXML
    private void clicDroite(MouseEvent m){
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
            joue(DROITE);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
    /**
     * Cette fonction permet de déplacer les cases sur la grille supérieure.
     * @param m 
     * Paramètre de type MouseEvent
     */
    @FXML
    private void clicSup(MouseEvent m){
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
            joue(SUPERIEUR);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
    /**
     * Cette fonction permet de déplacer les cases sur la grille inférieure.
     * @param m 
     * Paramètre de type MouseEvent
     */
    @FXML
    private void clicInf(MouseEvent m){
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
            joue(INFERIEUR);
        }
        else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
    /**
     * Cette fonction permet de lancer le mouvement assigné à une touche:
     * z pour haut, s pour bas, q pour gauche, d pour droite, r pour supérieur 
     * et f pour inférieur.
     * @param ke 
     * Paramètre de type KeyEvent
     */
    @FXML
    public void toucheClavier(KeyEvent ke) {
//        System.out.println(modelGrille.getModeJeu());
//        System.out.println(clientCoopController.gare.aLaMain());
        if (modelGrille.getModeJeu() == COOPERATION && !clientCoopController.gare.aLaMain()) {
            // Ce n'est pas à ce joueur de jouer
        } else if (!modelGrille.partieFinie()){
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
        } else {
            if (modelGrille.getModeJeu() == SOLO) {
                String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                resultat.setText(aAfficher);
                this.fenetreBDD(aAfficher);
            } else if (modelGrille.getModeJeu() == COMPETITION) {
                if (!this.joueur.getFini()){
                    this.joueur.setFini(true);
                    this.joueur.stopTemps();
                    this.joueur.setScore(modelGrille.getScore());
                    this.joueur.setTuileMax(modelGrille.getValeurMax());
                    clientCompetController.gare.update(joueur);
                    clientCompetController.gare.share();
                }
            }
        }
    }
    
    
    // Les méthodes
    
    /**
     * Cette fonction renvoie le mode de jeu de la partie en cours
     * @return mode de jeu
     */
    public int getModeJeu() {
        return modelGrille.getModeJeu();
    }
    
    /**
     * Cette fonction permet de placer la case sur la fenêtre graphique sans effet
     * @param c 
     * Paramètre de type Case
     */
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
    
    /**
     * Cette fonction permet d'afficher une case sur la fenêtre graphique avec effet
     * @param c 
     * Paramètre de type Case
     */
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

    /**
     * Cette fonction permet de faire gisser les cases sur la fenêtre graphique.
     * @param c
     * Paramètre de type Case
     * @return Thread
     */
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

    /**
     * Cette fonction permet d'afficher la grille sur la fenêtre graphique.
     * @param gr 
     * Paramètre de type Grille
     */
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

    /**
     * Cette fonction permet de débuter une nouvelle partie selon le mode de jeu.
     * @param modeJeu 
     * Paramètre de type int
     */
    public void nouvellePartie(int modeJeu){
        try {
            //        fond.getScene().getWindow().centerOnScreen();
            fond.getScene().getWindow().requestFocus();
            resultat.setText("C'est parti !");
            if (modeJeu == SOLO) {
                avancerUnCoup.setDisable(false);
                backMove.setDisable(false);
                nouveauJeu.setDisable(false);
            } else {
                avancerUnCoup.setDisable(true);
                backMove.setDisable(true);
                nouveauJeu.setDisable(true);
            }
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
            
            if (modeJeu == COOPERATION) {
                clientCoopController.gare.updateGrille(modelGrille);
//            clientCoopController.gare.share();
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * Cette fonction permet de débuter une nouvelle partie selon le mode de jeu
     * et de spécifier le joueur.
     * @param j
     * Paramètre de type Joueur
     * @param modeJeu 
     * Paramètre de type int
     */
    public void nouvellePartie(Joueur j, int modeJeu){
        try {
            fond.getScene().getWindow().requestFocus();
            if (modeJeu == SOLO) {
                nouveauJeu.setDisable(false);
            } else {
                nouveauJeu.setDisable(true);
            }
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
            
            if (modeJeu == COOPERATION) {
                clientCoopController.gare.updateGrille(modelGrille);
                clientCoopController.gare.share();
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Cette fonction permet d'afficher la page de scores.
     * @param aAfficher
     * Paramètre de type String
     */
    public void fenetreBDD(String aAfficher){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLSaveDataBase.fxml"));
            Parent root = loader.load();
            FXMLSaveDataBaseController databaseController = loader.getController();
            databaseController.transferStyle(style);
            databaseController.giveObjects(this);
            databaseController.getData(modelGrille.getScore(), modelGrille.getValeurMax(), aAfficher);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Enregistrer score");
            stage.initModality(Modality.WINDOW_MODAL);
            scene.getStylesheets().add(style.styleActuel);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    /**
     * Cette fonction permet de déplacer les cases dans une direction.
     * @param direction 
     * Paramètre de type int
     */
    public void joue(int direction){
        System.out.println();
        if (direction != 0) {
            try {
                boolean b2 = modelGrille.initialiserDeplacement(direction);
                
                if (b2) {
                    
                    if (modelGrille.getValeurMax()>=OBJECTIF){
                        modelGrille.victoire();
                        String aAfficher = "Bravo ! Vous avez atteint " + modelGrille.getValeurMax() + "\nVotre score est " + modelGrille.getScore() + ".";
                        resultat.setText(aAfficher);
                        switch (modelGrille.getModeJeu()) {
                            case SOLO:
                                this.fenetreBDD(aAfficher);
                                break;
                            case COMPETITION:
                                this.joueur.setFini(true);
                                this.joueur.stopTemps();
                                break;
                            case COOPERATION:
                                clientCoopController.gare.updateGrille(modelGrille);
                                clientCoopController.gare.share();
                                clientCoopController.gare.shareInfos("PartieFinie");
                                clientCoopController.gare.donnerMain();
                                break;
                            default:
                                break;
                        }
                    } else {
                        boolean b = modelGrille.nouvelleCase();
                        if (!b) {
                            modelGrille.defaite();
                            String aAfficher = "La partie est finie. Votre score est " + modelGrille.getScore() + ".";
                            resultat.setText(aAfficher);
                            switch (modelGrille.getModeJeu()) {
                                case SOLO:
                                    this.fenetreBDD(aAfficher);
                                    break;
                                case COMPETITION:
                                    this.joueur.setFini(true);
                                    this.joueur.stopTemps();
                                    break;
                                case COOPERATION:
                                    clientCoopController.gare.updateGrille(modelGrille);
                                    clientCoopController.gare.share();
                                    clientCoopController.gare.shareInfos("PartieFinie");
                                    clientCoopController.gare.donnerMain();
                                    break;
                                default:
                                    break;
                            }
                        } else if (modelGrille.getModeJeu() == COOPERATION) {
                            // update grille
                            //                System.out.println("COOPERATION");
                            resultat.setText("A l'autre joueur");
                            clientCoopController.gare.updateGrille(modelGrille);
                            clientCoopController.gare.share();
                            clientCoopController.gare.donnerMain();
                        }
                    }
                }
                originator.set(modelGrille.clone());
                caretaker.addMemento(originator.saveToMemento());
                
                afficheGrille(modelGrille);
                System.out.println(modelGrille);
//            if (modelGrille.getValeurMax()>=OBJECTIF){
//                modelGrille.victoire();
//                resultat.setText("Bravo ! Vous avez atteint " + modelGrille.getValeurMax() + "\nVotre score est " + modelGrille.getScore() + ".");
//                if (modelGrille.getModeJeu() == COMPETITION) {
//                    this.joueur.setFini(true);
//                    this.joueur.stopTemps();
//                }
//            }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (modelGrille.getModeJeu() == COMPETITION) {
            this.joueur.setScore(modelGrille.getScore());
            this.joueur.setTuileMax(modelGrille.getValeurMax());
            clientCompetController.gare.update(joueur);
            clientCompetController.gare.share();
        }
    }
    
    /**
     * Cette fonction permet de d'afficher la listes de joueurs.
     * @param liste 
     * Paramètre de type ListeJoueurs
     */
    public void afficherListeJoueurs(ListeJoueurs liste) {
        String listeJoueurs = "";
        for (Joueur p : liste.getListe()){
            listeJoueurs = listeJoueurs + p.toString() + "\n";
        }
        resultat.setText(listeJoueurs);
    }
    
    /**
     * Cette fonction permet d'alerter le joueur qu'il va couper la connexion 
     * avec d'autres joueurs quand il est en mode compétition.
     * @param fermer 
     * Paramètre de type boolean
     */
    public void showAlertCloseServeurCompet(boolean fermer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez couper la connexion avec d'autres joueurs.");
        alert.setContentText("Fermer quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                serveurCompetController.arreterServeur(fermer);
                fond.getScene().getWindow().requestFocus();
                menuCompet.setDisable(false);
                menuCoop.setDisable(false);
                serveurCompetController = null;
            }
        });
    }
    
    /**
     * Cette fonction permet d'alerter le joueur qu'il va se déconnecter du 
     * serveur.
     * @param s 
     * Paramètre de type Stage
     */
    public void showAlertCloseClientCompet(Stage s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez vous déconnecter du serveur.");
        alert.setContentText("Fermer quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                clientCompetController.arreterClient();
                newCoopMenu.setDisable(false);
                joinCoopMenu.setDisable(false);
                newCompetMenu.setDisable(false);
                joinCompetMenu.setDisable(false);
                fond.getScene().getWindow().requestFocus();
                s.close();
                clientCompetController = null;
            }
        });
    }
    
    /**
     * Cette fonction permet d'alerter le joueur qu'il va couper la connexion 
     * avec d'autres joueurs quand il est en mode coopératif.
     * @param fermer 
     * Paramètre de type boolean
     */
    public void showAlertCloseServeurCoop(boolean fermer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez couper la connexion avec d'autres joueurs.");
        alert.setContentText("Fermer quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                serveurCoopController.arreterServeur(fermer);
                fond.getScene().getWindow().requestFocus();
                menuCompet.setDisable(false);
                menuCoop.setDisable(false);
                serveurCoopController = null;
            }
        });
    }
    
    /**
     * Cette fonction permet d'alerter le joueur, en mode coopératif, qu'il va 
     * se déconnecter du serveur et couper la connexion à l'autre joueur .
     * @param s 
     * Paramètre de type Stage
     */
    public void showAlertCloseClientCoop(Stage s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("Vous allez vous déconnecter du serveur et couper la connexion à l'autre joueur.");
        alert.setContentText("Fermer quand même ?");
        
        final Button btnOK = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.setOnAction( event -> {
            System.out.println("OK");
            clientCoopController.arreterClient();
            newCoopMenu.setDisable(false);
            joinCoopMenu.setDisable(false);
            newCompetMenu.setDisable(false);
            joinCompetMenu.setDisable(false);
            fond.getScene().getWindow().requestFocus();
            if (s != null) {
                s.close();
                clientCoopController = null;
            } else {
                System.out.println("Alert closed");
                alert.close();
            }
        } );
        
        final Button btnCANCEL = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnOK.setOnAction( event -> {} );
        
        alert.show();
    }
    
    /**
     * Cette fonction permet de réactiver le menu qui permet de lancer une 
     * nouvelle partie en compétition.
     */
    public void reactiverMenuCompet() {
        menuCompet.setDisable(false);
        newCompetMenu.setDisable(false);
        joinCompetMenu.setDisable(false);
    }
    /**
     * Cette fonction permet de réactiver le menu qui permet de lancer une 
     * nouvelle partie en mode coopératif.
     */
    public void reactiverMenuCoop() {
        menuCoop.setDisable(false);
        newCoopMenu.setDisable(false);
        joinCoopMenu.setDisable(false);
    }
    
    /**
     * Cette fonction permet de couper la connexion avec le serveur client.
     */
    public void fermerReseau() {
        if (clientCompetController != null) {
            if (clientCompetController.isConnected()) {
                clientCompetController.arreterClient();
                newCompetMenu.setDisable(false);
                joinCompetMenu.setDisable(false);
                clientCompetController = null;
            }
        }
        if (serveurCompetController != null) {
            if (serveurCompetController.isConnected()) {
                showAlertCloseServeurCompet(true);
            }
        }
    }
    
    /**
     * Cette fonction permet de modifier la valeur du resultat sur l'interface 
     * graphique .
     * @param str 
     * Paramètre de type String
     */
    public void setInfos(String str) {
        resultat.setText(str);
    }
    
    /**
     * Cette fonction permet d'afficher une fenêtre en avant sur l'écran
     */
    public void focus() {
        fond.getScene().getWindow().requestFocus();
    }
    
    /**
     * Cette fonction permet de mettre la grille à jour
     * @param gr 
     * Paramètre de type Grille
     */
    public void updateGrille(Grille gr) {
        modelGrille = Grille.setInstance(gr);
    }
    
    /**
     * Cette fonction permet d'afficher des grilles vides sur l'écran
     */
    public void effacerGrille() {
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
    }
    
    /**
     * Cette fonction permet de placer une fenêtre dans le coin supérieur droit de l'écran
     */
    public void placeStageNE(Stage stage) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMaxX());
        stage.setY(primaryScreenBounds.getMinY());
    }
    
    /**
     * Cette fonction permet de déterminer le coup qui donne le meilleur score.
     * @return int
     */
    public int unCoupIA() throws CloneNotSupportedException{
        int dir = 0;
        int[] scoretab = new int[3];
        int scoreMax = 0;
        int index = caretaker.getIndex();
        modelGrille.initialiserDeplacement(INFERIEUR);
        index++;
        System.out.println(modelGrille);
        scoretab[0]=modelGrille.getScore();
        modelGrille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index - 1)));
        caretaker.setIndex(index - 1);
        index--;
        System.out.println(modelGrille);
        modelGrille.initialiserDeplacement(GAUCHE);
        index++;
        System.out.println(modelGrille);
        scoretab[1]=modelGrille.getScore();
        modelGrille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index - 1)));
        caretaker.setIndex(index - 1);
        index--;
        System.out.println(modelGrille);
        modelGrille.initialiserDeplacement(BAS);
        index++;
        System.out.println(modelGrille);
        scoretab[2]=modelGrille.getScore();
        modelGrille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index - 1)));
        caretaker.setIndex(index - 1);
        index--;
        System.out.println(modelGrille);
        for (int k=0; k<3 ;k++){
            if (scoretab[k]>scoreMax){
                scoreMax = scoretab[k];
                dir = k-3;
            }
        }
        return dir;
    }
    
     
    //////////////////////////////////////////////////////////////////////
    

    /**
     * Cette fonction permet d'initialiser l'interface graphique avec le bon 
     * style et la grille sauvegardée le cas échéant.
     * @param url
     * Paramètre de type URL
     * @param rb 
     * Paramètre de type ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
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
              
        } catch (CloneNotSupportedException ex){Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
}
        
    }
    
    
}
