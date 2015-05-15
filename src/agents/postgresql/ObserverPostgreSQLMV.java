/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.postgresql;

import agents.ObserverMV;
import agents.interfaces.IObserverMV;
import static base.Base.log;
import base.MaterializedView;
import drivers.postgresql.DriverPostgreSQL;
import drivers.postgresql.MaterializedViewPostgreSQL;
import drivers.postgresql.QueriesPostgreSQL;
import java.sql.SQLException;

public class ObserverPostgreSQLMV extends ObserverMV implements IObserverMV {

    public ObserverPostgreSQLMV() {
        super();
        driver = new DriverPostgreSQL();
        this.queries = new QueriesPostgreSQL();
    }

    @Override
    public void getQueriesNotAnalized() {
        try {
            driver.createStatement();
            this.capturedQueriesForAnalyses.clear();
            this.resultset = driver.executeQuery(this.queries.getSqlQueriesNotAnalized());
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    MaterializedView currentQuery = new MaterializedViewPostgreSQL();
                    currentQuery.setResultSet(this.resultset);
                    currentQuery.setSchemaDataBase(this.schema);
                    this.capturedQueriesForAnalyses.add(currentQuery);
                }
                if (!this.capturedQueriesForAnalyses.isEmpty()) {
                    this.updateQueryAnalizedCount();
                }
            }
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}
