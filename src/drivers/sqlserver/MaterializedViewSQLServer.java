/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.sqlserver;

import base.MaterializedView;
import java.math.BigDecimal;

/**
 *
 * @author Rafael
 */
public class MaterializedViewSQLServer extends MaterializedView {

    @Override
    public void setCost() {
        if (!this.getPlan().isEmpty() && this.getPlan().toLowerCase().contains("statementsubtreecost")) {
            int ini = this.getPlan().toLowerCase().indexOf("statementsubtreecost=") + 22;
            int end = this.getPlan().substring(ini).indexOf('"') + ini;
            String numero = this.getPlan().substring(ini, end);
            try {
                this.cost = Math.round(Double.valueOf(numero).intValue());
            } catch (Exception e) {
                log.msgPrint(e.getMessage(), this.getClass().toString());
            }
        }
    }

    @Override
    public void setHypoNumRow() {
        if (this.isValidHypoView()) {
            int ini = this.getHypoPlan().toLowerCase().indexOf("statementestrows=") + 18;
            try {
                int end = this.getHypoPlan().substring(ini).indexOf('"') + ini;
                try {
                    this.hypoNumRow = new BigDecimal(this.getHypoPlan().substring(ini, end)).longValue();
                    if (this.hypoNumRow < 0) {
                        System.err.print(this.getHypoPlan());
                        System.exit(0);
                    }
                } catch (Exception e) {
                    System.out.println("Erro leitura parametro: " + this.getHypoPlan().substring(ini - 18, end + 2));
                }
            } catch (Exception ex) {
                System.out.println("Erro leitura parametro: " + this.getHypoPlan());
            }
        }
    }

    @Override
    public void setHypoSizeRow() {
        if (this.isValidHypoView()) {
            int ini = this.getHypoPlan().toLowerCase().indexOf("avgrowsize=") + 12;
            int end = this.getHypoPlan().substring(ini).indexOf('"') + ini;
            this.hypoSizeRow = Integer.valueOf(this.getHypoPlan().substring(ini, end));
            log.writeToFile(this.getHypoPlan(), "lografael.txt");
        }
    }

    @Override
    public void setHypoCost() {
        double numPagesTemp = this.hypoNumRow * this.hypoSizeRow;
        numPagesTemp = (numPagesTemp * fillfactory);
        numPagesTemp = (numPagesTemp / this.getPageSize());
        this.hypoCost = Math.round(numPagesTemp);
        this.printStatistics();
    }

    @Override
    public boolean isValidHypoView() {
        String query = this.getHypoPlan().toLowerCase();
        return (query.contains("avgrowsize")
                && query.contains("avgrowsize")
                && query.contains("statementsubtreecost")
                && query.contains("statementestrows"));
    }

    @Override
    public String getDDLCreateMV() {
        return this.getSignatureToDifferentiate() + "CREATE VIEW dbo." + this.getNameMaterizedView() + " WITH SCHEMABINDING AS " + this.getHypoMaterializedView() + "GO;";
    }

}
