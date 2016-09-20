/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.SetRestrictions;

import parser.LeafConditionString;
import java.util.ArrayList;

/**
 *
 * @author Alain
 */
public class StringRestriction extends SetRestriction{
    ArrayList<LeafConditionString> stringConds;
    public StringRestriction(){
        super();
        stringConds = new ArrayList<LeafConditionString>();
    }
    public StringRestriction(LeafConditionString cond){
        super();
        stringConds = new ArrayList<LeafConditionString>();
        stringConds.add(cond);
    }
    public ArrayList<LeafConditionString> getStringConds(){return stringConds;}
    @Override
    public boolean matchRestriction(SetRestriction restriction){
        if(restriction instanceof StringRestriction){
            StringRestriction rest = (StringRestriction)restriction;
            for(int i=0;i<rest.stringConds.size();i++){
                for(int j=0; j < stringConds.size();j++){
                    if(rest.stringConds.get(i).getAtt().equals(stringConds.get(j).getAtt()) &&
                            rest.stringConds.get(i).getConst().equals(stringConds.get(j).getConst())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @Override
    public String toString(){
        String res = "";
        for(LeafConditionString con : stringConds){
            if(res == "")
                res = " "+con.toString()+" ";
            else{
                res = res + " AND " + con.toString()+" ";
            }
        }
        return res;
    }
    @Override
    public String att(){
        return stringConds.get(0).getAtt();
    }
}
