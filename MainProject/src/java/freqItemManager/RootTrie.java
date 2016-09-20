/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqItemManager;

import agents.sgbd.IndexCandidate;
import agents.sgbd.Schema;
import clusteringAtributte.ClusteringUtil;
import indexableAttribute.IndexableAttrManager;
import java.util.ArrayList;
import java.util.BitSet;
import parser.SetRestrictions.DateRestriction;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.SortableSet;

/**
 *
 * @author Alain
 */
public class RootTrie extends NodeTrie{
    public static double percent = 0.05;
    Schema schema;
    
    public RootTrie(TransactionManager database, Schema schema){
        super(-1, database, null);
        this.schema = schema;
        Integer [] itemsets = database.dataBase.keySet().toArray(new Integer[0]);
        for(int i=0;i<itemsets.length;i++){
            childs.put(itemsets[i], new TransitionTrie(itemsets[i], database, this));
        }
    }
    public ArrayList<IndexCandidate> checkFrequentItemset(ArrayList<ArrayList<SetRestriction>> transactions){
        Integer [] transaction = null;
        ArrayList<ArrayList<Integer>> frequent = new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<transactions.size();i++){
            transaction = new Integer[transactions.get(i).size()];
            for(int j=0;j<transaction.length;j++){
                transaction[j] = IndexableAttrManager.exploreMap(transactions.get(i).get(j), database.map);
            }
            for(int j=0;j<transaction.length;j++){
                childs.get(transaction[j]).checkItemset(transaction, j+1, frequent);
            }
        } 
        if(frequent.isEmpty())
            return null;
        
        ArrayList<IndexCandidate> candidates = proposePIndex(frequent, transaction);
        
        
        return candidates;
    }
    private ArrayList<SetRestriction> merge(ArrayList<SetRestriction> a, ArrayList<SetRestriction> b){ 
        ArrayList<SetRestriction> c = new ArrayList<SetRestriction>();
        for(SetRestriction s : a)
            c.add(s);
        for(SetRestriction s : b)
            c.add(s);
        return c; 
    }
    public ArrayList<IndexCandidate> proposePIndex(ArrayList<ArrayList<Integer>> frequentItemset, Integer [] transaction){
        ArrayList<IndexCandidate> candidates = new ArrayList<IndexCandidate>();
        for(int i=0; i< frequentItemset.size();i++){
            if(frequentItemset.get(i).size()<=1)
                continue;
            Integer [] itemset = frequentItemset.get(i).toArray(new Integer[0]);
            BitSet bit = database.combineItemset(itemset, 0, itemset.length);
            
            int p =bit.nextSetBit(0);
            IndexCandidate pIndex = new IndexCandidate(itemset, schema);
            
            while(p>=0){
                //count += database.countTransaction.get(p);
                ArrayList<SetRestriction> conjuntion= new ArrayList<SetRestriction>();
                
                BitSet tmp;
                ArrayList<SetRestriction> rest = new ArrayList<SetRestriction>();
                for(int j=0;j<itemset.length;j++){
                    tmp= new BitSet(p+1);
                    tmp.set(0, p+1, true);
                    
                    BitSet item = database.dataBase.get(itemset[j]);
                    tmp.and(item);
                    
                    rest=merge(rest, database.restrictions.get(itemset[j]).get(tmp.cardinality()-1));
                    
                }
                conjuntion = IndexCandidate.makeInterval(rest);
                ArrayList<SetRestriction> conjuntionTMP= new ArrayList<SetRestriction>();
                
                for(int m=0;m<conjuntion.size();m++){
                    if(conjuntion.get(m) instanceof SortableSet){
                            SortableSet r = ClusteringUtil.cluster.matchClusterDouble((SortableSet)conjuntion.get(m));
                            conjuntionTMP.add(r);
                        }
                        else if(conjuntion.get(m) instanceof DateRestriction){
                            DateRestriction r = ClusteringUtil.cluster.matchClusterDate((DateRestriction)conjuntion.get(m));
                            conjuntionTMP.add(r);
                        }
                        else
                            conjuntionTMP.add(conjuntion.get(m));
                }
                conjuntion = conjuntionTMP;
                
                pIndex.addConjuntion(conjuntion);
                        
                p = bit.nextSetBit(p+1);
            }
            boolean contain = false;
            if(pIndex.getFields().size()>0){
                for(IndexCandidate ind : candidates){
                    if(ind.getId().equalsIgnoreCase(pIndex.getId())){
                        contain = true;
                        break;
                    }
                }
                if(!contain)
                    candidates.add(pIndex);
            }
        }
        if(candidates.isEmpty())
            return null;
        return candidates;
    }
}
