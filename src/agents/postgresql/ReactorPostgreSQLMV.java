/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.postgresql;

import agents.Reactor;
import static base.Base.log;
import base.MaterializedView;
import drivers.postgresql.DriverPostgreSQL;
import drivers.postgresql.MaterializedViewPostgreSQL;
import drivers.postgresql.QueriesPostgreSQL;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public class ReactorPostgreSQLMV extends Reactor {

    public ReactorPostgreSQLMV() {
        driver = new DriverPostgreSQL();
        this.queries = new QueriesPostgreSQL();
    }

    @Override
    public void getDDLNotAnalized() {
        try {
            driver.createStatement();
            this.capturedQueriesForAnalyses.clear();
            this.resultset = driver.executeQuery(this.queries.getSqlDDLNotAnalizedReactor());
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    MaterializedView currentQuery = new MaterializedViewPostgreSQL();
                    currentQuery.setResultSet(this.resultset);
                    currentQuery.setSchemaDataBase(this.schema);
                    this.capturedQueriesForAnalyses.add(currentQuery);
                }
            }
            log.msgPrint("Quantidade de DDLs encontradas para materialização: " + this.capturedQueriesForAnalyses.size(), this.getClass().toString());
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}