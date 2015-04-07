/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.postgresql;

import base.Queries;

public class QueriesPostgreSQL extends Queries {

    @Override
    public String getSqlQueriesNotAnalized() {
        return this.getSignatureToDifferentiate() + " select * from agent.tb_workload where "
                + "wld_capture_count > wld_analyze_count order by wld_capture_count desc";
    }

    @Override
    public String getSqlDDLNotAnalizedPredictor() {
        return this.getSignatureToDifferentiate() + " select cmv_id, cmv_cost, cmv_profit "
                + "from agent.tb_candidate_view where cmv_profit > 0 and cmv_profit > cmv_cost and cmv_status = 'H';";
    }

    @Override
    public String getSqlDDLNotAnalizedReactor() {
        return this.getSignatureToDifferentiate() + " select * "
                + "from agent.tb_workload inner join agent.tb_candidate_view on "
                + "(wld_id = cmv_id) where cmv_status = 'M'";
    }

    @Override
    public String getSqlTableNames() {
        return this.getSignatureToDifferentiate() + " select tablename,\n"
                + "array_to_string(array_agg(columns.name), ', ') AS fields\n"
                + "from \n"
                + "(SELECT tablename FROM pg_catalog.pg_tables \n"
                + "WHERE schemaname \n"
                + "NOT IN ('pg_catalog', 'information_schema', 'pg_toast') \n"
                + "ORDER BY schemaname, tablename) AS consulta\n"
                + "join \n"
                + "(\n"
                + "SELECT DISTINCT\n"
                + "    a.attname as name,\n"
                + "    pgc.relname\n"
                + "FROM pg_attribute a \n"
                + "JOIN pg_class pgc ON pgc.oid = a.attrelid\n"
                + "WHERE a.attnum > 0 AND pgc.oid = a.attrelid\n"
                + "AND pg_table_is_visible(pgc.oid)\n"
                + "AND NOT a.attisdropped\n"
                + ") as columns on (consulta.tablename = columns.relname)\n"
                + "group by \n"
                + "tablename";
    }

    @Override
    public String getSqlTableLength() {
        return this.getSignatureToDifferentiate() + " SELECT reltuples FROM pg_class WHERE relname= ?;";
    }

    @Override
    public String getSqlTableFields() {
        return this.getSignatureToDifferentiate() + " select tablename,\n"
                + "array_to_string(array_agg(columns.name), ', ') AS fields\n"
                + "from \n"
                + "(SELECT tablename FROM pg_catalog.pg_tables \n"
                + "WHERE schemaname \n"
                + "NOT IN ('pg_catalog', 'information_schema', 'pg_toast') \n"
                + "ORDER BY schemaname, tablename) AS consulta\n"
                + "join \n"
                + "(\n"
                + "SELECT DISTINCT\n"
                + "    a.attname as name,\n"
                + "    pgc.relname\n"
                + "FROM pg_attribute a \n"
                + "JOIN pg_class pgc ON pgc.oid = a.attrelid\n"
                + "WHERE a.attnum > 0 AND pgc.oid = a.attrelid\n"
                + "AND pg_table_is_visible(pgc.oid)\n"
                + "AND NOT a.attisdropped\n"
                + ") as columns on (consulta.tablename = columns.relname)\n"
                + "group by \n"
                + "tablename WHERE tablename = ?;";
    }

    @Override
    public String getSqlClauseToCreateMV(String query, String nameView) {
        return this.getSignatureToDifferentiate() + " DROP MATERIALIZED VIEW IF EXISTS public."
                + nameView + "; CREATE MATERIALIZED VIEW public." + nameView + " AS " + query + ";";
    }

    @Override
    public String getSqlClauseToInsertDDLCreateMV() {
        return this.getSignatureToDifferentiate() + " INSERT INTO agent.tb_candidate_view "
                + "(cmv_id, cmv_ddl_create, cmv_cost, cmv_profit, cmv_status) VALUES (?, ?, ?, ?, ?); "
                + "update agent.tb_workload set wld_analyze_count = wld_capture_count where wld_id = ?;";
    }

    @Override
    public String getSqlClauseToUpdateWldAnalyzeCount() {
        return this.getSignatureToDifferentiate() + " update agent.tb_workload set "
                + "wld_analyze_count = wld_capture_count;";
    }

    @Override
    public String getSqlClauseToCaptureCurrentQueries(String database) {
        return this.getSignatureToDifferentiate() + " select pid as processo_id, query as sql, query_start as inicio, datname as database from pg_stat_activity where datname like '" + database + "';";
    }

    @Override
    public String getSqlClauseToUpdateQueryTbWorkload() {
        return this.getSignatureToDifferentiate() + "update agent.tb_workload set "
                + "wld_capture_count = wld_capture_count + 1, wld_plan = ? where wld_id =?";
    }

    @Override
    public String getSqlClauseToInsertQueryTbWorkload() {
        return this.getSignatureToDifferentiate() + "insert into agent.tb_workload ( wld_sql,"
                + " wld_plan, wld_capture_count, wld_analyze_count, wld_type ) values (?,?,?,?,?)";
    }

    @Override
    public String getSqlClauseToCheckIfQueryIsAlreadyCaptured() {
        return this.getSignatureToDifferentiate() + " select wld_id from agent.tb_workload where "
                + "wld_sql = ?";
    }

    @Override
    public String getSqlClauseToIncrementBenefictDDLCreateMV() {
        return this.getSignatureToDifferentiate() + " update agent.tb_candidate_view set cmv_cost = ?, "
                + "cmv_profit = ? where cmv_id = ?;";
    }

    @Override
    public String getSqlClauseToUpdateDDLCreateMVToMaterialization(String value) {
        return this.getSignatureToDifferentiate() + " update agent.tb_candidate_view set cmv_status = '" + value + "' where cmv_id = ?;";
    }

    @Override
    public String getSqlClauseToGetDiskSpaceOccupied() {
        return this.getSignatureToDifferentiate() + "select sum(cmv_cost) from agent.tb_candidate_view where cmv_status <> 'H'";
    }

}
