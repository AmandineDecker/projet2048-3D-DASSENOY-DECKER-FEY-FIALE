/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.sql.*;

/**
 * FXML Controller class
 *
 * @author Amandine
 */
public class FXMLSaveDataBaseController implements Initializable {

    private static String URL = "jdbc:mysql://mysql-2048-dassenoyfialedeckerfey.alwaysdata.net:3306/2048-dassenoyfialedeckerfey_scores";
    private static String UTILISATEUR = "195812";
    private static String MDP = "juxje5-maQkum-pacrim";
    
    
//    try {
//            // TODO code application logic here
//            Connection con = DriverManager.getConnection(URL, UTILISATEUR, MDP);
//            
//            String requete = "INSERT INTO scores VALUES ('TestMain', 1024, 29374)";
//            
//            Statement stmt = con.createStatement();
//            stmt.executeUpdate(requete);
//            
////            while (res.next()) {
////                String pseudo = res.getString("pseudo");
////                System.out.println(pseudo);
////            }
////            
////            res.close();
//            stmt.close();
//            con.close();
//            
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(ConsoleTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
