/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Amandine
 */
public class Case implements Parametres {
    
    // Attributs
    private int x, y, z; // Coordonnees (x,y,z) dans la grille (de 0 à 2)
    private int valeur; // valeur de la case (puissance de 2)
    private Grille grille; // La grille à laquelle appartient la case
    
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
    
    public boolean valeurEgale(Case c) {
        if (c != null) {
            return this.valeur == c.valeur;
        } else {
            return false;
        }
    }
    
    public Case getVoisinDirect(int direction) {
        if (direction == HAUT) {
            for (int i = this.y - 1; i >= 0; i--) {
                for (Case c : grille.getGr()) {
                    if (c.getX() == this.x && c.getZ() == this.z && c.getY() == i) {
                        return c;
                    }
                }
            }
        } else if (direction == BAS) {
            for (int i = this.y + 1; i < TAILLE; i++) {
                for (Case c : grille.getGr()) {
                    if (c.getX() == this.x && c.getZ() == this.z && c.getY() == i) {
                        return c;
                    }
                }
            }
        } else if (direction == GAUCHE) {
            for (int i = this.x - 1; i >= 0; i--) {
                for (Case c : grille.getGr()) {
                    if (c.getX() == i && c.getZ() == this.z && c.getY() == this.y) {
                        return c;
                    }
                }
            }
        } else if (direction == DROITE) {
            for (int i = this.x + 1; i < TAILLE; i++) {
                for (Case c : grille.getGr()) {
                    if (c.getX() == i && c.getZ() == this.z && c.getY() == this.y) {
                        return c;
                    }
                }
            }
        } else if (direction == SUPERIEUR) {
            for (int i = this.z - 1; i >= 0; i--) {
                for (Case c : grille.getGr()) {
                    if (c.getZ() == i && c.getX() == this.x && c.getY() == this.y) {
                        return c;
                    }
                }
            }
        } else if (direction == INFERIEUR) {
            for (int i = this.z + 1; i < TAILLE; i++) {
                for (Case c : grille.getGr()) {
                    if (c.getZ() == i && c.getX() == this.x && c.getY() == this.y) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
    
}
