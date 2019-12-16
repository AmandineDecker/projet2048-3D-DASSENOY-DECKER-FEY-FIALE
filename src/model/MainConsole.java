/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import application.MainInterface;
import java.util.Random;
import java.util.Scanner;

/**
 * Main du jeu dans la console. Possibilité de lancer l'interface graphique.
 */
public class MainConsole implements Parametres {
    
    static final String[] PHRASEPREMIEREPARTIE = {
        "Alors c'est parti! Bon jeu à vous ;)",
        "C'est noté! Bonne chance :)"
    };
    
    static final String[] PHRASEMAUVAISEREPONSE = {
        "Vous devez répondre 'Oui' ou 'Non'...",
        "Mauvaise réponse... Un indice, il  faut répondre 'Oui' ou 'Non' (ou 'O' ou 'N' pour les personnes pressées)."
    };
    
    static final String[] PHRASENOUVELLEPARTIE = {
        "Alors c'est reparti! Amusez vous bien ;p",
        "Alors on y retourne! Essayez de battre cotre record !"
    };
    
    /**
     * Renvoie une phrase signifiant que la première partie va commencer. 
     * @return 
     * Format String.
     */
    public static String getPhrasePremierePartie() {
        int i = 0;
        Random ra = new Random();
        i = ra.nextInt(PHRASEPREMIEREPARTIE.length);
        return PHRASEPREMIEREPARTIE[i];
    }
    
    /**
     * Renvoie une phrase signifiant que la réponse n'est pas appropriée.
     * @return 
     * Format String.
     */
    public static String getPhraseMauvaiseReponse() {
        int i;
        Random ra = new Random();
        i = ra.nextInt(PHRASEMAUVAISEREPONSE.length);
        return PHRASEMAUVAISEREPONSE[i];
    }
    
    /**
     * Renvoie une phrase signifiant qu'une nouvelle partie va commencer. 
     * @return 
     * Format String.
     */
    public static String getPhraseNouvellePartie() {
        int i;
        Random ra = new Random();
        i = ra.nextInt(PHRASENOUVELLEPARTIE.length);
        return PHRASENOUVELLEPARTIE[i];
    }
    
    /**
     * Fonction quitter.
     * Cette fonction permet de quitter le jeu tout en sauvegardant la partie. 
     */
    public static void jouerPartie() {
        
        Grille g = Grille.getInstance();
        boolean b = g.nouvelleCase();
        b = g.nouvelleCase();
        System.out.println(g);
        
        Scanner sc = new Scanner(System.in);
        
        while (!g.partieFinie()) {
            System.out.println("Déplacer vers la Droite (d), Gauche (q), Haut (z), Bas (s), Supérieur (r) ou Inférieur (f)?");
            String s = sc.nextLine().toLowerCase();
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        System.out.println("Bonjour! Bienvenue dans 2048-3D. Voulez-vous ouvrir l'interface graphique ?");
        Scanner sc = new Scanner(System.in);
        boolean start = false;
        String s = null;
        while (!start) {
            s = sc.nextLine();
            if (s.equalsIgnoreCase("O") || s.equalsIgnoreCase("OUI")) {
                // Lancer interface graphique et fermer la console
                String[] arg = {"1"};
                MainInterface.main(arg);
                System.exit(1);
            } else if (s.equalsIgnoreCase("N") || s.equalsIgnoreCase("NON")) {
                System.out.println(getPhrasePremierePartie());
                start = true;
            } else {
                System.out.println(getPhraseMauvaiseReponse());
            }
        }
        do {
            jouerPartie();
            System.out.println();
            System.out.println();
            System.out.println("Voulez-vous rejouer ?");
            start = false;
            while (!start) {
                s = sc.nextLine();
                if (s.equalsIgnoreCase("O") || s.equalsIgnoreCase("OUI")) {
                    System.out.println(getPhraseNouvellePartie());
                    start = true;
                } else if (s.equalsIgnoreCase("N") || s.equalsIgnoreCase("NON")) {
                    System.out.println("Au revoir !");
                    System.exit(1);
                } else {
                    System.out.println(getPhraseMauvaiseReponse());
                }
            }
        } while (true);
    }
    
}
