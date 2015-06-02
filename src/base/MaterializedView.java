/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import static base.Base.log;
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
    private int pageSize;
    private double hypoGainAC;

    protected double fillfactory;
    protected int hypoSizeRow;
    protected long hypoNumRow;

    public String getHypoPlan() {
        if (this.hypoPlan != null) {
            return hypoPlan;
        } else {
            return "";
        }
    }

    public void setHypoPlan(String hypoPlan) {
        this.hypoPlan = hypoPlan;
        this.setPageSize();
        this.setFillfactory();
        this.setHypoNumRow();
        this.setHypoSizeRow();
        this.setHypoNumPages();
        this.setHypoCost();
        this.setHypoGain();
        this.setHypoGainAC();
        this.setHypoCreationCost();
    }

    @Override
    public void setResultSet(ResultSet resultset) {
        try {
            super.setResultSet(resultset);
            if (this.checkColumnName(resultset, "cmv_ddl_create")) {
                this.setHypoMaterializedView(resultset.getString("cmv_ddl_create").toLowerCase());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public double getFillfactory() {
        return fillfactory;
    }

    public void setFillfactory() {
        this.fillfactory = Float.valueOf(this.propertiesFile.getProperty("fillfactorydb"));
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
        this.hypoGainAC = this.getHypoGain() * this.getCapture_count();
    }

    public long getHypoNumRow() {
        return hypoNumRow;
    }

    public long getHypoSizeRow() {
        return hypoSizeRow;
    }

    public long getHypoNumPages() {
        return hypoNumPages;
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

    public String getHypoMaterializedView() {
        return hypoMaterializedView;
    }

    public void setHypoMaterializedView(String hypoMaterializedView) {
        this.hypoMaterializedView = hypoMaterializedView;
    }

    private void setPageSize() {
        this.pageSize = Integer.valueOf(this.propertiesFile.getProperty("sizepagedb"));
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
            log.errorPrint(e, this.getClass().toString());
            return false;
        }

    }

    protected void printStatistics() {
        log.title("custo hypotético visão " + this.getComents(), this.getClass().toString());
        log.msgPrint("hypoNumRow: " + this.hypoNumRow, this.getClass().toString());
        log.msgPrint("hypoSizeRow: " + this.hypoSizeRow, this.getClass().toString());
        log.msgPrint("hypoCost: " + this.hypoCost, this.getClass().toString());
        log.msgPrint("fillFactory: " + this.fillfactory, this.getClass().toString());
        log.msgPrint("pageSize: " + this.getPageSize(), this.getClass().toString());
        log.msgPrint("Cost: " + this.getCost(), this.getClass().toString());
        log.msgPrint("Cost - hypoCost: " + (this.getCost() - this.hypoCost), this.getClass().toString());
        log.endTitle(this.getClass().toString());
    }

    @Override
    public void setPlan(String plan) {
        super.setPlan(plan);
        this.setCost();
    }

    public boolean containsField(String clause, String field) {
        return clause.contains(" " + field + " ")
                || clause.contains(" " + field + ",")
                || clause.contains(" " + field + ";")
                || clause.contains(" " + field + "=")
                || clause.contains(" " + field + ">")
                || clause.contains(" " + field + "<")
                || clause.contains("," + field + ",")
                || clause.contains("," + field + ";")
                || clause.contains("," + field + "=")
                || clause.contains("," + field + ">")
                || clause.contains("," + field + "<")
                || clause.contains("." + field + ",")
                || clause.contains("." + field + ";")
                || clause.contains("." + field + "=")
                || clause.contains("." + field + ">")
                || clause.contains("." + field + "<");
    }

}
