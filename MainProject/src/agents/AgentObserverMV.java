/*
 * Automatic Creation Materialized Views
 *    *
 */
package agents;

import algorithms.mv.Agrawal;
import algorithms.mv.DefineView;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import mv.MaterializedView;

/**
 *
 * @author Rafael
 */
public class AgentObserverMV extends AgentObserver {

    @Override
    public void analyzeQueriesCaptured() {
        DefineView defineView = new DefineView();
        Agrawal agrawal = new Agrawal();
        ArrayList<MaterializedView> MVCandiates = this.getQueriesNotAnalized();
        MVCandiates = agrawal.getWorkloadSelected(MVCandiates);
        MVCandiates = defineView.getWorkloadSelected(MVCandiates);
        MVCandiates = this.getPlanDDLViews(MVCandiates);
        this.persistDDLCreateMV(MVCandiates);
    }

    private ArrayList<MaterializedView> getQueriesNotAnalized() {
        ArrayList<MaterializedView> MVCandiates = new ArrayList<>();
        try {
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlQueriesNotAnalizedObserver"));
            if (resultset != null) {
                while (resultset.next()) {
                    MaterializedView currentQuery = new MaterializedView();
                    currentQuery.setResultSet(resultset);
                    currentQuery.setSchemaDataBase(captor.getSchemaDataBase());
                    MVCandiates.add(currentQuery);
                }
                if (!MVCandiates.isEmpty()) {
                    this.updateQueryAnalizedCount();
                }
            }
            resultset.close();
        } catch (SQLException e) {
            log.error(e);
        }
        return MVCandiates;
    }

    public void persistDDLCreateMV(ArrayList<MaterializedView> MVCandiates) {
        try {
            if (!MVCandiates.isEmpty()) {
                log.title("Persist ddl create MV");
                this.updateQueryAnalizedCount();
                for (MaterializedView mvQuery : MVCandiates) {
                    if (!mvQuery.getHypoMaterializedView().isEmpty()) {
                        mvQuery.print();
                        if (mvQuery.getAnalyzeCount() == 0) {
                            String ddlCreateMV = mvQuery.getDDLCreateMV();
                            String[] queries = prop.getProperty("getSqlClauseToInsertDDLCreateMV").split(";");
                            PreparedStatement preparedStatementInsert = driver.prepareStatement(queries[0]);
                            log.msg(prop.getProperty("getSqlClauseToInsertDDLCreateMV"));
                            preparedStatementInsert.setInt(1, mvQuery.getId());
                            preparedStatementInsert.setString(2, ddlCreateMV);
                            preparedStatementInsert.setLong(3, mvQuery.getHypoCreationCost());
                            preparedStatementInsert.setDouble(4, mvQuery.getHypoGainAC());
                            preparedStatementInsert.setString(5, "H");
                            mvQuery.setAnalyzeCount(1);
                            driver.executeUpdate(preparedStatementInsert);
                            PreparedStatement preparedStatementUpdate = driver.prepareStatement(queries[1]);
                            preparedStatementUpdate.setInt(1, mvQuery.getId());
                            driver.executeUpdate(preparedStatementUpdate);
                        } else {
                            PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
                            log.msg(prop.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
                            preparedStatement.setLong(1, mvQuery.getHypoCreationCost());
                            preparedStatement.setDouble(2, mvQuery.getHypoGainAC());
                            preparedStatement.setInt(3, mvQuery.getId());
                            driver.executeUpdate(preparedStatement);
                        }
                    }
                }
                log.endTitle();
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private ArrayList<MaterializedView> getPlanDDLViews(ArrayList<MaterializedView> MVCandiates) {
        for (MaterializedView MVCandiate : MVCandiates) {
            MVCandiate.setHypoPlan(this.getPlanFromQuery(MVCandiate.getHypoMaterializedView()));
        }
        return MVCandiates;
    }

}
