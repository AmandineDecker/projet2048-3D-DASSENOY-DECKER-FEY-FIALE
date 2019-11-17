/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reseauClient;

import java.time.Duration;
import javafx.beans.property.*;

/**
 *
 * @author Amandine
 */
public class JoueurTableView {
    
    private final StringProperty pseudo;
    private final IntegerProperty score;
    private final IntegerProperty tuileMax;
    private final ObjectProperty<Duration> tempsJeu;
    
    public JoueurTableView() {
        this(null, 0, 0, null);
    }
    
    /**
     * Constructor with some initial data.
     * 
     * @param pseudo
     * @param score
     * @param tuileMax
     * @param tempsJeu
     */
    public JoueurTableView(String pseudo, int score, int tuileMax, Duration tempsJeu) {
        this.pseudo = new SimpleStringProperty(pseudo);
        this.score = new SimpleIntegerProperty(score);
        this.tuileMax = new SimpleIntegerProperty(tuileMax);
        this.tempsJeu = new SimpleObjectProperty(tempsJeu);
    }
    
    // Pseudo
    
    public String getPseudo() {
        return pseudo.get();
    }

    public void setPseudo(String pseudo) {
        this.pseudo.set(pseudo);
    }
    
    public StringProperty pseudoProperty() {
        return pseudo;
    }
    
    // Score
    
    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }
    
    public IntegerProperty scoreProperty() {
        return score;
    }
    
    // Tuile Max
    
    public int getTuileMax() {
        return tuileMax.get();
    }

    public void setTuileMax(int tuileMax) {
        this.tuileMax.set(tuileMax);
    }
    
    public IntegerProperty tuileMaxProperty() {
        return tuileMax;
    }
    
    // Temps de jeu
    
    public Duration getTempsJeu() {
        return tempsJeu.get();
    }

    public void setTempsJeu(Duration tempsJeu) {
        this.tempsJeu.set(tempsJeu);
    }
    
    public ObjectProperty<Duration> tempsJeuProperty() {
        return tempsJeu;
    }
    
}
