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
public class LogicOperator extends Node {
    EnumLogicOperator operator; 
    
    public LogicOperator(EnumLogicOperator operator){
        super();
        this.operator = operator;
    }
    public LogicOperator(EnumLogicOperator operator, Node father){
        super(father);
        this.operator = operator;
    }
    public Node clone(){
        LogicOperator tmp = new LogicOperator(operator);
        for(int i=0; i< getChilds().size();i++){
            Node clon = getChilds().get(i).clone();
            tmp.addChild(clon);
            clon.setFather(tmp);
        }
        return tmp;
    }
    public void AddChild(Node ch){
        childs.add(ch);
    }
    public EnumLogicOperator getOperator(){return operator;}
    public static EnumLogicOperator parseOperator(String operator){
        EnumLogicOperator oper = EnumLogicOperator.AND;
        switch(operator){
            case " AND ":
                oper = EnumLogicOperator.AND;
                break;
            case " OR ":
                oper = EnumLogicOperator.OR;
                break;
            case "NOT":
                oper = EnumLogicOperator.NOT;
                break;
        }
        return oper;
    }
    public void processComplement(){
        if(operator == EnumLogicOperator.NOT){
            if(getChilds().get(0) instanceof LeafCondition){
                LeafCondition leaf = (LeafCondition)getChilds().get(0);
                leaf.complement();
                
                leaf.setFather(father);
                father.getChilds().add(father.getChilds().indexOf(this), leaf);
                father.getChilds().remove(this);
                
            }
            else if(((LogicOperator)getChilds().get(0)).getOperator() != EnumLogicOperator.NOT){
                LogicOperator log = (LogicOperator)getChilds().get(0);
                LogicOperator tmp1 = null;
                if(log.getOperator() == EnumLogicOperator.AND){
                    tmp1 = new LogicOperator(EnumLogicOperator.OR,father);
                }
                else if(log.getOperator() == EnumLogicOperator.OR){
                    tmp1 = new LogicOperator(EnumLogicOperator.AND,father);
                }
                tmp1.setFather(father);
                father.getChilds().add(father.getChilds().indexOf(this), tmp1);
                father.getChilds().remove(this);
                
                LogicOperator node1 = new LogicOperator(EnumLogicOperator.NOT, tmp1);
                LogicOperator node2 = new LogicOperator(EnumLogicOperator.NOT, tmp1);
                
                node1.getChilds().add(log.getChilds().get(0));
                node2.getChilds().add(log.getChilds().get(1));
                
                tmp1.getChilds().add(node1);
                tmp1.getChilds().add(node2);
                
                node1.processComplement();
                node2.processComplement();
            }
            else{
                Node tmp = getChilds().get(0).getChilds().get(0);
                
                tmp.setFather(father);
                father.getChilds().add(father.getChilds().indexOf(this), tmp);
                father.getChilds().remove(this);
            }
        }
        else{
            for(int i=0;i<getChilds().size();i++){
                if (getChilds().get(i) instanceof LogicOperator)
                    ((LogicOperator)getChilds().get(i)).processComplement();
            }
        }
    }
    protected void getIndexabelAttr(ArrayList<ArrayList<SetRestriction>> set){
        if(operator == EnumLogicOperator.OR){
            for(int i=0;i<getChilds().size();i++){
                if (getChilds().get(i) instanceof LogicOperator && 
                        ((LogicOperator)getChilds().get(i)).operator == EnumLogicOperator.AND){
                    set.add(new ArrayList<SetRestriction>());
                    ((LogicOperator)getChilds().get(i)).getIndexabelAttr(set);
                }
                else{
                    ((Node)getChilds().get(i)).getIndexabelAttr(set);
                }
            }
        }
        else{
            if(operator == EnumLogicOperator.AND && set.isEmpty())
                set.add(new ArrayList<SetRestriction>());
            for(int i=0;i<getChilds().size();i++){
                ((Node)getChilds().get(i)).getIndexabelAttr(set);
            }
        }
    }
    public boolean processASTNode(){
        boolean rotated =false;
        if(operator == EnumLogicOperator.OR){
            for(int i=0;i<getChilds().size();i++){
                if (getChilds().get(i) instanceof LogicOperator){
                    if(((LogicOperator)getChilds().get(i)).processASTNode())
                        rotated = true;
                }
            }
            return rotated;
        }
        else if(operator == EnumLogicOperator.AND){
            Node left = getChilds().get(0);
            Node right = getChilds().get(1);
            if( left instanceof LogicOperator && 
                    ((LogicOperator)left).getOperator() == EnumLogicOperator.OR){
                // Rotate
                left.setFather(father);
                father.getChilds().add(father.getChilds().indexOf(this), left);
                father.getChilds().remove(this);
                
                LogicOperator node1 = new LogicOperator(EnumLogicOperator.AND, left);
                LogicOperator node2 = new LogicOperator(EnumLogicOperator.AND, left);
                
                
                node1.getChilds().add(left.getChilds().get(0));
                left.getChilds().get(0).setFather(node1);
                node1.getChilds().add(right);
                right.setFather(node1);
                // create clone
                right = right.clone();
                
                node2.getChilds().add(left.getChilds().get(1));
                left.getChilds().get(1).setFather(node2);
                node2.getChilds().add(right);
                right.setFather(node2);
                
                left.getChilds().clear();
                left.getChilds().add(node1);
                left.getChilds().add(node2);
                
                node1.processASTNode();
                node2.processASTNode();
                return true;
            }
            else if(right instanceof LogicOperator && 
                    ((LogicOperator)right).getOperator() == EnumLogicOperator.OR){
                // Rotate
                right.setFather(father);
                father.getChilds().add(father.getChilds().indexOf(this), right);
                father.getChilds().remove(this);
                
                LogicOperator node1 = new LogicOperator(EnumLogicOperator.AND, right);
                LogicOperator node2 = new LogicOperator(EnumLogicOperator.AND, right);
                
                
                node1.getChilds().add(right.getChilds().get(0));
                right.getChilds().get(0).setFather(node1);
                node1.getChilds().add(left);
                left.setFather(node1);
                // create clone
                left = left.clone();
                
                
                node2.getChilds().add(right.getChilds().get(1));
                right.getChilds().get(1).setFather(node2);
                node2.getChilds().add(left);
                left.setFather(node2);
                
                right.getChilds().clear();
                right.getChilds().add(node1);
                right.getChilds().add(node2);
                
                node1.processASTNode();
                node2.processASTNode();
                return true;
            }
            else if(left instanceof LogicOperator && 
                    ((LogicOperator)left).getOperator() == EnumLogicOperator.AND
                    || 
                    right instanceof LogicOperator && 
                    ((LogicOperator)right).getOperator() == EnumLogicOperator.AND){
                for(int i=0;i<getChilds().size();i++){
                    if (getChilds().get(i) instanceof LogicOperator){
                        if(((LogicOperator)getChilds().get(i)).processASTNode()){
                            rotated = true;
                        }
                    }
                }
                if(rotated)
                    return processASTNode();
            }
            else return false;
        }
        return false;
    }
    public void DFSTest(){
        for(int i=0;i<getChilds().size();i++){
                if (getChilds().get(i) instanceof LogicOperator){
                    ((LogicOperator)getChilds().get(i)).DFSTest();
                }
                else
                    ((LeafCondition)getChilds().get(i)).DFSTest();
            }
        System.out.println(operator);
    }
    @Override
    public String toString(){
        return "";
    }
}
