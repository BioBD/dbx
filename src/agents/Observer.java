/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import static agents.Agent.driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public abstract class Observer extends Agent {

    protected ArrayList<String> capturedQueriesWorkload;
    protected ArrayList<String> lastcapturedQueries;

    public void getLastExecutedQueries() {
        this.captureQueries();
        this.removeLastQueries();
        this.insertWorkload();
    }

    public Observer() {
        this.capturedQueriesWorkload = new ArrayList<>();
        this.lastcapturedQueries = new ArrayList<>();
    }

    public void captureQueries() {
        driver.createStatement();
        this.resultset = driver.executeQuery(this.queries.getSqlClauseToCaptureCurrentQueries(this.propertiesFile.getProperty("databaseName")));
        this.processQueries();
        driver.closeStatement();
    }

    public void processQueries() {
        try {
            while (this.resultset.next()) {
                String currentQuery = this.resultset.getString("sql");
                if (this.isQueryValid(currentQuery)) {
                    this.capturedQueriesWorkload.add(currentQuery);
                }
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public boolean isQueryValid(String query) {
        boolean isValid = true;
        if ((this.isQueryGeneratedByACMV(query))
                || (this.isQueryGeneratedBySGBD(query))
                || (this.isNoQuerySelect(query))) {
            isValid = false;
        }
        return isValid;
    }

    public boolean isQueryGeneratedBySGBD(String query) {
        boolean isCommand = false;
        ArrayList<String> wordsBySGBD = new ArrayList<>();
        wordsBySGBD.add("vacuum");
        wordsBySGBD.add("analyze");
        wordsBySGBD.add("pg_type");
        wordsBySGBD.add("pg_namespace");
        wordsBySGBD.add("tb_candidate_view");
        wordsBySGBD.add("db.dattablespace");
        wordsBySGBD.add("tb_workload");
        wordsBySGBD.add("pg_catalog");
        wordsBySGBD.add("pg_attribute");
        wordsBySGBD.add("pg_roles");
        wordsBySGBD.add("news");
        wordsBySGBD.add("captured");
        wordsBySGBD.add("pg_stat_activity");
        wordsBySGBD.add("EXPLAIN");

        for (String word : wordsBySGBD) {
            if (query.toLowerCase().contains(word)) {
                isCommand = true;
            }
        }
        return isCommand;
    }

    public boolean isQueryGeneratedByACMV(String query) {
        return query.contains(this.signatureToDifferentiate);
    }

    private boolean isNoQuerySelect(String query) {
        return !query.toLowerCase().contains("select");
    }

    public boolean isQueryAlreadyCaptured(String query) {
        int queryId = this.getIdWorkload(query);
        return queryId > 0;
    }

    public void insertWorkload() {
        String query;
        if (!this.capturedQueriesWorkload.isEmpty()) {
            log.title("Insert / update TB_WORKLOAD", this.getClass().toString());
            for (int i = 0; i < this.capturedQueriesWorkload.size(); ++i) {
                query = this.capturedQueriesWorkload.get(i);
                this.capturedQueriesWorkload.remove(i);
                int queryId = this.getIdWorkload(query);
                log.dmlPrint(query, this.getClass().toString());
                if (queryId == 0) {
                    this.insertQueryTbWorkload(query);
                } else {
                    this.updateQueryData(queryId, query);
                }
            }
            log.endTitle(this.getClass().toString());
        }
    }

    public void insertQueryTbWorkload(String query) {
        try {
            String plan = this.getPlanQuery(query);
            try (PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToInsertQueryTbWorkload())) {
                log.dmlPrint(this.queries.getSqlClauseToInsertQueryTbWorkload() + " value of " + query, this.getClass().toString());
                preparedStatement.setString(1, query);
                preparedStatement.setString(2, plan);
                preparedStatement.setInt(3, 1);
                preparedStatement.setInt(4, 0);
                preparedStatement.setString(5, this.getTypeQuery(query));
                driver.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public String getTypeQuery(String query) {
        String queryType = null;
        if (query.toLowerCase().contains("select")) {
            queryType = "Q";
        } else if (query.toLowerCase().contains("update")) {
            queryType = "U";
        }
        return queryType;
    }

    public void updateQueryData(int queryId, String query) {
        try {
            String plan = this.getPlanQuery(query);
            try (PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToUpdateQueryTbWorkload())) {
                log.dmlPrint(this.queries.getSqlClauseToUpdateQueryTbWorkload() + " value of " + query, this.getClass().toString());
                preparedStatement.setString(1, plan);
                preparedStatement.setInt(2, queryId);
                driver.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public int getIdWorkload(String query) {
        try {
            PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToCheckIfQueryIsAlreadyCaptured());
            preparedStatement.setString(1, query);
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result.next()) {
                return result.getInt("wld_id");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            log.errorPrint(e.getMessage(), this.getClass().toString());
            return 1;
        }
    }

    private void removeLastQueries() {
        for (int i = 0; i < this.lastcapturedQueries.size(); i++) {
            if (this.capturedQueriesWorkload.contains(this.lastcapturedQueries.get(i))) {
                this.capturedQueriesWorkload.remove(this.lastcapturedQueries.get(i));
            } else {
                this.lastcapturedQueries.remove(i);
            }
        }

        for (int i = 0; i < this.capturedQueriesWorkload.size(); i++) {
            if (!this.lastcapturedQueries.contains(this.capturedQueriesWorkload.get(i))) {
                this.lastcapturedQueries.add(this.capturedQueriesWorkload.get(i));
            }
        }

    }

}
