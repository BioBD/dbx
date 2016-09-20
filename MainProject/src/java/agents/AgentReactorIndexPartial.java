/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.libraries.Log;
import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.sgbd.Captor;
import agents.sgbd.Column;
import agents.sgbd.Index;
import agents.sgbd.IndexCandidate;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Alain
 */
public class AgentReactorIndexPartial extends ReactorAgent{
    ArrayList<IndexCandidate> candidateIndexes;
    ArrayList<IndexCandidate> deleteIndexes;
    
    Captor captor;

    public AgentReactorIndexPartial() {
        this.candidateIndexes = new ArrayList<>();
        deleteIndexes = new ArrayList<>();
        captor = new Captor();
    }

    @Override
    public void getLastTuningActionsNotAnalyzed() {
        this.getDDLNotAnalized();
    }

    public void getDDLNotAnalized() {
        candidateIndexes = IndexCandidate.getReadyToCreate(captor.getSchemaDataBase());            
        //deleteIndexes = IndexCandidate.getRealIndexes(captor.getSchemaDataBase()); 
    }

    @Override
    public void executeTuningActions() {
        //Percorre os índices a serem criados fisicamente (como índices reais)
        for (IndexCandidate ind : this.candidateIndexes) {
            if(ind.isPartial()&& ind.checkReindexPartial()){
                ind.delete();
            }
            ind.create();
        }
        for(IndexCandidate ind : deleteIndexes)
            ind.delete();
    }

    @Override
    public void updateStatusTuningActions() {
        
        try {
            
                PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("setSqlUpdateToRealReactor"));
                connection.executeUpdate(preparedStatement);
                preparedStatement.close();
                candidateIndexes.clear();
        } catch (SQLException e) {
            log.error(e);
        }
    }

}