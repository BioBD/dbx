/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import clusteringAtributte.SetInterval;
import clusteringAtributte.TypeDouble;
import parser.EnumOperator;
import parser.LeafCondition;
import parser.LeafConditionNumber;
import parser.LeafConditionString;

/**
 *
 * @author Alain
 */
public class UnarySetRestriction extends SortableSet{
    LeafConditionNumber condition;
    public UnarySetRestriction(LeafConditionNumber condition){
        super();
        
        this.condition = condition;
    }
    public void setCondition(LeafConditionNumber condition){
        this.condition = condition;
    }
    public LeafConditionNumber getCondition(){
        return condition;
    }
    public SortableSet interception(UnarySetRestriction restriction){
        
            // If null then empty interception
            return condition.interception(restriction.getCondition());
    }
    public SortableSet interception(IntervalSetRestriction restriction){
        SetRestriction pivot = condition.interception(restriction.getGtORNlt());
        if(pivot == null) 
            return null;
        if(pivot instanceof UnarySetRestriction){
            return ((UnarySetRestriction)pivot).getCondition().interception(restriction.getLtORNgt());
        }
        else{
            return ((UnarySetRestriction)condition.interception(restriction.getLtORNgt()))
                    .getCondition().interception(restriction.getGtORNlt());
        }
    }
    @Override
    public boolean matchRestriction(SetRestriction restriction){
        if(restriction instanceof UnarySetRestriction && 
                ((UnarySetRestriction)restriction).getCondition().getAtt().equals(getCondition().getAtt())){
            UnarySetRestriction rest = (UnarySetRestriction)restriction;
            SortableSet result = interception(rest);
            if(result == null){
                return false;
            }
            if(result instanceof IntervalSetRestriction){
                return false;
            }
            else{
                UnarySetRestriction r = (UnarySetRestriction)result;
                return r.equalsRestriction(rest);
            }
        }
        return false;
    }
    public boolean equalsRestriction(UnarySetRestriction obj){
        if(obj.getCondition().getAtt().equals(getCondition().getAtt())
                && obj.getCondition().getOper() == getCondition().getOper()
                && obj.getCondition().getConst() == getCondition().getConst()){
            return true;
        }
        return false;
    }
    @Override
    public SetInterval<Double> getTypeDouble(){
        if(condition.getOper() == EnumOperator.GT || condition.getOper() == EnumOperator.NLT)
            return new SetInterval<Double>(null,new TypeDouble(new Double(condition.getConst())), condition.getAtt());
        else if(condition.getOper() == EnumOperator.LT || condition.getOper() == EnumOperator.NGT)
            return new SetInterval<Double>(new TypeDouble(new Double(condition.getConst())),null,condition.getAtt());
        else if(condition.getOper() == EnumOperator.EQ)
            return new SetInterval<Double>(new TypeDouble(new Double(condition.getConst())), new TypeDouble(new Double(condition.getConst())),condition.getAtt());
        return null;
    }
    @Override
    public String toString(){
        return " "+condition.toString()+" ";
    }
    @Override
    public String att(){
        return condition.getAtt();
    }
}
