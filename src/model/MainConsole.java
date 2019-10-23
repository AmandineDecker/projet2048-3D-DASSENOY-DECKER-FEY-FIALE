/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Scanner;

/**
 *
 * @author Amandine
 */
public class MainConsole implements Parametres {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        
        
        Grille g = new Grille();
        boolean b = g.nouvelleCase();
        b = g.nouvelleCase();
        System.out.println(g);
        
        int e = 0xFE;
        System.out.println(e);
        
        Scanner sc = new Scanner(System.in);
        /*System.out.println("X:");
        int x= sc.nextInt();
        System.out.println("Y:");
        int y= sc.nextInt();
        System.out.println("Valeur:");
        int valeur= sc.nextInt();
        Case c = new Case(x,y,valeur);
        g.getGrille().remove(c);
        System.out.println(g);*/
        
        while (!g.partieFinie()) {
            System.out.println("Déplacer vers la Droite (d), Gauche (q), Haut (z), Bas (s), Supérieur (r) ou Inférieur (f)?");
            String s = sc.nextLine();
            s.toLowerCase();
            if (!(s.equals("d") || s.equals("droite")
                    || s.equals("q") || s.equals("gauche")
                    || s.equals("z") || s.equals("haut")
                    || s.equals("s") || s.equals("bas")
                    || s.equals("r") || s.equals("supérieur")
                    || s.equals("f") || s.equals("inférieur"))) {
                System.out.println("Vous devez écrire d pour Droite, q pour Gauche, z pour Haut, s pour Bas, r pour Supérieur ou f pour Inférieur");
            } else {
                int direction;
                if (s.equals("d") || s.equals("droite")) {
                    direction = DROITE;
                } else if (s.equals("q") || s.equals("gauche")) {
                    direction = GAUCHE;
                } else if (s.equals("z") || s.equals("haut")) {
                    direction = HAUT;
                } else if (s.equals("s") || s.equals("bas")) {
                    direction = BAS;
                } else if (s.equals("r") || s.equals("supérieur")) {
                    direction = SUPERIEUR;
                } else {
                    direction = INFERIEUR;
                }
                boolean b2 = g.initialiserDeplacement(direction);
                if (b2) {
                    b = g.nouvelleCase();
                    if (!b) g.defaite();
                }
                System.out.println(g);
                if (g.getValeurMax()>=OBJECTIF) g.victoire();
            }
        }
        g.defaite();
        
        
    }
    
}
