/*
 * Automatic Creation Materialized Views
 *    *
 */
package agents;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import mv.MaterializedView;

/**
 *
 * @author Rafael
 */
public class AgentReactorMV extends AgentReactor {

    ArrayList<MaterializedView> MVCandiates;
    protected ArrayList<MaterializedView> capturedQueriesForAnalyses;

    public AgentReactorMV() {
        this.capturedQueriesForAnalyses = new ArrayList<>();
        this.MVCandiates = new ArrayList<>();
    }

    @Override
    public void getLastTuningActionsNotAnalyzed() {
        this.getDDLNotAnalized();
        //TODO: Zé
    }

    public void getDDLNotAnalized() {
        try {
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlDDLNotAnalizedReactor"));
            if (resultset != null) {
                while (resultset.next()) {
                    MaterializedView currentQuery = new MaterializedView();
                    currentQuery.setResultSet(resultset);
                    MVCandiates.add(currentQuery);
                    currentQuery.setResultSet(resultset);
                    this.capturedQueriesForAnalyses.add(currentQuery);
                }
            }
            log.msg("Quantidade de DDLs encontradas para materialização: " + this.capturedQueriesForAnalyses.size());
            resultset.close();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Override
    public void executeTuningActions() {
        PreparedStatement preparedStatement;
        for (MaterializedView workload : this.MVCandiates) {
            if (!workload.getHypoMaterializedView().isEmpty()) {
                try {
                    log.msg("Materializando: " + workload.getHypoMaterializedView());
                    preparedStatement = driver.prepareStatement(workload.getHypoMaterializedView());
                    driver.executeUpdate(preparedStatement);
                    preparedStatement.close();
                } catch (SQLException ex) {
                    log.error(ex);
                }
            }
        }
    }

    @Override
    public void updateStatusTuningActions() {
        try {
            log.title("Persist update ddl create MV");
            for (MaterializedView currentQuery : this.MVCandiates) {
                PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                preparedStatement.setString(1, "R");
                preparedStatement.setInt(2, currentQuery.getId());
                driver.executeUpdate(preparedStatement);
                preparedStatement.close();
            }
            log.endTitle();
        } catch (SQLException e) {
            log.error(e);
        }
    }

}
