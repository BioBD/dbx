/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.Date;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import parser.SetRestrictions.DateUnaryRestriction;
import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public class LeafConditionDate extends LeafConditionString{
    public LeafConditionDate(String attribute, EnumOperator operator, String constant){
        super(attribute, operator, constant);
        this.constant = constant;
    }
    public LeafConditionDate(String attribute, EnumOperator operator, Date constant){
        super(attribute,operator,"");
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        super.setConst(formatter.format(constant));
    }
    public LeafConditionDate(String attribute, EnumOperator operator, String constant, Node father){
        super(attribute, operator, constant, father);
        this.constant = constant;
    }
    public Date getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//         DateFormat dateformat = DateFormat.getDateInstance();
         try{
             return format.parse(constant.replace("'", "")); 
         }catch(Exception e){
             //throw new Exception("Incorrect date format");
         }
         return null;
    }
    @Override
    public void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> set){
        set.get(set.size()-1).add(new DateUnaryRestriction(this));
    }
    @Override
    public String toString(){ 
        String r = getAtt() + " " + Node.matchOperator(getOper())+ " date '" +getConst().replace("'", "")+"' "; 
        return r;
    }
    @Override
    public Node clone(){
        return new LeafConditionDate(attribute, operator, constant);
    }
}
