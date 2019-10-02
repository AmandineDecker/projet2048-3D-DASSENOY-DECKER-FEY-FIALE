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
            System.out.println("Déplacer vers la Droite (d), Gauche (g), Haut (h), Bas (b), Supérieur (q) ou Inférieur (e)?");
            String s = sc.nextLine();
            s.toLowerCase();
            if (!(s.equals("d") || s.equals("droite")
                    || s.equals("g") || s.equals("gauche")
                    || s.equals("h") || s.equals("haut")
                    || s.equals("b") || s.equals("bas")
                    || s.equals("q") || s.equals("supérieur")
                    || s.equals("e") || s.equals("inférieur"))) {
                System.out.println("Vous devez écrire d pour Droite, g pour Gauche, h pour Haut, b pour Bas, q pour Supérieur ou e pour Inférieur");
            } else {
                int direction;
                if (s.equals("d") || s.equals("droite")) {
                    direction = DROITE;
                } else if (s.equals("g") || s.equals("gauche")) {
                    direction = GAUCHE;
                } else if (s.equals("h") || s.equals("haut")) {
                    direction = HAUT;
                } else if (s.equals("b") || s.equals("bas")) {
                    direction = BAS;
                } else if (s.equals("q") || s.equals("supérieur")) {
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
