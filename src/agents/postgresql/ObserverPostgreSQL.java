/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.postgresql;

import agents.ObserverMV;
import agents.interfaces.IObserverMV;
import drivers.postgresql.DriverPostgreSQL;
import drivers.postgresql.QueriesPostgreSQL;
import java.util.ArrayList;

public class ObserverPostgreSQL extends ObserverMV implements IObserverMV {

    public ObserverPostgreSQL() {
        super();
        driver = new DriverPostgreSQL();
        this.queries = new QueriesPostgreSQL();
        this.capturedQueriesWorkload = new ArrayList<>();
    }

}
