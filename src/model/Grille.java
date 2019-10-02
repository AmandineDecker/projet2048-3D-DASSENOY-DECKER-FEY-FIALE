/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * @author Amandine
 */
public class Grille implements Parametres{
    
    // Atributs
    private final HashSet<Case> grille;
    private int valeurMax = 0;
    private boolean deplacement;
    
    // Constructeur
    public Grille() {
        this.grille = new HashSet<>();
        this.deplacement = true;
    }
    
    // Setter
    public void setValeurMax(int val){
        this.valeurMax = val;
    }
    
//    public void setDeplacement(boolean move){
//        this.deplacement = move;
//    }
    
    // Getter
    public HashSet<Case> getGr() {
        return grille;
    }

    public int getValeurMax() {
        return valeurMax;
    }
    
    // Methodes
    
    @Override
    public String toString() {
        int[][][] tableau = new int[TAILLE][TAILLE][TAILLE];
        for (Case c : this.grille) {
            tableau[c.getZ()][c.getY()][c.getX()] = c.getVal();
        }
        String result = "";
        for (int i = 0; i < tableau.length; i++){
            for (int k = 0; k < TAILLE; k++) {
                result += Arrays.toString(tableau[k][i]) + "   ";
            }
            result += "\n";
        }
        return result;
    }
    
//    public String toHTML() {
//        int[][][] tableau = new int[TAILLE][TAILLE][TAILLE];
//        for (Case c : this.grille) {
//            tableau[c.getZ()][c.getY()][c.getX()] = c.getVal();
//        }
//        String result = "<html>";
//        for (int i = 0; i < tableau.length; i++){
//            for (int k = 0; k < TAILLE; k++) {
//                result += Arrays.toString(tableau[k][i]) + "   ";
//            }
//            result += "<br/>";
//        }
//        result += "</html>";
//        return result;
//    }
    
    private void fusion(Case c) {
        c.setVal(c.getVal() * 2);
        if (this.valeurMax < c.getVal()) {
            this.valeurMax = c.getVal();
        }
        deplacement = true;
    }
    
    public boolean nouvelleCase() {
        if (this.grille.size() < TAILLE * TAILLE * TAILLE) {
            ArrayList<Case> casesLibres = new ArrayList<>();
            Random ra = new Random();
            int valeur = (1 + ra.nextInt(2)) * 2;
            // on crée toutes les cases encore libres
            for (int x = 0; x < TAILLE; x++) {
                for (int y = 0; y < TAILLE; y++) {
                    for (int z = 0; z < TAILLE; z++) {
                        Case c = new Case(x, y, z, valeur);
                        if (!this.grille.contains(c)) { // contains utilise la méthode equals dans Case
                            casesLibres.add(c);
                        }
                    }
                }
            }
            // on en choisit une au hasard et on l'ajoute à la grille
            Case ajout = casesLibres.get(ra.nextInt(casesLibres.size()));
            ajout.setGr(this);
            this.grille.add(ajout);
            if ((this.grille.size() == 1) || (this.valeurMax == 2 && ajout.getVal() == 4)) { // Mise à jour de la valeur maximale présente dans la grille si c'est la première case ajoutée ou si on ajoute un 4 et que l'ancien max était 2
                this.valeurMax = ajout.getVal();
            }
            return true;
        } else {
            return false;
        }
    }
    
    
    public void victoire() {
        System.out.println("Bravo ! Vous avez atteint " + this.valeurMax);
        System.exit(0);
    }

    public void defaite() {
        System.out.println("La partie est finie. Votre score est " + this.valeurMax);
        System.exit(1);
    }
    
    
    public boolean partieFinie() {
        if (this.grille.size() < TAILLE * TAILLE * TAILLE) {
            return false;
        } else {
            for (Case c : this.grille) {
                for (int i = 1; i <= 3; i++) {
                    if (c.getVoisinDirect(i) != null) {
                        if (c.valeurEgale(c.getVoisinDirect(i))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    

    
}
