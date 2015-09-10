/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.interfaces.IObserver;
import algorithms.mv.Agrawal;
import algorithms.mv.DefineView;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.sgbd.Captor;
import bib.sgbd.SQL;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import mv.MaterializedView;

/**
 *
 * @author Rafael
 */
public class AgentObserver extends Agent implements IObserver {

    protected final Captor captor;

    public AgentObserver() {
        this.captor = new Captor();
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastExecutedSQL();
                this.analyzeQueriesCaptured();
                sleep(2000);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    private void insertWorkload(ArrayList<SQL> sqlCaptured) {
        if (!sqlCaptured.isEmpty()) {
            log.title("Insert / update TB_WORKLOAD");
            for (int i = 0; i < sqlCaptured.size(); ++i) {
                SQL query = sqlCaptured.get(i);
                sqlCaptured.remove(i);
                int queryId = this.getIdWorkload(query);
                log.msg(query.getSql());
                if (queryId == 0) {
                    this.insertQueryTbWorkload(query);
                } else {
                    query.setId(queryId);
                    this.updateQueryTbWorkload(query);
                }
            }
            log.endTitle();
        }
    }

    private int getIdWorkload(SQL query) {
        try {
            String queryTemp = prop.getProperty("getSqlClauseToCheckIfQueryIsAlreadyCaptured");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, query.getSql());
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result.next()) {
                int number = result.getInt("wld_id");
                result.close();
                return number;
            } else {
                result.close();
                return 0;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            return 1;
        }
    }

    private void insertQueryTbWorkload(SQL query) {
        try {
            try (PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToInsertQueryTbWorkload"))) {
                log.msg("SQL: " + query.getSql());
                log.msg("Plan: " + query.getPlan());
                log.msg("Type: " + query.getType());
                preparedStatement.setString(1, query.getSql());
                preparedStatement.setString(2, query.getPlan());
                preparedStatement.setInt(3, 1);
                preparedStatement.setInt(4, 0);
                preparedStatement.setInt(5, 0);
                preparedStatement.setString(6, query.getType());
                driver.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private void updateQueryTbWorkload(SQL query) {
        try {
            try (PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateQueryTbWorkload"))) {
                log.msg(prop.getProperty("getSqlClauseToUpdateQueryTbWorkload") + " value of " + query.getSql());
                preparedStatement.setString(1, query.getPlan());
                preparedStatement.setInt(2, query.getId());
                driver.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public void getLastExecutedSQL() {
        ArrayList<SQL> sqlCaptured = captor.getLastExecutedSQL();
        this.insertWorkload(sqlCaptured);
    }

    protected void updateQueryAnalizedCount() {
        PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        log.msg(prop.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        driver.executeUpdate(preparedStatement);
    }

    public String getPlanFromQuery(String query) {
        return captor.getPlanExecution(query);
    }

    @Override
    public void analyzeQueriesCaptured() {
        DefineView defineView = new DefineView();
        Agrawal agrawal = new Agrawal();
        ArrayList<MaterializedView> MVCandiates = this.getQueriesNotAnalizedForVMHypotetical();
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

    private ArrayList<MaterializedView> getQueriesNotAnalizedForVMHypotetical() {
        ArrayList<MaterializedView> MVCandiates = this.getQueriesNotAnalized();
        for (int i = 0; i < MVCandiates.size(); i++) {
            if (MVCandiates.get(i).getType().equals("Q")) {
                MVCandiates.remove(i);
            }
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
            if (MVCandiate.getType().equals("Q")) {
                MVCandiate.setHypoPlan(this.getPlanFromQuery(MVCandiate.getHypoMaterializedView()));
            }
        }
        return MVCandiates;
    }
}
