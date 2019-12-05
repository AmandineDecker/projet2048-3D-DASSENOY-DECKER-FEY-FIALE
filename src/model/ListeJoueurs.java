/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.time.Duration;

/**
 *
 * @author Amandine
 */
public class ListeJoueurs implements Serializable {
    private HashSet<Joueur> liste;
    private Joueur admin;
    private boolean competFinie;
    
    private static ListeJoueurs instance = new ListeJoueurs();
    
    // Constructeur
    private ListeJoueurs(){
        liste = new HashSet();
    }
    
    // Setter
    
    public void setAdmin(Joueur p){
        admin = p;
    }
    
    public void setCompetFinie(boolean b) {
        competFinie = b;
    }
    
    
    // Getter
    
    public static ListeJoueurs getInstance() {
        return instance;
    }
    
    public Joueur getAdmin(){
        return admin;
    }
    
    public Joueur getJoueur(int id){
        for (Joueur p : liste){
            if (p.getID() == id){
                return p;
            }
        }
        return null;
    }
    
    public HashSet<Joueur> getListe() {
        return liste;
    }
    
    public boolean getCompetFinie() {
        return competFinie;
    }
    
    
    
    // Méthodes
    
    @Override
    public String toString(){
        return liste.toString() + ", admin: " + admin;
    }
    
    /**
     * Met un objet Duration sous forme de String. 
     * @param tps
     * paramètre de type Duration.
     * @return 
     */
    public String durationToString(Duration tps) {
        long heures = tps.toHours();
        long minutes = tps.toMinutes() - 60*heures;
        long secondes = tps.getSeconds() - 3600*heures - 60*minutes;
        String str = Long.toString(heures) + ":" + Long.toString(minutes) + ":" + Long.toString(secondes);
        return str;
    }
    
    /**
     * Reconstitue un objet sérializé. Appelé automatiquement.
     * @return 
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        instance = this;
    }
    
    /**
     * Reconstitue un objet sérializé. Appelé automatiquement.
     * @return 
     */
    public Object readResolve() {
        return instance; // FIXME
    }
    
    /**
     * Initialise une partie. La liste de joueur est vide et la partie n'est pas finie.
     */
    public void initGame(){
        liste = new HashSet();
        competFinie = false;
    }
    
    /**
     * Vérifie s'il y a un admin parmi les joueurs.
     * @return 
     */
    public Boolean hasAdmin(){
        return admin != null;
    }
    
    /**
     * Met à jour l'attribut competFinie.
     */
    public void updateCompetFinie() {
        int compteur = 0;
        for (Joueur j : liste){
            if (j.getFini()){
                compteur++;
            }
        }
        competFinie = (compteur == liste.size());
    }
    
    /**
     * Détermine si les joueurs sont tous prêts à relancer une partie.
     * @return 
     */
    public boolean pretsAJouer() {
        int compteur = 0;
        for (Joueur j : liste){
            if (j.getFini()){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Ajoute un joueur à la liste.
     * @param p
     * paramètre de type Joueur.
     */
    public void addJoueur(Joueur p){
        liste.add(p);
    }
    
    /**
     * Retire un joueur de la liste.
     * @param p
     * paramètre de type Joueur.
     */
    public void removeJoueur(Joueur p){
        this.liste.remove(p);
        if (p.isAdmin()){
            if (this.liste.size() > 0){
                Iterator it = this.liste.iterator();
                Joueur play = (Joueur) it.next();
                play.setAdmin();
                this.admin = play;
            } else {
                this.admin = null;
            }
        }
    }
    
    /**
     * Met à jour un joueur de la liste.
     * @param p
     * paramètre de type Joueur.
     */
    public void updateJoueur(Joueur p){
        for (Joueur player : liste){
            if (player.equals(p)){
                player.setScore(p.getScore());
                player.setTuileMax(p.getTuileMax());
                player.setFini(p.getFini());
                player.setTemps(p.getTemps());
                player.setTempsIni(p.getTempsIni());
                break;
            }
        }
    }
    
    /**
     * Renvoie le texte à afficher en fin de partie.
     * @return 
     */
    public String afficherScore() {
        HashSet<Joueur> meilleurTemps = new HashSet();
        Duration temps = null;
        HashSet<Joueur> meilleurScore = new HashSet();
        int score = 0;
        boolean gagnant = false;
        
        for (Joueur j : liste) {
            if (temps == null) {
                temps = j.getTemps();
            }
            if (gagnant) {
                if (j.aGagne()) {
                    // Les listes ne sont pas vides puisqu'il y a un gagnant
                    // Le score
                    if (score < j.getScore()){
                        // j a un meilleur score que les gagnants actuels
                        meilleurScore.clear();
                        meilleurScore.add(j);
                        score = j.getScore();
                    } else if (score == j.getScore()) {
                        meilleurScore.add(j);
                    }
                    // Le temps
                    if (temps.compareTo(j.getTemps()) < 0){
                        // j a un meilleur temps que les gagnants actuels
                        meilleurTemps.clear();
                        meilleurTemps.add(j);
                        temps = j.getTemps();
                    } else if (temps.compareTo(j.getTemps()) == 0){
                        meilleurTemps.add(j);
                    }
                }
                // Si j n'a pas gagné, on ne cherche pas à le classer
            } else {
                if (j.aGagne()) {
                    gagnant = true;
                    meilleurScore.clear();
                    meilleurScore.add(j);
                    score = j.getScore();
                    meilleurTemps.clear();
                    meilleurTemps.add(j);
                } else {
                    if (score < j.getScore()){
                        // j a un meilleur score que les gagnants actuels
                        meilleurScore.clear();
                        meilleurScore.add(j);
                        score = j.getScore();
                    } else if (score == j.getScore()) {
                        meilleurScore.add(j);
                    }
                }
            }
        }
        
        if (gagnant) {
            String str = "";
            // Le meilleur temps
            int k = meilleurTemps.size();
            Iterator it = meilleurTemps.iterator();
            if (k > 1){
                for (int i = 0; i < k - 2; i++){
                    Joueur j = (Joueur) it.next();
                    str = str + j.getPseudo() + ", ";
                }
                Joueur j = (Joueur) it.next();
                str = str + j.getPseudo() + " et ";
                j = (Joueur) it.next();
                str = str + j.getPseudo() + " ont été les plus rapides: " + durationToString(j.getTemps()) + ".\n";
            } else {
                Joueur j = (Joueur) it.next();
                str = j.getPseudo() + " a été le plus rapide: " + durationToString(j.getTemps()) + ".\n";
            }
            // Le meilleur score
            k = meilleurScore.size();
            it = meilleurScore.iterator();
            if (k > 1){
                for (int i = 0; i < k - 2; i++){
                    Joueur j = (Joueur) it.next();
                    str = str + j.getPseudo() + ", ";
                }
                Joueur j = (Joueur) it.next();
                str = str + j.getPseudo() + " et ";
                j = (Joueur) it.next();
                str = str + j.getPseudo() + " ont fait le meilleur score: " + Integer.toString(j.getScore()) + ".\n\nBravo à tous !";
            } else {
                Joueur j = (Joueur) it.next();
                str = j.getPseudo() + " a fait le meilleur score: " + Integer.toString(j.getScore()) + ".\n\nBravo à tous !";
            }
            
            return str;
        } else {
            // Le meilleur score
            String str = "";
            int k = meilleurScore.size();
            Iterator it = meilleurScore.iterator();
            if (k > 1){
                for (int i = 0; i < k - 2; i++){
                    Joueur j = (Joueur) it.next();
                    str = str + j.getPseudo() + ", ";
                }
                Joueur j = (Joueur) it.next();
                str = str + j.getPseudo() + " et ";
                j = (Joueur) it.next();
                str = str + j.getPseudo() + " ont fait le meilleur score: " + Integer.toString(j.getScore()) + ".\n\nBravo à tous !";
            } else {
                Joueur j = (Joueur) it.next();
                str = j.getPseudo() + " a fait le meilleur score: " + Integer.toString(j.getScore()) + ".\n\nBravo à tous !";
            }
            return str;
        }
    }
}
