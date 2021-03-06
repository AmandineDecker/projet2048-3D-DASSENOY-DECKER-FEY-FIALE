/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


/**
 * Gestion du Caretaker.
 */
public class Originator {
    
    private Grille state;
    
    /**
     * Met à jour state.
     * @param state
     * Paramètre de type Grille. Celle à enregistrer.
     */
    public void set(Grille state){
        this.state=state;
    }
    
    /**
     * Crée un memento pour l'état actuel.
     * @return 
     * Paramètre de type Object.
     * @throws java.lang.CloneNotSupportedException
     * Si le clonage échoue.
     */
    public Object saveToMemento() throws CloneNotSupportedException{
        return new Memento(state);
    }
    
    /**
     * Récupère l'objet sauvegardé dans le memento. 
     * @param m
     * Paramètre de type Object (Memento demandé).
     * @return 
     * Transforme l'object du memento en Grille.
     * @throws java.lang.CloneNotSupportedException
     * Si le clonage échoue.
     */
    public Grille restoreFromMemento(Object m) throws CloneNotSupportedException{
        if (m instanceof Memento){
            Memento memento = (Memento)m;
            state = memento.getSavedState();
        }
        return state;
    }
    

    private static class Memento{
    
        private final Grille state;

        public Memento(Grille stateToSave) throws CloneNotSupportedException{
            state = stateToSave.clone();
        }

        /**
         * Renvoie la grille sauvegardée.
         * @return 
         * @throws java.lang.CloneNotSupportedException
         */
        public Grille getSavedState() throws CloneNotSupportedException{
            return state.clone();
        }
    }
}
