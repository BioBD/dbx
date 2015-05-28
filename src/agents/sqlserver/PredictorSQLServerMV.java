/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents.sqlserver;

import agents.PredictorMV;
import drivers.sqlserver.DriverSQLServer;
import drivers.sqlserver.QueriesSQLServer;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class PredictorSQLServerMV extends PredictorMV {

    public PredictorSQLServerMV() {
        super();
        this.itemsBag = new ArrayList<>();
        this.idDDLForMaterialization = new ArrayList<>();
        driver = new DriverSQLServer();
        this.queries = new QueriesSQLServer();
    }

}
