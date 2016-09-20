/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package clusteringAtributte;

import java.util.Date;
import parser.EnumOperator;
import parser.LeafConditionDate;
import parser.LeafConditionNumber;
import parser.SetRestrictions.DateIntervalRestriction;
import parser.SetRestrictions.DateUnaryRestriction;
import parser.SetRestrictions.IntervalSetRestriction;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.UnarySetRestriction;

/**
 *
 * @author Alain
 */
public class SetInterval <T>{
    TypeSet<T> less;
    TypeSet<T> greather;
    String att;
    
    public SetInterval(TypeSet<T> greather, TypeSet<T> less, String att){
        this.less = less;
        this.greather = greather;
        this.att = att;
    }
    public String getAtt(){return att;}
    public TypeSet<T> getLess(){return less;}
    public TypeSet<T> getGreather(){return greather;}
    public void setLess(TypeSet<T> less){this.less = less;}
    public void setGreather(TypeSet<T> greather){this.greather = greather;}
    public double distenceOf(SetInterval<T> set){
        double dist =0;
        if((less==null && greather==null) || (set.getGreather()==null && set.getLess() == null))
            return Double.MAX_VALUE;
        else if(less == null && set.getLess() == null)
            return greather.diff(set.getGreather())*greather.diff(set.getGreather()); 
        else if(greather == null && set.getGreather() == null)
            return less.diff(set.getLess())*less.diff(set.getLess());
        else if(less == null && set.greather == null)
            return Double.MAX_VALUE;
        else if(set.less == null && greather == null)
            return Double.MAX_VALUE;
        else if(less == null || set.less == null)
            return greather.diff(set.getGreather())*greather.diff(set.getGreather());
        else if(greather==null || set.greather==null)
            return less.diff(set.getLess())*less.diff(set.getLess());
            
        dist+= less.diff(set.getLess())*less.diff(set.getLess());
        dist+=greather.diff(set.getGreather())*greather.diff(set.getGreather());
        return dist;
                
    }
    public SetInterval<T> merge(SetInterval<T> set){
        TypeSet<T> a;
        TypeSet<T> b;
        if(less== null || set.getLess()==null)
            a= null;
        else if(less.diff(set.getLess()) > 0)
            a=set.getLess();
        else
            a = less;
        
        if(greather== null || set.getGreather()==null)
            b= null;
        else if(greather.diff(set.getGreather()) > 0)
            b = greather;
        else 
            b= set.greather;
        return new SetInterval<T>(b,a, att);
    }
    public String lessToString(){
        return less.toString();
    }
    public String greatherToString(){
        return greather.toString();
    }
    public SetRestriction getSetRestriction(){
        if((less!=null && less.getElement() instanceof Double) || (greather!=null && greather.getElement() instanceof Double)){
            if(less == null)
                return new UnarySetRestriction(new LeafConditionNumber(att,EnumOperator.LT,(Double)greather.getElement()));
            else if(greather == null)
                return new UnarySetRestriction(new LeafConditionNumber(att,EnumOperator.GT,(Double)less.getElement()));
            else 
                return new IntervalSetRestriction(new LeafConditionNumber(att,EnumOperator.LT,(Double)greather.getElement())
                        ,new LeafConditionNumber(att,EnumOperator.GT,(Double)less.getElement()));
        }
        else{
            if(less == null)
                return new DateUnaryRestriction(new LeafConditionDate(att,EnumOperator.LT,ClusteringUtil.dateToString((Date)greather.getElement())));
            else if(greather == null)
                return new DateUnaryRestriction(new LeafConditionDate(att,EnumOperator.GT,ClusteringUtil.dateToString((Date)less.getElement())));
            else 
                return new DateIntervalRestriction(new LeafConditionDate(att,EnumOperator.LT,ClusteringUtil.dateToString((Date)greather.getElement()))
                        ,new LeafConditionDate(att,EnumOperator.GT,ClusteringUtil.dateToString((Date)less.getElement())));
        }
    }
    public String getSQlClause(){
        String clause = "";
        if(less != null && greather!=null)
            clause += " "+att+" >= "+lessToString()+ " and "+att+" <= "+greatherToString()+" ";
        else if(less != null)
            clause += " "+att+" >= "+lessToString()+" ";
        else if(greather !=null)
            clause+= " "+att+" <= "+greatherToString()+" ";
        return clause;
    }
}
