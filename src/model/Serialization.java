/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import css.Style;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Gère la sérialization.
 * Grille, style.
 */
public class Serialization {
    
    /**
     * Sauvegarde la Grille pour la prochaine ouverture.
     * @param gr
     * Paramètre de type Grille. La Grille à sauvegarder.
     */
    public static void saveGrille(Grille gr) {
        FileOutputStream fichierGrille = null;
        ObjectOutputStream oosGrille = null;
        try {
            fichierGrille = new FileOutputStream("data/model.ser");
            oosGrille = new ObjectOutputStream(fichierGrille);
            oosGrille.writeObject(gr);
            oosGrille.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (oosGrille != null) {
                    oosGrille.flush();
                    oosGrille.close();
                }
                if (fichierGrille != null) {
                    fichierGrille.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Sauvegarde le Style pour la prochaine ouverture.
     * @param st
     * Paramètre de type Style. Le Style à sauvegarder.
     */
    public static void saveStyle(Style st) {
        FileOutputStream fichierStyle = null;
        ObjectOutputStream oosStyle = null;
        try {
            fichierStyle = new FileOutputStream("data/style.ser");
            oosStyle = new ObjectOutputStream(fichierStyle);
            oosStyle.writeObject(st);
            oosStyle.flush();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (oosStyle != null) {
                    oosStyle.flush();
                    oosStyle.close();
                }
                if (fichierStyle != null) {
                    fichierStyle.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Deserialize un objet.
     * @param nomFic
     * Paramètre de type String. Le fichier à déserializer.
     * @return 
     * Renvoie l'objet contenu dans le fichier au format Object.
     */
    public static Object deserialize(String nomFic) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try{
            final FileInputStream fichier = new FileInputStream(nomFic);
            ois = new ObjectInputStream(fichier);
            return ois.readObject();
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } finally{
            try{
                if(ois != null){
                    ois.close();
                }
            }catch (final IOException exc){
                exc.printStackTrace();
            }
        }
        return null;
    }
    
}
