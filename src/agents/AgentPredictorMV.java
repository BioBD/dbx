/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import algorithms.mv.ItemBag;
import algorithms.mv.Knapsack;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class AgentPredictorMV extends Agent {

    protected ArrayList<ItemBag> itemsBag;
    protected ArrayList<Long> idDDLForMaterialization;

    private int getSizeSpaceToTuning() {
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
            driver.createStatement();
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlClauseToGetDiskSpaceOccupied"));
            if (resultset != null) {
                while (resultset.next()) {
                    result = resultset.getLong(1);
                }
            }
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e);
        }
        if (result > 0) {
            int pagesize = Integer.valueOf(prop.getProperty("pagesize" + prop.getProperty("sgbd")));
            result = (result * pagesize) / 1024;
        }
        return (int) result;
    }

    public AgentPredictorMV() {
        this.itemsBag = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastExecutedDDL();
                this.analyzeDDLCaptured();
                this.updateDDLForMaterialization();
                sleep(4000);
            } catch (InterruptedException e) {
                log.errorPrint(e);
            }
        }
    }

    public void updateDDLForMaterialization() {
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                log.title("Persist update ddl create MV");
                for (Long item : this.idDDLForMaterialization) {
                    PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                    log.msgPrint("Materialized View Hypothetical ID: " + item);
                    preparedStatement.setString(1, "M");
                    preparedStatement.setLong(2, item);
                    driver.executeUpdate(preparedStatement);
                }
                log.endTitle();
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    private void analyzeDDLCaptured() {
        this.executeKnapsack();
    }

    private void executeKnapsack() {
        Knapsack knapsack = new Knapsack();
        this.idDDLForMaterialization = knapsack.exec(itemsBag, this.getSizeSpaceToTuning());

    }

    public void getLastExecutedDDL() {
        this.itemsBag.clear();
        try {
            driver.createStatement();
            PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateTemporaryDDLCreateMVToMaterialization"));
            driver.executeUpdate(preparedStatement);
            log.msgPrint("Space for tuning remainder: " + (this.getSizeSpaceToTuning() / 1024 / 1024) + "GB");
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlDDLNotAnalizedPredictor"));
            if (resultset != null) {
                while (resultset.next()) {
                    int pagesize = Integer.valueOf(prop.getProperty("pagesize" + prop.getProperty("sgbd")));
                    long cost = (resultset.getLong(2) * pagesize) / 1024;
                    long gain = (resultset.getLong(3) * pagesize) / 1024;
                    ItemBag item = new ItemBag(resultset.getInt(1), cost, gain);
                    System.out.println("cost: " + cost);
                    System.out.println("space: " + this.getSizeSpaceToTuning());
                    this.itemsBag.add(item);
                }
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

}
