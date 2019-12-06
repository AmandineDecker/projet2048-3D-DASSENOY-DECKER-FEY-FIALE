/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauServeurCompet;

import java.io.*;
import java.net.*;
import model.*;

/**
 *
 * @author Amandine
 */
public class GestionServeurCompet implements Runnable {
    // Le controller
    private final FXMLServeurCompetController controller;
    // La connexion
    Socket s;
    // L'échange de data
    InputStream in = null;
    ObjectInputStream objIn;
    OutputStream out = null;
    ObjectOutputStream objOut;
    // Le jeu
    ListeJoueurs aPartager;
    boolean isAdmin;
    
    
    public GestionServeurCompet (Socket s, ListeJoueurs o, FXMLServeurCompetController c) throws IOException{ 
        this.s = s;
        this.aPartager = ListeJoueurs.getInstance();
        this.controller = c;
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
            setAdmin(aPartager.hasAdmin());
            
            while (!s.isClosed()){
                receive();
            }
//            System.out.println("Serveur: socket " + s + " fermé");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Partage true ou false avec le serveur. true s'il n'y a pas encore d'admin.
     */
    public void setAdmin(Boolean b){
        try {
            objOut.writeObject(b);
            //objOut.flush();
//            System.out.println("Serveur (admin): colis " + b + " envoyé");
        } catch (IOException ex) {
            try {
                s.close();
//                System.out.println("Serveur (admin): fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        }
    }
    
    /**
     * Partage l'état des joueurs avec tout le monde.
     */
    public void share(){
        try {
            // On partage
            this.aPartager = ListeJoueurs.getInstance();
//            System.out.println("Partage serveur: hashcode " + this.aPartager.hashCode());
            objOut.writeObject(ListeJoueurs.getInstance());
            // On update
            objOut.flush();
//            System.out.println("Serveur: colis " + aPartager + " envoyé");
//            System.out.println();
//            System.out.println();
        } catch (IOException ex) {
            try {
                s.close();
                controller.listeConnexions.remove(this);
//                System.out.println("Client: fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        } 
    }
    
    /**
     * Partage des informations avec tout le monde. Peut partager "disconnected"
     * ou "newGame?".
     * @param str
     * paramètre de type String.
     */
    public void shareInfos(String str) {
        try {
            // On partage
            objOut.writeObject(str);
            // On update
            objOut.flush();
//            System.out.println("Serveur: colis " + str + " envoyé");
//            System.out.println();
//            System.out.println();
        } catch (IOException ex) {
            try {
                s.close();
                controller.listeConnexions.remove(this);
//                System.out.println("Client: fermeture de " + s);
            } catch (IOException e) {
                e.printStackTrace();
                out = null;
            }
            ex.printStackTrace();
        } 
    }
    
    /**
     * Récupère les données des joueurs.
     */
    public void receive() {
        Object colis;
        try {
            colis = objIn.readObject();
            if (colis instanceof ListeJoueurs) {
                this.aPartager = (ListeJoueurs) colis;
//                System.out.println("Serveur: colis recu, " + this.aPartager);
//                System.out.println();
//                System.out.println();
                if (this.aPartager != null){
                    //System.out.println("Serveur: colis stocké");
                    for (GestionServeurCompet quai : controller.listeConnexions){
//                        System.out.println(quai);
                        quai.share();
                    }
                    //System.out.println("Serveur: colis partagé");
                } else {
                    try {
                        s.close();
//                        System.out.println("Serveur: fermeture de " + s);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        s = null;
                    }
                }
            } else if (colis instanceof String) {
                String str = (String) colis;
//                System.out.println(str);
                if (str.equals("Start")){
                    // Lancer partie
                    for (GestionServeurCompet quai : controller.listeConnexions){
//                        System.out.println(quai);
                        quai.shareInfos("Start");
                    }
                } else if (str.equals("newGame?")){
                    // Demander le lancement d'une nouvelle partie
                    for (GestionServeurCompet quai : controller.listeConnexions){
                        quai.shareInfos("newGame?");
                    }
                }
            } 
        } catch (IOException ex) {
            try {
                s.close();
                controller.listeConnexions.remove(this);
//                System.out.println("Client: fermeture de " + s);
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
