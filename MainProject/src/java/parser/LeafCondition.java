/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.util.ArrayList;
import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public abstract class LeafCondition extends Node{
    String attribute;
    EnumOperator operator;
    public LeafCondition(String attribute, EnumOperator operator){
        super();
        this.attribute = attribute;
        this.operator = operator;
    }
    public LeafCondition(String attribute, EnumOperator operator, Node father){
        super(father);
        this.attribute = attribute;
        this.operator = operator;
    }
    public String getAtt(){return attribute;}
    public EnumOperator getOper(){return operator;}
    
    
    public void setAttr(String attr){ attribute = attr;}
    public void setOper(EnumOperator oper){ operator = oper;}
    
    public static EnumOperator parseOperator(String operator){
        EnumOperator oper = EnumOperator.EQ;
        switch(operator){
            case ">":
                oper = EnumOperator.GT;
                break;
            case "<":
                oper = EnumOperator.LT;
                break;
            case "=":
                oper = EnumOperator.EQ;
                break;
            case "LIKE":
                oper = EnumOperator.LIKE;
                break;
            case ">=":
                oper = EnumOperator.NGT;
                break;
            case "<=":
                oper = EnumOperator.NLT;
                break;
            case "!=":
                oper = EnumOperator.NEQ;
                break;
            case "<>":
                oper = EnumOperator.NEQ;
                break;
        }
        return oper;
    }
    public void complement(){
        if(operator == EnumOperator.GT)
            operator = EnumOperator.NGT;
        else if(operator == EnumOperator.LT)
            operator = EnumOperator.NLT;
        else if(operator == EnumOperator.EQ)
            operator = EnumOperator.NEQ;
        else if(operator == EnumOperator.NGT)
            operator = EnumOperator.GT;
        else if(operator == EnumOperator.NLT)
            operator = EnumOperator.LT;
        else if(operator == EnumOperator.NEQ)
            operator = EnumOperator.EQ;
    }
    
    public void DFSTest(){
        System.out.println("Atribute "+attribute+" operador "+operator);
    }
}

