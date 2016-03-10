/*
 * Classe implementada para dar suporte clausula JOIN em seleções.
 * A classe é recursiva para permitir uso de joins aninhados.
 * 
 * By Arlino Henrique 01/11/2011
 *    arlinoh@gmail.com
 * 
 */


package br.com.iqt.zql;

import java.util.Vector;

public class ZJoin{
    
    Vector firstTable;
    String joinType;
    Vector secondTable = null;
    ZJoin nestedJoin = null;
    ZExp OnExpression;
    
    public void setFirstTable(Vector firstTable){
        this.firstTable = firstTable;
    }
    
    public Vector getFirstTable(){
        return this.firstTable;
    }

    public ZExp getOnExpression() {
        return OnExpression;
    }

    public void setOnExpression(ZExp onExpression) {
        this.OnExpression = onExpression;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public ZJoin getNestedJoin() {
        return nestedJoin;
    }

    public void setNestedJoin(ZJoin nestedJoin) {
        this.nestedJoin = nestedJoin;
    }

    public Vector getSecondTable() {
        return secondTable;
    }

    public void setSecondTable(Vector secondTable) {
        this.secondTable = secondTable;
    }
    
};
