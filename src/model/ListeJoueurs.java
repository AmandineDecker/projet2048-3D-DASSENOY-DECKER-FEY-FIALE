/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Amandine
 */
public class ListeJoueurs implements Serializable {
    private HashSet<Joueur> liste;
    private Joueur admin;
    private boolean competFinie;
    
    private static ListeJoueurs instance = new ListeJoueurs();
    
    private ListeJoueurs(){
//        System.out.println("Executing constructor");
        liste = new HashSet();
    }
    
    public void initGame(){
        liste = new HashSet();
    }
    
    public static ListeJoueurs getInstance() {
//        System.out.println("An instance is returned");
        return instance;
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        instance = this;
    }

    
    public Object readResolve() {
//        System.out.println("Executing readResolve");
        return instance; // FIXME
    }
    
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

    @Override
    public String toString(){
        return liste.toString() + ", admin: " + admin;
    }
    
    public void setAdmin(Joueur p){
        admin = p;
    }
    
    public Joueur getAdmin(){
        return admin;
    }
    
    public Boolean hasAdmin(){
        return admin != null;
    }
    
    public void addJoueur(Joueur p){
        liste.add(p);
    }
    
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
    
    public void setCompetFinie(boolean b) {
        competFinie = b;
    }
    
    public void updateCompetFinie() {
        int compteur = 0;
        for (Joueur j : liste){
            if (j.getFini()){
                compteur++;
            }
        }
        competFinie = compteur == liste.size();
    }
    
    public boolean getCompetFinie() {
        return competFinie;
    }
    
}
