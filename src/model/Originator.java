/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Wanda
 */
public class Originator {
    private Grille state;
    
    public void set(Grille state){
        System.out.println("Originator: etat affecte a: "+state);
        this.state=state;
    }
    
    public Object saveToMemento(){
        System.out.println("Originator: sauvegarder dans le memento.");
        return new Memento(state);
    }
    
    public void restoreFromMemento(Object m){
        if (m instanceof Memento){
            Memento memento = (Memento)m;
            state=memento.getSavedState();
            System.out.println("Originator: Etat après restauration: "+state);
        }
    }
    

private static class Memento{
    private Grille state;
    
    public Memento(Grille stateToSave){
        state=stateToSave;
    }
    
    public Grille getSavedState(){
        return state;
    }
}
}
