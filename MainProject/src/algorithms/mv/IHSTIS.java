/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.mv;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author josemariamonteiro
 */
public class IHSTIS {
    
    //Verificar se isso eh elegante
    public static void runAlg(ResultSet resultset){
        try {
            IHSTIS_CI.runAlg(resultset);//Algoritmo 2 do paper
            IHSTIS_RI.runAlg(resultset);//Algoritmo 3 do paper
            //Verificar se a clausula SQL eh um Insert, Update or Delete
            if (!resultset.getString("wld_type").equals("Q")){
                IHSTIS_U.runAlg(resultset);//Algoritmo 4 do paper
            }
        } catch (SQLException ex) {
            Logger.getLogger(IHSTIS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
