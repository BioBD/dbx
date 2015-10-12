/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.sgbd.Index;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import mv.MaterializedView;

/**
 *
 * @author josemariamonteiro
 */
public class AgentReactorIndex extends AgentReactor{
    ArrayList<Index> candiateIndexes;

    public AgentReactorIndex() {
        this.candiateIndexes = new ArrayList<>();
    }

    @Override
    public void getLastTuningActionsNotAnalyzed() {
        this.getDDLNotAnalized();
    }

    public void getDDLNotAnalized() {
        try {
            ResultSet resultset = driver.executeQuery(prop.getProperty("getSqlIndexNotAnalizedReactor"));
            if (resultset != null) {
                while (resultset.next()) {
                    Index ind = new Index();
                    ind.setCidId(resultset.getInt("cid_id"));
                    ind.setTableName(resultset.getString("cid_table_name"));
                    candiateIndexes.add(ind);
                }
            }
            resultset.close();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    @Override
    public void executeTuningActions() {
        PreparedStatement preparedStatement;
        //Percorre os índices a serem criados fisicamente (como índices reais)
        for (Index ind : this.candiateIndexes) {
            //Monta o comando CREATE INDEX
            String ddl = null;
            
            //Executa o comando CREATE INDEX
            try {
                preparedStatement = driver.prepareStatement(ddl);
                driver.executeUpdate(preparedStatement);
                preparedStatement.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
            
        }
    }

    @Override
    public void updateStatusTuningActions() {
        try {
            log.title("Persist update ddl create Index");
            for (Index ind : this.candiateIndexes) {
                PreparedStatement preparedStatement = driver.prepareStatement(prop.getProperty("getSqlClauseToUpdateDDLCreateIndexToMaterialization"));
                preparedStatement.setString(1, "R");
                preparedStatement.setInt(2, ind.getCidId());
                driver.executeUpdate(preparedStatement);
                preparedStatement.close();
            }
            log.endTitle();
        } catch (SQLException e) {
            log.error(e);
        }
    }

    
}
