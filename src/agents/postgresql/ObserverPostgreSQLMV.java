/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.postgresql;

import agents.ObserverMV;
import agents.interfaces.IObserverMV;
import drivers.postgresql.DriverPostgreSQL;
import drivers.postgresql.QueriesPostgreSQL;

public class ObserverPostgreSQLMV extends ObserverMV implements IObserverMV {

    public ObserverPostgreSQLMV() {
        super();
        driver = new DriverPostgreSQL();
        this.queries = new QueriesPostgreSQL();
    }

}
