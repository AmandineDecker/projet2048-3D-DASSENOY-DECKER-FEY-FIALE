/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauServeurCoop;

import java.io.*;
import java.net.Socket;
import model.*;
import static model.Parametres.COOPERATION;

/**
 *
 * @author Amandine
 */
public class GestionServeurCoop implements Runnable {
    // Le controller
    private final FXMLServeurCoopController controller;
    // La connexion
    Socket s;
    // L'échange de data
    InputStream in = null;
    ObjectInputStream objIn;
    OutputStream out = null;
    ObjectOutputStream objOut;
    private int indiceConnexion;
    // Le jeu
    Grille aPartager;
    Joueur joueur1;
    Joueur joueur2;
    int main = -1;
    boolean isAdmin;
    
    
    public GestionServeurCoop (Socket s, FXMLServeurCoopController c, int indiceConnexion) throws IOException{ 
        this.s = s;
        this.aPartager = Grille.getInstance();
        this.aPartager.newGame(COOPERATION);
        this.controller = c;
        this.indiceConnexion = indiceConnexion;
        try {
            in = s.getInputStream();
            out = s.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return "Quai " + s.toString() + aPartager.toString();
    }
    
    @Override
    public void run() { 
        try {
            // La sortie
            out = s.getOutputStream();
            //System.out.println("Serveur: rails sortants créés");
            objOut = new ObjectOutputStream(out);
            //System.out.println("Serveur: gare ouverte");
            
            // L'entrée
            in = s.getInputStream();
            //System.out.println("Serveur: rails entrants créés");
            objIn = new ObjectInputStream(in);
            //System.out.println("Serveur: gare ouverte");
            
            share();
            setAdmin(controller.hasAdmin());
            
            while (!s.isClosed()){
                receive();
            }
            System.out.println("Serveur: socket " + s + " fermé");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setAdmin(Boolean b){
        try {
            objOut.writeObject(b);
            //objOut.flush();
            System.out.println("Serveur (admin): colis " + b + " envoyé");
        } catch (IOException ex) {
            try {
                s.close();
                System.out.println("Serveur (admin): fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        }
    }
    
    public void share(){
        try {
            // On partage
            this.aPartager = Grille.getInstance();
//            System.out.println("Partage serveur: hashcode " + this.aPartager.hashCode());
            objOut.writeObject(Grille.getInstance());
            // On update
            objOut.flush();
            System.out.println("Serveur: colis " + aPartager + " envoyé");
            System.out.println();
            System.out.println();
        } catch (IOException ex) {
            try {
                s.close();
                // Arret du jeu
//                controller.listeConnexions.remove(this);
                System.out.println("Client: fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        } 
    }
    
    public void shareJoueur(Joueur j){
        try {
            // On partage
            objOut.writeObject(j);
            // On update
            objOut.flush();
            System.out.println("Serveur: colis " + j + " envoyé");
            System.out.println();
            System.out.println();
        } catch (IOException ex) {
            try {
                s.close();
                // Arret du jeu
//                controller.listeConnexions.remove(this);
                System.out.println("Client: fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        } 
    }
    
    public void shareInfos(String str) {
        try {
            // On partage
            objOut.writeObject(str);
            // On update
            objOut.flush();
            System.out.println("Serveur: colis " + str + " envoyé");
            System.out.println();
            System.out.println();
        } catch (IOException ex) {
            try {
                s.close();
//                controller.listeConnexions.remove(this);
                System.out.println("Client: fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        } 
    }
    
    public void receive() {
        Object colis;
        try {
            colis = objIn.readObject();
            System.out.println("Serveur: colis recu, \n" + colis);
            if (colis instanceof Grille) {
                this.aPartager = Grille.setInstance((Grille) colis);
                System.out.println("Serveur: colis enregistré,\n" + this.aPartager);
                System.out.println();
                System.out.println();
                main = (this.indiceConnexion + 1) % 2;
//                System.out.println("Grille au joueur " + (main + 1));
                controller.listeConnexions[main].share();
            } else if (colis instanceof Joueur) {
                Joueur j = (Joueur) colis;
                joueur1 = controller.getJoueur1();
                joueur2 = controller.getJoueur2();
                if (joueur1 == null) {
//                    System.out.println("SET J1");
                    joueur1 = j;
                    controller.setJoueur1(j);
                } else if (joueur2 == null) {
//                    System.out.println("SET J2");
                    joueur2 = j;
                    controller.setJoueur2(j);
                }
                if (joueur1 != null && joueur2 != null) {
                    controller.listeConnexions[0].shareJoueur(joueur2);
                    controller.listeConnexions[1].shareJoueur(joueur1);
                }
            } else if (colis instanceof String) {
                String str = (String) colis;
                System.out.println(str);
                if (str.equals("Start")){
                    // Lancer partie
                    controller.listeConnexions[0].shareInfos("Start");
                    controller.listeConnexions[1].shareInfos("Start");
                } else if (str.equals("newGame?")){
                    // Demander le lancement d'une nouvelle partie
                    controller.listeConnexions[0].shareInfos("newGame?");
                    controller.listeConnexions[1].shareInfos("newGame?");
                } else if (str.equals("Stop")) {
                    // Informer l'autre joueur que la partie a été stoppée
                    main = (this.indiceConnexion + 1) % 2;
                    controller.listeConnexions[main].shareInfos("Stop");
                } else if (str.equals("PartieFinie")) {
                    main = (this.indiceConnexion + 1) % 2;
                    controller.listeConnexions[main].shareInfos("PartieFinie");
                } else if (str.equals("FinJeu")) {
                    main = (this.indiceConnexion + 1) % 2;
                    controller.listeConnexions[main].shareInfos("FinJeu");
                } else if (str.equals("NouvellePartie")) {
                    main = (this.indiceConnexion + 1) % 2;
                    controller.listeConnexions[main].shareInfos("NouvellePartie");
                }
            } 
//            System.out.println("Joueurs: " + controller.getJoueur1() + " et " + controller.getJoueur2());
        } catch (IOException ex) {
            try {
                s.close();
//                controller.listeConnexions.remove(this);
                System.out.println("Client: fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                in = null;
            }
            System.err.println(ex.getMessage());
        } catch (ClassNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }
}
