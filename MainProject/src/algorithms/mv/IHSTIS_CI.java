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
        
        //Usa a comando SQL para pegar os atributos envolvidos nas clausulas SEL:ECT, GROUP e ORDER
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
        
        return sso;  
    }

    private static ArrayList<Column> getSelectGroupOrderColumns(SQL query) {
        ArrayList<Column> cols = new ArrayList();
        
        return cols;
    }
    
    
}
