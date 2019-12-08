/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.Case;
import static model.Parametres.TPSSLEEP;

/**
 * Glissement des cases avec effet.
 */
public class Glissement extends Task<Void> {
    
    AnchorPane anchor;
    GridPane grille;
    Case tuile;
    
    public Glissement(AnchorPane ap, GridPane gr, Case c){
        this.anchor = ap;
        this.grille = gr;
        this.tuile = c;
    }
    
    /**
     * Renvoie la largeur que doit avoir une case.
     * @param gr
     * Paramètre de type GridPane.
     * @return 
     * Le double spécifiant la largeur conseillée.
     */
    public double getPaneWidth(GridPane gr){
        double w = gr.getWidth();
        return w/3;
    }
    /**
     * Renvoie la hauteur que doit avoir une case.
     * @param gr
     * Paramètre de type GridPane.
     * @return 
     * Le double spécifiant la hauteur conseillée.
     */
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
            
            StackPane p = new StackPane();
            p.getStyleClass().add("tuile" + tuile.getVal());
            Label l = new Label(String.valueOf(tuile.getVal()));
            l.getStyleClass().add("valeurTuile");
            p.getChildren().add(l);
            
            double x = w * tuile.getGlisseX0();
            double y = h * tuile.getGlisseY0();
            double x2 = w * tuile.getX();
            double y2 = h * tuile.getY();
            
            
            p.setPrefSize(w, h);
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchor.getChildren().add(p);
                    p.relocate(w * tuile.getGlisseX0(), h * tuile.getGlisseY0());
                    p.setVisible(true);
                }
            });
            
            if (tuile.getGlisseX0() > tuile.getX()){ // Glissement vers la gauche
                while ((int) x > (int) x2){
                    x -= 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            p.relocate(posX, posY);
                            p.setVisible(true);
                        }
                    });
                    Thread.sleep(TPSSLEEP);
                }
            }
            else if (tuile.getGlisseX0() < tuile.getX()){ // Glissement vers la droite
                while ((int) x < (int) x2){
                    x += 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            p.relocate(posX, posY);
                            p.setVisible(true);
                        }
                    });
                    Thread.sleep(TPSSLEEP);
                }
            }
            else if (tuile.getGlisseY0() > tuile.getY()){ // Glissement vers le haut
                while ((int)y > (int)y2){
                    y -= 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            p.relocate(posX, posY);
                            p.setVisible(true);
                        }
                    });
                    Thread.sleep(TPSSLEEP);
                }
            }
            else if (tuile.getGlisseY0() < tuile.getY()){ // Glissement vers le bas
                while ((int)y < (int)y2){
                    y += 1;
                    double posX = x;
                    double posY = y;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            p.relocate(posX, posY);
                            p.setVisible(true);
                        }
                    });
                    Thread.sleep(TPSSLEEP);
                }
            }
            else {}
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchor.getChildren().remove(p);
                    grille.add(p, tuile.getX(), tuile.getY());
                    p.setVisible(true);
                }
            });

        } catch (Exception e) {System.out.println("Thread tué");}
        return null;
    }
}
