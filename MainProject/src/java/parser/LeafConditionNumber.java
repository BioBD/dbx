/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import parser.SetRestrictions.IntervalSetRestriction;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.SortableSet;
import parser.SetRestrictions.StringRestriction;
import parser.SetRestrictions.UnarySetRestriction;

/**
 *
 * @author Alain
 */
public class LeafConditionNumber extends LeafCondition {
    double constant;
    public LeafConditionNumber(String attribute, EnumOperator operator, double constant){
        super(attribute, operator);
        this.constant = constant;
    }
    public LeafConditionNumber(String attribute, EnumOperator operator, double constant, Node father){
        super(attribute, operator, father);
        this.constant = constant;
    }
    public Node clone(){
        return new LeafConditionNumber(attribute, operator, constant);
    }
    public SortableSet interception(LeafConditionNumber condition){
        if(equalsNumeber(condition))
            return new UnarySetRestriction(this); 
        if(this.contain(condition.getConst()) || condition.contain(constant)){
            if(operator == EnumOperator.EQ)
                return new UnarySetRestriction(this); 
            else if(condition.getOper() == EnumOperator.EQ)
                return new UnarySetRestriction(condition);
            else if((operator == EnumOperator.GT || operator == EnumOperator.NLT) && 
                    (condition.getOper()== EnumOperator.GT || condition.getOper()== EnumOperator.NLT)){
                if(constant > condition.getConst())
                    return new UnarySetRestriction(this);
                else 
                    return new UnarySetRestriction(condition);
            }
            else if((operator == EnumOperator.LT || operator == EnumOperator.NGT) && 
                    (condition.getOper()== EnumOperator.LT || condition.getOper()== EnumOperator.NGT)){
                if(constant < condition.getConst())
                    return new UnarySetRestriction(this);
                else 
                    return new UnarySetRestriction(condition);
            }
            else{
                if(operator == EnumOperator.LT || operator == EnumOperator.NGT)
                    return new IntervalSetRestriction(this, condition);
                else 
                    return new IntervalSetRestriction(condition, this);
            }
        }
        else{
            
        }
        return null; // Hacer que se devuelva la intercepcion ...
    }
    private boolean contain(double cons){
        if(cons == constant){
            if(operator == EnumOperator.EQ || operator == EnumOperator.NGT || operator == EnumOperator.NLT)
                return true;
            else 
                return false;
        }
        else if(operator == EnumOperator.GT || operator == EnumOperator.NLT)
            return constant < cons;
        else if(operator == EnumOperator.LT || operator == EnumOperator.NGT)
            return cons < constant;
        else if(operator == EnumOperator.NEQ)
           return true;
        
        return false;
    }
    public boolean equalsNumeber(LeafConditionNumber n){
        if(n.getOper()==getOper()&& n.getConst() == getConst() && n.getAtt().equalsIgnoreCase(getAtt()))
            return true;
        return false;
    }
    public double getConst(){return constant;}
    public void setConst(double cons){ constant = cons;}
    public void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> set){
        set.get(set.size()-1).add(new UnarySetRestriction(this));
    }
    @Override
    public String toString(){ 
        return getAtt() + " " + Node.matchOperator(getOper())+ " " + String.valueOf(getConst());
    }
}
