/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import agents.interfaces.IReactor;
import base.MaterializedView;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public abstract class Reactor extends Agent implements IReactor {

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastExecutedDDL();
                this.CreateMV();
                this.updateDDLForMaterialization();
                sleep(4000);
            } catch (InterruptedException e) {
                log.errorPrint(e, this.getClass().toString());
            }
        }
    }

    public void getLastExecutedDDL() {
        this.getDDLNotAnalized();
    }

    public void CreateMV() {
        PreparedStatement preparedStatement;
        for (MaterializedView workload : this.capturedQueriesForAnalyses) {
            if (!workload.getHypoMaterializedView().isEmpty()) {
                log.ddlPrint("Materializando: " + workload.getHypoMaterializedView(), this.getClass().toString());
                preparedStatement = driver.prepareStatement(workload.getHypoMaterializedView());
                driver.executeUpdate(preparedStatement);
            }
        }
    }

    public void updateDDLForMaterialization() {
        try {
            if (this.capturedQueriesForAnalyses.size() > 0) {
                log.title("Persist update ddl create MV", this.getClass().toString());
                for (MaterializedView currentQuery : this.capturedQueriesForAnalyses) {
                    PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToUpdateDDLCreateMVToMaterialization("R"));
                    preparedStatement.setLong(1, currentQuery.getId());
                    driver.executeUpdate(preparedStatement);
                    log.msgPrint(currentQuery.getHypoMaterializedView(), this.getClass().toString());
                    log.dmlPrint(this.queries.getSqlClauseToUpdateDDLCreateMVToMaterialization("M"), this.getClass().toString());
                }
                log.endTitle(this.getClass().toString());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}
