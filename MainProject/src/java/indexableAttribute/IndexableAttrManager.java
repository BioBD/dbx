/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package indexableAttribute;

import agents.libraries.Log;
import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.sgbd.Schema;
import agents.sgbd.Table;
import agents.sgbd.Column;
import agents.sgbd.IndexCandidate;
import static agents.sgbd.IndexCandidate.config;
import static agents.sgbd.IndexCandidate.log;
import agents.sgbd.SQL;
import agents.sgbd.PlanPostgreSQL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import parser.RootNode;
import parser.SetRestrictions.DateIntervalRestriction;
import parser.SetRestrictions.DateUnaryRestriction;
import parser.SetRestrictions.IntervalSetRestriction;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.StringRestriction;
import parser.SetRestrictions.UnarySetRestriction;



/**
 *
 * @author Alain
 */
public class IndexableAttrManager {
    static Hashtable<String, Integer> attrAsign = new Hashtable<String, Integer>();
    Schema sc;
    public static Log log;
    public static Configuration config=null;
    public static Hashtable<String, Integer> getAttrAsig(Schema sc){
        if(sc == null)
           return attrAsign; 
        int i=0;
        for (int k =0;k < sc.tables.size(); k++){
            Table tab = sc.tables.get(k);
            for (int m = 0; m < tab.getFields().size(); m++){
                Column col = tab.getFields().get(m);
                attrAsign.put(col.getName().toUpperCase(),i+m);
            }
            i=i+100;
        }
        return attrAsign;
    }
    public IndexableAttrManager(Schema schema){
        if(config==null){
            config= new Configuration();
        }
        if(log==null)
            log = new Log(config);
        this.sc = schema;
        getAttrAsig(sc);
    }
    public Hashtable<String, Integer> getAssign(){
        return attrAsign;
    }
    public ArrayList<ArrayList<SetRestriction>> processQuery(String query){
        ArrayList<ArrayList<SetRestriction>> set = new ArrayList<ArrayList<SetRestriction>>();
        ArrayList<ArrayList<SetRestriction>> set1 = new ArrayList<ArrayList<SetRestriction>>();
        RootNode predicate = new RootNode();
        ArrayList<ArrayList<SetRestriction>> indexsets;
        indexsets = predicate.process(query); 
        
        if(indexsets == null)
            return null;
        
        for(int i=0;i<indexsets.size();i++){
            Collections.sort(indexsets.get(i), new MyComparator(attrAsign));
            Integer last = -100;
            for(int j =0;j<indexsets.get(i).size();j++){
                Integer num = exploreMap(indexsets.get(i).get(j),attrAsign);
                if(last/100 !=num/100){
                    ArrayList<SetRestriction> elem = new ArrayList<SetRestriction>();
                    elem.add(indexsets.get(i).get(j));
                    set.add(elem);
                    last = num; 
                }
                else{
                    set.get(set.size()-1).add(indexsets.get(i).get(j));
                }
            }
        }  
        for(ArrayList<SetRestriction> conj : set){
            ArrayList<SetRestriction> tmp = IndexCandidate.makeInterval(conj);
            set1.add(tmp);
            for(SetRestriction rest : tmp){
                for(Table t : sc.tables){
                    String n = rest.att();
                    String n1 = t.getFields().get(0).getName();
                    if(!attrAsign.containsKey(n) || !attrAsign.containsKey(n1.toUpperCase()))
                        return null;
                    if(attrAsign.get(t.getFields().get(0).getName().toUpperCase())/100 ==  attrAsign.get(n)/100){
                        selectivityClase(rest,t);
                    }
                }
            }
        }
        return set1;
    }
    private static double selectivityClase(SetRestriction restriction, Table table){
        if(restriction.selectivity == -1){
            String query = "select * from "+table.getName()+" where "+restriction.toString();
            
            PlanPostgreSQL pPlan = new PlanPostgreSQL(getPlanExecutionPostgreSQL(query));
            restriction.selectivity = (double)pPlan.getNumRow()/table.getNumberRows();
        }
        return restriction.selectivity;
    }
    private static String getPlanExecutionPostgreSQL(String query) {
        String partitionedPlan = "";
        ConnectionSGBD driver = new ConnectionSGBD();
        if (!query.isEmpty()) {
            try {
                ResultSet result;
                result = driver.executeQuery(config.getProperty("signature") + " EXPLAIN " + query);
                while (result.next()) {
                    partitionedPlan += "\n" + result.getString(1);
                }
                result.close();
            } catch (SQLException ex) {
                log.msg(query);
                log.error(ex);
            }
        }
        return partitionedPlan;
    }
    public ArrayList<ArrayList<SetRestriction>> processQuery(SQL query){
        if(query.getSql().toLowerCase().contains("where ")){
            return processQuery(query.getClauseFromSql("where").replaceFirst("where ", " "));
        }
        return null;
    } 
    public static Integer exploreMap(SetRestriction a, Hashtable<String, Integer> attrAsign){
            if(a instanceof StringRestriction){
                StringRestriction a1 = (StringRestriction)a;  
                //ArrayList<LeafConditionString> aLeaf = pivotdate.getStringConds();
                return (Integer)(attrAsign.get(a1.getStringConds().get(0).getAtt()));
            }
            else if(a instanceof DateUnaryRestriction){
                DateUnaryRestriction a1 = (DateUnaryRestriction)a;
                return (Integer)(attrAsign.get(a1.date.getAtt()));
            }
            else if(a instanceof DateIntervalRestriction){
                DateIntervalRestriction a1 = (DateIntervalRestriction)a;
                return (Integer)(attrAsign.get(a1.gt.date.getAtt()));
            }
            else if(a instanceof IntervalSetRestriction){
                IntervalSetRestriction a1 = (IntervalSetRestriction)a;
                return (Integer)(attrAsign.get(a1.getGtORNlt().getAtt()));
            }
            else if(a instanceof UnarySetRestriction ){
                UnarySetRestriction a1 = (UnarySetRestriction)a;
                return (Integer)(attrAsign.get(a1.getCondition().getAtt()));
            }
            return -1;
        }
    public static Integer exploreMapName(String name, Hashtable<String, Integer> attrAsign){
        return (Integer)(attrAsign.get(name.toUpperCase()));
    }
    class Attref{
        String attribute;
        String table;
        public Attref(String attribute, String table){
            this.attribute = attribute;
            this.table = table;
        }
        public String getAttr(){return attribute;}
        public String getTab(){return table;}
        
    }
    class MyComparator implements Comparator<SetRestriction>{
        Hashtable<String, Integer> attrAsign;
        public MyComparator(Hashtable<String, Integer> attrAsign){
            this.attrAsign = attrAsign;
        }
        
        @Override
        public int compare(SetRestriction a, SetRestriction b){
            return exploreMap(a, attrAsign).compareTo(exploreMap(b, attrAsign));
        }
    }
}
