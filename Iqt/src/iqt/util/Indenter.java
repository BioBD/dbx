/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.iqt.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Arlino
 */
public class Indenter {
    
    public static final String ident(String sql){
        String sqlOutput = null;
        if((sql != null) && (!sql.isEmpty())){
            sql = removeWhiteSpace(sql);
            StringBuilder sqlImput = new StringBuilder(sql);
            StringBuilder sqlAux = new StringBuilder();
            String aux = "", blanks = "", oldBlanks = "";
            boolean consumed = false, unindent = false;
            int lengthBlanks = 0, oldLengthBlanks = 0, parentheses= 0;
            int begin = 0;
            
            int length = sqlImput.length();
            int i =0;
            while (i < length) {
                //Identifica SELECT
                if(i+6<sqlImput.length())
                    aux  = sqlImput.substring(i, i+6);
                if(aux.equalsIgnoreCase("SELECT")){
                    if(i != 0){//Calcula nova indentação
                        oldLengthBlanks = lengthBlanks;
                        lengthBlanks = sqlAux.length() - sqlAux.lastIndexOf("\n") - 1;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    
                    //sqlAux.append(blanks);
                    sqlAux.append(aux);
                    i += 5;
                    consumed = true;
                }
                
                //Identifica FROM
                if(i+4<sqlImput.length())
                    aux  = sqlImput.substring(i, i+4);
                if(aux.equalsIgnoreCase("FROM")){
                    
                    sqlAux.append("\n");
                    sqlAux.append(blanks);
                    sqlAux.append(aux);
                    if(i != 0){
                        lengthBlanks = oldLengthBlanks;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    i += 3;
                    consumed = true;
                }
                
                //Identifica WHERE
                if(i+5<sqlImput.length())
                    aux  = sqlImput.substring(i, i+5);
                if(aux.equalsIgnoreCase("WHERE")){
                    if(parentheses != 0){//Calcula nova indentação
                        oldLengthBlanks = lengthBlanks;
                        lengthBlanks = sqlAux.toString().toUpperCase().lastIndexOf("FROM") - sqlAux.lastIndexOf("\n") -1;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    sqlAux.append("\n");
                    sqlAux.append(blanks);
                    sqlAux.append(aux);
                    if(i != 0){
                        lengthBlanks = oldLengthBlanks;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    i += 4;
                    consumed = true;
                }
                
                //Identifica GROUP BY
                if(i+8<sqlImput.length())
                    aux  = sqlImput.substring(i, i+8);
                //System.out.println(aux);
                if(aux.equalsIgnoreCase("GROUP BY")){
                    
                    if(parentheses != 0){//Calcula nova indentação
                        oldLengthBlanks = lengthBlanks;
                        lengthBlanks = sqlAux.toString().toUpperCase().lastIndexOf("WHERE") - sqlAux.lastIndexOf("\n") -1;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    sqlAux.append("\n");
                    sqlAux.append(blanks);
                    sqlAux.append(aux);
                    if(i != 0){
                        lengthBlanks = oldLengthBlanks;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    i += 7;
                    consumed = true;
                }
                
                //Consome um caractere caso não tenha sido consumida nenhuma palavra-chave
                if((!consumed) && (i < length)){
                    char charAt = sqlImput.charAt(i);
                    sqlAux.append(charAt);
                    if(charAt == '(')
                        parentheses++;
                    if(charAt == ')')
                        parentheses--;
                    
                }
                aux = "";
                consumed = false;
                
                i++;
            }
            sqlOutput = sqlAux.toString();
        }
        return sqlOutput;
    }
    
    public static final String unindent(String sql){
        sql = sql.replace("\n", " \n");
        sql = sql.replace("\n", "");
        sql = removeWhiteSpace(sql);
        return sql;
    }
    
    private static final String removeWhiteSpace(String string){
        String padrao = "\\s{2,}";
        Pattern regPat = Pattern.compile(padrao);
        Matcher matcher = regPat.matcher(string);
        String res = matcher.replaceAll(" ").trim();
        return res;
    }
    
    
    //TERMINAR DE IMPLEMENTAR RECURSIVIDADE
    public static final String ident2(String sql, String blanks){
        if((sql != null) && (!sql.isEmpty())){
            String aux = "";
            StringBuffer sqlAux = new StringBuffer();
            
            int length = sql.length();
            int i =0,lengthBlanks, parentheses= 0;
            while ((i < length) && (i>=0)) {
                
                if(i+6<sql.length())
                    aux  = sql.substring(i, i+6);
                if(aux.equalsIgnoreCase("SELECT")){
                    if(i != 0){//Calcula nova indentação
                        //oldLengthBlanks = lengthBlanks;
                        lengthBlanks = sqlAux.length() - sqlAux.lastIndexOf("\n") - 1;
                        blanks = Indenter.getBlanks(lengthBlanks);
                    }
                    sqlAux.append(aux);
                    System.out.println("1:" + sql.substring(i+6));
                    String ident2 = ident2(sql.substring(i+6),blanks);
                    System.out.println("2:" + ident2);
                    sqlAux.append(ident2);
                    i += ident2.length();
                }
                
                //Identifica FROM
                if(i+4<sql.length())
                    aux  = sql.substring(i, i+4);
                if(aux.equalsIgnoreCase("FROM")){
                    
                    sqlAux.append("\n");
                    sqlAux.append(blanks);
                    sqlAux.append(aux);
                    i += 4;
                }
                
                if((i < length)){
                    char charAt = sql.charAt(i);
                    sqlAux.append(charAt);
                    if(charAt == '(')
                        parentheses++;
                    if(charAt == ')')
                        parentheses--;
                }
                
                i++;
                
            }
            sql = sqlAux.toString();
        }
        return sql;
    }
    
    private static final String getBlanks(int length){
        StringBuilder blanks = new StringBuilder();
        //length *= 1.5;
        if(length>0)
            for (int i = 0; i < length; i++) {
                blanks.append(" ");
            }
        
        return blanks.toString();
    }
    
}
