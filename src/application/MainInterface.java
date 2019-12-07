/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.*;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.MainConsole;

/**
 * Main: lancement du jeu.
 * Lancement de l'interface graphique par défault, mais une option permet de 
 * choisir la console.
 */
public class MainInterface extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        
        // Recupérer le controller
        FXMLDocumentController controller = loader.getController();
        
        Scene scene = new Scene(root);
        
        // On relie la feuille de styles (Style par défaut de JavaFX -> https://gist.github.com/maxd/63691840fc372f22f470)
        boolean add = scene.getStylesheets().add("css/styles.css");
        
        stage.setScene(scene);
        stage.show();
        
        stage.requestFocus();
        
        
        stage.setOnCloseRequest(e -> {
            try {
                String distCSSFile = new File(FXMLDocumentController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "\\", cssFileName = "perso.css";
                if (new File(distCSSFile + cssFileName).exists()) {
                    File f = new File(distCSSFile + cssFileName);
                    f.delete();
                    
                }   controller.quitter();
            } catch (URISyntaxException ex) {
                Logger.getLogger(MainInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            try {
                if (!new File("data").exists()) {
                    new File("data").mkdirs();
                }
                final InputStream fichierData = new FileInputStream("data/data.txt");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(fichierData))) {
                    String ouverture = reader.readLine();
                    if (ouverture == null) {
                        reader.close();
                        try (PrintWriter writer = new PrintWriter("data/data.txt")) {
                            // Si rien n'est spécifié, on joue avec interface
                            writer.println("1");
                            writer.close();
                        }
                    } else if (ouverture.equals("0")) { // Jeu dans la console
                        reader.close();
                        MainConsole.main(args);
                        System.exit(1);
                    } // Sinon jeu avec interface graphique
                }
            } catch (FileNotFoundException ex) {
                try (PrintWriter writer = new PrintWriter("data/data.txt")) {
                            // Si rien n'est spécifié, on joue avec interface
                            writer.println("1");
                            writer.close();
                        } catch (FileNotFoundException ex1) {
                    System.err.println(ex1);
                }
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
        launch(args);
        
    }
    
}
