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
public class LeafConditionString extends LeafCondition{
    String constant;
    public LeafConditionString(String attribute, EnumOperator operator, String constant){
        super(attribute, operator);
        this.constant = constant;
    }
    public LeafConditionString(String attribute, EnumOperator operator, String constant, Node father){
        super(attribute, operator, father);
        this.constant = constant;
    }
    public Node clone(){
        return new LeafConditionString(attribute, operator, constant);
    }
    public String getConst(){return constant;}
    public void setConst(String cons){ constant = cons;}
    public void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> set){
        set.get(set.size()-1).add(new StringRestriction(this));
    }
    @Override
    public String toString(){ 
        return getAtt() + " " + Node.matchOperator(getOper())+ " " + getConst()+" ";
    }
}
