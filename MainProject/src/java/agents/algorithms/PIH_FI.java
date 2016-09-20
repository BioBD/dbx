/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents.algorithms;

import agents.sgbd.Filter;
import agents.sgbd.Index;
import agents.sgbd.IndexCandidate;
import agents.sgbd.SQL;
import agents.sgbd.Schema;
import agents.sgbd.SeqScan;
import agents.sgbd.Table;
import clusteringAtributte.ClusterManagerPerAttribute;
import clusteringAtributte.ClusteringUtil;
import freqItemManager.RootTrie;
import freqItemManager.TransactionManager;
import indexableAttribute.IndexableAttrManager;
import java.util.ArrayList;
import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public class PIH_FI {
    
    private ArrayList<SeqScan> sso = null;
    private SeqScan ss = null;
    private ArrayList<Table> tabsSelect = null;
    private ArrayList<Table> tabsGroup = null;
    private ArrayList<Table> tabsOrder = null;  
    private ArrayList<Table> allTabs = null;
    private ArrayList<Index> lCandidates = new ArrayList();
    private ArrayList<Filter> filterColumns = null;
    private Filter filterAux = null;
    private Index indexAuxP = null;
    private Index indexAuxS = null;
    private long indexScanCost = 0;
    private long seqScanCost = 0;
    private long wldId = 0;
    private long profit = 0;
    IndexableAttrManager indexableManager;
    TransactionManager transactionManager;
    RootTrie graphManager;
    Schema schema;
    
    public PIH_FI(Schema sh){
        indexableManager = new IndexableAttrManager(sh);
        transactionManager = new TransactionManager(indexableManager.getAssign());
        graphManager = new RootTrie(transactionManager, sh);  
        this.schema = sh;
        ClusteringUtil.cluster = new ClusterManagerPerAttribute(sh);
    }
    
    public void runAlg(SQL sql) { 
        // 1. Proponer indices 
        // 2. Chequear si existen los indices candidatos sino lo inserta
        // 3. Verifica si existe tarea asociada al indice (manejar indices por separado de la tarea que le da beneficios)
        // 4. Verificar si es hipotetico
        //     4.1 Darle beneficio --> En los seqscan verificar si existe indice que ayude a mejorar el beneficio .... 
        /////////// el indice es usable??, beneficio ???
        //     4.2 Ver si el indice debe de ser penalizado por no uso dado el caso
        
        //Recupera o codigo da Task (wld_id)
        wldId = sql.getId();
        // Procesamiento de la consulta --------------------------------------------
        ArrayList<ArrayList<SetRestriction>> indexedAtt = indexableManager.processQuery(sql);
        // --------------------------------------------------------------------------
        
        if(indexedAtt!= null && !indexedAtt.isEmpty()){
            // Actualizar rangos en los atributos
            ClusteringUtil.cluster.addTransactions(indexedAtt);
            // Proponer indices completos -----------------------------
            ArrayList<SeqScan> sso = sql.plan().getSeqScanOperations();
            IndexCandidate c;
            for(SeqScan ss : sso){   
                if(ss.getFilterColumns().size()>0){
                    for(int j=0;j<ss.getFilterColumns().size();j++){
                        Integer[] transac = new Integer[ss.getFilterColumns().size()-j];
                        for(int i=0;i < ss.getFilterColumns().size()-j;i++){
                            transac[i] = IndexableAttrManager.exploreMapName(ss.getFilterColumns().get(i).getName(), indexableManager.getAssign());
                        }
                        c = new IndexCandidate(transac, schema);
                        c.insertIntoDatabase();
                    }
                }
            }
            /*IndexCandidate c;
            for(ArrayList<SetRestriction> rest : indexedAtt){
                Integer[] transac = new Integer[rest.size()];
                for(int i=0;i<rest.size();i++){
                    transac[i] = IndexableAttrManager.exploreMap(rest.get(i), indexableManager.getAssign());
                }
                
                c = new IndexCandidate(transac, schema);
                c.insertIntoDatabase();
            }*/
            // ---------------------------------------------------------


            // Itemset frecuentes ---------------------------------
            ArrayList<IndexCandidate> candidates = null;
            
            transactionManager.addTransactions(indexedAtt);
            
            candidates = graphManager.checkFrequentItemset(indexedAtt);
            if(candidates != null){
            
                for(IndexCandidate cand : candidates){
                    if(cand.situableForPartialIndex())
                        cand.insertIntoDatabase();
                }
            }
            
            // ---------------------------------------------------
            
            
            
            candidates = IndexCandidate.getCandidates(schema);
            
            
            for(ArrayList<SetRestriction> cond : indexedAtt){
                IndexCandidate partialindex=null;
                boolean newIndexMatch=false;
                IndexCandidate index=null;
                
                long profit_max_partial =0;
                long profit_max =0;
                for(IndexCandidate cand : candidates){
                    if(cand.isPartial()){
                        long tmp = cand.setIndexScanCost(cond);
                        if(partialindex==null && tmp>profit_max_partial){
                            profit_max_partial = tmp;
                            partialindex = cand;
                            newIndexMatch = cand.getNewIndexMatch();
                        }
                        else if(tmp>0 && partialindex!=null && cand.getId().contains(partialindex.getId()+"_N_")){
                            profit_max_partial = tmp;
                            partialindex = cand;
                            newIndexMatch = cand.getNewIndexMatch();
                        }
                        else if(tmp>0 && partialindex!=null && partialindex.getId().contains(cand.getId()+"_N_")){
                            continue;
                        }
                        else if(tmp>profit_max_partial ){
                            profit_max_partial = tmp;
                            partialindex = cand;
                            newIndexMatch = cand.getNewIndexMatch();
                        }
                    }
                    else{
                        long tmp = cand.setIndexScanCost(cond);
                        if(tmp>profit_max){
                            profit_max = tmp;
                            index = cand;
                        }
                    }
                }
                if(index != null)
                    index.winner();
                if(partialindex != null)
                    partialindex.winner();
            }
            
            for(IndexCandidate cand : candidates){
                cand.insertIntoDatabaseNewIndex();
            }
            //-------- Rutina que analice las clauslas condicionales junto con los conjuntos indexables ------------
        }
    }
}
