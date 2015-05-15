/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package base;

import static base.Base.log;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    protected BigInteger hypoCost;
    private BigInteger hypoGain;
    private BigInteger hypoNumPages;
    private BigInteger hypoCreationCost;
    private int pageSize;
    private BigDecimal hypoGainAC;

    protected double fillfactory;
    protected int hypoSizeRow;
    protected BigInteger hypoNumRow;

    public String getHypoPlan() {
        return hypoPlan;
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

    public BigInteger getHypoCost() {
        return hypoCost;
    }

    public BigInteger getHypoGain() {
        return hypoGain;
    }

    public void setHypoGain() {
        this.hypoGain = this.getCost().subtract(this.getHypoCost());
        if (this.hypoGain.compareTo(BigInteger.ZERO) <= 0) {
            this.hypoGain = BigInteger.ZERO;
        }
    }

    public BigDecimal getHypoGainAC() {
        return hypoGainAC;
    }

    public void setHypoGainAC() {
        BigDecimal hypGain = new BigDecimal(this.getHypoGain());
        BigDecimal capture = new BigDecimal(this.getCapture_count());
        this.hypoGainAC = hypGain.multiply(capture);
    }

    public BigInteger getHypoNumRow() {
        return hypoNumRow;
    }

    public long getHypoSizeRow() {
        return hypoSizeRow;
    }

    public BigInteger getHypoNumPages() {
        return hypoNumPages;
    }

    public void setHypoNumPages() {
        BigDecimal numPagesTemp = new BigDecimal(this.hypoNumRow);
        numPagesTemp = numPagesTemp.multiply(BigDecimal.valueOf(this.hypoSizeRow)).multiply(BigDecimal.valueOf(fillfactory));
        numPagesTemp = numPagesTemp.divide(BigDecimal.valueOf(this.getPageSize()));
        this.hypoNumPages = numPagesTemp.toBigInteger();
    }

    public BigInteger getHypoCreationCost() {
        return hypoCreationCost;
    }

    public void setHypoCreationCost() {
        BigInteger temp = new BigInteger("2");
        temp = temp.multiply(this.getHypoNumPages());
        temp = temp.add(this.getCost());
        this.hypoCreationCost = temp;
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
        log.msgPrint("Cost - hypoCost: " + (this.getCost().subtract(this.hypoCost)), this.getClass().toString());
        log.endTitle(this.getClass().toString());
    }

    @Override
    public void setPlan(String plan) {
        super.setPlan(plan);
        this.setCost();
    }

}
