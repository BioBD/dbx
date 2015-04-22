/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.postgresql;

import agents.Reactor;
import drivers.postgresql.DriverPostgreSQL;
import drivers.postgresql.QueriesPostgreSQL;

/**
 *
 * @author Rafael
 */
public class ReactorPostgreSQL extends Reactor {

    public ReactorPostgreSQL() {
        driver = new DriverPostgreSQL();
        this.queries = new QueriesPostgreSQL();
    }

}
