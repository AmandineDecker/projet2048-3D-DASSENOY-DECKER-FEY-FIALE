/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Amandine
 */
public class Glissement extends Task<Void> {
    
    // Attributs
    AnchorPane anchor;
    GridPane grille;
    final StackPane pane;
    int x0, y0; // Les coordonnées initiales
    int x1, y1; // Les coordonnées finales
    
    // Constructeur
    public Glissement(AnchorPane ap, GridPane gr, StackPane p, int x0, int y0, int x1, int y1){
        this.anchor = ap;
        this.grille = gr;
        this.pane = p;
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }
    
    
                    
    
    public double getPaneWidth(GridPane gr){
        double w = gr.getWidth();
        return w/3;
    }
    public double getPaneHeigth(GridPane gr){
        double h = gr.getHeight();
        return h/3;
    }
    
    // Ce que fait le thread
    @Override
    public Void call() throws Exception {
        try {
            
            double w = getPaneWidth(this.grille);
            double h = getPaneHeigth(this.grille);
            
            double x = w * this.x0;
            double y = h * this.y0;
            double x2 = w * this.x1;
            double y2 = h * this.y1;
            
            this.pane.setPrefSize(w, h);
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchor.getChildren().add(pane);
                    pane.relocate(w * x0, h * y0);
                    pane.setVisible(true);
                }
            });
            
            if (this.x0 > this.x1){ // Glissement vers la gauche
                while ((int) x != (int) x2){
                    x -= 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pane.relocate(posX, posY);
                            pane.setVisible(true);
                        }
                    });
                    Thread.sleep(1);
                }
            }
            else if (this.x0 < this.x1){ // Glissement vers la droite
                while ((int) x != (int) x2){
                    x += 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pane.relocate(posX, posY);
                            pane.setVisible(true);
                        }
                    });
                    Thread.sleep(1);
                }
            }
            else if (this.y0 > this.y1){ // Glissement vers le haut
                while ((int)y != (int)y2){
                    y -= 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pane.relocate(posX, posY);
                            pane.setVisible(true);
                        }
                    });
                    Thread.sleep(1);
                }
            }
            else if (this.y0 < this.y1){ // Glissement vers le bas
                while ((int)y != (int)y2){
                    y += 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            pane.relocate(posX, posY);
                            pane.setVisible(true);
                        }
                    });
                    Thread.sleep(1);
                }
            }
            else {}
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchor.getChildren().remove(pane);
                    grille.add(pane, x1, y1);
                    pane.setVisible(true);
                }
            });

        } catch (Exception e) {System.out.println("Thread tué");}
        return null;
    }
}
