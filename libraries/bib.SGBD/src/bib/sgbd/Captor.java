/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bib.sgbd;
/*
 * Framework para apoiar a sintonia fina de banco de dados. PUC-RIO.
 * Ana Carolina Almeida [anacrl@gmail.com].
 * Rafael Pereira de Oliveira [rpoliveira@inf.puc-rio.br].
 * PUC-RIO - LABBIO - 2014.
 */

import bib.base.Base;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.driver.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafael
 */
public final class Captor extends Base {

    private final ArrayList<SQL> capturedSQL;
    private final ArrayList<SQL> lastcapturedSQL;
    protected Driver driver;
    private final Schema schema;

    public Captor() {
        super();
        this.capturedSQL = new ArrayList<>();
        this.lastcapturedSQL = new ArrayList<>();
        this.driver = new Driver();
        this.schema = new Schema();
        this.readSchemaDataBase();
    }

    private void readSchemaDataBase() {
        try {
            log.title("Reading schema database");
            String query = prop.getProperty("getSqlTableNames" + prop.getProperty("sgbd"));
            ResultSet schemaResult = driver.executeQuery(query);
            if (schemaResult != null) {
                while (schemaResult.next()) {
                    Table currentTable = new Table();
                    currentTable.setSchema(schemaResult.getString(1));
                    currentTable.setName(schemaResult.getString(2));
                    currentTable.setNumberRows(schemaResult.getInt(3));
                    switch (prop.getProperty("sgbd")) {
                        case "oracle":
                            int pagesize = Integer.valueOf(prop.getProperty("pagesize" + prop.getProperty("sgbd")));
                            int numPage = schemaResult.getInt(4) / pagesize;
                            currentTable.setNumberPages(numPage);
                            break;
                        default:
                            currentTable.setNumberPages(schemaResult.getInt(4));
                    }
                    currentTable.setFields(this.getColumns(currentTable.getSchema(), currentTable.getName()));
                    log.msg("Table: " + currentTable.getName());
                    log.msg("Fields: " + currentTable.getFieldsString());
                    schema.tables.add(currentTable);
                }
                schemaResult.close();
            }
            log.endTitle();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    public Schema getSchemaDataBase() {
        return this.schema;
    }

    private ArrayList<Column> getColumns(String schema, String tableName) {
        ArrayList<Column> result = new ArrayList<>();
        try {
            String sql = prop.getProperty("getSqlDetailsColumns" + prop.getProperty("sgbd"));
            sql = sql.replace("$schema$", schema);
            sql = sql.replace("$table$", tableName);
            PreparedStatement preparedStatement = driver.prepareStatement(sql);
            ResultSet fields = driver.executeQuery(preparedStatement);
            if (fields != null) {
                while (fields.next()) {
                    Column currentColumn = new Column();
                    currentColumn.setOrder(fields.getInt(1));
                    currentColumn.setName(fields.getString(2));
                    currentColumn.setTable(tableName);
                    currentColumn.setNotNull(fields.getBoolean(3));
                    currentColumn.setType(fields.getString(4));
                    currentColumn.setDomainRestriction(fields.getString(5));
                    currentColumn.setPrimaryKey(fields.getBoolean(6));
                    currentColumn.setUniqueKey(fields.getBoolean(7));
                    if (fields.getBoolean(8)) {
                        currentColumn.setForeignKey(this.getForeignKeyColumn(fields));
                    }
                    result.add(currentColumn);
                }
                fields.close();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return result;
    }

    private Column getForeignKeyColumn(ResultSet field) {
        Column foreignColumn = new Column();
        try {
            foreignColumn.setOrder(field.getInt(9));
            foreignColumn.setName(field.getString(10));
            foreignColumn.setTable(field.getString(11));
            foreignColumn.setType(field.getString(12));
            foreignColumn.setDomainRestriction("");
            foreignColumn.setNotNull(true);
        } catch (SQLException ex) {
            log.error(ex);
        }
        return foreignColumn;
    }

    public ArrayList<SQL> getLastExecutedSQL() {
        this.captureAndProcessQueries();
        return this.lastcapturedSQL;
    }

    private void captureAndProcessQueries() {
        try {
            String query = prop.getProperty("signature") + prop.getProperty("getSqlClauseToCaptureCurrentQueries" + prop.getProperty("sgbd"));
            PreparedStatement preparedStatement = driver.prepareStatement(query);
            preparedStatement.setString(1, prop.getProperty("databaseName"));
            ResultSet queriesResult = driver.executeQuery(preparedStatement);
            this.lastcapturedSQL.clear();
            while (queriesResult.next()) {
                String currentQuery = queriesResult.getString("sql");
                if (this.isQueryValid(currentQuery)) {
                    SQL sql = new SQL();
                    sql.setPid(queriesResult.getString("pid"));
                    sql.setDatabase(queriesResult.getString("database_name"));
                    sql.setTimeFirstCapture(queriesResult.getString("start_time"));
                    sql.setLastCapture();
                    sql.setSql(currentQuery);
                    sql.setSchemaDataBase(this.schema);
                    SQL temp = this.processAndgetCapturedSQLFromHistory(sql);
                    if (temp != null) {
                        this.lastcapturedSQL.add(temp);
                        this.insertQueryTableLog(temp);
                    }
                }
            }
            queriesResult.close();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    private SQL processAndgetCapturedSQLFromHistory(SQL sql) {
        for (SQL workload : capturedSQL) {
            if (sql.getSql().equals(workload.getSql())) {
                if (sql.getTimeFirstCapture().equals(workload.getTimeFirstCapture())) {
                    return null;
                } else {
                    workload.incrementCaptureCount(1);
                    workload.setTimeFirstCapture(sql.getTimeFirstCapture());
                    workload.setWaitAnalysis(true);
                    return workload;
                }
            }
        }
        sql.setPlan(this.getPlanExecution(sql.getSql()), prop.getProperty("sgbd"));
        sql.setSchemaDataBase(this.schema);
        sql.setCaptureCount(1);
        sql.setId(this.capturedSQL.size() + 1);
        sql.print();
        this.capturedSQL.add(sql);
        return sql;
    }

    private boolean isQueryValid(String query) {
        boolean isValid = true;
        if ((this.isQueryGeneratedByOuterTuning(query))
                || (this.isSQLGeneratedBySGBD(query))) {
            isValid = false;
        }
        return isValid;
    }

    private boolean isSQLGeneratedBySGBD(String query) {
        boolean isCommand = false;
        String[] wordsBySGBD = prop.getProperty("wordsBySGBD").split(";");
        for (String word : wordsBySGBD) {
            if (query.toLowerCase(Locale.getDefault()).contains(word)) {
                isCommand = true;
            }
        }
        return isCommand;
    }

    private boolean isQueryGeneratedByOuterTuning(String query) {
        return query.toLowerCase().contains(prop.getProperty("signature").toLowerCase());
    }

    private boolean isNoQuerySelect(String query) {
        return !query.toLowerCase(Locale.getDefault()).contains("select");
    }

    public ArrayList<SQL> getTopCapturedQuery() {
        SQL aux;
        for (int i = 0; i < this.capturedSQL.size(); i++) {
            for (int j = 0; j < this.capturedSQL.size() - 1; j++) {
                if (this.capturedSQL.get(j).getCaptureCount() > this.capturedSQL.get(j + 1).getCaptureCount()) {
                    aux = this.capturedSQL.get(j);
                    this.capturedSQL.set(j, this.capturedSQL.get(j + 1));
                    this.capturedSQL.set(j + 1, aux);
                }
            }
        }
        return this.capturedSQL;
    }

    public String getPlanExecution(String query) {
        switch (prop.getProperty("sgbd")) {
            case "postgresql":
                return this.getPlanExecutionPostgreSQL(query);
            case "sqlserver":
                return this.getPlanExecutionSQLServer(query);
            case "oracle":
                return this.getPlanExecutionOracle(query);
            default:
                return null;
        }

    }

    private String getPlanExecutionPostgreSQL(String query) {
        String partitionedPlan = "";
        if (!query.isEmpty()) {
            try {
                ResultSet result;
                if (prop.getProperty("executeExplainAnalyseForCaptureWorkload").equals("1")) {
                    result = driver.executeQuery(prop.getProperty("signature") + " EXPLAIN (ANALYZE TRUE, TIMING FALSE) " + query);
                } else {
                    result = driver.executeQuery(prop.getProperty("signature") + " EXPLAIN " + query);
                }
                while (result.next()) {
                    partitionedPlan += "\n" + result.getString(1);
                }
                result.close();
            } catch (SQLException ex) {
                log.msg(query);
                log.error(ex);
            }
        }
        return partitionedPlan;
    }

    private String getPlanExecutionSQLServer(String query) {
        String partitionedPlan = "";
        try {
            ResultSet resultset = this.getResultPlanQuerySQLServer(driver, query);
            while (resultset.next()) {
                partitionedPlan += " " + resultset.getString(1);
            }
            if (partitionedPlan.isEmpty()) {
                partitionedPlan = this.getResultEstimatedPlanQuerySQLServer(query);
            }
            resultset.close();
        } catch (SQLException ex) {
            log.error(ex);
        }
        return partitionedPlan;
    }

    private ResultSet getResultPlanQuerySQLServer(Driver driver, String query) {
        try {
            PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getResultPlanQuerySQLServer"));
            preparedStatement.setString(1, query);
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result != null) {
                return result;
            } else {
                log.msg("error");
            }
            result.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            log.error(ex);
        }
        return null;
    }

    private String getResultEstimatedPlanQuerySQLServer(String query) {
        String plan = "";
        try {
            Statement statement = driver.getStatement();
            statement.execute(prop.getProperty("signature") + "SET SHOWPLAN_TEXT OFF");
            statement.execute(prop.getProperty("signature") + "SET SHOWPLAN_XML ON");
            ResultSet resultset = statement.executeQuery(prop.getProperty("signature") + " " + query);
            while (resultset.next()) {
                plan += " " + resultset.getString(1);
            }
            statement.execute(prop.getProperty("signature") + "SET SHOWPLAN_XML OFF");
            statement.execute(prop.getProperty("signature") + "SET SHOWPLAN_TEXT OFF");
            resultset.close();
            statement.close();
        } catch (SQLException ex) {
            log.msg(ex);
        }
        return plan;
    }

    private String getPlanExecutionOracle(String query) {
        String partitionedPlan = "";
        if (!query.isEmpty()) {
            try {
                query = query.replace("'", "_");
                String temp = "SELECT * "
                        + "FROM v$sql s, v$sql_plan sp "
                        + "WHERE s.address = sp.address AND s.hash_value = sp.hash_value "
                        + "AND s.plan_hash_value = sp.plan_hash_value "
                        + "and s.sql_text like '" + query + "' AND ROWNUM <= 1 AND IO_COST is not null order by s.LAST_LOAD_TIME desc";
                ResultSet result = driver.executeQuery(temp);
                ResultSetMetaData rsmd = result.getMetaData();
                while (result.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        partitionedPlan += rsmd.getColumnName(i) + "=" + result.getString(i) + "\n";
                    }
                }
                if (partitionedPlan.trim().isEmpty()) {
                    partitionedPlan = this.getEstimatedPlanExecutionOracle(query);
                }
                result.close();
            } catch (SQLException ex) {
                log.msg(query);
                log.error(ex);
            }
        }
        return partitionedPlan;
    }

    private String getEstimatedPlanExecutionOracle(String query) {
        String partitionedPlan = "";
        if (!query.isEmpty()) {
            String queryExplain = "EXPLAIN PLAN SET STATEMENT_ID = 'dbx' for " + query;
            String queryGetPlan = " SELECT * FROM (SELECT * FROM plan_table CONNECT BY prior id = parent_id AND prior statement_id = statement_id START WITH id = 0 AND statement_id = 'dbx') p WHERE p.OPERATION = 'SELECT STATEMENT' AND ROWNUM <= 1";
            try {
                driver.executeUpdate(queryExplain);
            } catch (Error ex) {
                log.msg("Query com erro:" + queryExplain);
                log.error(ex);
            }
            try {
                ResultSet result = driver.executeQuery(queryGetPlan);
                ResultSetMetaData rsmd = result.getMetaData();
                while (result.next()) {
                    for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                        partitionedPlan += rsmd.getColumnName(i) + "=" + result.getString(i) + "\n";
                    }
                }
            } catch (SQLException ex) {
                log.msg("Query com erro:" + queryGetPlan);
                log.error(ex);
            }
        }
        return partitionedPlan;
    }

    public SQL getWorkloadFromAction(String command) {
        if (command.toLowerCase().contains("tpc_h test_view")) {
            int ini = command.toLowerCase().indexOf("tpc_h test_view") - 5;
            int end = command.toLowerCase().substring(ini).indexOf("*/") + 2 + ini;
            command = command.substring(ini, end);
        } else if (command.toLowerCase().contains("tpc-h/tpc-r")) {
            int ini = command.toLowerCase().indexOf("tpc-h/tpc-r") - 3;
            int end = command.toLowerCase().substring(ini).indexOf("*/") + 2 + ini;
            command = command.substring(ini, end);
        } else {
            for (SQL workload : capturedSQL) {
                if (command.toLowerCase().contains(workload.getClauseFromSql("select").toLowerCase())) {
                    return workload;
                }
            }
        }
        for (SQL workload : capturedSQL) {
            if (workload.getSql().toLowerCase().contains(command.toLowerCase())) {
                return workload;
            }
        }
        return null;
    }

    public ArrayList<SQL> getWorkloadFromTBWorkload() {
        ArrayList<SQL> sqlList = new ArrayList<>();
        try {
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlQueriesNotAnalizedObserver"));
            while (resultset.next()) {
                SQL sql = new SQL();
                sql.setResultSet(resultset);
                sqlList.add(sql);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Captor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sqlList;
    }

    //Calcula a fragmentação do índice
    private double getIndexFragmentation(String schema, String indexName) {
        String tableName = null;
        int numTuplesCurrent = 0, numPagesCurrent = 0;
        double fragmentation = 0, initialRatio = 0;
        try {
            //Obter o nome da tabela em que o índice for criado
            String sql = prop.getProperty("getDMLTableNameIndex" + prop.getProperty("sgbd"));
            sql = sql.replace("$schema$", schema);
            sql = sql.replace("$index$", indexName);
            PreparedStatement preparedStatement = driver.prepareStatement(sql);
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result != null) {
                while (result.next()) {
                    tableName = result.getString(1);
                }

                if (tableName != null) {
                    //Obter o numero atual de tuplas da tabela
                    sql = prop.getProperty("getDMLTableNumberTuples" + prop.getProperty("sgbd"));
                    sql = sql.replace("$schema$", schema);
                    sql = sql.replace("$table$", tableName);
                    preparedStatement = driver.prepareStatement(sql);
                    result = driver.executeQuery(preparedStatement);
                    if (result != null) {
                        while (result.next()) {
                            numTuplesCurrent = result.getInt(1);
                        }
                    }

                    //Obter o numero atual de blocos que o indice ocupa
                    sql = prop.getProperty("getDMLIndexNumberPages" + prop.getProperty("sgbd"));
                    sql = sql.replace("$schema$", schema);
                    sql = sql.replace("$index$", indexName);
                    preparedStatement = driver.prepareStatement(sql);
                    result = driver.executeQuery(preparedStatement);
                    if (result != null) {
                        while (result.next()) {
                            numPagesCurrent = result.getInt(1);
                        }
                    }

                    //Obter a razao inicial da fragmentacao na coluna cid_initial_ratio da tabela tb_candidate_index (numero de tuplas da tabela/numero de paginas que o indice ocupa)
                    sql = prop.getProperty("getDMLValueRatio" + prop.getProperty("sgbd"));
                    sql = sql.replace("$index$", indexName);
                    preparedStatement = driver.prepareStatement(sql);
                    result = driver.executeQuery(preparedStatement);
                    if (result != null) {
                        while (result.next()) {
                            initialRatio = result.getDouble(1);
                        }
                    }

                    //Calculando a fragmentacao
                    fragmentation = 100 - ((numTuplesCurrent / numPagesCurrent) / initialRatio) * 100;
                }

                preparedStatement.close();
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return fragmentation;
    }

    private void insertQueryTableLog(SQL sql) {
        if (prop.getProperty("insertTableLog").equals("1")) {
            try {
                try (PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToInsertQueryTbWorkloadLog"))) {
                    log.msg("SQL: " + sql.getSql());
                    log.msg("Plan: " + sql.getPlan());
                    log.msg("Type: " + sql.getType());
                    preparedStatement.setString(1, sql.getSql());
                    preparedStatement.setString(2, sql.getPlan());
                    preparedStatement.setTimestamp(3, new java.sql.Timestamp(sql.getTimeFirstCapture().getTime()));
                    preparedStatement.setString(4, sql.getType());
                    preparedStatement.setDouble(5, sql.getDuration());
                    driver.executeUpdate(preparedStatement);
                }
            } catch (SQLException e) {
                log.error(e);
            }
        }

    }

}
