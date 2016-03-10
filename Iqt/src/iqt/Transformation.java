/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iqt;

/**
 *
 * @author Arlino
 */
public class Transformation {
    
    private String heuristic, description;//Heuríctica aplica na tranformação da reescrita.
    private String sql1, sql2;//SQLs antes (slq1) e depois (sql2) reescrita.
    private int idSql1, idSql2; //Identificação da consulta. Serve como orientação das transformações executadas.

    /**
     * 
     * @param heuristic
     * Heurística utilizada.
     * @param description
     * Descrição da heurística utilizada.
     * @param sql1
     * SQL original que foi reescrita.
     * @param sql2
     * SQL depois da reescrita.
     * @param number1
     * Número de identificação da SQL original que foi reescrita.
     * @param number2
     * Número de identificação da SQL depois da reescrita.
     */
    public Transformation(String heuristic, String description, String sql1, String sql2, int number1, int number2) {
        this.heuristic = heuristic;
        this.description = description;
        this.sql1 = sql1;
        this.sql2 = sql2;
        this.idSql1 = number1;
        this.idSql2 = number2;
    }
    
    /**
     * Retorna uma descrição da heurística utilizada.
     * @return
     * Retorna uma String com a descrição.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrona a heurística utilizada.
     * @return
     * Retorna uma String com a heurística.
     */
    public String getHeuristic() {
        return heuristic;
    }

    /**
     * Retrona a SQL original que foi reescrita.
     * @return
     * Retorna uma String com a SQL orignal.
     */
    public String getSql1() {
        return sql1;
    }

    /**
     * Retrona a SQL depois da reescrita.
     * @return
     * Retorna uma String com a SQL reescrita.
     */
    public String getSql2() {
        return sql2;
    }

    /**
     * Retrona o número de identificação da SQL original que foi reescrita.
     * @return
     * Retorna um int com a identificação da SQL orignal.
     */
    public int getIdSql1() {
        return idSql1;
    }

    /**
     * Retrona o número de identificação da SQL depois da reescrita.
     * @return
     * Retorna um int com a identificação da SQL reescrita.
     */
    public int getIdSql2() {
        return idSql2;
    }
    
    @Override
    public String toString() {
        return "Heuristic = " + heuristic + "\nDescription = " + description + 
               "\nSQL Original("  + idSql1 + ")  = " + sql1 + "\nSQL Reescrita("  + idSql2 + ") = " + sql2;
    }
}
