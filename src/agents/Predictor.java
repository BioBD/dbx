/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public abstract class Predictor extends Agent {

    private ResultSet resultset;

    public int getSizeSpaceToTuning() {
        int sizeTotal = Integer.parseInt(prop.getProperty("sizespacetotuning"));
        int occupied = this.getDiskSpaceOccupied();
        sizeTotal = sizeTotal - occupied;
        if (sizeTotal < 0) {
            return 0;
        } else {
            return sizeTotal;
        }
    }

    private int getDiskSpaceOccupied() {
        int result = 0;
        try {
            driver.createStatement();
            this.resultset = driver.executeQuery(prop.getProperty("getSqlClauseToGetDiskSpaceOccupied"));
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    result = this.resultset.getInt(1);

                }
            }
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e);
        }

        return result;
    }

}
