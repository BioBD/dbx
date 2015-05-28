/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents.sqlserver;

import agents.Reactor;
import drivers.sqlserver.DriverSQLServer;
import drivers.sqlserver.QueriesSQLServer;

/**
 *
 * @author Rafael
 */
public class ReactorSQLServerMV extends Reactor {

    public ReactorSQLServerMV() {
        driver = new DriverSQLServer();
        this.queries = new QueriesSQLServer();
    }

    @Override
    public void getDDLNotAnalized() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
