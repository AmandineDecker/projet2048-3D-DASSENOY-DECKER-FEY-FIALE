/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author Valou
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        // On relie la feuille de styles
        boolean add = scene.getStylesheets().add("css/styles.css");
        
        stage.setScene(scene);
        stage.show();
        
        
        stage.setOnCloseRequest(e -> {
            // Ici mettre le code à utiliser quand on clique sur la croix
            e.consume();
            showAlert();
        });
    }
    
    public void showAlert() { 
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText("La partie ne sera pas sauvegardée, utilisez le menu Fichier pour quitter en sauvegardant.");
        alert.setContentText("Quitter quand même ?");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                // Si on veut supprimer la derniere sauvegarde quand le joueur quitte sans sauvegarder
//                File f = new File("../../model.ser");
//                if (f.exists()){
//                    f.delete();
//                }
                System.exit(0);
            }
//            if (rs == ButtonType.CANCEL) {
//                System.out.println("Pressed CANCEL.");
//            }
        });
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
