/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqItemManager;

import indexableAttribute.IndexableAttrManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.BitSet;
import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public class TransactionManager {
    public Hashtable<String, Integer> map;
    public Hashtable<Integer, BitSet> dataBase;
    public Hashtable<Integer, ArrayList<ArrayList<SetRestriction>>> restrictions;
    public Hashtable<Integer, Integer> countTransaction;
    private int count;
    public int trueCount;
    public TransactionManager(Hashtable<String, Integer> map){
        dataBase = new Hashtable<Integer, BitSet>();
        restrictions = new Hashtable<Integer, ArrayList<ArrayList<SetRestriction>>>();
        countTransaction = new Hashtable<Integer, Integer>();
        this.map = map;
        String [] names = map.keySet().toArray(new String[0]);
        for(int i=0;i<names.length;i++){
            BitSet bit = new BitSet();
            bit.set(64,true);
            Integer index = IndexableAttrManager.exploreMapName(names[i], map);
            dataBase.put(index, bit);
            restrictions.put(index, new ArrayList<ArrayList<SetRestriction>>());
//            countTransaction.put(index, new ArrayList<Integer>());
        }
        count = 0;
        trueCount=0;
    }
    public void addTransactions(ArrayList<ArrayList<SetRestriction>> transactions){
        trueCount++;
        Integer [] trans;
        for(int i=0;i<transactions.size();i++){
            trans = new Integer[transactions.get(i).size()];
            for(int j=0;j<trans.length;j++){
                trans[j] = IndexableAttrManager.exploreMap(transactions.get(i).get(j), map);
            }
            //int pos = exists(trans);
            addTransaction(trans);
            addRestriction(trans, transactions.get(i));
            /*if(pos ==-1){
                addTransaction(trans);
                addRestriction(trans, transactions.get(i));
            }
            else
                updateRestriction(trans, transactions.get(i), pos);*/
        }
    }
    private void addTransaction(Integer[] transaction){
        
        Integer [] ids = dataBase.keySet().toArray(new Integer[0]);
        if((count+1)%64 == 0){
            for(int i=0;i<ids.length;i++){
                BitSet bit = dataBase.get(ids[i]);
                
                bit.set(((count+1)/64+1)*64,true);
                bit.set(count+1,false);
            }
        }
        for(int i=0;i<transaction.length;i++){
            BitSet bit = dataBase.get(transaction[i]);
            bit.set(count,true); 
        }
        count++;
    }
    private void addRestriction(Integer [] transaction, ArrayList<SetRestriction> transc){
        for(int i=0;i<transaction.length;i++){
            if(!(i > 0 && transaction[i] == transaction[i-1])){
                restrictions.get(transaction[i]).add(new ArrayList<SetRestriction>());
  //              countTransaction.get(transaction[i]).add(0);
            }
        }
        updateRestriction(transaction, transc, count-1);
    }
    private void updateRestriction(Integer [] transaction, ArrayList<SetRestriction> transc, int transacNumber){
        for(int i=0;i<transaction.length;i++){
            BitSet bit = new BitSet(transacNumber+1);
            bit.set(0, transacNumber+1, true);
            BitSet itemMap = dataBase.get(transaction[i]);
            bit.and(itemMap);
            ArrayList<SetRestriction> list = restrictions.get(transaction[i]).get(bit.cardinality()-1);
            /*boolean found =false;
            for(SetRestriction s : list){
                if(s.toString().equalsIgnoreCase(transc.get(i).toString()))
                    found =true;
            }
            if(!found)*/
                list.add(transc.get(i));
        } 
        if(!countTransaction.containsKey(transacNumber))
                countTransaction.put(transacNumber, 1);
        else
            countTransaction.put(transacNumber, countTransaction.get(transacNumber)+1);
    }
    public BitSet combineItemset(Integer [] transaction, int index, int length){
        BitSet tmp = new BitSet(count);
        tmp.set(0, count, true);
        for(int i=index;i<length;i++){
            BitSet b =(BitSet)(dataBase.get(transaction[i])); 
            tmp.and(b);
        } 
        return tmp;
    }
    private int exists(Integer[] transaction){
        if(count > 0){ 
            return combineItemset(transaction, 0, transaction.length).nextSetBit(0);
        }
        return -1;
    }
}
