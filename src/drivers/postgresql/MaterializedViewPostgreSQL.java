/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.postgresql;

import algorithms.mv.CalculateHypoCostPostgres;
import base.MaterializedView;

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
            this.cost = Long.valueOf(this.getPlan().substring(ini, end));
        }
    }

    @Override
    public void setHypoNumRow() {
        int ini = this.getHypoPlan().indexOf("rows=") + 5;
        int end = this.getHypoPlan().substring(ini).indexOf(" ") + ini;
        this.hypoNumRow = Long.valueOf(this.getHypoPlan().substring(ini, end));
    }

    @Override
    public void setHypoSizeRow() {
        int ini = this.getHypoPlan().indexOf("width=") + 6;
        int end = this.getHypoPlan().substring(ini).indexOf(")") + ini;
        this.hypoSizeRow = Integer.valueOf(this.getHypoPlan().substring(ini, end));
    }

    @Override
    public void setHypoCost() {
        double numPages = this.hypoNumRow;
        numPages = numPages * this.hypoSizeRow;
        numPages = numPages * fillfactory;
        numPages = numPages / this.getPageSize();
        if (numPages < 1) {
            numPages = 1;
        }

        CalculateHypoCostPostgres hypoPostgres = new CalculateHypoCostPostgres();
        this.hypoCost = hypoPostgres.calculateHypoCostPostgres(this, numPages, this.hypoNumRow);
        this.printStatistics();
    }

    @Override
    public boolean isValidHypoView() {
        return true;
    }

}
