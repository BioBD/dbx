/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import agents.interfaces.IPredictorMV;
import algorithms.mv.ItemBag;
import algorithms.mv.Knapsack;
import static java.lang.Thread.sleep;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public abstract class PredictorMV extends Predictor implements IPredictorMV {

    protected ArrayList<ItemBag> itemsBag;
    protected ArrayList<BigDecimal> idDDLForMaterialization;
    private ResultSet resultset;

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
        log.msgPrint(this.idDDLForMaterialization);
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                log.title("Persist update ddl create MV");
                for (BigDecimal item : this.idDDLForMaterialization) {
                    PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                    log.dmlPrint(prop.getProperty("getSqlClauseToUpdateDDLCreateMVToMaterialization"));
                    preparedStatement.setBigDecimal(1, item);
                    driver.executeUpdate(preparedStatement);
                }
                log.endTitle();
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

    public void getLastExecutedDDL() {
        this.getDDLNotAnalized();
    }

    private void analyzeDDLCaptured() {
        this.executeKnapsack();
    }

    private void executeKnapsack() {
        Knapsack knapsack = new Knapsack();
        this.idDDLForMaterialization = knapsack.exec(itemsBag, this.getSizeSpaceToTuning());
    }

    public void getDDLNotAnalized() {
        this.itemsBag.clear();
        try {
            driver.createStatement();
            this.resultset = driver.executeQuery(prop.getProperty("getSqlDDLNotAnalizedPredictor"));
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    BigDecimal cost = new BigDecimal(String.valueOf(this.resultset.getInt(2)));
                    BigDecimal gain = new BigDecimal(String.valueOf(this.resultset.getInt(3)));
                    ItemBag item = new ItemBag(this.resultset.getInt(1), cost, gain);
                    this.itemsBag.add(item);
                }
            }
        } catch (SQLException e) {
            log.errorPrint(e);
        }
    }

}
