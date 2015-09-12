/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd;

/**
 *
 * @author Rafael
 */
public class Plan implements IPlan {

    private final String plan;

    public Plan(String plan) {
        this.plan = plan;
    }

    public String getPlan() {
        return plan;
    }
    
    public long getCost(){
        return 0;
    }

    public long getNumRow(){
        return 0;
    }

    public long getSizeRow(){
        return 0;
    }

    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
