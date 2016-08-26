/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.sgbd;

import agents.libraries.Configuration;
import agents.libraries.Log;
import java.util.Properties;

/**
 *
 * @author Rafael
 */
public class MaterializedView extends SQL {

    private Plan hypoPlan;
    private long hypoGain;
    private long hypoNumPages;
    private long hypoCreationCost;
    private long hypoGainAC;
    public final Properties config;
    public final Log log;

    public MaterializedView() {
        this.config = Configuration.getProperties();
        this.log = new Log(this.config);
    }

    public String getHypoPlan() {
        if (this.hypoPlan != null) {
            return hypoPlan.getPlan();
        } else {
            return "";
        }
    }

    public void setHypoPlan(String hypoPlan) {
        switch (config.getProperty("sgbd")) {
            case "postgresql":
                this.hypoPlan = new PlanPostgreSQL(hypoPlan);
                break;
            case "oracle":
                this.hypoPlan = new PlanOracle(hypoPlan);
                break;
            default:
                erro();
        }
        this.setHypoNumPages();
        this.setHypoGain();
        this.setHypoGainAC();
        this.setHypoCreationCost();
    }

    public long getHypoGain() {
        return hypoGain;
    }

    public void setHypoGain() {
        this.hypoGain = (this.getCost() - this.getHypoCost());
    }

    public long getHypoGainAC() {
        this.setHypoGainAC();
        return hypoGainAC;
    }

    public void setHypoGainAC() {
        this.hypoGainAC = this.hypoGain * this.getCaptureCount();
        if (this.hypoGainAC < 0) {
            this.hypoGainAC = -1;
        }
    }

    public void setHypoNumPages() {
        double fillfactory = Double.valueOf(config.getProperty("fillfactory" + config.getProperty("sgbd")));
        int pagesize = Integer.valueOf(config.getProperty("pagesize" + config.getProperty("sgbd")));
        this.hypoNumPages = (long) ((this.hypoPlan.getNumRow() * this.hypoPlan.getSizeRow() * fillfactory) / pagesize);
        if (this.hypoNumPages < 1) {
            this.hypoNumPages = 1;
        }
    }

    public long getHypoCreationCost() {
        return hypoCreationCost;
    }

    public void setHypoCreationCost() {
        this.hypoCreationCost = (this.getHypoNumPages() * 2) + this.getCost();
    }

    public long getHypoNumPages() {
        return hypoNumPages;
    }

    @Override
    public void print() {
        super.print();
        log.title("Hypothetical cost of a Materialized View " + this.getComents());
        log.msg("HypoGain: " + this.getHypoGain());
        log.msg("HypoGainAC: " + this.getHypoGainAC());
        log.msg("HypoNumPages: " + this.getHypoNumPages());
        log.msg("hypoNumRow: " + this.hypoPlan.getNumRow());
        log.msg("hypoSizeRow: " + this.hypoPlan.getSizeRow());
        log.msg("hypoCost: " + this.getHypoCost());
        log.msg("Cost - hypoCost: " + (this.getCost() - this.getHypoCost()));
        log.msg("HypoCreationCost: " + this.getHypoCreationCost());
        log.msg("Hypo Query MV: " + this.removerNl(this.getHypoMaterializedView()));
        log.endTitle();
    }

    public String getDDLCreateMV() {
        String ddl = config.getProperty("getDDLCreateMV" + config.getProperty("sgbd"));
        ddl = ddl.replace("$nameMV$", this.getNameMaterizedView());
        ddl = ddl.replace("$sqlMV$", this.getHypoMaterializedView());
        return ddl.trim();
    }

    private Object erro() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNameMaterizedView() {
        return ("v_dbx_view_" + this.getHypoMaterializedView().hashCode()).replace("-", "");
    }

    private long getHypoCost() {
        return this.getHypoNumPages();
    }

    public void copy(SQL sql) {
        this.setId(sql.getId());
        this.setSql(sql.getSql());
        this.setCaptureCount(sql.getCaptureCount());
        this.setAnalyzeCount(sql.getAnalyzeCount());
        this.setRelevance(sql.getRelevance());
        this.setPlan(sql.getPlan(), config.getProperty("sgbd"));
        this.setHypoMaterializedView(sql.getHypoMaterializedView());
        this.setHypoGain();
        this.setHypoGainAC();
    }
}
