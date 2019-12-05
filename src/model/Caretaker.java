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
public class Caretaker {
    
    private int index = -1;
    
    private ArrayList savedStates = new ArrayList();
    
    public void setIndex(int ind){
        index=ind;
    }
    
    public int getIndex(){
        return index;
    }
    
    /**
     * Ajoute un état au memento.
     * @param m
     * paramètre de type Object.
     */
    public void addMemento(Object m){
        int l = savedStates.size();
        if (index != l){
//            System.out.println("On efface");
            savedStates.add(index + 1, m);
            for (int k = index + 2; k < l; k++){
                savedStates.remove(k);
            }            
        }
        else{
            savedStates.add(m);
        }
        index++;
    }
    
    /**
     * Récupère un état du memento.
     * @param i
     * paramètre de type int.
     * @return 
     */
    public Object getMemento(int i){
        return savedStates.get(i);
    }
}

