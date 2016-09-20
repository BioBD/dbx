/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import clusteringAtributte.SetInterval;
import clusteringAtributte.TypeDate;
import java.util.Date;
import parser.LeafConditionDate;

/**
 *
 * @author Alain
 */
public class DateIntervalRestriction extends DateRestriction{
    public DateUnaryRestriction gt, lt;
    Date gtDate; Date ltDate;
    public DateIntervalRestriction(LeafConditionDate gt, LeafConditionDate lt){
        this.gt = new DateUnaryRestriction(gt);
        this.lt = new DateUnaryRestriction(lt);
        gtDate = gt.getDate();
        ltDate = lt.getDate();
    }
    public DateRestriction intercept(DateRestriction dat){
        if(dat instanceof DateUnaryRestriction){
            return ((DateUnaryRestriction)dat).intercept(this);
        }
        else {
            SetRestriction pivot1 = lt.intercept(((DateIntervalRestriction)dat).lt);
            SetRestriction pivot2 = gt.intercept(((DateIntervalRestriction)dat).gt);
            return ((DateUnaryRestriction)pivot1).intercept(((DateUnaryRestriction)pivot2));
        }
    }
    public boolean matchRestriction(SetRestriction restriction){
        if(restriction instanceof DateIntervalRestriction && 
                ((DateIntervalRestriction)restriction).gt.date.getAtt().equals(gt.date.getAtt())){
            DateIntervalRestriction rest = (DateIntervalRestriction)restriction;
            
            DateUnaryRestriction meGt = new DateUnaryRestriction(gt.date);
            DateUnaryRestriction restGt = new DateUnaryRestriction(rest.gt.date);
            
            DateUnaryRestriction meLt = new DateUnaryRestriction(lt.date);
            DateUnaryRestriction restLt = new DateUnaryRestriction(rest.lt.date);
            
            if(meGt.matchRestriction(restGt) && meLt.matchRestriction(restLt))
                return true;
        }
        return false;
    }
    @Override
    public SetInterval<Date> getTypeDate(){
        return new SetInterval(new TypeDate(ltDate), new TypeDate(gtDate),lt.att());
    }
    @Override
    public String toString(){
        return " "+lt.toString() + " AND " + gt.toString() + " ";
    }
    @Override
    public String att(){
        return gt.att();
    }
}
    
