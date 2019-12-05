/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Amandine
 */
public class Case implements Parametres, Serializable, Cloneable {
    
    // Attributs
    private int x, y, z; // Coordonnees (x,y,z) dans la grille (de 0 à 2)
    private int valeur; // valeur de la case (puissance de 2)
    private Grille grille; // La grille à laquelle appartient la case
    
    private boolean grimpe = false; // Pour savoir si la case doit bouger
    private boolean apparue = true;
    
    private int glisseX0 = -1; // Pour savoir si la case doit glisser ou juste apparaitre
    private int glisseY0 = -1; // Pour savoir si la case doit glisser ou juste apparaitre
    
    private int fusionneX0 = -1; // Pour savoir si une fusion a lieu et depuis ou
    private int fusionneY0 = -1; // Pour savoir si une fusion a lieu et depuis ou
    
    
    // Constructeur
    public Case(int x, int y, int z, int val){
        this.x = x;
        this.y = y;
        this.z = z;
        this.valeur = val;
    }
    
    // Setter
    
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setZ(int z){
        this.z = z;
    }
    
    public void setVal(int val){
        this.valeur = val;
    }
    
    public void setGr(Grille gr){
        this.grille = gr;
    }
    
    public void setGlisseX0(int k){
        this.glisseX0 = k;
    }
    
    public void setGlisseY0(int k){
        this.glisseY0 = k;
    }
    
    public void setGrimpe(boolean b){
        this.grimpe = b;
    }
    
    public void setApparue(boolean b){
        this.apparue = b;
    }
    
    public void setFusionneX0(int k){
        this.fusionneX0 = k;
    }
    
    public void setFusionneY0(int k){
        this.fusionneY0 = k;
    }
    
    // Getter
    
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getZ(){
        return this.z;
    }
    
    public int getVal(){
        return this.valeur;
    }
    
    public Grille getGr(){
        return this.grille;
    }
    
    public int getGlisseX0(){
        return this.glisseX0;
    }
    
    public int getGlisseY0(){
        return this.glisseY0;
    }
    
    public boolean getGrimpe(){
        return this.grimpe;
    }
    
    public boolean getApparue(){
        return this.apparue;
    }
    
    public int getFusionneX0(){
        return this.fusionneX0;
    }
    
    public int getFusionneY0(){
        return this.fusionneY0;
    }
    
    
    
    
    // Methodes
    
    @Override
    public String toString(){
        return "(" + this.getX() + ", " + this.getY() + ", " + this.getZ() + "; " + this.getVal() + ")";
    }
    
    @Override
    public int hashCode(){
        return this.x * 7 + this.y * 13 + this.z * 17;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Case){
            Case c = (Case) o;
            return (this.x == c.x && this.y == c.y && this.z == c.z);
        } else {
            return false;
        }
    }
    
    @Override
    public Case clone(){
        Case c = null;
        try {
            c = (Case) super.clone();
        }
        catch (CloneNotSupportedException e){
            System.out.println("Clone fail");
        }
        return c;
    }
    
    /**
     * Renvoie true si les cases ont la même valeur.
     * @param c
     * paramètre de type Case.
     * @return 
     */
    public boolean valeurEgale(Case c) {
        if (c != null) {
            return this.valeur == c.valeur;
        } else {
            return false;
        }
    }
    
    /**
     * Renvoie la voisine d'une case dans une direction donnée.
     * @param direction 
     * Paramètre de type int
     * @return 
     */
    public Case getVoisinDirect(int direction) {
        switch (direction) {
            case HAUT:
                for (int i = this.y - 1; i >= 0; i--) {
                    for (Case c : grille.getGr()) {
                        if (c.getX() == this.x && c.getZ() == this.z && c.getY() == i) {
                            return c;
                        }
                    }
                }   break;
            case BAS:
                for (int i = this.y + 1; i < TAILLE; i++) {
                    for (Case c : grille.getGr()) {
                        if (c.getX() == this.x && c.getZ() == this.z && c.getY() == i) {
                            return c;
                        }
                    }
                }   break;
            case GAUCHE:
                for (int i = this.x - 1; i >= 0; i--) {
                    for (Case c : grille.getGr()) {
                        if (c.getX() == i && c.getZ() == this.z && c.getY() == this.y) {
                            return c;
                        }
                    }
                }   break;
            case DROITE:
                for (int i = this.x + 1; i < TAILLE; i++) {
                    for (Case c : grille.getGr()) {
                        if (c.getX() == i && c.getZ() == this.z && c.getY() == this.y) {
                            return c;
                        }
                    }
                }   break;
            case SUPERIEUR:
                for (int i = this.z - 1; i >= 0; i--) {
                    for (Case c : grille.getGr()) {
                        if (c.getZ() == i && c.getX() == this.x && c.getY() == this.y) {
                            return c;
                        }
                    }
                }   break;
            case INFERIEUR:
                for (int i = this.z + 1; i < TAILLE; i++) {
                    for (Case c : grille.getGr()) {
                        if (c.getZ() == i && c.getX() == this.x && c.getY() == this.y) {
                            return c;
                        }
                    }
                }   break;
            default:
                break;
        }
        return null;
    }
    
}
