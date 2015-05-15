/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.sqlserver;

import agents.ObserverMV;
import agents.interfaces.IObserverMV;
import drivers.sqlserver.DriverSQLServer;
import drivers.sqlserver.QueriesSQLServer;

/**
 *
 * @author Rafael
 */
public class ObserverSQLServerMV extends ObserverMV implements IObserverMV {

    public ObserverSQLServerMV() {
        super();
        driver = new DriverSQLServer();
        this.queries = new QueriesSQLServer();
    }

}
