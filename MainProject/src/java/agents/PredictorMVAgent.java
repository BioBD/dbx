/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.algorithms.ItemBag;
import agents.algorithms.Knapsack;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class PredictorMVAgent extends PredictorAgent {

    protected ArrayList<ItemBag> itemsBag;
    protected ArrayList<Long> idDDLForMaterialization;

    public PredictorMVAgent() {
        this.itemsBag = new ArrayList<>();
    }

    @Override
    public void updateTuningActions() {
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                for (Long idMVtoUpdate : this.idDDLForMaterialization) {
                    PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                    log.msg("Materialized View Hypothetical ID: " + idMVtoUpdate);
                    preparedStatement.setString(1, "M");
                    preparedStatement.setLong(2, idMVtoUpdate);
                    connection.executeUpdate(preparedStatement);
                    preparedStatement.close();
                }
            }
        } catch (SQLException e) {
            log.error(e);
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

    @Override
    public void getLastExecutedDDL() {
        this.itemsBag.clear();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToUpdateTemporaryDDLCreateMVToMaterialization"));
            connection.executeUpdate(preparedStatement);
            ResultSet resultset = connection.executeQuery(config.getProperty("getSqlDDLNotAnalizedPredictor"));
            if (resultset != null) {
                while (resultset.next()) {
                    int pagesize = Integer.valueOf(config.getProperty("pagesize" + config.getProperty("sgbd")));
                    long cost = (resultset.getLong(2) * pagesize) / 1024;
                    long gain = (resultset.getLong(3) * pagesize) / 1024;
                    ItemBag item = new ItemBag(resultset.getInt(1), cost, gain);
                    this.itemsBag.add(item);
                }
            }
            resultset.close();
            preparedStatement.close();

        } catch (SQLException e) {
            log.error(e);
        }
    }

}
