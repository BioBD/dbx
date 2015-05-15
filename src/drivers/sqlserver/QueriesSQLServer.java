/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.sqlserver;

import static base.Base.log;
import base.Queries;
import drivers.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public class QueriesSQLServer extends Queries {

    @Override
    public String getSqlClauseToCaptureCurrentQueries(String database) {
        return this.getSignatureToDifferentiate() + "SELECT session_id as processo_id, "
                + "text as sql, "
                + "start_time as inicio, "
                + "database_id as database_name "
                + "FROM sys.dm_exec_requests req "
                + "CROSS APPLY sys.dm_exec_sql_text(sql_handle) AS sqltext";
    }

    @Override
    public String getSqlTableNames(String database) {
        return this.getSignatureToDifferentiate() + "USE " + database
                + " SELECT t.name AS table_name, "
                + "(SELECT STUFF(( SELECT ', ' + c.name "
                + "       FROM sys.columns c where t.OBJECT_ID = c.OBJECT_ID "
                + "        FOR XML PATH('') ), 1,1,'') AS activities ) AS f "
                + "FROM sys.tables AS t "
                + "INNER JOIN sys.columns c ON t.OBJECT_ID = c.OBJECT_ID "
                + "group by t.name, t.OBJECT_ID ";
    }

    @Override
    public String getSqlTableLength() {
        return this.getSignatureToDifferentiate() + " SELECT p.rows AS reltuples FROM sys.tables t INNER JOIN sys.indexes i ON t.OBJECT_ID = i.object_id INNER JOIN sys.partitions p ON i.object_id = p.OBJECT_ID AND i.index_id = p.index_id WHERE t.NAME NOT LIKE 'dt%' AND t.is_ms_shipped = 0 AND i.OBJECT_ID > 255 AND t.NAME like ?;";
    }

    @Override
    public String getSqlClauseToCreateMV(String query, String nameView) {
        return this.getSignatureToDifferentiate() + "CREATE VIEW dbo." + nameView + " WITH SCHEMABINDING AS " + query + "GO;";
    }

    @Override
    public String getPlanExecution(Driver driver, String query) {
        String partitionedPlan = "";
        try {
            ResultSet resultset = this.getResultPlanQuery(driver, query);
            while (resultset.next()) {
                partitionedPlan += " " + resultset.getString(1);
            }
            if (partitionedPlan.isEmpty()) {
                driver.executeQuery(query);

                resultset = this.getResultPlanQuery(driver, query);
                while (resultset.next()) {
                    partitionedPlan += " " + resultset.getString(1);
                }
            }
        } catch (SQLException ex) {
            log.errorPrint(ex, query);
        }
        return partitionedPlan;
    }

    private ResultSet getResultPlanQuery(Driver driver, String query) {
        String queryGetPlan = "SELECT "
                + "qp.query_plan AS QueryPlan "
                + "FROM sys.dm_exec_cached_plans AS cp "
                + "CROSS APPLY sys.dm_exec_query_plan(cp.plan_handle) AS qp "
                + "CROSS APPLY sys.dm_exec_sql_text(cp.plan_handle) AS st "
                + "where cp.objtype = 'Adhoc' "
                + "and st.TEXT like ?;";
        try {
            driver.createStatement();
            PreparedStatement preparedStatement = driver.prepareStatement(queryGetPlan);
            preparedStatement.setString(1, query);
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result != null) {

                return result;
            } else {
                log.msgPrint("error", "ff");
            }
        } catch (SQLException ex) {
            log.errorPrint(ex, query);
        }
        return null;
    }
}
