/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package css;


import application.FXMLDocumentController;
import application.Main;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Amandine
 */
public class Style implements Serializable{
    
//    final transient String chemin = "src/css/perso.css";
//    public transient String styleFond, styleTexte, styleTuile2, styleTuile4, styleTuile8, styleTuile16, styleTuile32, styleTuile64, styleTuile128, styleTuile256, styleTuile512, styleTuile1024, styleTuile2048;
    public String styleActuel = "css/amelie.css";
    
    public Style(){}
    
    public String makeCol(ColorPicker colorPic){
        Color c = colorPic.getValue();
        String col = c.toString().substring(2, 8);
        return col;
    }
    
    public void saveCSS(ColorPicker fondPicker, ColorPicker textePicker, ColorPicker tuile2Picker, ColorPicker tuile4Picker, ColorPicker tuile8Picker, ColorPicker tuile16Picker, ColorPicker tuile32Picker, ColorPicker tuile64Picker, ColorPicker tuile128Picker, ColorPicker tuile256Picker, ColorPicker tuile512Picker, ColorPicker tuile1024Picker, ColorPicker tuile2048Picker){
        
        PrintWriter writer = null;
        String cssFileName = "perso.css", distPath=null, srcPath = "src/css/" + cssFileName, buildPath = "build/classes/css/" + cssFileName;
        Color c = null;
        
        String col;
        
        try {
            distPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "\\" + cssFileName;
            File f = new File(srcPath);
            writer = new PrintWriter(f);
            
            // Reecrire le css
            // Le fond
            writer.println("/* Le fond  */");
            
            // CSS
            col = makeCol(fondPicker);
            writer.println(".pane {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Le texte
            writer.println("");
            writer.println("/* Le texte  */");
            
            col = makeCol(textePicker);
            
            // Logo
            // CSS
            writer.println(".logo {");
            writer.println("    -fx-text-fill: #" + col + ";");
            writer.println("}");
            
            // Resultat
            // CSS
            writer.println(".resultat {");
            writer.println("    -fx-text-fill: #" + col + ";");
            writer.println("}");
            
            // Commandes
            // CSS
            writer.println(".commandes {");
            writer.println("    -fx-text-fill: #" + col + ";");
            writer.println("}");
            
            
            
            // Les tuiles
            writer.println("");
            writer.println("/* Les tuiles  */");
            
            // Tuile 2
            // CSS
            col = makeCol(tuile2Picker);
            writer.println(".tuile2 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 4
            // CSS
            col = makeCol(tuile4Picker);
            writer.println(".tuile4 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 8
            // CSS
            col = makeCol(tuile8Picker);
            writer.println(".tuile8 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 16
            // CSS
            col = makeCol(tuile16Picker);
            writer.println(".tuile16 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 32
            // CSS
            col = makeCol(tuile32Picker);
            writer.println(".tuile32 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 64
            // CSS
            col = makeCol(tuile64Picker);
            writer.println(".tuile64 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 128
            // CSS
            col = makeCol(tuile128Picker);
            writer.println(".tuile128 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 256
            // CSS
            col = makeCol(tuile256Picker);
            writer.println(".tuile256 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 512
            // CSS
            col = makeCol(tuile512Picker);
            writer.println(".tuile512 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 1024
            // CSS
            col = makeCol(tuile1024Picker);
            writer.println(".tuile1024 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");
            
            // Tuile 2048
            // CSS
            col = makeCol(tuile2048Picker);
            writer.println(".tuile2048 {");
            writer.println("    -fx-background-color: #" + col + ";");
            writer.println("}");

        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writer != null) {
                writer.close();
                // copie du fichier style modifié dans build vers src pour la prochaine exécution
            }
            if (distPath != null) {
                Path dist = Paths.get(distPath);
                Path src = Paths.get(srcPath);
                Path build = Paths.get(buildPath);
                try {
                    Files.copy(src, dist, StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(src, build, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void applyCSS(AnchorPane fond) throws URISyntaxException{
        fond.getScene().getStylesheets().clear();
        File f = new File(new File(FXMLDocumentController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "\\" + "perso.css");
        String fileURI = f.toURI().toString();
        fond.getScene().getStylesheets().add(fileURI);
        styleActuel = "css/perso.css";
    }
    
}