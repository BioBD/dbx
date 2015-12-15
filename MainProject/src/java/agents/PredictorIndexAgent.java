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
 * @author josemariamonteiro
 */
public class PredictorIndexAgent extends PredictorAgent {

    protected ArrayList<ItemBag> itemsBag;
    protected ArrayList<Long> idDDLForMaterialization;

    public PredictorIndexAgent() {
        this.itemsBag = new ArrayList<>();
    }

    public void updateTuningActions() {
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                for (Long item : this.idDDLForMaterialization) {
                    PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToUpdateDDLCreateIndexToMaterialization"));
                    log.msg("Hypothetical Index ID: " + item);
                    preparedStatement.setString(1, "M");
                    preparedStatement.setLong(2, item);
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

    public void getLastExecutedDDL() {
        this.itemsBag.clear();
        try {
            //Altera os índices marcados com status=M para status=H, uma vez que estes não foram selecionados na última execução do algorítmo da mochila
            PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("getSqlClauseToUpdateTemporaryDDLCreateIndexToMaterialization"));
            connection.executeUpdate(preparedStatement);
            //log.msg("Space for tuning remainder: " + (this.getSizeSpaceToTuning() / 1024 / 1024) + "GB");

            //Verifica se tem índice hipotético com benefício acumulado maior que o custo de criação
            ResultSet resultset = connection.executeQuery(config.getProperty("getSqlDDLNotAnalizedIndexesPositivePredictor"));
            if (resultset != null) {
                //hasItemsToMaterialize = true;
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

            //Refatoração: Adicionar na mochila os índices reais e considerar o espaço todo
        } catch (SQLException e) {
            log.error(e);
        }
    }

    //getSqlDDLNotAnalizedIndexesPositivePredictor=select cid_id, cid_creation_cost, cid_index_profit from agent.tb_candidate_index where cid_index_profit > 0 and cid_index_profit > cid_creation_cost and cid_status = 'H'
    //getSqlDDLNotAnalizedIndexesNegativePredictor=select cid_id, cid_creation_cost, cid_index_profit from agent.tb_candidate_index where cid_index_profit < 0 and cid_status = 'R'
}
