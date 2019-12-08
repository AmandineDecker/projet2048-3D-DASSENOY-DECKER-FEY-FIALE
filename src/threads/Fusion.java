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
 * Fusion des cases avec effet.
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
            double x2 = w * tuile.getX();
            double y2 = h * tuile.getY();
            
            if ((tuile.getGlisseX0() != -1 && tuile.getGlisseY0() != -1) && (tuile.getGlisseX0() != tuile.getX() || tuile.getGlisseY0() != tuile.getY())){ // Les deux tuiles doivent glisser avant de fusionner
                
                // On ajoute les deux Pane
                Label lProche = new Label(String.valueOf(tuile.getVal()/2));
                lProche.getStyleClass().add("valeurTuile");
                Label lLoin = new Label(String.valueOf(tuile.getVal()/2));
                lLoin.getStyleClass().add("valeurTuile");
                // Le plus proche de la case d'arrivée
                StackPane pProche = new StackPane();
                pProche.getStyleClass().add("tuile" + tuile.getVal()/2);
                pProche.getChildren().add(lProche);
                pProche.setPrefSize(w, h);
                // Le plus éloigné de la case d'arrivée
                StackPane pLoin = new StackPane();
                pLoin.getStyleClass().add("tuile" + tuile.getVal()/2);
                pLoin.getChildren().add(lLoin);
                pLoin.setPrefSize(w, h);
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        anchor.getChildren().add(pProche);
                        pProche.relocate(w * tuile.getGlisseX0(), h * tuile.getGlisseY0());
                        pProche.setVisible(true);
                        anchor.getChildren().add(pLoin);
                        pLoin.relocate(w * tuile.getFusionneX0(), h * tuile.getFusionneY0());
                        pLoin.setVisible(true);
                    }
                });
                
                // On les fait glisser tous les deux un certain temps
                double xLoin = w * tuile.getFusionneX0();
                double yLoin = h * tuile.getFusionneY0();
                double xProche = w * tuile.getGlisseX0();
                double yProche = h * tuile.getGlisseY0();
                
                if (tuile.getFusionneX0() > tuile.getX()){ // Glissement vers la gauche
                    while ((int) xProche > (int) x2){
                        xProche -= 1;
                        xLoin -= 1;
                        double posXProche = xProche;
                        double posYProche = yProche;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pProche.relocate(posXProche, posYProche);
                                pProche.setVisible(true);
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                    while ((int) xLoin > (int) x2){
                        xLoin -= 1;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else if (tuile.getFusionneX0() < tuile.getX()){ // Glissement vers la droite
                    while ((int) xProche < (int) x2){
                        xProche += 1;
                        xLoin += 1;
                        double posXProche = xProche;
                        double posYProche = yProche;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pProche.relocate(posXProche, posYProche);
                                pProche.setVisible(true);
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                    while ((int) xLoin < (int) x2){
                        xLoin += 1;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else if (tuile.getFusionneY0() > tuile.getY()){ // Glissement vers le haut
                    while ((int)yProche > (int)y2){
                        yProche -= 1;
                        yLoin -= 1;
                        double posXProche = xProche;
                        double posYProche = yProche;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pProche.relocate(posXProche, posYProche);
                                pProche.setVisible(true);
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                    while ((int) yLoin > (int) y2){
                        yLoin -= 1;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else if (tuile.getFusionneY0() < tuile.getY()){ // Glissement vers le bas
                    while ((int)yProche < (int)y2){
                        yProche += 1;
                        yLoin += 1;
                        double posXProche = xProche;
                        double posYProche = yProche;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pProche.relocate(posXProche, posYProche);
                                pProche.setVisible(true);
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                    while ((int) yLoin < (int) y2){
                        yLoin += 1;
                        double posXLoin = xLoin;
                        double posYLoin = yLoin;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pLoin.relocate(posXLoin, posYLoin);
                                pLoin.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else {}
                
                // On affiche la tuile finale
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lLoin.setText(String.valueOf(tuile.getVal()));
                        pLoin.getStyleClass().add("tuile" + tuile.getVal());
                        anchor.getChildren().remove(pLoin);
                        anchor.getChildren().remove(pProche);
                        grille.add(pLoin, tuile.getX(), tuile.getY());
                        pLoin.setVisible(true);
                    }
                });
                
            }
            else {// Une seule tuile glisse vers l'autre
                
                // On ajoute une tuile temporaire
                StackPane pTemp = new StackPane();
                pTemp.getStyleClass().add("tuile" + tuile.getVal()/2);
                Label l1 = new Label(String.valueOf(tuile.getVal()/2));
                l1.getStyleClass().add("valeurTuile");
                pTemp.getChildren().add(l1);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        grille.add(pTemp, tuile.getX(), tuile.getY());
                        pTemp.setVisible(true);
                    }
                });
                
                // On fait glisser l'autre
                StackPane pGlisse = new StackPane();
                pGlisse.getStyleClass().add("tuile" + tuile.getVal()/2);
                Label l2 = new Label(String.valueOf(tuile.getVal()/2));
                l2.getStyleClass().add("valeurTuile");
                pGlisse.getChildren().add(l2);
                pGlisse.setPrefSize(w, h);
                
                double x = w * tuile.getFusionneX0();
                double y = h * tuile.getFusionneY0();
            
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        anchor.getChildren().add(pGlisse);
                        pGlisse.relocate(w * tuile.getFusionneX0(), h * tuile.getFusionneY0());
                        pGlisse.setVisible(true);
                    }
                });
            
                if (tuile.getFusionneX0() > tuile.getX()){ // Glissement vers la gauche
                    while ((int) x > (int) x2){
                        x -= 1;
                        double posX = x;
                        double posY = y;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pGlisse.relocate(posX, posY);
                                pGlisse.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else if (tuile.getFusionneX0() < tuile.getX()){ // Glissement vers la droite
                    while ((int) x < (int) x2){
                        x += 1;
                        double posX = x;
                        double posY = y;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pGlisse.relocate(posX, posY);
                                pGlisse.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else if (tuile.getFusionneY0() > tuile.getY()){ // Glissement vers le haut
                    while ((int)y > (int)y2){
                        y -= 1;
                        double posX = x;
                        double posY = y;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pGlisse.relocate(posX, posY);
                                pGlisse.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else if (tuile.getFusionneY0() < tuile.getY()){ // Glissement vers le bas
                    while ((int)y < (int)y2){
                        y += 1;
                        double posX = x;
                        double posY = y;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                pGlisse.relocate(posX, posY);
                                pGlisse.setVisible(true);
                            }
                        });
                        Thread.sleep(TPSSLEEP);
                    }
                }
                else {}
                
                
                // On affiche la tuile finale
                
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        l1.setText(String.valueOf(tuile.getVal()));
                        pTemp.getStyleClass().add("tuile" + tuile.getVal());
                        anchor.getChildren().remove(pGlisse);
                    }
                });
                
            }

        } catch (Exception e) {System.out.println("Fusion tuée");}
        return null;
    }
}
