/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqItemManager;

import java.util.ArrayList;
import java.util.BitSet;
import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public class TransitionTrie extends NodeTrie{
    int count =0;
    boolean active = false;
    ArrayList<Integer> parents;
    public TransitionTrie(Integer itemset, TransactionManager database, NodeTrie parent){
        super(itemset, database, parent);
        parents = new ArrayList<Integer>();
        parents.add(itemset);
        
        while(parent.itemset>=0){
            parents.add(parent.itemset);
            parent = parent.parent;
        }
        parents.sort(null);
        
        BitSet bit = database.combineItemset(parents.toArray(new Integer[0]), 0, parents.size());
        int p =bit.nextSetBit(0);
        while(p>=0){
            count += database.countTransaction.get(p);
            p = bit.nextSetBit(p+1);
        }
    }
    public boolean checkItemset(Integer[] transaction, int offset, ArrayList<ArrayList<Integer>> results){
        count++;
        boolean report = false;
        if(database.trueCount*RootTrie.percent < count && database.trueCount>5){
            setChilds();
            report = true;
        }
        
        for(int i=offset;i<transaction.length;i++){
            if(childs.containsKey(transaction[i])){
                // Actualizar indice parcial candidato si hay
               if(childs.get(transaction[i]).checkItemset(transaction, i+1, results))
                   report = false;
            }
        }
        if(report && !active /*&& parents.size()>1*/ && !results.contains(parents)){
            results.add(parents);
            active = true;
        }
        return report;
    }
    
    private void setChilds(){
        Integer [] itemsets = database.dataBase.keySet().toArray(new Integer[0]);
        for(int i=0;i<itemsets.length;i++){
            if(itemset/100 == itemsets[i]/100 && itemset < itemsets[i])
                childs.put(itemsets[i], new TransitionTrie(itemsets[i], database, this));
        }
    }
}
