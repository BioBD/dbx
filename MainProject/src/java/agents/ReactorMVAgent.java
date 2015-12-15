/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.sgbd.MaterializedView;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class ReactorMVAgent extends ReactorAgent {

    ArrayList<MaterializedView> MVCandiates;
    protected ArrayList<MaterializedView> capturedQueriesForAnalyses;

    public ReactorMVAgent() {
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
            this.MVCandiates.clear();
            ResultSet resultset = connection.executeQuery(config.getProperty("getSqlDDLNotAnalizedReactor"));
            if (resultset != null) {
                while (resultset.next()) {
                    MaterializedView currentQuery = new MaterializedView();
                    currentQuery.setResultSet(resultset);
                    MVCandiates.add(currentQuery);
                    currentQuery.setResultSet(resultset);
                    this.capturedQueriesForAnalyses.add(currentQuery);
                }
            }
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
                    log.msg("Materializing view: " + workload.getHypoMaterializedView());
                    preparedStatement = connection.prepareStatement(workload.getHypoMaterializedView());
                    connection.executeUpdate(preparedStatement);
                    preparedStatement.close();
                    log.msg("Finish materializing view: " + workload.getHypoMaterializedView());
                } catch (SQLException ex) {
                    log.error(ex);
                }
            }
        }
    }

    @Override
    public void updateStatusTuningActions() {
        try {
            for (MaterializedView currentQuery : this.MVCandiates) {
                PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterializationReactor"));
                preparedStatement.setString(1, "R");
                preparedStatement.setInt(2, currentQuery.getId());
                connection.executeUpdate(preparedStatement);
                preparedStatement.close();
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

}
