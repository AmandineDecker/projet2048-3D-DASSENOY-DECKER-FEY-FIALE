/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package css;


import application.FXMLDocumentController;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ColorPicker;
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
    
//    public void saveColor(ColorPicker fondPicker, ColorPicker textePicker, ColorPicker tuile2Picker, ColorPicker tuile4Picker, ColorPicker tuile8Picker, ColorPicker tuile16Picker, ColorPicker tuile32Picker, ColorPicker tuile64Picker, ColorPicker tuile128Picker, ColorPicker tuile256Picker, ColorPicker tuile512Picker, ColorPicker tuile1024Picker, ColorPicker tuile2048Picker){
//        
//        File f = new File(chemin);
//        PrintWriter writer = null;
//        Color c = null;
//        
//        String col;
//        
//        try {
//            writer = new PrintWriter(f);
//            
//            // Le fond
//            writer.println("/* Le fond  */");
//            
//            // CSS
//            col = makeCol(fondPicker);
//            writer.println(".pane {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleFond = "    -fx-background-color: #" + col;
//            
//            // Le texte
//            writer.println("");
//            writer.println("/* Le texte  */");
//            
//            col = makeCol(textePicker);
//            
//            // Logo
//            // CSS
//            writer.println(".logo {");
//            writer.println("    -fx-text-fill: #" + col + ";");
//            writer.println("}");
//            
//            // Resultat
//            // CSS
//            writer.println(".resultat {");
//            writer.println("    -fx-text-fill: #" + col + ";");
//            writer.println("}");
//            
//            // Commandes
//            // CSS
//            writer.println(".commandes {");
//            writer.println("    -fx-text-fill: #" + col + ";");
//            writer.println("}");
//            
//            // Modification instantanée
//            styleTexte = "    -fx-text-fill: #" + col;
//            
//            
//            // Les tuiles
//            writer.println("");
//            writer.println("/* Les tuiles  */");
//            
//            // Tuile 2
//            // CSS
//            col = makeCol(tuile2Picker);
//            writer.println(".tuile2 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile2 = "    -fx-background-color: #" + col;
//            
//            // Tuile 4
//            // CSS
//            col = makeCol(tuile4Picker);
//            writer.println(".tuile4 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile4 = "    -fx-background-color: #" + col;
//            
//            // Tuile 8
//            // CSS
//            col = makeCol(tuile8Picker);
//            writer.println(".tuile8 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile8 = "    -fx-background-color: #" + col;
//            
//            // Tuile 16
//            // CSS
//            col = makeCol(tuile16Picker);
//            writer.println(".tuile16 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile16 = "    -fx-background-color: #" + col;
//            
//            // Tuile 32
//            // CSS
//            col = makeCol(tuile32Picker);
//            writer.println(".tuile32 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile32 = "    -fx-background-color: #" + col;
//            
//            // Tuile 64
//            // CSS
//            col = makeCol(tuile64Picker);
//            writer.println(".tuile64 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile64 = "    -fx-background-color: #" + col;
//            
//            // Tuile 128
//            // CSS
//            col = makeCol(tuile128Picker);
//            writer.println(".tuile128 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile128 = "    -fx-background-color: #" + col;
//            
//            // Tuile 256
//            // CSS
//            col = makeCol(tuile256Picker);
//            writer.println(".tuile256 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile256 = "    -fx-background-color: #" + col;
//            
//            // Tuile 512
//            // CSS
//            col = makeCol(tuile512Picker);
//            writer.println(".tuile512 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile512 = "    -fx-background-color: #" + col;
//            
//            // Tuile 1024
//            // CSS
//            col = makeCol(tuile1024Picker);
//            writer.println(".tuile1024 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile1024 = "    -fx-background-color: #" + col;
//            
//            // Tuile 2048
//            // CSS
//            col = makeCol(tuile2048Picker);
//            writer.println(".tuile2048 {");
//            writer.println("    -fx-background-color: #" + col + ";");
//            writer.println("}");
//            // Modification instantanée
//            styleTuile2048 = "    -fx-background-color: #" + col;
//            
//            writer.flush();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            if (writer != null){
//                writer.close();
//            }
//        }
//    }
}