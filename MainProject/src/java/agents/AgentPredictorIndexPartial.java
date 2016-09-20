/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.algorithms.ItemBag;
import agents.algorithms.Knapsack;
import agents.libraries.Log;
import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.sgbd.Captor;
import agents.sgbd.IndexCandidate;
import agents.sgbd.SQL;
import indexableAttribute.IndexableAttrManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.sql.Connection;

import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public class AgentPredictorIndexPartial extends PredictorAgent{
    protected ArrayList<MyItemBag> itemsBag;
    protected ArrayList<IndexCandidate> idDDLForMaterialization;
    boolean jointEnumeration;
    Captor captor;
    IndexableAttrManager indexableManager;
    long lastBestProfit;

    public AgentPredictorIndexPartial() {
        this.itemsBag = new ArrayList<>();
        idDDLForMaterialization = new ArrayList<IndexCandidate>();
        jointEnumeration = true;
        captor = new Captor();
        indexableManager = new IndexableAttrManager(captor.getSchemaDataBase());
        lastBestProfit=0;
    }

    public void updateTuningActions() {
        try {
            if (this.idDDLForMaterialization.size() > 0) {
                
                /*Connection conn1 = connection.newConnection();
                conn1.setAutoCommit(false); */           
                
                PreparedStatement preparedStatement1 = connection.prepareStatement(config.getProperty("resetMaterialized"));
                preparedStatement1.executeUpdate();
                preparedStatement1.close();                
                
                PreparedStatement preparedStatement = connection.prepareStatement(config.getProperty("setNewIndexToMaterialization"));
                
                for (IndexCandidate item : this.idDDLForMaterialization) {
                    log.msg("Hypothetical Index ID: " + item.getId()+" status  M");
                    preparedStatement.setString(1, item.getId().trim());
                    preparedStatement.addBatch();
                    //driver.executeUpdate(preparedStatement);
          //          preparedStatement.close();
                }

                
                preparedStatement.executeBatch();
                preparedStatement.close();
                
                preparedStatement = connection.prepareStatement(config.getProperty("setReIndexIndex"));
                
                for (IndexCandidate item : this.idDDLForMaterialization) {
                    if(item.isPartial() && item.checkReindexPartial()){
                        preparedStatement.setString(1, item.getId().trim());
                        preparedStatement.addBatch();
                    }
                    //driver.executeUpdate(preparedStatement);
          //          preparedStatement.close();
                }

                
                preparedStatement.executeBatch();
                preparedStatement.close();
                
            }
        } catch (SQLException e) {
            log.error(e);
        } 
        
    }
    public void getLastExecutedDDL() {
        this.itemsBag.clear();
        try {
            
            //Verifica se tem índice com benefício acumulado maior que o custo de criação
            ResultSet resultset = connection.executeQuery(config.getProperty("getFindCandidatesToCreationAll"));
            if (resultset != null) {
                //hasItemsToMaterialize = true;
                while (resultset.next()) {
                    int pagesize = Integer.valueOf(config.getProperty("pagesize" + config.getProperty("sgbd")));
                    long cost = (resultset.getLong("cid_creation_cost"));
                    long gain = (resultset.getLong("cid_index_profit"));
                    MyItemBag item = new MyItemBag(resultset.getString("cid_index_name"), cost, gain);
                    this.itemsBag.add(item);
                }
            }
            resultset.close();
            
            // Actualiza status das condicoes dos indices parciais que podem se considerar todas as condicoes
            ArrayList<String> toUpdate = new ArrayList<>();
            resultset = connection.executeQuery(config.getProperty("getFindCandidatesToCreationParc"));
            if (resultset != null) {
                //hasItemsToMaterialize = true;
                while (resultset.next()) {
                    toUpdate.add(resultset.getString("cid_index_name"));
                }
            }
            resultset.close();
            for(String s : toUpdate){
                String up = "UPDATE agent.tb_index_condition SET active=true WHERE cid_index_name=?";
                PreparedStatement preparedStatement = connection.prepareStatement(up);
                preparedStatement.setString(1, s);
                connection.executeUpdate(preparedStatement);
                preparedStatement.close();
            }

            //Refatoração: Adicionar na mochila os índices reais e considerar o espaço todo
        } catch (SQLException e) {
            log.error(e);
        }
    }
    @Override
    public void analyzeDDLCaptured() {
        idDDLForMaterialization.clear();
        if(!jointEnumeration){
            /*Hashtable<Integer, String> map = new Hashtable<Integer, String>();
            ArrayList<ItemBag> itembagKnapsack = new ArrayList<ItemBag>();
            for(int i=1;i<=itemsBag.size();i++){
                itembagKnapsack.add(new ItemBag(i, itemsBag.get(i-1).getCost(), itemsBag.get(i-1).getGain()));
                map.put(i, itemsBag.get(i-1).getId());
            }
            Knapsack knapsack = new Knapsack();
            ArrayList<Long> materialize = knapsack.exec(itembagKnapsack, this.getSizeSpaceToTuning());
            for(Long id_index : materialize){
                idDDLForMaterialization.add(map.get(id_index));
            }*/
        }
        else{
            idDDLForMaterialization = jointEnumeration(captor.getALLWorkload());
        }
    }
    
    private long evaluateConfig(ArrayList<IndexCandidate> candidates, 
            ArrayList<ArrayList<ArrayList<SetRestriction>>> processedWorkload, 
            ArrayList<ArrayList<Hashtable<String, Long>>> cost){
        
        long gain=0;
        for(IndexCandidate cand : candidates){
            cand.resetProfit();
        }
        for(int i=0;i<processedWorkload.size();i++){
            ArrayList<ArrayList<SetRestriction>> indexedAtt= processedWorkload.get(i);
            if(cost.size()<=i)
                cost.add(i, new ArrayList<>());
            // Procesamiento de la consulta --------------------------------------------
            if(indexedAtt == null)
                continue;
            // --------------------------------------------------------------------------
            IndexCandidate index = null;
            
            for(int j=0;j<indexedAtt.size();j++){
                ArrayList<SetRestriction> cond = indexedAtt.get(j);
                if(cost.get(i).size()<=j)
                    cost.get(i).add(j,new Hashtable<String, Long>());
                long profit_max = 0;
                for(IndexCandidate cand : candidates){
                    long tmp;
                    if(!cost.get(i).get(j).containsKey(cand.getId())){
                         tmp= cand.setIndexScanCost(cond);
                         if(tmp>0)
                             cost.get(i).get(j).put(cand.getId(), tmp);
                    }
                    else 
                        tmp = cost.get(i).get(j).get(cand.getId());
                    if(tmp>profit_max){
                        profit_max = tmp;
                        index = cand;
                    }
                    cand.resetProfit();
                }
                if(profit_max>0 && index != null){
                    index.setProfit();
                    gain += index.getProfit();
                }
            }
            
        }
        return gain;
    }
    
    private ArrayList<IndexCandidate> newArray(ArrayList<IndexCandidate> array){
        ArrayList<IndexCandidate> result = new ArrayList<IndexCandidate>();
        for(IndexCandidate elem : array)
            result.add(elem);
        return result;
    }
    
    private ArrayList<ArrayList<ArrayList<SetRestriction>>> processWorkLoad(ArrayList<SQL> workload){
        ArrayList<ArrayList<ArrayList<SetRestriction>>> result = new ArrayList<>();
        for(SQL sql : workload){
            result.add(indexableManager.processQuery(sql));
        }
        return result;
    }
    private ArrayList<IndexCandidate> jointEnumeration(ArrayList<SQL> workload){
        ArrayList<IndexCandidate> bestConfig = new ArrayList<IndexCandidate>();
        ArrayList<IndexCandidate> tmpConfig = new ArrayList<IndexCandidate>();
        ArrayList<IndexCandidate> candidateIndexes= new ArrayList<IndexCandidate>();
        ArrayList<ArrayList<Hashtable<String, Long>>> cost = new ArrayList<>();
        long[] evalIndexInConfig= new long[itemsBag.size()];
        long gain=0;
        ArrayList<ArrayList<ArrayList<SetRestriction>>> processedWorkload = processWorkLoad(workload);
        
        
        for(int i=0;i<itemsBag.size();i++){
            candidateIndexes.add(IndexCandidate.getCandidateAnalise(itemsBag.get(i).getId(), captor.getSchemaDataBase()));
            evalIndexInConfig[i]=0;
        }
        
        long best=0;
        long lastBest;
        for(int i=0;i<candidateIndexes.size();i++){
            tmpConfig = newArray(bestConfig);
            lastBest = best;
            for(int j=0;j<candidateIndexes.size();j++){
                if(!bestConfig.contains(candidateIndexes.get(j))){
                    tmpConfig.add(candidateIndexes.get(j));
                }
                else 
                    continue;
                
                long tmp = evaluateConfig(tmpConfig, processedWorkload,cost);
                evalIndexInConfig[i] = tmp;
                if(tmp > best){
                    best = tmp;
                    gain = best-lastBest;
                    bestConfig = newArray(tmpConfig);
                }
                tmpConfig.remove(candidateIndexes.get(j));
            }
            if(lastBest == best)
                break;
        }
        if(lastBestProfit>best)
            return new ArrayList<>();
        else
            lastBestProfit = best;
        
        
        return bestConfig;
    }
    class MyItemBag{
        String id;
        long cost;
        long gain;
        
        public MyItemBag(String id, long cost, long gain) {
            this.id = id;
            this.cost = cost;
            this.gain = gain;
        }
        
        public String getId() {
            return id;
        }
        
        public long getCost() {
            return cost;
        }
        
        public long getGain() {
            return gain;
        }
        
    }
}
