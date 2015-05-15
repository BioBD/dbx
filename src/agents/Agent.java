/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import static agents.Agent.driver;
import base.Base;
import static base.Base.log;
import base.MaterializedVision;
import base.Queries;
import drivers.Driver;
import drivers.Schema;
import drivers.Table;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Agent extends Base {

    protected static Driver driver;
    protected ResultSet resultset;
    protected Queries queries;
    protected Schema schema;
    protected ArrayList<MaterializedVision> capturedQueriesForAnalyses;

    public Agent() {
        super();
        this.schema = new Schema();
        this.capturedQueriesForAnalyses = new ArrayList<>();
    }

    public void getSchemaDataBase() {
        try {
            driver.createStatement();
            String query = this.queries.getSqlTableNames(this.propertiesFile.getProperty("databaseName"));
            this.resultset = driver.executeQuery(query);
            log.title("Criando o objeto Schema", this.getClass().toString());
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    Table currentTable = new Table();
                    currentTable.setName(this.resultset.getString(1));
                    currentTable.setFields(Arrays.asList(this.resultset.getString(2).split("\\s*,\\s*")));
                    this.schema.tables.add(currentTable);
                    log.msgPrint(currentTable.getName() + ": " + currentTable.getFields(), this.getClass().toString());
                }
            }
            log.endTitle(this.getClass().toString());
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public String getPlanQuery(String query) {
        return this.queries.getPlanExecution(driver, query).trim();
    }

}
