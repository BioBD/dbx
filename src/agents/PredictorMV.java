/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents;

import static agents.Agent.driver;
import agents.interfaces.IPredictorMV;
import algorithms.mv.ItemBag;
import algorithms.mv.Knapsack;
import static base.Base.log;
import static java.lang.Thread.sleep;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.math.BigDecimal;


/**
 *
 * @author Rafael
 */
public abstract class PredictorMV extends Predictor implements IPredictorMV {

    protected ArrayList<ItemBag> itemsBag;
    protected ArrayList<BigDecimal> idDDLForMaterialization;

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastExecutedDDL();
                this.analyzeDDLCaptured();
                this.updateDDLForMaterialization();
                sleep(4000);
            } catch (InterruptedException e) {
                log.errorPrint(e, this.getClass().toString());
            }
        }
    }

    public void updateDDLForMaterialization() {
        log.msgPrint(this.idDDLForMaterialization, this.getClass().toString());
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                log.title("Persist update ddl create MV", this.getClass().toString());
                for (BigDecimal item : this.idDDLForMaterialization) {
                    PreparedStatement preparedStatement = driver.prepareStatement(this.queries.getSqlClauseToUpdateDDLCreateMVToMaterialization("M"));
                    log.dmlPrint(this.queries.getSqlClauseToUpdateDDLCreateMVToMaterialization("M"), this.getClass().toString());
                    preparedStatement.setBigDecimal(1, item);
                    driver.executeUpdate(preparedStatement);
                }
                log.endTitle(this.getClass().toString());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
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
            this.resultset = driver.executeQuery(this.queries.getSqlDDLNotAnalizedPredictor());
            if (this.resultset != null) {
                while (this.resultset.next()) {
                    ItemBag item = new ItemBag(this.resultset.getInt(1), this.resultset.getBigDecimal(2,0)
                                                                            , this.resultset.getBigDecimal(3,0));
                    this.itemsBag.add(item);
                }
            }
            driver.closeStatement();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}
