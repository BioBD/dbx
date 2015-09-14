/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.mv;

import bib.sgbd.SQL;

/**
 *
 * @author josemariamonteiro
 */
public class IHSTIS {

    //Verificar se isso eh elegante
    public void runAlg(SQL sql) {
        IHSTIS_CI ci = new IHSTIS_CI();
        ci.runAlg(sql);//Algoritmo 2 do paper
        IHSTIS_RI ri = new IHSTIS_RI();
        ri.runAlg(sql);//Algoritmo 3 do paper
        //Verificar se a clausula SQL eh um Insert, Update or Delete
        if (!sql.getType().equals("Q")) {
            IHSTIS_U u = new IHSTIS_U();
            u.runAlg(sql);//Algoritmo 4 do paper
        }
    }
}
