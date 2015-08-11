/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.interfaces.IObserver;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.sgbd.Captor;
import bib.sgbd.SQL;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public abstract class AgentObserver extends Agent implements IObserver {

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
                log.errorPrint(e);
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
                log.dmlPrint(query.getSql());
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
            log.errorPrint(e.getMessage());
            return 1;
        }
    }

    private void insertQueryTbWorkload(SQL query) {
        try {
            try (PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToInsertQueryTbWorkload"))) {
                log.dmlPrint("Query: " + query.getSql());
                log.dmlPrint("Query: " + query.getPlan());
                log.dmlPrint("Query: " + query.getType());
                preparedStatement.setString(1, query.getSql());
                preparedStatement.setString(2, query.getPlan());
                preparedStatement.setInt(3, 1);
                preparedStatement.setInt(4, 0);
                preparedStatement.setInt(5, 0);
                preparedStatement.setString(6, query.getType());
                driver.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    private void updateQueryTbWorkload(SQL query) {
        try {
            try (PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateQueryTbWorkload"))) {
                log.dmlPrint(prop.getProperty("getSqlClauseToUpdateQueryTbWorkload") + " value of " + query.getSql());
                preparedStatement.setString(1, query.getPlan());
                preparedStatement.setInt(2, query.getId());
                driver.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    public void getLastExecutedSQL() {
        ArrayList<SQL> sqlCaptured = captor.getLastExecutedSQL();
        this.insertWorkload(sqlCaptured);
    }

    protected void updateQueryAnalizedCount() {
        PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        log.dmlPrint(prop.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        driver.executeUpdate(preparedStatement);
    }

    public String getPlanFromQuery(String query) {
        return captor.getPlanExecution(query);
    }

}
