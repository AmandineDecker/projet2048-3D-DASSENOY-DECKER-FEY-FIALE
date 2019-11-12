/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import css.Style;
import java.io.*;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author Valou
 */
public class FXMLColorPickerController implements Initializable {
    
    @FXML
    BorderPane fond;
    @FXML
    Button saveButton;
    @FXML
    ColorPicker fondPicker, infosPicker, textePicker, tuile2Picker, tuile4Picker, tuile8Picker, tuile16Picker, tuile32Picker, tuile64Picker, tuile128Picker, tuile256Picker, tuile512Picker, tuile1024Picker, tuile2048Picker;
    @FXML
    Label labelFond, labelInfos, labelTexte, labelTuile2, labelTuile4, labelTuile8, labelTuile16, labelTuile32, labelTuile64, labelTuile128, labelTuile256, labelTuile512, labelTuile1024, labelTuile2048;
    
    //(ColorPicker fondPicker, ColorPicker textePicker, ColorPicker tuile2Picker, ColorPicker tuile4Picker, ColorPicker tuile8Picker, ColorPicker tuile16Picker, ColorPicker tuile32Picker, ColorPicker tuile64Picker, ColorPicker tuile128Picker, ColorPicker tuile256Picker, ColorPicker tuile512Picker, ColorPicker tuile1024Picker, ColorPicker tuile2048Picker)
    
    Style perso;
    AnchorPane fondPrincipal;
    
    // Recevoir le style de l'autre page
    public void transferStyle(Style s, AnchorPane p){
        perso = s;
        fondPrincipal = p;
        fond.getStylesheets().clear();
        fond.getStylesheets().add("css/basePerso.css");
        fond.getStylesheets().add(perso.styleActuel);
        fond.getStyleClass().add("style-perso-fond");
        saveButton.getStyleClass().add("style-perso-bouton");
        labelFond.getStyleClass().add("style-perso-labels");
        labelInfos.getStyleClass().add("style-perso-labels");
        labelTexte.getStyleClass().add("style-perso-labels");
        labelTuile2.getStyleClass().add("style-perso-labels");
        labelTuile4.getStyleClass().add("style-perso-labels");
        labelTuile8.getStyleClass().add("style-perso-labels");
        labelTuile16.getStyleClass().add("style-perso-labels");
        labelTuile32.getStyleClass().add("style-perso-labels");
        labelTuile64.getStyleClass().add("style-perso-labels");
        labelTuile128.getStyleClass().add("style-perso-labels");
        labelTuile256.getStyleClass().add("style-perso-labels");
        labelTuile512.getStyleClass().add("style-perso-labels");
        labelTuile1024.getStyleClass().add("style-perso-labels");
        labelTuile2048.getStyleClass().add("style-perso-labels");
        fondPicker.getStyleClass().add("style-perso-colorPicker");
        infosPicker.getStyleClass().add("style-perso-colorPicker");
        textePicker.getStyleClass().add("style-perso-colorPicker");
        tuile2Picker.getStyleClass().add("style-perso-colorPicker");
        tuile4Picker.getStyleClass().add("style-perso-colorPicker");
        tuile8Picker.getStyleClass().add("style-perso-colorPicker");
        tuile16Picker.getStyleClass().add("style-perso-colorPicker");
        tuile32Picker.getStyleClass().add("style-perso-colorPicker");
        tuile64Picker.getStyleClass().add("style-perso-colorPicker");
        tuile128Picker.getStyleClass().add("style-perso-colorPicker");
        tuile256Picker.getStyleClass().add("style-perso-colorPicker");
        tuile512Picker.getStyleClass().add("style-perso-colorPicker");
        tuile1024Picker.getStyleClass().add("style-perso-colorPicker");
        tuile2048Picker.getStyleClass().add("style-perso-colorPicker");
        
        try {
            fondPicker.setValue(Color.web(perso.colFond));
            infosPicker.setValue(Color.web(perso.colInfos));
            textePicker.setValue(Color.web(perso.colTexte));
            tuile2Picker.setValue(Color.web(perso.colTuile2));
            tuile4Picker.setValue(Color.web(perso.colTuile4));
            tuile8Picker.setValue(Color.web(perso.colTuile8));
            tuile16Picker.setValue(Color.web(perso.colTuile16));
            tuile32Picker.setValue(Color.web(perso.colTuile32));
            tuile64Picker.setValue(Color.web(perso.colTuile64));
            tuile128Picker.setValue(Color.web(perso.colTuile128));
            tuile256Picker.setValue(Color.web(perso.colTuile256));
            tuile512Picker.setValue(Color.web(perso.colTuile512));
            tuile1024Picker.setValue(Color.web(perso.colTuile1024));
            tuile2048Picker.setValue(Color.web(perso.colTuile2048));
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    @FXML
    public void saveStyle(ActionEvent event) {
        perso.saveCSS(fondPicker, infosPicker, textePicker, tuile2Picker, tuile4Picker, tuile8Picker, tuile16Picker, tuile32Picker, tuile64Picker, tuile128Picker, tuile256Picker, tuile512Picker, tuile1024Picker, tuile2048Picker);
        
        if (perso.styleActuel.equals("css/perso.css")){
            perso.applyCSS(fondPrincipal);
            //perso.applyCSS(fond);
        }
        //fondPicker.getV
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
    }    
    
}
