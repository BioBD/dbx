/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import static bib.base.Base.log;
import bib.sgbd.SQL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public abstract class MaterializedView extends SQL implements IMaterializedView {

    private String hypoPlan;
    private String hypoMaterializedView;
    protected long hypoCost;
    private long hypoGain;
    private long hypoNumPages;
    private long hypoCreationCost;
    private double hypoGainAC;
    protected int hypoSizeRow;
    protected long hypoNumRow;
    protected final double fillfactory;
    private final int pageSize;
    private int analyze_count;

    public int getHypoSizeRow() {
        return hypoSizeRow;
    }

    public void setHypoSizeRow(int hypoSizeRow) {
        this.hypoSizeRow = hypoSizeRow;
    }

    public int getAnalyze_count() {
        return analyze_count;
    }

    public void setAnalyze_count(int analyze_count) {
        this.analyze_count = analyze_count;
    }

    public MaterializedView(double fillfactory, int pageSize) {
        this.fillfactory = fillfactory;
        this.pageSize = pageSize;
    }

    public String getHypoPlan() {
        if (this.hypoPlan != null) {
            return hypoPlan;
        } else {
            return "";
        }
    }

    public void setHypoPlan(String hypoPlan) {
        System.out.println("PLANO: " + hypoPlan);
        this.hypoPlan = hypoPlan;
        this.setHypoNumRow();
        this.setHypoSizeRow();
        this.setHypoNumPages();
        this.setHypoCost();
        this.setHypoGain();
        this.setHypoGainAC();
        this.setHypoCreationCost();
    }

    public void setResultSet(ResultSet resultset) {
        try {
            if (this.checkColumnName(resultset, "cmv_ddl_create")) {
                this.setHypoMaterializedView(resultset.getString("cmv_ddl_create"));
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    public double getFillfactory() {
        return fillfactory;
    }

    public long getHypoCost() {
        return hypoCost;
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

    public long getHypoNumRow() {
        return hypoNumRow;
    }

    public void setHypoNumPages() {
        this.hypoNumPages = (long) (this.hypoSizeRow * fillfactory) / this.getPageSize();
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

    public String getHypoMaterializedView() {
        return hypoMaterializedView;
    }

    public void setHypoMaterializedView(String hypoMaterializedView) {
        this.hypoMaterializedView = hypoMaterializedView;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    private boolean checkColumnName(ResultSet resultset, String name) {
        try {
            ResultSetMetaData meta = resultset.getMetaData();
            int numCol = meta.getColumnCount();

            for (int i = 1; i < numCol + 1; i++) {
                if (meta.getColumnName(i).equals(name)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            log.errorPrint(e);
            return false;
        }

    }

    protected void printStatistics() {
        log.title("custo hypotético visão " + this.getComents());
        log.msgPrint("hypoNumRow: " + this.hypoNumRow);
        log.msgPrint("hypoSizeRow: " + this.hypoSizeRow);
        log.msgPrint("hypoCost: " + this.hypoCost);
        log.msgPrint("fillFactory: " + this.fillfactory);
        log.msgPrint("pageSize: " + this.getPageSize());
        log.msgPrint("Cost: " + this.getCost());
        log.msgPrint("Cost - hypoCost: " + (this.getCost() - this.hypoCost));
        log.endTitle();
    }

    @Override
    public void setPlan(String plan) {
        super.setPlan(plan);
        this.setCost();
    }

    public String getDDLCreateMV(String database) {
        switch (database) {
            case "sqlserver":
                return "select into dbo." + this.getNameMaterizedView() + " from " + this.getHypoMaterializedView() + " GO;";
            case "postgresql":
                this.erro();
                break;

        }
        return erro().toString();
    }

    private Object erro() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getNameMaterizedView() {
        return "v_ot_workload_" + String.valueOf(this.getId());
    }

}
