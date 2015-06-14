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
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public abstract class Reactor extends Agent implements IReactor {

    ArrayList<MaterializedView> MVCandiates;

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastExecutedDDL();
                this.CreateMV();
                this.updateDDLForMaterialization();
                sleep(4000);
            } catch (InterruptedException e) {
                log.errorPrint(e);
            }
        }
    }

    public void getLastExecutedDDL() {
        this.getDDLNotAnalized();
    }

    public void CreateMV() {
        PreparedStatement preparedStatement;
        for (MaterializedView workload : this.MVCandiates) {
            if (!workload.getHypoMaterializedView().isEmpty()) {
                log.ddlPrint("Materializando: " + workload.getHypoMaterializedView());
                preparedStatement = driver.prepareStatement(workload.getHypoMaterializedView());
                driver.executeUpdate(preparedStatement);
            }
        }
    }

    public void updateDDLForMaterialization() {
        try {
            log.title("Persist update ddl create MV");
            for (MaterializedView currentQuery : this.MVCandiates) {
                PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                preparedStatement.setLong(1, currentQuery.getId());
                driver.executeUpdate(preparedStatement);
                log.msgPrint(currentQuery.getHypoMaterializedView());
                log.dmlPrint(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
            }
            log.endTitle();
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

}
