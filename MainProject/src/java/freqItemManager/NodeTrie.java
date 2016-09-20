/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package freqItemManager;

import static indexableAttribute.IndexableAttrManager.exploreMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeSet;
import parser.SetRestrictions.SetRestriction;

/**
 *
 * @author Alain
 */
public class NodeTrie {
    Integer itemset; // -1 raiz; -2 terminal
    protected Hashtable<Integer, TransitionTrie> childs;
    TransactionManager database;
    NodeTrie parent;
    public NodeTrie(Integer itemset, TransactionManager database, NodeTrie parent){
        this.itemset = itemset;
        this.parent = parent;
        this.database = database;
        this.childs = new Hashtable<Integer, TransitionTrie>();
        
        /*Integer [] ls = database.dataBase.keySet().toArray(new Integer[0]);
        for(int i=0;i<ls.length;i++){
            if(itemset < ls[i] && itemset/100 == ls[i]/100)
                childs.add(new TransitionTrie(ls[i], database, this));
        }*/
        
    }
    class MyComparator implements Comparator<NodeTrie>{
        public MyComparator(){
        }
        
        @Override
        public int compare(NodeTrie a, NodeTrie b){
            return a.itemset.compareTo(b.itemset);
        }
    }
}
