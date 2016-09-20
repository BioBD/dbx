/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;


import clusteringAtributte.SetInterval;
import clusteringAtributte.TypeDate;
import clusteringAtributte.TypeDouble;
import java.util.Date;
import parser.EnumOperator;
import parser.LeafConditionDate;

/**
 *
 * @author Alain
 */
public class DateUnaryRestriction extends DateRestriction{
    Date dateTime;
    public LeafConditionDate date; 
    public DateUnaryRestriction(LeafConditionDate date){
        super();
        this.date = date;
        dateTime = date.getDate();
    }
    public DateRestriction intercept(DateRestriction dat){
        if(dat instanceof DateUnaryRestriction){
            DateUnaryRestriction dat1 = (DateUnaryRestriction)dat;
            if(equalsRestriction(dat1))
                return this;
            if(this.contain(dat1.date.getDate()) || dat1.contain(dateTime)){
                if(date.getOper() == EnumOperator.EQ)
                    return new DateUnaryRestriction(date); 
                else if(dat1.date.getOper() == EnumOperator.EQ)
                    return new DateUnaryRestriction(dat1.date);
                else if((date.getOper() == EnumOperator.GT || date.getOper() == EnumOperator.NLT) && 
                        (dat1.date.getOper()== EnumOperator.GT || dat1.date.getOper()== EnumOperator.NLT)){
                    if(dateTime.after(dat1.date.getDate()))
                        return new DateUnaryRestriction(date);
                    else 
                        return new DateUnaryRestriction(dat1.date);
                }
                else if((date.getOper() == EnumOperator.LT || date.getOper() == EnumOperator.NGT) && 
                        (dat1.date.getOper()== EnumOperator.LT || dat1.date.getOper()== EnumOperator.NGT)){
                    if(dateTime.before(dat1.date.getDate()))
                        return new DateUnaryRestriction(date);
                    else 
                        return new DateUnaryRestriction(dat1.date);
                }
                else{
                    if(date.getOper() == EnumOperator.LT || date.getOper() == EnumOperator.NGT)
                        return new DateIntervalRestriction(dat1.date, date);
                    else 
                        return new DateIntervalRestriction( date, dat1.date);
                }
            }
            else{
                
            }
        }
        else{
            DateIntervalRestriction dat1 = (DateIntervalRestriction)dat;
            DateRestriction pivot = intercept(dat1.gt);
            if(pivot == null)
                return null;
            if(pivot instanceof DateUnaryRestriction){
                return ((DateUnaryRestriction)pivot).intercept(dat1.lt);
            }
            else{
                return ((DateUnaryRestriction)intercept(dat1.lt)).intercept(dat1.gt);
            }
        }
        return null;
    }
    @Override
    public boolean matchRestriction(SetRestriction restriction){
        if(restriction instanceof DateUnaryRestriction && 
                ((DateUnaryRestriction)restriction).date.getAtt().equals(date.getAtt())){
            DateUnaryRestriction rest = (DateUnaryRestriction)restriction;
            DateRestriction result = intercept(rest);
            if(result instanceof DateIntervalRestriction){
                return false;
            }
            else{
                DateUnaryRestriction r = (DateUnaryRestriction)result;
                return r.equalsRestriction(rest);
            }
        }
        return false;
    }
    public boolean equalsRestriction(DateUnaryRestriction obj){
        if(obj.date.getAtt().equals(date.getAtt())
                && obj.date.getOper() == date.getOper()
                && obj.date.getDate().equals(date.getDate())){
            return true;
        }
        return false;
    }
    private boolean contain(Date cons){
        if(dateTime.equals(cons)){
            if(date.getOper() == EnumOperator.EQ || date.getOper() == EnumOperator.NGT || date.getOper() == EnumOperator.NLT)
                return true;
            else 
                return false;
        }
        else if(date.getOper() == EnumOperator.GT || date.getOper() == EnumOperator.NLT)
            return dateTime.before(cons);
        else if(date.getOper() == EnumOperator.LT || date.getOper() == EnumOperator.NGT)
            return cons.before(dateTime);
        else if(date.getOper() == EnumOperator.NEQ)
           return true;
        
        return false;
    }
    @Override
    public SetInterval<Date> getTypeDate(){
        if(date.getOper() == EnumOperator.GT || date.getOper() == EnumOperator.NLT)
            return new SetInterval<Date>( null,new TypeDate(dateTime), date.getAtt());
        else if(date.getOper() == EnumOperator.LT || date.getOper() == EnumOperator.NGT)
            return new SetInterval<Date>( new TypeDate(dateTime),null,date.getAtt());
        else if(date.getOper() == EnumOperator.EQ)
            return new SetInterval<Date>(new TypeDate(dateTime), new TypeDate(dateTime),date.getAtt());
        return null;
    }
    @Override
    public String toString(){
        return " "+date.toString()+" ";
    }
    @Override
    public String att(){
        return date.getAtt();
    }
}

