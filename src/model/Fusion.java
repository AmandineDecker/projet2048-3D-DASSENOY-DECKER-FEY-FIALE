/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Amandine
 */
public class Fusion extends Task<Void> {
    // Attributs
    AnchorPane anchor;
    GridPane grille;
    final Case tuile;
    
    // Constructeur
    public Fusion(AnchorPane ap, GridPane gr, Case c){
        this.anchor = ap;
        this.grille = gr;
        this.tuile = c;
    }
    
    // Ce que fait le thread
    @Override
    public Void call() throws Exception {
        try {
            
            Thread th1;
            Glissement gl1;
            Thread th2;
            Glissement gl2;
            
            // La case qui va potentiellement bouger avant de recevoir l'autre case pour fusionner
            Case cFin = new Case(this.tuile.getX(), this.tuile.getY(), this.tuile.getZ(), this.tuile.getVal()/2);; 
            
            // La case qui va bouger avant de disparaître
            Case cTemp = new Case(this.tuile.getX(), this.tuile.getY(), this.tuile.getZ(), this.tuile.getVal()/2);
            cTemp.setGlisseX0(this.tuile.getFusionneX0());
            cTemp.setGlisseY0(this.tuile.getFusionneY0());
            
            if ((tuile.getGlisseX0() != -1 && tuile.getGlisseY0() != -1) && (tuile.getGlisseX0() != tuile.getX() || tuile.getGlisseY0() != tuile.getY())){ // Les deux tuiles doivent glisser avant de fusionner
                cFin.setGlisseX0(this.tuile.getGlisseX0());
                cFin.setGlisseY0(this.tuile.getGlisseY0());
                
                gl1 = new Glissement(this.anchor, this.grille, cTemp, true);
                gl2 = new Glissement(this.anchor, this.grille, cFin, false);
                th1 = new Thread(gl1);
                th2 = new Thread(gl2);
                th1.setDaemon(true);
                th2.setDaemon(true);
                th1.start();
                th2.start();
                
            }
            else {// Une seule tuile glisse vers l'autre
                // On ajoute une tuile temporaire
                StackPane p = new StackPane();
                p.getStyleClass().add("tuile" + tuile.getVal()/2);
                Label l = new Label(String.valueOf(tuile.getVal()/2));
                l.getStyleClass().add("valeurTuile");
                p.getChildren().add(l);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        grille.add(p, tuile.getX(), tuile.getY());
                        p.setVisible(true);
                    }
                });
                gl1 = new Glissement(this.anchor, this.grille, cTemp, true);
                th1 = new Thread(gl1);
                th1.setDaemon(true);
                th1.start();
                th1.join();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        grille.getChildren().remove(p);
                    }
                });
                
            }

        } catch (Exception e) {System.out.println("Fusion tuée");}
        return null;
    }
}
