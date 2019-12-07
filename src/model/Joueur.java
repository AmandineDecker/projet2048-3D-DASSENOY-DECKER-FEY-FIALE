/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.time.*;
import java.util.Objects;
import static model.Parametres.OBJECTIF;

/**
 * Les données à partager en multijoueur.
 */
public class Joueur implements Serializable {
    private int id;
    private boolean admin = false; // Un joueur est administrateur de la partie
    private final String pseudo; // Pseudo du joueur (visible en cours de partie)
    private int score = 0; // Points du joueur (visible en cours de partie)
    private int tuileMax = 0; // Tuile max sur la grille du joueur (visible en cours de partie)
    private Instant tempsIni;
    private Duration temps = Duration.ZERO; // Temps mis pour jouer
    private boolean fini = false; // Le joueur a fini de jouer (visible en cours de partie)
    
    // Constructeur
    public Joueur(String p){
        pseudo = p;
    }
    
    @Override
    public String toString(){
        if (fini) {
            long heures = temps.toHours();
            long minutes = temps.toMinutes() - 60*heures;
            long secondes = temps.getSeconds() - 3600*heures - 60*minutes;
            String tps = Long.toString(heures) + ":" + Long.toString(minutes) + ":" + Long.toString(secondes);
            if (tuileMax == OBJECTIF) {
                return pseudo + ": PARTIE GAGNEE, " + score + " points, temps de jeu: " + tps;
            } else {
                return pseudo + ": PARTIE PERDUE, " + score + " points, temps de jeu: " + tps;
            }
        }
        return pseudo + ": " + score + " points, tuile maximale: " + tuileMax;// + "\nJoue depuis " + temps.toString();
    }
    
    @Override
    public int hashCode(){
        return id; // Il faudra trouver quelque chose pour que l'ID soit unique
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Joueur other = (Joueur) obj;
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.pseudo, other.pseudo);
    }
    
    /**
     * Réinitialise les données de jeu du joueur.
     */
    public void reinitialiser() {
        score = 0;
        tuileMax = 0;
        tempsIni = null;
        temps = Duration.ZERO;
        fini = false;
    }
    
    // ID
    
    public void setID(int k){
        id = k;
    }
    
    public int getID(){
        return id;
    }
    
    // Admin
    
    public void setAdmin(){
        this.admin = true;
    }
    
    public boolean isAdmin(){
        return admin;
    }
    
    // Pseudo
    
    public String getPseudo(){
        return pseudo;
    }
    
    // Score
    
    public void setScore(int k){
        score = k;
    }
    
    public int getScore(){
        return score;
    }
    
    // TuileMax
    
    public void setTuileMax(int k){
        tuileMax = k;
    }
    
    public int getTuileMax(){
        return tuileMax;
    }
    
    
    // Temps
    
    /**
     * Enregistre l'instant initial dans tempsIni.
     */
    public void startGame(){
        tempsIni = Instant.now();
    }
    
    public void setTempsIni(Instant t) {
        tempsIni = t;
    }
    
    public Instant getTempsIni() {
        return tempsIni;
    }
    
    /**
     * Calcule le temps total de jeu et enregistre cette valeur dans temps.
     */
    public void stopTemps(){
        temps = Duration.between(tempsIni, Instant.now());
    }
    
    public void setTemps(Duration d) {
        temps = d;
    }
    
    public Duration getTemps(){
        return temps;
    }
    
    // Fini
    
    public void setFini(boolean b){
        fini = b;
    }
    
    public boolean getFini(){
        return fini;
    }
    
    /**
     * Vérifie si le joueur a atteint l'objectif.
     * @return 
     * Renvoie True si le joueur a atteint l'objectif.
     */
    public boolean aGagne() {
        return tuileMax == OBJECTIF;
    }
    
}
