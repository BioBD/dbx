/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.interfaces.IPredictor;
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public abstract class PredictorAgent extends Agent implements IPredictor {

    @Override
    public void run() {
        log.msg("Execute agent " + this.getClass());
        while (true) {
            try {
                this.getLastExecutedDDL();
                this.analyzeDDLCaptured();
                this.updateTuningActions();
                sleep(4000);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    protected int getSizeSpaceToTuning() {
        int sizeTotal = Integer.parseInt(config.getProperty("sizespacetotuning" + config.getProperty("sgbd")));
        sizeTotal = sizeTotal * 1024;
        sizeTotal = sizeTotal * 1024;
        int occupied = this.getDiskSpaceOccupied();
        sizeTotal = sizeTotal - occupied;
        if (sizeTotal < 0) {
            return 0;
        } else {
            return sizeTotal;
        }
    }

    private int getDiskSpaceOccupied() {
        long result = 0;
        try {
            ResultSet resultset = connection.executeQuery(config.getProperty("getSqlClauseToGetDiskSpaceOccupied"));
            if (resultset != null) {
                while (resultset.next()) {
                    result = resultset.getLong(1);
                }
            }
            resultset.close();
        } catch (SQLException e) {
            log.error(e);
        }
        if (result > 0) {
            int pagesize = Integer.valueOf(config.getProperty("pagesize" + config.getProperty("sgbd")));
            result = (result * pagesize) / 1024;
        }
        return (int) result;
    }
}
