/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bdd;

import java.sql.*;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * Gestion des échanges avec la BDD.
 */
public class BDD {
    
    private static final String URL = "jdbc:mysql://mysql-2048-dassenoyfialedeckerfey.alwaysdata.net:3306/2048-dassenoyfialedeckerfey_scores";
    private static final String UTILISATEUR = "195812";
    private static final String MDP = "juxje5-maQkum-pacrim";
    
    /**
     * Sauvegarde les résultats de la partie (pseudo, tuileMax, score) dans la 
     * BDD.
     * @param pseudo
     * Paramètre de type String
     * @param tuileMax 
     * Paramètre de type int
     * @param score 
     * Paramètre de type int
     * @param nbMvts 
     * Paramètre de type int
     */
    public void save(String pseudo, int tuileMax, int score, int nbMvts) {
        try {
            try (Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP)) {
                String requete = "INSERT INTO scores VALUES ('" + pseudo + "', " + Integer.toString(tuileMax) + ", " + Integer.toString(score) + ", " + Integer.toString(nbMvts) + ")";
                
                Statement stmt = con.createStatement();
                stmt.executeUpdate(requete);
                stmt.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
     /**
     * Détermine si un pseudo existe déjà dans la BDD.
     * @param pseudo
     * Paramètre de type String
     * @return 
     * Renvoie True si le pseudo esxiste déjà dns la BDD.
     */
    public boolean pseudoAlreadyExists(String pseudo) {
        try {
            try (Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP)) {
                String requete = "SELECT pseudo FROM scores WHERE pseudo = '" + pseudo + "'";
                
                Statement stmt = con.createStatement();
                ResultSet res = stmt.executeQuery(requete);
                
                if (res.next()) {
                    con.close();
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return false;
    }
    
    /**
     * Donne le nombre de fois qu'apparait un joueur dans la BDD.
     * @param pseudo
     * Paramètre de type String. Le pseudo du joueur en question.
     * @return 
     * Le nombre d'apparitions.
     */
    public int nbParticipations(String pseudo) {
        int nbParticipations = 0;
        try {
            try (Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP)) {
                String requete = "SELECT COUNT(*) AS count FROM scores WHERE pseudo = '" + pseudo + "'";
                
                Statement stmt = con.createStatement();
                ResultSet res = stmt.executeQuery(requete);
                
                if (res.next()) {
                    nbParticipations = res.getInt("count");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return nbParticipations;
    }
    
    /**
     * Vérifie si le nouveau résultat est meilleur que celui déjà enregistré.
     * Si la tuile maximale est plus grande, le résultat est meilleur, sinon on
     * regarde le nombre de coups et le score.
     * @param pseudo
     * Paramètre de type String
     * @param tuileMax 
     * Paramètre de type int
     * @param score 
     * Paramètre de type int
     * @param nbMvts 
     * Paramètre de type int
     * @return 
     * Renvoie true si le résultat est meilleur que le moins bon déjà enregistré.
     * Le résultat est calculé ainsi: Score - 2*nbMvts.
     */
    public boolean meilleurResultat(String pseudo, int score, int tuileMax, int nbMvts) {
        try {
            try (Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP)) {
                String requete = "SELECT * FROM scores WHERE pseudo = '" + pseudo + "' ORDER BY (score - 2*nbMvts) ASC";
                
                Statement stmt = con.createStatement();
                ResultSet res = stmt.executeQuery(requete);
                
                int scoreSaved = 0;
                int tuileMaxSaved = 0;
                int nbMvtsSaved = 0;
                
                if (res.next()) {
                    scoreSaved = res.getInt("score");
                    tuileMaxSaved = res.getInt("tuileMax");
                    nbMvtsSaved = res.getInt("nbMvts");
                }
                
                int resuSaved = scoreSaved - 2*nbMvtsSaved;
                
                if (tuileMax > tuileMaxSaved) {
                    con.close();
                    return true;
                } else if (tuileMax == tuileMaxSaved) {
                    if (score - 2*nbMvts > resuSaved) {
                        con.close();
                        return true;
                    } 
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return false;
    }
    
     /**
     * Supprime la ligne avec le moins bon résultat concerant un pseudo dans la 
     * BDD.
     * @param pseudo
     * Paramètre de type String
     */
    public void deleteLine(String pseudo) {
        try {
            try (Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP)) {
                String requete = "DELETE FROM `scores` WHERE `pseudo` = '" + pseudo + "' AND ((`score` - 2*`nbMvts`) = (SELECT MIN(`score` - 2*`nbMvts`) FROM scores WHERE pseudo = '" + pseudo + "'))";
                
                Statement stmt = con.createStatement();
                stmt.executeUpdate(requete);
                
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(BDD.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
     /**
     * Récupère les données de la BDD et de les met en forme dans un tableView.
     * @param tableview
     * Paramètre de type TableView
     */
    public void buildDataTableScores(TableView tableview){
        Connection c;
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        try {
            c = DriverManager.getConnection(URL, UTILISATEUR, MDP);
            //SQL FOR SELECTING ALL OF CUSTOMER
            String SQL = "SELECT * from scores";
            //ResultSet
            ResultSet rs = c.createStatement().executeQuery(SQL);

            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             */
            String[] nomsCol = {"Pseudo", "Tuile maximale", "Score", "Nombre de mouvements"};
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(nomsCol[i]);
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                if (i > 0) {
                    col.setComparator(new CustomComparator());
                }
                tableview.getColumns().addAll(col);
//                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
//                System.out.println("Row [1] added " + row);
                data.add(row);
            }
            

            //FINALLY ADDED TO TableView
            tableview.setItems(data);
            tableview.getSortOrder().add(tableview.getColumns().get(2));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    
    private class CustomComparator implements Comparator<String>{

    @Override
    public int compare(String o1, String o2) {
        int i1 = Integer.parseInt(o1);
        int i2 = Integer.parseInt(o2);
        return -Integer.compare(i1, i2);
    }
}
}
