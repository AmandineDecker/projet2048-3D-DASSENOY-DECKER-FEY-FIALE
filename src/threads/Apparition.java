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
import static model.Parametres.X;

/**
 *
 * @author Amandine
 */
public class Apparition extends Task<Void> {
    AnchorPane anchor;
    GridPane grille;
    Case tuile;
    
    public Apparition(AnchorPane ap, GridPane gr, Case c){
        this.anchor = ap;
        this.grille = gr;
        this.tuile = c;
    }
    
    /**
     * Renvoie la largeur que doit avoir une case.
     * @param gr
     * paramètre de typer GridPane.
     * @return 
     */
    public double getPaneWidth(GridPane gr){
        double w = gr.getWidth();
        return w/3;
    }
    /**
     * Renvoie la hauteur que doit avoir une case.
     * @param gr
     * paramètre de typer GridPane.
     * @return 
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
            
            double x = w * tuile.getX();
            double y = h * tuile.getY();
            
            StackPane p = new StackPane();
            p.getStyleClass().add("tuile" + tuile.getVal());
            Label l = new Label(String.valueOf(tuile.getVal()));
            l.getStyleClass().add("valeurTuile");
            p.getChildren().add(l);
            
            p.setPrefSize(0, 0);
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchor.getChildren().add(p);
                    p.relocate(x, y);
                    p.setVisible(false);
                }
            });
            
            double w2 = 0;
            double h2 = 0;
            
            int augmente = 1;
            while (((int) w2 < (int) w-X) || ((int) h2 < (int) h-X)){
                if (augmente == 0){
                    w2 += 1;
                    h2 += 1;
                }
                augmente = (augmente + 1)%3;
                double a = w2;
                double b = h2;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        anchor.getChildren().remove(p);
                        p.setPrefSize(a, b);
                        anchor.getChildren().add(p);
                        if ((a > w*0.8) || (b > h*0.8)){
                            p.setVisible(true);
                        }
                    }
                });
                Thread.sleep(TPSSLEEP);
            }
            
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    anchor.getChildren().remove(p);
                    grille.add(p, tuile.getX(), tuile.getY());
                    p.setVisible(true);
                }
            });
            
        }
        catch (Exception e){System.out.println("Apparition tuée");}
        return null;
    }
}
