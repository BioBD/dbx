/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import static agents.Agent.driver;
import agents.interfaces.IAgent;
import base.Base;
import static base.Base.log;
import base.MaterializedVision;
import base.Queries;
import drivers.Driver;
import drivers.Schema;
import drivers.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Agent extends Base implements IAgent {

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
            this.resultset = driver.executeQuery(this.queries.getSqlTableNames());
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

    @Override
    public String getPlanQuery(String query) {
        String partitionedPlan = "";
        System.out.println(query);
        try {
            PreparedStatement preparedStatement;
            preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToGetThePlan(query));
            try (ResultSet result = driver.executeQuery(preparedStatement)) {
                while (result.next()) {
                    partitionedPlan += "\n" + result.getString(1);
                }
            }
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(this.queries.getSqlClauseToGetThePlan(query), this.getClass().toString());
            log.errorPrint(e, this.getClass().toString());
        }
        return partitionedPlan.trim();
    }

}
