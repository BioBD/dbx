/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.sqlserver;

import base.MaterializedView;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author Rafael
 */
public class MaterializedViewSQLServer extends MaterializedView {

    @Override
    public void setCost() {
        if (!this.getPlan().isEmpty()) {
            int ini = this.getPlan().toLowerCase().indexOf("statementsubtreecost=") + 22;
            int end = this.getPlan().substring(ini).indexOf('"') + ini;
            String numero = this.getPlan().substring(ini, end);
            numero = numero.replaceAll("\\.", "");
            this.cost = new BigInteger(numero);
        }
    }

    @Override
    public void setHypoNumRow() {
        int ini = this.getHypoPlan().toLowerCase().indexOf("statementestrows=") + 18;
        int end = this.getHypoPlan().substring(ini).indexOf('"') + ini;
        this.hypoNumRow = new BigInteger(this.getHypoPlan().substring(ini, end));
    }

    @Override
    public void setHypoSizeRow() {
        int ini = this.getHypoPlan().toLowerCase().indexOf("avgrowsize=") + 12;
        int end = this.getHypoPlan().substring(ini).indexOf('"') + ini;
        this.hypoSizeRow = Integer.valueOf(this.getHypoPlan().substring(ini, end));
    }

    @Override
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
        this.hypoCost = numPages;
        this.printStatistics();
    }

}
