/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import static base.Base.log;
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
            super.setResultSet(resultset);
            if (this.checkColumnName(resultset, "cmv_ddl_create")) {
                this.setHypoMaterializedView(resultset.getString("cmv_ddl_create"));
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
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

}
