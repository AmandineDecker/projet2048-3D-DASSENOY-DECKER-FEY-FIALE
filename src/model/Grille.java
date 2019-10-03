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
    
    public String arraysToString(int[] tab){
        String resu = "[";
        for (int i = 0; i < tab.length - 1; i++){
            int k = tab[i];
            if (k < 10){
                resu += "  " + k + "  ,";
            }
            else if (k < 100){
                resu += "  " + k + " ,";
            }
            else if (k < 1000){
                resu += " " + k + " ,";
            }
            else {
                resu += " " + k + ",";
            }
        }
        int k = tab[tab.length - 1];
        if (k < 10){
            resu += "  " + k + "  ]";
        }
        else if (k < 100){
            resu += "  " + k + " ]";
        }
        else if (k < 1000){
            resu += " " + k + " ]";
        }
        else {
            resu += " " + k + "]";
        }
        return resu;
    }
    
    @Override
    public String toString() {
        int[][][] tableau = new int[TAILLE][TAILLE][TAILLE];
        for (Case c : this.grille) {
            tableau[c.getZ()][c.getY()][c.getX()] = c.getVal();
        }
        String result = "";
        for (int i = 0; i < tableau.length; i++){
            for (int k = 0; k < TAILLE; k++) {
                result += arraysToString(tableau[k][i]) + "   ";
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

    
    
     /*
    * Si direction = HAUT : retourne les 3 cases qui sont le plus en haut (une pour chaque colonne) pour chaque sous-grille (9 cases au total, chaque ligne correspond à 1 sous-grille)
    * Si direction = DROITE : retourne les 3 cases qui sont le plus à droite (une pour chaque ligne) pour chaque sous-grille (9 cases au total, chaque ligne correspond à 1 sous-grille)
    * Si direction = BAS : retourne les 3 cases qui sont le plus en bas (une pour chaque colonne) pour chaque sous-grille (9 cases au total, chaque ligne correspond à 1 sous-grille)
    * Si direction = GAUCHE : retourne les 3 cases qui sont le plus à gauche (une pour chaque ligne) pour chaque sous-grille (9 cases au total, chaque ligne correspond à 1 sous-grille)
    * Si direction = SUPERIEUR : retourne les 9 cases qui sont le plus supérieures 
    * Si direction = INFERIEUR : retourne les 9 cases qui sont le plus inférieures 
    * Attention : le tableau retourné peut contenir des null si les lignes/colonnes/tours sont vides
     */
    public Case[][] getCasesExtremites(int direction) {
        Case[][] result = new Case[TAILLE][TAILLE];
        for (int k = 0; k < TAILLE; k++){
            for (Case c : this.grille) {
                switch (direction) {
                    case HAUT:
                        if (c.getZ() == k && ((result[k][c.getX()] == null) || (result[k][c.getX()].getY() > c.getY()))) { // si on n'avait pas encore de case pour cette rangée ou si on a trouvé un meilleur candidat
                            result[k][c.getX()] = c;
                        }
                        break;
                    case BAS:
                        if (c.getZ() == k && ((result[k][c.getX()] == null) || (result[k][c.getX()].getY() < c.getY()))) {
                            result[k][c.getX()] = c;
                        }
                        break;
                    case GAUCHE:
                        if (c.getZ() == k && ((result[k][c.getY()] == null) || (result[k][c.getY()].getX() > c.getX()))) {
                            result[k][c.getY()] = c;
                        }
                        break;
                    case DROITE:
                        if (c.getZ() == k && ((result[k][c.getY()] == null) || (result[k][c.getY()].getX() < c.getX()))) {
                            result[k][c.getY()] = c;
                        }
                        break;
                    case SUPERIEUR:
                        if (c.getY() == k && ((result[k][c.getX()] == null) || (result[k][c.getX()].getZ() > c.getZ()))) {
                            result[k][c.getX()] = c;
                        }
                        break;
                    default: // case INFERIEUR
                        if (c.getY() == k && ((result[k][c.getX()] == null) || (result[k][c.getX()].getZ() < c.getZ()))) {
                            result[k][c.getX()] = c;
                        }
                        break;
                }
            }
        }
        return result;
    }

    
    
    

    public boolean initialiserDeplacement(int direction) {
        Case[][] extremites = this.getCasesExtremites(direction);
        deplacement = false; // pour vérifier si on a bougé au moins une case après le déplacement, avant d'en rajouter une nouvelle
        for (int j = 0; j < TAILLE; j++) {
            for (int i = 0; i < TAILLE; i++) {
                switch (direction) {
                    case HAUT:
                        this.deplacerRecursif(extremites, i, j, direction, 0);
                        break;
                    case BAS:
                        this.deplacerRecursif(extremites, i, j, direction, 0);
                        break;
                    case GAUCHE:
                        this.deplacerRecursif(extremites, i, j, direction, 0);
                        break;
                    case DROITE:
                        this.deplacerRecursif(extremites, i, j, direction, 0);
                        break;
                    case SUPERIEUR:
                        this.deplacerRecursif(extremites, i, j, direction, 0);
                        break;
                    default: // case INFERIEUR
                        this.deplacerRecursif(extremites, i, j, direction, 0);
                        break;
                }
            }
        }
        return deplacement;
    }
    
    private void deplacerRecursif(Case[][] extremites, int rangee, int sousGrille, int direction, int compteur) {
        if (extremites[sousGrille][rangee] != null) {
            if ((direction == HAUT && extremites[sousGrille][rangee].getY() != compteur)
                    || (direction == BAS && extremites[sousGrille][rangee].getY() != TAILLE - 1 - compteur)
                    || (direction == GAUCHE && extremites[sousGrille][rangee].getX() != compteur)
                    || (direction == DROITE && extremites[sousGrille][rangee].getX() != TAILLE - 1 - compteur)
                    || (direction == SUPERIEUR && extremites[sousGrille][rangee].getZ() != compteur)
                    || (direction == INFERIEUR && extremites[sousGrille][rangee].getZ() != TAILLE - 1 - compteur)) {
                this.grille.remove(extremites[sousGrille][rangee]);
                switch (direction) {
                    case HAUT:
                        extremites[sousGrille][rangee].setY(compteur);
                        break;
                    case BAS:
                        extremites[sousGrille][rangee].setY(TAILLE - 1 - compteur);
                        break;
                    case GAUCHE:
                        extremites[sousGrille][rangee].setX(compteur);
                        break;
                    case DROITE:
                        extremites[sousGrille][rangee].setX(TAILLE - 1 - compteur);
                        break;
                    case SUPERIEUR:
                        extremites[sousGrille][rangee].setZ(compteur);
                        break;
                    default: // case INFERIEUR
                        extremites[sousGrille][rangee].setZ(TAILLE - 1 - compteur);
                        break;
                }
                this.grille.add(extremites[sousGrille][rangee]);
                deplacement = true;
            }
            Case voisin = extremites[sousGrille][rangee].getVoisinDirect(-direction);
            if (voisin != null) {
                if (extremites[sousGrille][rangee].valeurEgale(voisin)) {
                    this.fusion(extremites[sousGrille][rangee]);
                    extremites[sousGrille][rangee] = voisin.getVoisinDirect(-direction);
                    this.grille.remove(voisin);
                    this.deplacerRecursif(extremites, rangee, sousGrille, direction, compteur + 1);
                } else {
                    extremites[sousGrille][rangee] = voisin;
                    this.deplacerRecursif(extremites, rangee, sousGrille, direction, compteur + 1);
                }
            }
        }
    }
    
   
    
}
