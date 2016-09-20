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
public abstract  class Node {
    protected Node father;
    protected ArrayList<Node> childs;
    public Node(){
        childs = new ArrayList<Node>();
        father = null;
    }
    public abstract Node clone();
    public Node(Node father){
        childs = new ArrayList<Node>();
        this.father = father;
    }
    public Node getFather(){return father;}
    public void setFather(Node father){this.father = father;}
    public ArrayList<Node> getChilds(){
        return childs;
    }
    public void setChilds(ArrayList<Node> childs){
        this.childs = childs;
    }
    public void addChild(Node node){
        childs.add(node);
    }
    protected abstract void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> set);
    public abstract String toString();
    public static String matchOperator(EnumOperator oper){
        switch(oper){
            case EQ:
                return " = ";
            case LT:
                return " < ";
            case NLT:
                return " >= ";
            case GT:
                return " > ";
            case NGT:
                return " <= ";
            case NEQ: 
                return " <> ";
            default: 
                return " LIKE ";
        }
    }
    
}
