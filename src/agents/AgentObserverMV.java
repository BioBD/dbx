/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import algorithms.mv.Agrawal;
import algorithms.mv.DefineView;
import algorithms.mv.DefineViewSQLServer;
import base.MaterializedView;
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
public class AgentObserverMV extends Agent implements Runnable {

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
        this.MVCandiates = agrawal.getWorkloadSelected(sql);
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
        this.MVCandiates = defineView.getWorkloadSelected(this.MVCandiates);
    }

    public void getPlanDDLViews() {
        driver.createStatement();
        MaterializedView mvTemp;
        ArrayList<MaterializedView> tempQueryForAnalyses = new ArrayList<>();
        tempQueryForAnalyses.addAll(this.MVCandiates);
        this.MVCandiates.clear();
        for (int i = 0; i < tempQueryForAnalyses.size(); ++i) {
            mvTemp = tempQueryForAnalyses.get(i);
            System.out.println("antes da consulta: " + mvTemp.getSql());
            System.out.println("PLANO: " + observer.getPlanExecution(mvTemp.getHypoMaterializedView()));
            System.exit(0);
            mvTemp.setHypoPlan(observer.getPlanExecution(mvTemp.getHypoMaterializedView()));
            if (mvTemp.isValidHypoView()) {
                this.MVCandiates.add(mvTemp);
                log.msgPrint("Query valida: " + mvTemp.getSql());
            } else {
                log.msgPrint("Query descartada: " + mvTemp.getSql());
            }
        }
    }

    public void persistDDLCreateMV() {
        try {
            log.title("Persist ddl create MV");
            for (MaterializedView query : this.MVCandiates) {
                log.msgPrint(query.getComents());
                if (!query.getHypoMaterializedView().isEmpty()) {
                    if (query.getAnalyze_count() == 0) {
                        String ddlCreateMV = query.getDDLCreateMV();
                        System.out.println(ddlCreateMV);
                        System.exit(0);
                        PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToInsertDDLCreateMV"));
                        log.dmlPrint(prop.getProperty("getSqlClauseToInsertDDLCreateMV"));
                        preparedStatement.setInt(1, query.getId());
                        preparedStatement.setString(2, ddlCreateMV);
                        preparedStatement.setLong(3, query.getHypoCreationCost());
                        preparedStatement.setDouble(4, query.getHypoGainAC());
                        preparedStatement.setString(5, "H");
                        preparedStatement.setInt(6, query.getId());
                        query.setAnalyze_count(1);
                        driver.executeUpdate(preparedStatement);
                    } else {
                        PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
                        log.dmlPrint(prop.getProperty("getSqlClauseToIncrementBenefictDDLCreateMV"));
                        BigDecimal temp = new BigDecimal(query.getHypoCreationCost());
                        preparedStatement.setBigDecimal(1, temp);
                        preparedStatement.setDouble(2, query.getHypoGainAC());
                        preparedStatement.setInt(3, query.getId());
                        driver.executeUpdate(preparedStatement);
                    }
                }
                log.endTitle();
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    protected void updateQueryAnalizedCount() {
        PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        log.dmlPrint(prop.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        driver.executeUpdate(preparedStatement);
    }

}
