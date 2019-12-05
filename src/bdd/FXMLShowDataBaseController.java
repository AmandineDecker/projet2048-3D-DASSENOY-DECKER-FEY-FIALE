/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd;

import css.Style;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

/**
 * FXML Controller class
 *
 * @author Amandine
 */
public class FXMLShowDataBaseController implements Initializable {

    @FXML
    private BorderPane fond;
    @FXML
    private TableView tableScores;
    
    Style perso;
    
    /**
     * Fonction transferStyle
     * Cette fonction permet de récupérer le style en cours d'utilisation.
     * @param s
     * Paramètre de type Style
     */
    public void transferStyle(Style s){
        perso = s;
        if (perso.styleActuel.equals("css/perso.css")){
            perso.applyCSS(fond);
        } else {
            fond.getStylesheets().clear();
            fond.getStylesheets().add(perso.styleActuel);
        }
    }
    
    /**
     * Fonction initialize
     * Cette fonction permet d'initialiser l'interface graphique avec le bon 
     * style et la base de donnée.
     * @param url
     * Paramètre de type URL
     * @param rb 
     * Paramètre de type ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        fond.getStyleClass().add("style-scores-compet-fond");
        
        BDD reachBDD = new BDD();
        reachBDD.buildDataTableScores(tableScores);
    }    
    
}
