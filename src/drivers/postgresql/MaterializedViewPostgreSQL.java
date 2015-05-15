/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.postgresql;

import algorithms.mv.CalculateHypoCostPostgres;
import base.MaterializedView;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Rafael
 */
public class MaterializedViewPostgreSQL extends MaterializedView {

    @Override
    public void setCost() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().indexOf("..") + 2;
            int end = this.getPlan().substring(ini).indexOf(".") + ini;
            this.cost = new BigInteger(this.getPlan().substring(ini, end));
        }
    }

    @Override
    public void setHypoNumRow() {
        int ini = this.getHypoPlan().indexOf("rows=") + 5;
        int end = this.getHypoPlan().substring(ini).indexOf(" ") + ini;
        this.hypoNumRow = new BigInteger(this.getHypoPlan().substring(ini, end));
    }

    @Override
    public void setHypoSizeRow() {
        int ini = this.getHypoPlan().indexOf("width=") + 6;
        int end = this.getHypoPlan().substring(ini).indexOf(")") + ini;
        this.hypoSizeRow = Integer.valueOf(this.getHypoPlan().substring(ini, end));
    }

    public void setHypoCost() {
        BigInteger numPages;
        BigDecimal numPagesTemp = new BigDecimal(this.hypoNumRow);
        numPagesTemp = numPagesTemp.multiply(BigDecimal.valueOf(this.hypoSizeRow));
        numPagesTemp = numPagesTemp.multiply(BigDecimal.valueOf(fillfactory));
        numPagesTemp = numPagesTemp.divide(BigDecimal.valueOf(this.getPageSize()));
        numPages = numPagesTemp.toBigInteger();
        if (numPages.intValue() < 1) {
            numPages = BigInteger.valueOf(1);
        }

        CalculateHypoCostPostgres hypoPostgres = new CalculateHypoCostPostgres();
        this.hypoCost = hypoPostgres.calculateHypoCostPostgres(this, numPages, this.hypoNumRow);
        this.printStatistics();
    }

}
