/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Le constituant principal du jeu.
 */
public class Grille implements Parametres, Serializable, Cloneable {
    
    // Atributs
    private HashSet<Case> grille;
    private int valeurMax = 0;
    private int score = 0;
    private transient boolean deplacement;
    private int modeJeu = 0;
    private int nbMvts = 0;
    
    private static Grille instance = new Grille();
    
    // Constructeur
    private Grille() {
        this.grille = new HashSet<>();
    }
    
    // Setter
    public void setValeurMax(int val){
        this.valeurMax = val;
    }
    
    public void setModeJeu(int modeJeu) {
        this.modeJeu = modeJeu;
    }
    
    public void setNbMvts(int nbMvts) {
        this.nbMvts = nbMvts;
    }
    
    public static Grille setInstance(Grille gr){
        instance.valeurMax = gr.valeurMax;
        instance.score = gr.score;
        instance.modeJeu = gr.getModeJeu();
        instance.grille = gr.getGr();
        return instance;
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
    
    public int getScore(){
        return score;
    }
    
    public int getModeJeu() {
        return this.modeJeu;
    }
    
    public int getNbMvts() {
        return this.nbMvts;
    }
    
    public static Grille getInstance() {
        //System.out.println("An instance is returned");
        return instance;
    }
    
    
    // Methodes
    
    /**
     * Reconstitue un objet sérializé. Appelé automatiquement.
     * @param ois
     * Paramètre de type ObjectInputStream. Celui à lire pour récupérer l'objet.
     * @throws IOException
     * Si la lecture échoue.
     * @throws ClassNotFoundException
     * Si la classe est mauvaise.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        instance = this;
    }
    
    /**
     * Reconstitue un objet sérializé. Appelé automatiquement.
     * @return 
     * Renvoie la Grille reconstituée au format Object.
     */
    public Object readResolve() {
        return instance;
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
    
    @Override
    public Grille clone() throws CloneNotSupportedException{
        Grille gr = null;
        try {
            gr = (Grille) super.clone();
            gr.grille = new HashSet();
            for (Case c : this.grille){
                gr.grille.add(c.clone());
            }
        }
        catch (CloneNotSupportedException e){
            System.out.println("Clone fail");
        }
        return gr;
    }
    
    
    /**
     * Réinitialise la grille.
     * @return 
     * Renvoie la grille réinitialisée.
     */
    public Grille newGame(){
        instance.valeurMax = 0;
        instance.score = 0;
        instance.nbMvts = 0;
        instance.grille.clear();
        return instance;
    }
    
    /**
     * Réinitialise la grille et modifie le mode de jeu.
     * @param modeJeu
     * paramètre de type int
     * @return 
     * Renvoie la grille réinitialisée.
     */
    public Grille newGame(int modeJeu){
        instance.valeurMax = 0;
        instance.score = 0;
        instance.nbMvts = 0;
        instance.grille.clear();
        instance.modeJeu = modeJeu;
        return instance;
    }
    
    
    /**
     * Renvoie la chaîne de caractère correspondant à une ligne d'une sous-grille.
     * @param tab
     * paramètre de type int[].
     * @return 
     * Format [  _  ,  __ , ___ ].
     */
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
    
    
    /**
     * Augmente le nombre de mouvements de 1.
     */
    public void addMvt() {
        nbMvts++;
    }
    
    /**
     * Modifie une case qui fusionne.
     * @param c 
     * Paramètre de type Case
     */
    private void fusion(Case c) {
        c.setVal(c.getVal() * 2);
        if (this.valeurMax < c.getVal()) {
            this.valeurMax = c.getVal();
        }
        deplacement = true;
        this.score += c.getVal();
    }
    
    /**
     * Crée une case 2 ou 4 sur la grille.
     * @return 
     * Renvoie true si la case a été créée.
     */
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
            ajout.setApparue(false);
            this.grille.add(ajout);
            if ((this.grille.size() == 1) || (this.valeurMax == 2 && ajout.getVal() == 4)) { // Mise à jour de la valeur maximale présente dans la grille si c'est la première case ajoutée ou si on ajoute un 4 et que l'ancien max était 2
                this.valeurMax = ajout.getVal();
            }
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Affiche le texte de victoire dans la console.
     */
    public void victoire() {
        System.out.println("Bravo ! Vous avez atteint " + this.valeurMax + ".\nVotre score est " + this.score + ".");
//        System.exit(0);
    }

    /**
     * Affiche le texte de défaite dans la console.
     */
    public void defaite() {
        System.out.println("La partie est finie. Votre score est " + this.score + ".");
//        System.exit(1);
    }
    
    /**
     * Vérifie si une partie est finie.
     * @return 
     * Renvoie true si elle l'est.
     */
    public boolean partieFinie() {
        if (this.valeurMax == OBJECTIF){
            return true;
        }
        else if (this.grille.size() < TAILLE * TAILLE * TAILLE) {
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

    
    /**
     * Renvoie les cases les plus proches d'une extrémité.
     * Si direction = HAUT : retourne les 3 cases qui sont le plus en haut 
     * (une pour chaque colonne) pour chaque sous-grille (9 cases au total, 
     * chaque ligne correspond à 1 sous-grille).
     *  Si direction = HAUT : retourne les 3 cases qui sont le plus en haut 
     * (une pour chaque colonne) pour chaque sous-grille (9 cases au total, 
     * chaque ligne correspond à 1 sous-grille).
     * Si direction = DROITE : retourne les 3 cases qui sont le plus à droite 
     * (une pour chaque ligne) pour chaque sous-grille (9 cases au total, 
     * chaque ligne correspond à 1 sous-grille).
     * Si direction = BAS : retourne les 3 cases qui sont le plus en bas
     * (une pour chaque colonne) pour chaque sous-grille (9 cases au total,
     * chaque ligne correspond à 1 sous-grille).
     * Si direction = GAUCHE : retourne les 3 cases qui sont le plus à gauche
     * (une pour chaque ligne) pour chaque sous-grille (9 cases au total, 
     * chaque ligne correspond à 1 sous-grille).
     * Si direction = SUPERIEUR : retourne les 9 cases qui sont le plus supérieures.
     * Si direction = INFERIEUR : retourne les 9 cases qui sont le plus inférieures.
     * Attention : le tableau retourné peut contenir des null si les 
     * lignes/colonnes/tours sont vides
     * @param direction 
     * Paramètre de type int
     * @return 
     * Renvoie une matrice de taille 3x3 avec les cases en question ou null.
     */
    public Case[][] getCasesExtremites(int direction) {
        Case[][] result = new Case[TAILLE][TAILLE];
        for (int k = 0; k < TAILLE; k++){
//            System.out.println("Ligne " + k);
//            System.out.println(Arrays.toString(result[k]));
//            System.out.println();
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
                    case INFERIEUR: // case INFERIEUR
                        if (c.getY() == k && ((result[k][c.getX()] == null) || (result[k][c.getX()].getZ() < c.getZ()))) {
                            result[k][c.getX()] = c;
//                            System.out.println(c);
//                            System.out.println(Arrays.toString(result[k]));
//                            System.out.println();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
//        if (direction == INFERIEUR) {
//            System.out.println();
//            System.out.println();
//            System.out.println(Arrays.toString(result[0]));
//            System.out.println(Arrays.toString(result[1]));
//            System.out.println(Arrays.toString(result[2]));
//        }
        return result;
    }

    
    
    
    /**
     * Vérifie si un déplacement est possible dans la direction donnée et lance 
     * le déplacement.
     * @param direction 
     * Paramètre de type int
     * @return 
     * Renvoie True s'il l'est.
     */
    public boolean initialiserDeplacement(int direction) {
        Case[][] extremites = this.getCasesExtremites(direction);
        deplacement = false; // pour vérifier si on a bougé au moins une case après le déplacement, avant d'en rajouter une nouvelle
        for (int j = 0; j < TAILLE; j++) {
            for (int i = 0; i < TAILLE; i++) {
                this.deplacerRecursif(extremites, i, j, direction, 0);
//                switch (direction) {
//                    case HAUT:
//                        this.deplacerRecursif(extremites, i, j, direction, 0);
//                        break;
//                    case BAS:
//                        this.deplacerRecursif(extremites, i, j, direction, 0);
//                        break;
//                    case GAUCHE:
//                        this.deplacerRecursif(extremites, i, j, direction, 0);
//                        break;
//                    case DROITE:
//                        this.deplacerRecursif(extremites, i, j, direction, 0);
//                        break;
//                    case SUPERIEUR:
//                        this.deplacerRecursif(extremites, i, j, direction, 0);
//                        break;
//                    default: // case INFERIEUR
//                        this.deplacerRecursif(extremites, i, j, direction, 0);
//                        break;
//                }
            }
        }
        return deplacement;
    }
    
    /**
     * Déplace les cases.
     * @param extremites
     * Paramètre de type Case[][].
     * @param rangee
     * Paramètre de type int.
     * @param sousGrille
     * Paramètre de type int.
     * @param direction 
     * Paramètre de type int.
     * @param compteur
     * Paramètre de type int.
     */
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
                        if (extremites[sousGrille][rangee].getGlisseY0() == -1){
                            extremites[sousGrille][rangee].setGlisseY0(extremites[sousGrille][rangee].getY());
                            extremites[sousGrille][rangee].setGlisseX0(extremites[sousGrille][rangee].getX());
                        }
                        extremites[sousGrille][rangee].setGrimpe(false);
                        extremites[sousGrille][rangee].setY(compteur);
                        break;
                    case BAS:
                        if (extremites[sousGrille][rangee].getGlisseY0() == -1){
                            extremites[sousGrille][rangee].setGlisseY0(extremites[sousGrille][rangee].getY());
                            extremites[sousGrille][rangee].setGlisseX0(extremites[sousGrille][rangee].getX());
                        }
                        extremites[sousGrille][rangee].setGrimpe(false);
                        extremites[sousGrille][rangee].setY(TAILLE - 1 - compteur);
                        break;
                    case GAUCHE:
                        if (extremites[sousGrille][rangee].getGlisseX0() == -1){
                            extremites[sousGrille][rangee].setGlisseX0(extremites[sousGrille][rangee].getX());
                            extremites[sousGrille][rangee].setGlisseY0(extremites[sousGrille][rangee].getY());
                        }
                        extremites[sousGrille][rangee].setGrimpe(false);
                        extremites[sousGrille][rangee].setX(compteur);
                        break;
                    case DROITE:
                        if (extremites[sousGrille][rangee].getGlisseX0() == -1){
                            extremites[sousGrille][rangee].setGlisseX0(extremites[sousGrille][rangee].getX());
                            extremites[sousGrille][rangee].setGlisseY0(extremites[sousGrille][rangee].getY());
                        }
                        extremites[sousGrille][rangee].setGrimpe(false);
                        extremites[sousGrille][rangee].setX(TAILLE - 1 - compteur);
                        break;
                    case SUPERIEUR:
                        if (compteur != extremites[sousGrille][rangee].getZ()){
                            extremites[sousGrille][rangee].setZ(compteur);
                            extremites[sousGrille][rangee].setGrimpe(true);
                        }
                        break;
                    default: // case INFERIEUR
                        if (TAILLE - 1 - compteur != extremites[sousGrille][rangee].getZ()){
                            extremites[sousGrille][rangee].setZ(TAILLE - 1 - compteur);
                            extremites[sousGrille][rangee].setGrimpe(true);
                        }
                        break;
                }
                this.grille.add(extremites[sousGrille][rangee]);
                deplacement = true;
            }
            Case voisin = extremites[sousGrille][rangee].getVoisinDirect(-direction);
            if (voisin != null) {
                if (extremites[sousGrille][rangee].valeurEgale(voisin)) {
                    this.fusion(extremites[sousGrille][rangee]);
                    if (direction != SUPERIEUR && direction != INFERIEUR){
                        extremites[sousGrille][rangee].setFusionneX0(voisin.getX());
                        extremites[sousGrille][rangee].setFusionneY0(voisin.getY());
                    }
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
    
    /**
     * Modifie les cases pour que lors de l'affichage elles apparaissent sans 
     * effet.
     */
    public void fige() {
        this.grille.forEach((c) -> {
            c.fige();
        });
    }
    
}
