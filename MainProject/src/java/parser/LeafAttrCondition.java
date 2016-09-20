/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.StringRestriction;

/**
 *
 * @author Alain
 */
public class LeafAttrCondition extends LeafCondition{
    String rightOper;
    public LeafAttrCondition(String attribute, EnumOperator operator, String rightOper){
        super(attribute, operator);
        this.rightOper = rightOper;
    }
    public LeafAttrCondition(String attribute, EnumOperator operator, String rightOper, Node father){
        super(attribute, operator, father);
        this.rightOper = rightOper;
    }
    public String getRightOper(){return rightOper;}
    @Override 
    public Node clone(){
        return new LeafAttrCondition(attribute, operator, rightOper);
    }
    public void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> set){
        return;
    }
    @Override
    public String toString(){
        String to = "";
        return to;
    }
}
