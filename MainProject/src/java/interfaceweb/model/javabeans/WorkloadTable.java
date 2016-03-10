package interfaceweb.model.javabeans;

/**
 *
 * @author Italo
 */
public class WorkloadTable {
    private int id;
    private int numberOfExecutions;
    private String type;
    private int relevance;
    private String sql;
    private String plan;
    private String indexes;
    private String vms;
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the numberOfExecutions
     */
    public int getNumberOfExecutions() {
        return numberOfExecutions;
    }

    /**
     * @param numberOfExecutions the numberOfExecutions to set
     */
    public void setNumberOfExecutions(int numberOfExecutions) {
        this.numberOfExecutions = numberOfExecutions;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the relevance
     */
    public int getRelevance() {
        return relevance;
    }

    /**
     * @param relevance the relevance to set
     */
    public void setRelevance(int relevance) {
        this.relevance = relevance;
    }

    /**
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the plan
     */
    public String getPlan() {
        return plan;
    }

    /**
     * @param plan the plan to set
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * @return the indexes
     */
    public String getIndexes() {
        return indexes;
    }

    /**
     * @param indexes the indexes to set
     */
    public void setIndexes(String indexes) {
        this.indexes = indexes;
    }

    /**
     * @return the vms
     */
    public String getVms() {
        return vms;
    }

    /**
     * @param vms the vms to set
     */
    public void setVms(String vms) {
        this.vms = vms;
    }
}
