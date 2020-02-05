/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import static model.Parametres.BAS;
import static model.Parametres.DROITE;
import static model.Parametres.GAUCHE;
import static model.Parametres.HAUT;
import static model.Parametres.INFERIEUR;
import static model.Parametres.SUPERIEUR;

/**
 *
 * @author Amandine
 */
public class IntelligenceArtificielle extends AnimationTimer {
    
    Grille modelGrille;
    Caretaker caretaker;
    Originator originator;
    
    public IntelligenceArtificielle(Grille modelGrille, Caretaker caretaker, Originator originator) {
        this.modelGrille = modelGrille;
        this.caretaker = caretaker;
        this.originator = originator;
    }
    
    /**
     * Cette fonction permet de déterminer le coup qui donne le meilleur score.
     * @return int
     * La direction la plus avantageuse.
     * @throws java.lang.CloneNotSupportedException
     * Si le clonage échoue.
     */
    public int unCoupIA() throws CloneNotSupportedException{
        int dir = 0;
        int[] scoretab = new int[3];
        int scoreMax = modelGrille.getScore();
        int index = caretaker.getIndex();
        modelGrille.initialiserDeplacement(INFERIEUR);
        scoretab[0] = modelGrille.getScore();
        Grille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index)));
        caretaker.setIndex(index);
        modelGrille.initialiserDeplacement(GAUCHE);
        scoretab[1] = modelGrille.getScore();
        Grille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index)));
        caretaker.setIndex(index);
        modelGrille.initialiserDeplacement(BAS);
        scoretab[2] = modelGrille.getScore();
        Grille.setInstance(originator.restoreFromMemento(caretaker.getMemento(index)));
        caretaker.setIndex(index);
        for (int k=0; k<3 ;k++){
            if (scoretab[k]>scoreMax){
                scoreMax = scoretab[k];
                dir = k-3;
            }
        }
        // Si c'est 0, on utilise les fréquences calculées 
        if (dir == 0) {
            Random r = new Random();
            int d = r.nextInt(100);
            if (d < 1) {
                dir = SUPERIEUR;
            } else if (d < 11) {
                dir = INFERIEUR;
            } else if (d < 35) {
                dir = BAS;
            } else if (d < 64) {
                dir = HAUT;
            } else if (d < 83) {
                dir = GAUCHE;
            } else {
                dir = DROITE;
            }
        }
        return dir;
    }

    @Override
    public void handle(long now) {
        try {
            System.out.println("start");
            int direction = this.unCoupIA();
            boolean b2 = modelGrille.initialiserDeplacement(direction);
            modelGrille.addMvt();
            this.stop();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        
    }
    
}
