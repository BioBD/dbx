/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import static agents.Agent.driver;
import java.sql.SQLException;
/**
 *
 * @author Rafael
 */
public abstract class Predictor extends Agent {

    public int getSizeSpaceToTuning() {
        int sizeTotal = Integer.parseInt(this.propertiesFile.getProperty("sizespacetotuning"));
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
            this.resultset = driver.executeQuery(this.queries.getSqlClauseToGetDiskSpaceOccupied());  
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    System.out.println(this.resultset.getBigDecimal(1,0));
                    result = this.resultset.getInt(1);
                   
                }
            }
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
        
        return result;
    }

}
