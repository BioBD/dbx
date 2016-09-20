/*
 * Biblioteca de codigo fonte criada por Rafael Pereira
 * Proibido o uso sem autorizacao formal do autor
 *
 * rpoliveirati@gmail.com
 */
package clusteringAtributte;

import agents.libraries.Log;
import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.sgbd.IndexCandidate;
import static agents.sgbd.IndexCandidate.config;
import static agents.sgbd.IndexCandidate.log;
import agents.sgbd.Schema;
import agents.sgbd.Table;
import agents.sgbd.PlanPostgreSQL;
import indexableAttribute.IndexableAttrManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import parser.SetRestrictions.DateRestriction;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.SortableSet;

/**
 *
 * @author Alain
 */
public class ClusterManagerPerAttribute {
    Hashtable<String, ArrayList<SetInterval<Double>>> mapdouble;
    Hashtable<String, ArrayList<SetInterval<Date>>> mapdate;
    Schema sc;
    public static Log log;
    public static Configuration config=null;
    
    public ClusterManagerPerAttribute(Schema sc){
        if(config==null){
            config= new Configuration();
        }
        if(log==null)
            log = new Log(config);
        mapdouble = new Hashtable<String, ArrayList<SetInterval<Double>>>();
        mapdate = new Hashtable<String, ArrayList<SetInterval<Date>>>();
        this.sc = sc;
    }
    public SortableSet matchClusterDouble(SortableSet set){
        SetInterval<Double> s;
        if(mapdouble.containsKey(set.att())){
            s = clusteringDouble(mapdouble.get(set.att()), set.getTypeDouble());
            return (SortableSet)s.getSetRestriction();
        }
        else return set;
    }
    public DateRestriction matchClusterDate(DateRestriction set){
        SetInterval<Date> s;
        if(mapdate.containsKey(set.att())){
            s = clusteringDate(mapdate.get(set.att()), set.getTypeDate());
            return (DateRestriction)s.getSetRestriction();
        }
        else return set;
    }
    // Plan de la consulta
    private String getPlanExecutionPostgreSQL(String query) {
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
    private boolean checkSelectivityDouble(SetInterval<Double> set){
        
        int attId=IndexableAttrManager.getAttrAsig(null).get(set.getAtt());
        Table table = sc.tables.get(attId/100);
        
        String query = "select * from "+table.getName()+" where "+set.getSQlClause();
        
        PlanPostgreSQL pPlan = new PlanPostgreSQL(getPlanExecutionPostgreSQL(query));
        return pPlan.getNumRow()/table.getNumberRows()<=0.10;
    }
    private boolean checkSelectivityDate(SetInterval<Date> set){
        
        int attId=IndexableAttrManager.getAttrAsig(null).get(set.getAtt());
        Table table = sc.tables.get(attId/100);
        
        String query = "select * from "+table.getName()+" where "+set.getSQlClause();
        
        PlanPostgreSQL pPlan = new PlanPostgreSQL(getPlanExecutionPostgreSQL(query));
        return pPlan.getNumRow()/table.getNumberRows()<=0.10;
    }
    private SetInterval<Double> clusteringDouble(ArrayList<SetInterval<Double>> clusters, SetInterval<Double> set){
        SetInterval<Double> result = set;
        clusters.add(set);
        /*ArrayList<SetInterval<Double>> cl = new ArrayList<SetInterval<Double>>();
        for(SetInterval<Double> setI : clusters)
            cl.add(setI);
        while(cl.size()>0){
            int pos =0;
            double less = Double.MAX_VALUE;
            for(int i=0;i<cl.size();i++){
                double tmp = cl.get(i).distenceOf(set);
                if(tmp<less){
                    less = tmp;
                    pos = i;
                }
            }
            
            if(less == Double.MAX_VALUE){
                cl.clear();
                break;
            }
            SetInterval<Double> s = cl.get(pos).merge(set);
            if(checkSelectivityDouble(s)){
                clusters.remove(cl.get(pos));
                clusters.add(s);
                result = s;
                break;
            }
            cl.remove(pos);
        }
        if(cl.size()==0){
            clusters.add(set);
        }*/
        return result;
    }
    private SetInterval<Date> clusteringDate(ArrayList<SetInterval<Date>> clusters, SetInterval<Date> set){
        SetInterval<Date> result = set; 
        clusters.add(set);
        /*ArrayList<SetInterval<Date>> cl = new ArrayList<SetInterval<Date>>();
        for(SetInterval<Date> setI : clusters)
            cl.add(setI);
        while(cl.size()>0){
            int pos =0;
            double less = Double.MAX_VALUE;
            for(int i=0;i<cl.size();i++){
                double tmp = cl.get(i).distenceOf(set);
                if(tmp<less){
                    less = tmp;
                    pos = i;
                }
            }
            if(less == Double.MAX_VALUE){
                cl.clear();
                break;
            }
            SetInterval<Date> s = cl.get(pos).merge(set);
            if(checkSelectivityDate(s)){
                clusters.remove(cl.get(pos));
                clusters.add(s);
                result = s;
                break;
            }
            cl.remove(pos);
        }
        if(cl.size()==0){
            clusters.add(set);
        }*/
        return result;
    }
    public void addTransactions(ArrayList<ArrayList<SetRestriction>> transactions){
        for(ArrayList<SetRestriction> restriction : transactions){
            ArrayList<SetRestriction> restriction1 = IndexCandidate.makeInterval(restriction);
            for(SetRestriction rest : restriction1){ 
                if(rest instanceof SortableSet){
                    if(!mapdouble.containsKey(rest.att())){
                        ArrayList<SetInterval<Double>> t = new ArrayList<SetInterval<Double>>();
                        t.add(((SortableSet)rest).getTypeDouble());
                        mapdouble.put(rest.att(), t);
                    }
                    else{
                        clusteringDouble(mapdouble.get(rest.att()), ((SortableSet)rest).getTypeDouble());
                    }
                }
                else if(rest instanceof DateRestriction){
                    if(!mapdate.containsKey(rest.att())){
                        ArrayList<SetInterval<Date>> t = new ArrayList<SetInterval<Date>>();
                        t.add(((DateRestriction)rest).getTypeDate());
                        mapdate.put(rest.att(), t);
                    }
                    else{
                        clusteringDate(mapdate.get(rest.att()), ((DateRestriction)rest).getTypeDate());
                    }
                }
            } 
        }
    }
}
