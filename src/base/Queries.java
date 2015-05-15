package base;

public abstract class Queries extends Base implements IQueries {

    public String getSignatureToDifferentiate() {
        return signatureToDifferentiate;
    }

    public void setSignatureToDifferentiate(String signatureToDifferentiate) {
        this.signatureToDifferentiate = signatureToDifferentiate;
    }

    @Override
    public String getSqlClauseToGetThePlan(String query) {
        if (!query.isEmpty()) {
            return this.getSignatureToDifferentiate() + " EXPLAIN " + query + ";";
        }
        return "";
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
    public String getSqlQueriesNotAnalized() {
        return this.getSignatureToDifferentiate() + " select * from agent.tb_workload where "
                + "wld_capture_count > wld_analyze_count order by wld_capture_count desc";
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
    public String getSqlClauseToIncrementBenefictDDLCreateMV() {
        return this.getSignatureToDifferentiate() + " update agent.tb_candidate_view set cmv_cost = ?, "
                + "cmv_profit = ? where cmv_id = ?;";
    }

    @Override
    public String getSqlDDLNotAnalizedPredictor() {
        return this.getSignatureToDifferentiate() + " select cmv_id, cmv_cost, cmv_profit "
                + "from agent.tb_candidate_view where cmv_profit > 0 and cmv_profit > cmv_cost and cmv_status = 'H';";
    }

    @Override
    public String getSqlClauseToUpdateDDLCreateMVToMaterialization(String value) {
        return this.getSignatureToDifferentiate() + " update agent.tb_candidate_view set cmv_status = '" + value + "' where cmv_id = ?;";
    }

    @Override
    public String getSqlClauseToGetDiskSpaceOccupied() {
        return this.getSignatureToDifferentiate() + "select sum(cmv_cost) from agent.tb_candidate_view where cmv_status <> 'H'";
    }

    @Override
    public String getSqlDDLNotAnalizedReactor() {
        return this.getSignatureToDifferentiate() + " select * "
                + "from agent.tb_workload inner join agent.tb_candidate_view on "
                + "(wld_id = cmv_id) where cmv_status = 'M'";
    }

}
