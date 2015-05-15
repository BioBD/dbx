/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.sqlserver;

import agents.ObserverMV;
import agents.interfaces.IObserverMV;
import static base.Base.log;
import base.MaterializedView;
import drivers.sqlserver.DriverSQLServer;
import drivers.sqlserver.MaterializedViewSQLServer;
import drivers.sqlserver.QueriesSQLServer;
import java.sql.SQLException;

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

    @Override
    public void getQueriesNotAnalized() {
        try {
            driver.createStatement();
            this.capturedQueriesForAnalyses.clear();
            this.resultset = driver.executeQuery(this.queries.getSqlQueriesNotAnalized());
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    MaterializedView currentQuery = new MaterializedViewSQLServer();
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
