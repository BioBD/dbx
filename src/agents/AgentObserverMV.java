/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import algorithms.mv.Agrawal;
import algorithms.mv.DefineView;
import algorithms.mv.DefineViewSQLServer;
import base.MaterializedView;
import bib.base.Base;
import bib.driver.Driver;
import bib.sgbd.Observer;
import bib.sgbd.SQL;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public abstract class AgentObserverMV extends Base implements Runnable {

    private Driver driver;
    private final Observer observer;
    ArrayList<MaterializedView> MVCandiates;

    public AgentObserverMV() {
        this.observer = new Observer();
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.analyzeQueriesCaptured();
                sleep(200);
            } catch (InterruptedException e) {
                log.errorPrint(e);
            }
        }
    }

    private void analyzeQueriesCaptured() {
        ArrayList<SQL> sql = observer.getLastExecutedSQL();
        this.executeAgrawal(sql);
        this.executeDefineView();
        this.getPlanDDLViews();
        this.persistDDLCreateMV();
    }

    public void executeAgrawal(ArrayList<SQL> sql) {
        Agrawal agrawal = new Agrawal();
        this.capturedQueriesForAnalyses = agrawal.getWorkloadSelected(sql);
    }

    public void executeDefineView() {
        DefineView defineView;
        switch (prop.getProperty("database")) {
            case "sqlserver":
                defineView = new DefineViewSQLServer();
                break;
            default:
                defineView = new DefineView();

        }
        this.capturedQueriesForAnalyses = defineView.getWorkloadSelected(this.capturedQueriesForAnalyses);
    }

    public void getPlanDDLViews() {
        driver.createStatement();
        SQL query;
        ArrayList<SQL> tempQueryForAnalyses = new ArrayList<>();
        tempQueryForAnalyses.addAll(this.capturedQueriesForAnalyses);
        this.capturedQueriesForAnalyses.clear();
        for (int i = 0; i < tempQueryForAnalyses.size(); ++i) {
            query = tempQueryForAnalyses.get(i);
            System.out.println("antes da consulta: " + query.getSql());
            System.out.println("PLANO: " + this.getPlanQuery(query.getHypoMaterializedView()));
            System.exit(0);
            query.setHypoPlan(this.getPlanQuery(query.getHypoMaterializedView()));
            if (query.isValidHypoView()) {
                this.capturedQueriesForAnalyses.add(query);
                log.msgPrint("Query valida: " + query.getSql(), this.getClass().toString());
            } else {
                log.msgPrint("Query descartada: " + query.getSql(), this.getClass().toString());
            }
        }
    }

    public void persistDDLCreateMV() {
        try {
            if (this.capturedQueriesForAnalyses.size() > 0) {
                log.title("Persist ddl create MV", this.getClass().toString());
                for (MaterializedView query : this.capturedQueriesForAnalyses) {
                    log.msgPrint(query.getComents(), this.getClass().toString());
                    if (!query.getHypoMaterializedView().isEmpty()) {
                        if (query.getAnalyze_count() == 0) {
                            String ddlCreateMV = query.getDDLCreateMV();
                            System.out.println(ddlCreateMV);
                            System.exit(0);
                            PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToInsertDDLCreateMV());
                            log.dmlPrint(this.queries.getSqlClauseToInsertDDLCreateMV(), this.getClass().toString());
                            preparedStatement.setInt(1, query.getId());
                            preparedStatement.setString(2, ddlCreateMV);
                            preparedStatement.setLong(3, query.getHypoCreationCost());
                            preparedStatement.setDouble(4, query.getHypoGainAC());
                            preparedStatement.setString(5, "H");
                            preparedStatement.setInt(6, query.getId());
                            query.setAnalyze_count(1);
                            driver.executeUpdate(preparedStatement);
                        } else {
                            PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToIncrementBenefictDDLCreateMV());
                            log.dmlPrint(this.queries.getSqlClauseToIncrementBenefictDDLCreateMV(), this.getClass().toString());
                            BigDecimal temp = new BigDecimal(query.getHypoCreationCost());
                            preparedStatement.setBigDecimal(1, temp);
                            preparedStatement.setDouble(2, query.getHypoGainAC());
                            preparedStatement.setInt(3, query.getId());
                            driver.executeUpdate(preparedStatement);
                        }
                    }
                }
                log.endTitle(this.getClass().toString());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    protected void updateQueryAnalizedCount() {
        PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToUpdateWldAnalyzeCount());
        log.dmlPrint(this.queries.getSqlClauseToUpdateWldAnalyzeCount(), this.getClass().toString());
        driver.executeUpdate(preparedStatement);
    }

}
