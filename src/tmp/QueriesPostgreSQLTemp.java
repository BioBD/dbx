package tmp;

import base.Queries;

/**
 * @author rafaeldeoliveira
 */
public class QueriesPostgreSQLTemp extends Queries {

    @Override
    public String getSqlTableNames() {
        return this.getSignatureToDifferentiate() + "SELECT tablename AS tabela FROM "
                + "pg_catalog.pg_tables WHERE schemaname NOT IN ('pg_catalog', "
                + "'information_schema', 'pg_toast') ORDER BY schemaname, tablename;";
    }

    @Override
    public String getSqlClauseToCaptureCurrentQueries(String database) {
        return this.getSignatureToDifferentiate() + " SELECT pg_stat_get_backend_pid(s.backendid)"
                + " AS processo_id, pg_stat_get_backend_activity(s.backendid) AS sql "
                + " FROM (SELECT pg_stat_get_backend_idset() AS backendid) AS s "
                + " WHERE pg_stat_get_backend_activity(s.backendid) not in ('<IDLE>')";
    }

    public String getSqlClauseToGetQueryData() {
        return this.getSignatureToDifferentiate() + "select wld_id, wld_sql, wld_plan, "
                + "wld_capture_count, wld_analyze_count from agent.tb_workload where wld_sql = ?";
    }

    public String getSqlClauseToInsertPartitionedPlan() {
        return this.getSignatureToDifferentiate() + " insert into agent.tb_access_plan ( wld_id, "
                + "apl_id_seq, apl_text_line ) values (?,?,?)";
    }

    public String getSqlClauseToDeletePartitionedPlan() {
        return this.getSignatureToDifferentiate() + "delete from agent.tb_access_plan where wld_id = ?";
    }

    public String getSqlClauseToGetNextIdTbWorkload() {
        return this.getSignatureToDifferentiate() + "SELECT MAX(wld_id) as maxId FROM agent.tb_workload";
    }

    public String getSqlClauseToGetNextIdTbCandidateIndex() {
        return this.getSignatureToDifferentiate() + "SELECT MAX(cid_id) as maxId FROM agent.tb_candidate_index";
    }

    public String getSqlClauseToGetTheFieldsNames() {
        return this.getSignatureToDifferentiate() + "Select attname From pg_class c , pg_attribute a Where c.relname = ? "
                + " AND c.oid = a.attrelid AND attstattarget < 0";
    }

    public String getSqlTreeOrder() {
        return this.getSignatureToDifferentiate() + "SELECT typlen from pg_attribute t1, pg_type t2 WHERE "
                + "t1.atttypid = t2.oid and t1.attname = ? and t1.attrelid = (SELECT oid FROM pg_class WHERE relname = ?)";
    }

    public String getSqlMCVSelectivity() {
        return this.getSignatureToDifferentiate() + "SELECT null_frac, n_distinct, most_common_vals, most_common_freqs FROM "
                + "pg_stats WHERE tablename= ? AND attname= ?;";
    }

    public String getSqlNDistinct() {
        return this.getSignatureToDifferentiate() + "SELECT n_distinct FROM pg_stats WHERE tablename = ? AND attname = ?;";
    }

    public String getSqlTableLength() {
        return this.getSignatureToDifferentiate() + "SELECT reltuples FROM pg_class WHERE relname= ?;";
    }

    public String getSqlColumType() {
        return this.getSignatureToDifferentiate() + "SELECT typname from pg_attribute t1, pg_type t2 WHERE "
                + "t1.atttypid = t2.oid and t1.attname = ? and t1.attrelid =(SELECT oid FROM pg_class WHERE relname = ?)";
    }

    public String getSqlHistogramBucket() {
        return this.getSignatureToDifferentiate() + "SELECT histogram_bounds FROM pg_stats WHERE tablename=? AND attname= ?";
    }

    public String getSqlIndexExist() {
        return this.getSignatureToDifferentiate() + "select i.indexname from pg_indexes i where i.schemaname=? and i.tablename=?";
    }

    public String getSqlIndexExistFisic() {
        return this.getSignatureToDifferentiate() + "select a.attname from pg_class c, pg_indexes i, pg_attribute a where "
                + "c.relname=i.indexname and i.schemaname=? and i.tablename=? and i.indexname = ? and c.oid=a.attrelid";
    }

    public String getSqlQueriesNotAnalized() {
        return this.getSignatureToDifferentiate() + "select wld_id from agent.tb_workload where wld_capture_count > wld_analyze_count";
    }

    public String getSqlEnableQueryFlag() {
        return this.getSignatureToDifferentiate() + "update agent.tb_workload set wld_fl_exec = 'S' where wld_id = ?;";
    }

    public String getSqlRowsNumberTable() {
        return this.getSignatureToDifferentiate() + " select reltuples as num_linhas from pg_class where relname in ("
                + " select tablename from pg_tables where schemaname like 'public' and tablename like ?)";
    }

    public String getSqlRowsNumberTableWitchSchema() {
        return this.getSignatureToDifferentiate() + " select reltuples as num_linhas from pg_class where relname in ("
                + " select tablename from pg_tables where schemaname like ? and tablename like ?)";
    }

    public String getSqlGetBlocksNumberTable() {
        return this.getSignatureToDifferentiate() + " select relpages as num_paginas from pg_class where relname in ("
                + " select tablename from pg_tables where schemaname like 'public' and tablename like ?)";
    }

    public String getSqlGetBlocksNumberTableWhithSchema() {
        return this.getSignatureToDifferentiate() + " select relpages as num_paginas from pg_class where relname in ("
                + " select tablename from pg_tables where schemaname like ? and tablename like ?)";
    }

    public String getSqlIndexName() {
        return this.getSignatureToDifferentiate() + " select indexname from pg_indexes"
                + " where schemaname like 'public' and tablename like ?;";
    }

    public String getSqlIndexNameDetails() {
        return this.getSignatureToDifferentiate() + "select a.attname from pg_class c, pg_indexes i, pg_attribute a "
                + "where c.relname=i.indexname and i.schemaname=? and i.tablename=? and i.indexname = ? and c.oid=a.attrelid";
    }

    public String getSqlColumnIsPrimaryKey() {
        return this.getSignatureToDifferentiate() + "SELECT attname as collumn, pg_constraint.contype FROM pg_class, "
                + "pg_attribute, pg_constraint WHERE pg_class.oid=attrelid and pg_class.oid = conrelid and contype  = 'p' "
                + "AND attnum>0 and lower(attname) = ? and relname= ?;";
    }

    public String getSqlAccessMethod() {
        return this.getSignatureToDifferentiate() + " select (select amname from pg_am where pg_am.oid= pg_class.relam) as nome"
                + " from pg_class where relname like (select indexname from pg_indexes"
                + " where schemaname like 'public' and indexname like ?);";
    }

    public String getSqlTaskType() {
        return this.getSignatureToDifferentiate() + "select wld_type from agent.tb_workload where wld_id=?";
    }

    public String getSqlWldQtd() {
        return this.getSignatureToDifferentiate() + "select (wld_capture_count - wld_analyze_count) as qtd from "
                + "agent.tb_workload where wld_id=?";
    }

    public String getSqlBestPrimaryIndexProfit() {
        return this.getSignatureToDifferentiate() + "select max(cid_index_profit) as max_profit from agent.tb_candidate_index";
    }

    public String getSqlExistRealPrimaryIndex() {
        return this.getSignatureToDifferentiate() + "select cid_id from agent.tb_candidate_index where cid_table_name=? and "
                + "cid_type='C' and cid_status='R'";
    }

    public String getSqlRealPrimaryIndexName() {
        return this.getSignatureToDifferentiate() + "select cid_index_name from agent.tb_candidate_index where cid_table_name=? "
                + "and cid_status='R' and cid_type='C'";
    }

    public String getSqlIndexIdLM() {
        return this.getSignatureToDifferentiate() + "select ci.cid_id from agent.tb_candidate_index ci where "
                + "ci.ci_table_name=? and ci.type=?";
    }

    public String getSqlIndexIdLMWhitchId() {
        return this.getSignatureToDifferentiate() + "select cic.cid_column_name from agent.tb_candidate_index_column cic "
                + "where cic.cid_id = ?";
    }

    public String getSqlisClustered() {
        return this.getSignatureToDifferentiate() + "select c.relname from pg_class c, pg_index i, pg_class t "
                + "where c.oid = i.indexrelid and t.oid = i.indrelid and t.relname=? and indisclustered=true";
    }

    public String getSqlIsIndiceClusteredTable() {
        return this.getSignatureToDifferentiate() + "select a.attname from pg_class c, pg_indexes i, "
                + "pg_attribute a where c.relname=i.indexname and i.schemaname=? and i.tablename=? "
                + "and c.oid=a.attrelid and i.indexname = ?";
    }

    public String getSqlupdateWldAnalyzeCount() {
        return this.getSignatureToDifferentiate() + "update agent.tb_workload set "
                + "wld_analyze_count = wld_capture_count where wld_id = ?";
    }

    public String getSqlCreateIndex(String indexName, String tableName, String accessMethod, String collumns) {
        return this.getSignatureToDifferentiate() + " CREATE INDEX \"" + indexName.toLowerCase() + "\" ON  \"" + tableName + "\"  USING \"" + accessMethod + "\"  " + collumns + " ;";

    }

    public String getSqlSelectDataTbCandidateIndexAfterCreateIndex() {
        return this.getSignatureToDifferentiate() + " select cid_initial_profit, cid_creation_cost from tb_candidate_index "
                + "where cid_id=?;";
    }

    public String getSqlUpdateTbCandidateIndexAfterCreateIndex() {
        return this.getSignatureToDifferentiate() + " update tb_candidate_index set cid_status=?, cid_initial_profit=?, "
                + "cid_initial_cardinality=?, cid_initial_disk_pages=? where cid_id=?;";
    }

    public String getSqlCreateUniqueIndex() {
        return this.getSignatureToDifferentiate() + " CREATE UNIQUE INDEX ? ON ?.?  USING ? (?);";
    }

    public String getSqlDropIndex(String indexName) {
        return this.getSignatureToDifferentiate() + " DROP INDEX \"public\"." + indexName.toLowerCase();
    }

    public String getSqlIncrementPrimaryIndexBenefict() {
        return this.getSignatureToDifferentiate() + " update agent.tb_candidate_index set "
                + "cid_index_profit = cid_index_profit + ? where cid_id = ?";
    }

    public String getSqlSelectMaxTbProfit() {
        return this.getSignatureToDifferentiate() + "  select max(pro_timestamp) as max_profit "
                + "from agent.tb_profits where cid_id = ?";
    }

    public String getSqlInsertTbProfit() {
        return this.getSignatureToDifferentiate() + " insert into agent.tb_profits values(?,?,?,?,?)";
    }

    public String getSqlSelectCurrentIndexProfit() {
        return this.getSignatureToDifferentiate() + " select cid_index_profit from agent.tb_candidate_index where cid_id=?";
    }

    public String getSqlIncrementSecondaryIndexBenefict() {
        return this.getSignatureToDifferentiate() + " update agent.tb_candidate_index set cid_index_profit = "
                + "cid_index_profit + ? where cid_id = ?;";
    }

    public String getSqlCusterizationTable(String tableName, String realIndexName) {
        return this.getSignatureToDifferentiate() + " CLUSTER " + realIndexName + " ON " + tableName + ";";
    }

    public String getSqlInsertTbCandidateIndex() {
        return this.getSignatureToDifferentiate() + " insert into agent.tb_candidate_index (cid_id, cid_table_name, "
                + "cid_index_profit, cid_creation_cost, cid_status, cid_type, cid_initial_profit, "
                + "cid_fragmentation_level, cid_initial_cardinality, cid_initial_disk_pages, cid_index_name) "
                + "values(?,?,?,?,?,?,?,?,?,?,?)";
    }

    public String getSqlInsertTbCadidateIndexColumns() {
        return this.getSignatureToDifferentiate() + " insert into agent.tb_candidate_index_column values(?,?,?);";
    }

    public String getSqlGetTypeTbCandidateIndex() {
        return this.getSignatureToDifferentiate() + "select ci.cid_status from agent.tb_candidate_index ci where ci.cid_id=?";
    }

    public String getSqlTableRowCount() {
        return this.getSignatureToDifferentiate() + " select reltuples as linecount from pg_class where relname in ("
                + " select tablename from pg_tables where schemaname like ? and tablename like ?)";
    }

    public String getSqlIndexBlocksNumber() {
        return this.getSignatureToDifferentiate() + "select relpages as num_paginas from pg_class where relname in "
                + "(select indexname from pg_indexes where schemaname =? and indexname=?)";
    }

    public String getSqlUpdateIndexName() {
        return this.getSignatureToDifferentiate() + " update tb_candidate_index set cid_index_name = ?, cid_status=? where cid_id=?;";
    }

    @Override
    public String getSqlClauseToUpdateQueryTbWorkload() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToInsertQueryTbWorkload() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToCheckIfQueryIsAlreadyCaptured() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToCreateMV(String query, String nameView) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToInsertDDLCreateMV() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlTableFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToUpdateWldAnalyzeCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToIncrementBenefictDDLCreateMV() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlDDLNotAnalizedPredictor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToGetDiskSpaceOccupied() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlDDLNotAnalizedReactor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSqlClauseToUpdateDDLCreateMVToMaterialization(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
