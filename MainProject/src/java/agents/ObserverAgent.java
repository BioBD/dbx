/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.algorithms.ActionMV;
import agents.algorithms.IHSTIS;
import agents.sgbd.Captor;
import agents.sgbd.SQL;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class ObserverAgent extends Agent {

    protected final Captor captor;
    protected final ActionMV controlMV;

    public ObserverAgent() {
        this.captor = new Captor();
        this.controlMV = new ActionMV();
    }

    @Override
    public void run() {
        log.msg("Execute agent " + this.getClass());
        while (true) {
            try {
                this.getLastExecutedSQL();
                this.evaluateFromAllTypesTuning();
                sleep(20);
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
            String queryTemp = config.getProperty("getSqlClauseToCheckIfQueryIsAlreadyCaptured");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            preparedStatement.setString(1, query.getSql());
            ResultSet result = connection.executeQuery(preparedStatement);
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
            try (PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToInsertQueryTbWorkload"))) {
                log.msg("SQL: " + query.getSql());
                log.msg("Plan: " + query.getPlan());
                log.msg("Type: " + query.getType());
                preparedStatement.setString(1, query.getSql());
                preparedStatement.setString(2, query.getPlan());
                preparedStatement.setInt(3, 1);
                preparedStatement.setInt(4, 0);
                preparedStatement.setInt(5, 0);
                preparedStatement.setString(6, query.getType());
                connection.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private void updateQueryTbWorkload(SQL query) {
        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToUpdateQueryTbWorkload"))) {
                log.msg(config.getProperty("getSqlClauseToUpdateQueryTbWorkload") + " value of " + query.getSql());
                preparedStatement.setString(1, query.getPlan());
                preparedStatement.setInt(2, query.getId());
                connection.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public void getLastExecutedSQL() {
        ArrayList<SQL> sqlCaptured = captor.getLastExecutedSQL();
        this.insertWorkload(sqlCaptured);
    }

    private void updateQueryAnalizedCount() {
        PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("signature") + config.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
//        log.msg(config.getProperty("signature") + config.getProperty("getSqlClauseToUpdateWldAnalyzeCount"));
        connection.executeUpdate(preparedStatement);
    }

    public void evaluateMV(ArrayList<SQL> sqlList) {
        controlMV.evaluateMV(sqlList);
    }

    public void evaluateIndexes(ArrayList<SQL> sqlList) {
        for (SQL sql : sqlList) {
            IHSTIS ihstis = new IHSTIS();
            ihstis.runAlg(sql);
        }
    }

    private void evaluateFromAllTypesTuning() {
        ArrayList<SQL> sqlList = captor.getWorkloadFromTBWorkload();
        if (config.getProperty("indexActive").equals("1")) {
            this.evaluateIndexes(sqlList);
        }
        if (config.getProperty("materializedViewActive").equals("1")) {
            this.evaluateMV(sqlList);
        }
        this.updateQueryAnalizedCount();
    }
}
