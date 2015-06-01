/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents.sqlserver;

import agents.Reactor;
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
public class ReactorSQLServerMV extends Reactor {

    public ReactorSQLServerMV() {
        driver = new DriverSQLServer();
        this.queries = new QueriesSQLServer();
    }

    @Override
    public void getDDLNotAnalized() {
        try {
            driver.createStatement();
            this.capturedQueriesForAnalyses.clear();
            this.resultset = driver.executeQuery(this.queries.getSqlDDLNotAnalizedReactor());
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    MaterializedView currentQuery = new MaterializedViewSQLServer();
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
