/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.mv;

import bib.sgbd.Column;
import bib.sgbd.Plan;
import bib.sgbd.SQL;
import bib.sgbd.SeqScan;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrador
 */
public class IHSTIS_CI {
    public static void runAlg(ResultSet resultset){
        Plan p = null;
        ArrayList<SeqScan> sso = null;
        SeqScan ss = null;
        SQL query = null;
        ArrayList<Column> cols = null;
        
        //Usa o Plano para pegar os atributos envolvidos em filtros
        try {
            p = new Plan(resultset.getString("wld_plan"));
        } catch (SQLException ex) {
            Logger.getLogger(IHSTIS_CI.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (p!=null){
            sso = getSeqScanOperations(p);
        }
        for (int i = 0; i < sso.size(); i++) {
            ss = sso.get(i);     
        }
        
        //Usa a comando SQL para pegar os atributos envolvidos nas clausulas SELECT, GROUP e ORDER
        try {
            query = new SQL();
            query.setSql(resultset.getString("wld_sql"));
        } catch (SQLException ex) {
            Logger.getLogger(IHSTIS_CI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (query!=null){
            cols = getSelectGroupOrderColumns(query);
        }
        
    }
    
    public static ArrayList<SeqScan> getSeqScanOperations(Plan p){
        ArrayList<SeqScan> sso = new ArrayList();
        ArrayList<Column> columns = new ArrayList();
        Column col;
        String plan, name = null;
        String[] cols, attributes;
 
        //Deixa o texto em caixa baixa e retira os espaços
        plan = ((p.getPlan()).toLowerCase()).replaceAll(" ", "");
        //Gera um vetor, tal que o seu tamanha é o número de SeqScan do plano
        String[] scan = plan.split("seqscanon");
        
        Pattern rest_name = Pattern.compile("(.*).cost");
        Pattern rest_cols = Pattern.compile("filter:(.*)");
        
        for(int i=1; i<scan.length; i++){
            Matcher nameM, str;
            
            nameM = rest_name.matcher(scan[i]);
            while(nameM.find()){
                name = nameM.group();
            }
            
            //Refinanco a string
            name = name.substring(0,(name.length()-5)); 
            
            //Matcher filter = rest_cols.matcher(plan);
            str = rest_cols.matcher(scan[i]);  
            while(str.find()){
                scan[i] = str.group();
            }

            //Trata o SeqScan atual
            scan[i] = (scan[i]).replaceAll("[(]|[)]| |filter:", "");
            scan[i] = (scan[i]).replaceAll("::\\w+,*", "");
            scan[i] = (scan[i]).replaceAll("and|or", ",");

            //Cria um array de atributos
            attributes = scan[i].split(",");
            rest_cols = Pattern.compile(".*[|]");

            //Armazena os atributos no ArrayList columns
            for(int j=0; j<attributes.length; j++){
                attributes[j] = attributes[j].replaceFirst("(>|<|=)", "|");
                str = rest_cols.matcher(attributes[j]);
                while(str.find()){
                    attributes[j] = str.group();
                }
                attributes[j]=(attributes[j]).replaceAll("[|]", "");
                col = new Column();
                col.setName(attributes[j]);
                columns.add(col);
            }
             //Monta um objeto e o adiciona no ArrayList<SecScan>
            SeqScan objSeq = new SeqScan(name, columns);
            sso.add(objSeq);
        }
        
        return sso;  
    }

    private static ArrayList<Column> getSelectGroupOrderColumns(SQL query) {
        ArrayList<Column> cols = new ArrayList();
        String sql = ((query.getSql()).toLowerCase()).replaceAll(" ", "");
        String[] attributes = null;
        String subStr = null;
        Column col;
        
        if(sql.matches("select")){  
            Pattern rest;
            Matcher str;
            //Cria-se uma restrição para seleção de uma parte do comando sql
            //Em seguida essa parte é armazenada na variavel chamada str
            rest = Pattern.compile("select(.*)from");
            str = rest.matcher(sql);

            //Transforma a variável str em uma string chamada substr
            while(str.find()){
                subStr = str.group();
            }
            //Pega apenas os atributos do SELECT
            subStr = subStr.substring(6,(subStr.length()-4)); 
            
            //Verifica se não é um SELECT genérico
            //Adiciona seus atributos em cols
            if(!(subStr=="*")){
                //Verifica se há uma virgula (neste caso há mais de um atributo
                if(subStr.matches(".*[,]*")){
                    attributes = subStr.split(",");
                    
                //Caso contrário há apenas um atributo.
                }else{
                    attributes[0] = subStr;
                }
                
                //Adiciona os atributos do SELECT no ArrayList cols
                for(int i=1 ; i<attributes.length ; i++){
                    col = new Column();
                    col.setName(attributes[i]);
                    cols.add(col);
                }
            }
 
            //Verifica se há um ORDER BY no comando sql
            //Adiciona seus atributos em cols
            if(sql.matches("(.*)orderby(.*)")){
                rest = Pattern.compile("orderby(.*)");
                str = rest.matcher(sql);          
                
                //Transforma a variável str em uma string chamada subStr
                while(str.find()){
                    subStr = str.group();
                }
                
                attributes = subStr.split("(orderby|asc,*|desc,*)");
                
                //Adiciona os atributos do ORDER BY no ArrayList cols            
                for(int i=1 ; i<attributes.length ; i++){
                    col = new Column();
                    col.setName(attributes[i]);
                    cols.add(col);
                }
            }    
            
            //Verifica se há um ORDER BY no comando sql
            //Adiciona seus atributos em cols
            if(sql.matches("(.*)groupby(.*)")){
                rest = Pattern.compile("groupby(.*)");
                str = rest.matcher(sql);          
                
                //Transforma a variável str em uma string chamada subStr
                while(str.find()){
                    subStr = str.group();
                }
                
                attributes = subStr.split("(groupby)|,");
                
                //Adiciona os atributos do ORDER BY no ArrayList cols            
                for(int i=1 ; i<attributes.length ; i++){
                    col = new Column();
                    col.setName(attributes[i]);
                    cols.add(col);
                }
            }
        }
        return cols;  
    }
}
