/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.interfaces.IPredictor;
import static bib.base.Base.log;
import static bib.base.Base.prop;
import static java.lang.Thread.sleep;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public abstract class AgentPredictor extends Agent implements IPredictor {

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
        int sizeTotal = Integer.parseInt(prop.getProperty("sizespacetotuning" + prop.getProperty("sgbd")));
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
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlClauseToGetDiskSpaceOccupied"));
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
            int pagesize = Integer.valueOf(prop.getProperty("pagesize" + prop.getProperty("sgbd")));
            result = (result * pagesize) / 1024;
        }
        return (int) result;
    }
}
