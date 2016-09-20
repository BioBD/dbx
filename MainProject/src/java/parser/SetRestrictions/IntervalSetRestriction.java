/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import clusteringAtributte.SetInterval;
import clusteringAtributte.TypeDouble;
import parser.EnumOperator;
import parser.LeafConditionNumber;

/**
 *
 * @author Alain
 */
public class IntervalSetRestriction extends SortableSet{
    LeafConditionNumber ltORNgt;
    LeafConditionNumber gtORNlt;
    public IntervalSetRestriction(LeafConditionNumber ltORNgt, LeafConditionNumber gtORNlt){
        if(ltORNgt.getOper() != EnumOperator.LT && ltORNgt.getOper() != EnumOperator.NGT ){
            System.out.println("Bat Instance on IntervalSetRestriction");
        }
        if(gtORNlt.getOper() != EnumOperator.GT && gtORNlt.getOper() != EnumOperator.NLT ){
            System.out.println("Bat Instance on IntervalSetRestriction");
        }
        this.ltORNgt = ltORNgt;
        this.gtORNlt = gtORNlt;
    }
    public LeafConditionNumber getLtORNgt(){
        return ltORNgt;
    }
    public LeafConditionNumber getGtORNlt(){
        return gtORNlt;
    }
    public SortableSet interception(UnarySetRestriction restriction){
        return restriction.interception(this);
    }
    @Override
    public SetInterval<Double> getTypeDouble(){
        return new SetInterval(new TypeDouble(new Double(ltORNgt.getConst())), new TypeDouble(new Double(gtORNlt.getConst())),ltORNgt.getAtt());
    }
    public SortableSet interception(IntervalSetRestriction restriction){
        SetRestriction pivot1 = getLtORNgt().interception(restriction.getLtORNgt());
        SetRestriction pivot2 = getGtORNlt().interception(restriction.getGtORNlt());
        return ((UnarySetRestriction)pivot1).getCondition()
                .interception(((UnarySetRestriction)pivot2).getCondition());
    }
    @Override
    public boolean matchRestriction(SetRestriction restriction){
        if(restriction instanceof IntervalSetRestriction && 
                ((IntervalSetRestriction)restriction).getGtORNlt().getAtt().equals(getGtORNlt().getAtt())){
            IntervalSetRestriction rest = (IntervalSetRestriction)restriction;
            
            UnarySetRestriction meGt = new UnarySetRestriction(getGtORNlt());
            UnarySetRestriction restGt = new UnarySetRestriction(rest.getGtORNlt());
            
            UnarySetRestriction meLt = new UnarySetRestriction(getLtORNgt());
            UnarySetRestriction restLt = new UnarySetRestriction(rest.getLtORNgt());
            
            if(meGt.matchRestriction(restGt) && meLt.matchRestriction(restLt))
                return true;
        }
        return false;
    }
    @Override
    public String toString(){
        return " "+ltORNgt.toString()+" AND " + gtORNlt.toString()+ " ";
    }
    @Override
    public String att(){
        return ltORNgt.getAtt();
    }
}
