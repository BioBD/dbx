/*
 * Automatic Creation Materialized Views
 *    *
 */
package agents;

import algorithms.mv.ItemBag;
import algorithms.mv.Knapsack;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class AgentPredictorMV extends AgentPredictor {

    protected ArrayList<ItemBag> itemsBag;
    protected ArrayList<Long> idDDLForMaterialization;

    public AgentPredictorMV() {
        this.itemsBag = new ArrayList<>();
    }

    public void updateTuningActions() {
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                log.title("Persist update ddl create MV");
                for (Long item : this.idDDLForMaterialization) {
                    PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                    log.msgPrint("Materialized View Hypothetical ID: " + item);
                    preparedStatement.setString(1, "M");
                    preparedStatement.setLong(2, item);
                    driver.executeUpdate(preparedStatement);
                    preparedStatement.close();
                }
                log.endTitle();
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    @Override
    public void analyzeDDLCaptured() {
        this.executeKnapsack();
    }

    private void executeKnapsack() {
        Knapsack knapsack = new Knapsack();
        this.idDDLForMaterialization = knapsack.exec(itemsBag, this.getSizeSpaceToTuning());

    }

    public void getLastExecutedDDL() {
        this.itemsBag.clear();
        try {
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
                    this.itemsBag.add(item);
                }
            }
            resultset.close();
            preparedStatement.close();

        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

}
