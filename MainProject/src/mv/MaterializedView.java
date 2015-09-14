/*
 * Automatic Creation Materialized Views
 *    *
 */
package mv;

import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.sgbd.Plan;
import bib.sgbd.SQL;
import bib.sgbd.oracle.PlanOracle;
import bib.sgbd.postgresql.PlanPostgreSQL;

/**
 *
 * @author Rafael
 */
public class MaterializedView extends SQL {

    private Plan hypoPlan;
    private long hypoGain;
    private long hypoNumPages;
    private long hypoCreationCost;
    private double hypoGainAC;

    public MaterializedView() {
    }

    public String getHypoPlan() {
        if (this.hypoPlan != null) {
            return hypoPlan.getPlan();
        } else {
            return "";
        }
    }

    public void setHypoPlan(String hypoPlan) {
        switch (prop.getProperty("sgbd")) {
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

    public double getHypoGainAC() {
        return hypoGainAC;
    }

    public void setHypoGainAC() {
        this.hypoGainAC = this.getHypoGain() * this.getCaptureCount();
    }

    public void setHypoNumPages() {
        double fillfactory = Double.valueOf(prop.getProperty("fillfactory" + prop.getProperty("sgbd")));
        int pagesize = Integer.valueOf(prop.getProperty("pagesize" + prop.getProperty("sgbd")));
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
        log.title("custo hypotético visão " + this.getComents());
        log.msg("HypoGain: " + this.getHypoGain());
        log.msg("HypoGainAC: " + this.getHypoGainAC());
        log.msg("HypoNumPages: " + this.getHypoNumPages());
        log.msg("hypoNumRow: " + this.hypoPlan.getNumRow());
        log.msg("hypoSizeRow: " + this.hypoPlan.getSizeRow());
        log.msg("hypoCost: " + this.getHypoCost());
        log.msg("Cost - hypoCost: " + (this.getCost() - this.getHypoCost()));
        log.msg("HypoCreationCost: " + this.getHypoCreationCost());
        log.msg("Hypo Query MV: " + this.getHypoMaterializedView());
        log.endTitle();
    }

    public String getDDLCreateMV() {
        String ddl = prop.getProperty("getDDLCreateMV" + prop.getProperty("sgbd"));
        ddl = ddl.replace("$nameMV$", this.getNameMaterizedView());
        ddl = ddl.replace("$sqlMV$", this.getHypoMaterializedView());
        return ddl;
    }

    private Object erro() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNameMaterizedView() {
        return "v_ot_workload_" + String.valueOf(this.getId());
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
        this.setPlan(sql.getPlan(), prop.getProperty("sgbd"));
        this.setHypoMaterializedView(sql.getHypoMaterializedView());
    }
}
