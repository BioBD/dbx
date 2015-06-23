/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import base.MaterializedView;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.sgbd.Captor;
import bib.sgbd.SQL;
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class AgentObserverMV extends Agent implements Runnable {

    private final Captor captor;
    ArrayList<MaterializedView> MVCandiates;

    public AgentObserverMV() {
        this.captor = new Captor();
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastExecutedSQL();
                this.analyzeQueriesCaptured();
                sleep(200);
            } catch (InterruptedException e) {
                log.errorPrint(e);
            }
        }
    }

    private void analyzeQueriesCaptured() {
        this.executeAgrawal();
//        this.executeDefineView();
    }
//

    public void executeAgrawal() {
        Agrawal agrawal = new Agrawal();
        this.capturedQueriesForAnalyses = agrawal.getWorkloadSelected(this.capturedQueriesForAnalyses);
    }
//
//    public void executeDefineView() {
//        DefineView defineView = new DefineView();
//        this.capturedQueriesForAnalyses = defineView.getWorkloadSelected(this.capturedQueriesForAnalyses);
//        this.getPlanDDLViews();
//        this.persistDDLCreateMV();
//    }
//
//    public void getPlanDDLViews() {
//        driver.createStatement();
//        MaterializedView query;
//        ArrayList<MaterializedView> tempQueryForAnalyses = new ArrayList<>();
//        tempQueryForAnalyses.addAll(this.capturedQueriesForAnalyses);
//        this.capturedQueriesForAnalyses.clear();
//        for (int i = 0; i < tempQueryForAnalyses.size(); ++i) {
//            query = tempQueryForAnalyses.get(i);
//            query.setHypoPlan(this.getPlanQuery(query.getHypoMaterializedView()));
//            if (query.isValid()) {
//                this.capturedQueriesForAnalyses.add(query);
//                log.msgPrint("Query valida: " + query.getSql(), this.getClass().toString());
//            } else {
//                log.msgPrint("Query descartada: " + query.getSql(), this.getClass().toString());
//            }
//        }
//    }
//
//    public void persistDDLCreateMV() {
//        try {
//            if (this.capturedQueriesForAnalyses.size() > 0) {
//                log.title("Persist ddl create MV", this.getClass().toString());
//                for (MaterializedView query : this.capturedQueriesForAnalyses) {
//                    log.msgPrint(query.getComents(), this.getClass().toString());
//                    if (!query.getHypoMaterializedView().isEmpty()) {
//                        if (query.getAnalyze_count() == 0) {
//                            String ddlCreateMV = this.queries.getSqlClauseToCreateMV(query.getHypoMaterializedView(), query.getNameMaterizedView());
//                            PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToInsertDDLCreateMV());
//                            log.dmlPrint(this.queries.getSqlClauseToInsertDDLCreateMV(), this.getClass().toString());
//                            preparedStatement.setInt(1, query.getId());
//                            preparedStatement.setString(2, ddlCreateMV);
//                            preparedStatement.setLong(3, query.getHypoCreationCost());
//                            preparedStatement.setDouble(4, query.getHypoGainAC());
//                            preparedStatement.setString(5, "H");
//                            preparedStatement.setInt(6, query.getId());
//                            query.setAnalyze_count(1);
//                            driver.executeUpdate(preparedStatement);
//                        } else {
//                            PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToIncrementBenefictDDLCreateMV());
//                            log.dmlPrint(this.queries.getSqlClauseToIncrementBenefictDDLCreateMV(), this.getClass().toString());
//                            BigDecimal temp = new BigDecimal(query.getHypoCreationCost());
//                            preparedStatement.setBigDecimal(1, temp);
//                            preparedStatement.setDouble(2, query.getHypoGainAC());
//                            preparedStatement.setInt(3, query.getId());
//                            driver.executeUpdate(preparedStatement);
//                        }
//                    }
//                }
//                log.endTitle(this.getClass().toString());
//            }
//        } catch (SQLException e) {
//            log.errorPrint(e, this.getClass().toString());
//        }
//    }
//
//    protected void updateQueryAnalizedCount() {
//        PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToUpdateWldAnalyzeCount());
//        log.dmlPrint(this.queries.getSqlClauseToUpdateWldAnalyzeCount(), this.getClass().toString());
//        driver.executeUpdate(preparedStatement);
//    }

    private void getLastExecutedSQL() {
        ArrayList<SQL> workload = captor.getLastExecutedSQL();
        this.MVCandiates.clear();
        for (SQL sql : workload) {
            this.MVCandiates.add(this.getNewMVCandidate(sql));
        }
    }

    private MaterializedView getNewMVCandidate(SQL sql) {
        try {
            driver.createStatement();
            ResultSet result = driver.executeQuery(prop.getProperty("signature") + " EXPLAIN " + query + ";");
            while (result.next()) {
                partitionedPlan += "\n" + result.getString(1);
            }
        } catch (SQLException ex) {
            log.errorPrint(ex);
        }

        return null;
    }

}
