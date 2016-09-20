/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents.sgbd;

import agents.libraries.Log;
import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import static agents.sgbd.IndexCandidate.config;
import static agents.sgbd.IndexCandidate.log;
import agents.sgbd.PlanPostgreSQL;
import clusteringAtributte.SetInterval;
import indexableAttribute.IndexableAttrManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import parser.SetRestrictions.DateIntervalRestriction;
import parser.SetRestrictions.DateRestriction;
import parser.SetRestrictions.DateUnaryRestriction;
import parser.SetRestrictions.IntervalSetRestriction;
import parser.SetRestrictions.SetRestriction;
import parser.SetRestrictions.SortableSet;
import parser.SetRestrictions.StringRestriction;
import parser.SetRestrictions.UnarySetRestriction;

/**
 *
 * @author Alain
 */
public class IndexCandidate {
    ArrayList<ArrayList<ArrayList<SetRestriction>>> restrictionsSet;
    
    ArrayList<String> fields;
    String id_Name;
    String table;
    int tuples;
    int pages;
    long profit=0;
    long profitNewIndex=0;
    String type;
    long lastprofitGain;    
    boolean increment =true;
    public static Log log;
    public static Configuration config=null;
    boolean newIndexMatch=false;
    boolean real=false;
    int lastGenIndex = 0;
    
    public IndexCandidate(Integer [] transactions, Schema schema){
        if(config==null){
            config= new Configuration();
        }
        if(log==null)
            log = new Log(config);
        restrictionsSet = new ArrayList<ArrayList<ArrayList<SetRestriction>>>();
        fields = new ArrayList<String>();
        
        id_Name = "C_";
        boolean first =true;
        for(int i=0;i<transactions.length;i++){
            Integer t = transactions[i];
            String name = schema.tables.get(t/100).getFields().get(t%100).getName();
            if(!fields.contains(name)){
                fields.add(name);
                
                if(first){
                    id_Name = id_Name + transactions[i].toString();
                    first = false;
                }
                else
                    id_Name = id_Name +"_" + transactions[i].toString();
            }
            
        }
        Table tableObj = schema.tables.get(transactions[0]/100);
        tuples = tableObj.getNumberRows();
        pages = tableObj.getNumberPages();
        table = tableObj.getName();
        
        lastprofitGain = Long.MIN_VALUE;
        profit =0;
        type = "H";
        
    }
    private IndexCandidate(String id_name, String table, long profit, String type, int tuples, int pages, ArrayList<String> fields,String status, int last){
        this(id_name, table, profit, type, tuples, pages, fields);
        if(status.equalsIgnoreCase("R"))
            real=true;
        this.lastGenIndex = last;
    }
    private IndexCandidate(String id_name, String table, long profit, String type, int tuples, int pages, ArrayList<String> fields){
        if(config==null){
            config= new Configuration();
        }
        if(log==null)
            log = new Log(config);
        restrictionsSet = new ArrayList<ArrayList<ArrayList<SetRestriction>>>();
        this.id_Name = id_name;
        this.table = table;
        this.profit = profit;
        this.type = type;
        this.tuples = tuples;
        this.pages = pages;
        
        lastprofitGain = Long.MIN_VALUE;
        
        this.fields = fields;
    }
    public boolean getNewIndexMatch(){
        return newIndexMatch;
    }
    public boolean getReal(){
        return real;
    }
    public long getProfit(){return profit;}
    
    public boolean isPartial(){
        return restrictionsSet.size() > 0;
    }
    public ArrayList<String> getFields(){
        return fields;
    }
    public boolean situableForPartialIndex(){
        if(isPartial()){
            for(SetRestriction rest : restrictionsSet.get(0).get(0)){
                if(rest instanceof DateRestriction || rest instanceof SortableSet){
                    return true;
                }
            }
        }
        return false;
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
    public long indexedTuplesNum(int index){
        String query;
        if(isPartial())
            query = "select * from "+table+" where ";
        else 
            query = "select * from "+table+" ";
        boolean first =true;
        for(ArrayList<SetRestriction> rest : restrictionsSet.get(index)){
            if(first){
                query = query + " ( "+conjuntionToString(rest)+" ) ";
                first = false;
            }
            else 
                query = query + " or ( "+conjuntionToString(rest)+" ) ";
        }
        
        PlanPostgreSQL pPlan = new PlanPostgreSQL(getPlanExecutionPostgreSQL(query));
        return pPlan.getNumRow();
    }
    public String getId(){return id_Name;}
    public String getType(){return type;}
    private void addConjuntion(ArrayList<SetRestriction> conjuntion, int index){
        if(!isPartial()){
            id_Name = id_Name.replaceAll("C_", "P_");
        }
        //loadFields(conjuntion);
        ArrayList<SetRestriction> interv = makeInterval(conjuntion);
        //for(SetRestriction s : interv)
        if(restrictionsSet.size()==0)
            restrictionsSet.add(new ArrayList<ArrayList<SetRestriction>>());
        if(restrictionsSet.size()==1)
            restrictionsSet.add(new ArrayList<ArrayList<SetRestriction>>());
        restrictionsSet.get(index).add(interv);
    } 
    public void addConjuntion(ArrayList<SetRestriction> conjuntion){
        addConjuntion(conjuntion,0);
    } 
    private double MackertandLohman(double selectivity){
        double b = (64*1024/8)*0.7;
        double D = 2*pages*b/(2*pages - b);
        
        //long indexedTuples = indexedTuplesNum();
        
        if(pages <= b)  
            return Math.min(2*selectivity*tuples*pages/(2*pages+selectivity*tuples), pages);
        else if(selectivity*tuples <= D)
            return 2*selectivity*tuples*pages/(2*pages+selectivity*tuples);
        else
            return b+(selectivity*tuples-D)*(pages-b)/pages;
    }
    private long indexScanCost(double selectivity){
        double uncorr = MackertandLohman(selectivity);
                
        double corr = selectivity*pages;
        
        return 4*(accessCostFunction(0, uncorr, corr)+accessCostFunction(1, uncorr, corr)+(long)(selectivity*tuples*0.003));//+accessCostFunction(0.7, uncorr, corr));
    }
    private long bitmapScanCost(double selectivity){
        
        
        double uncorr = 2*selectivity*tuples*pages/(2*pages+selectivity*tuples);
        
        double cost_per_page = Math.min(4,4 - (4 - 1)*Math.sqrt(uncorr/pages));
        
        return (long)(uncorr*cost_per_page)+(long)(selectivity*tuples*0.003);
    }
    /*private long scanIndexCost(){
        ArrayList<String> fieldsOrder = getFiledOrder();
        for(String field : getFiledOrder()){
            if(isContinuosCondition(field, restrictions.get(0))){
                
            }
        }
    }*/
    public long setIndexScanCost(ArrayList<SetRestriction> queryConjuntion){
        double selectivity = matchIndex(queryConjuntion);
        if(selectivity == 0)
            return -1; // No es usado el indice
        
        //long indexTuples = indexedTuplesNum();
        
        long indexCost = Math.min(indexScanCost(selectivity), bitmapScanCost(selectivity));
        
        long tableScan = getSeqScanCost(table);
        if(tableScan > indexCost){
            lastprofitGain = tableScan-indexCost;
            return lastprofitGain;
        }
        else{
            lastprofitGain = -2;
            return -2; // El indice puede ser usado pero no es viable
        }
    }
    public ArrayList<String> getColumns(){return fields;}
    public String getTableName(){return table;}
    public void winner(){
        setProfit();
        if(!newIndexMatch)
            updateProfit();
        
        newIndexMatch =false;
    }
    public void resetProfit(){
        profit = 0;
    }
    public void setProfit(){
        if(newIndexMatch)
            profitNewIndex += lastprofitGain;
        else
            profit += lastprofitGain;
    }
    private void updateProfit() {
        //Atualiza o beneficio acumulado do indice
        ConnectionSGBD driver = new ConnectionSGBD();
        try {
            String queryTemp = config.getProperty("setDMLprofitonpostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            //cid_index_profit
            preparedStatement.setLong(1, profit);
            //cid_id
            preparedStatement.setString(2, id_Name);
            
            //Executa a inserção
            driver.executeUpdate(preparedStatement);
            
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
    private long accessCostFunction(double r,double uncorr,double corr){
        return (long)((1-r*r)*uncorr*2+r*r*corr);
    }
    private boolean inFieldSet(String attribute){
        for(String attr : fields){
            if(attr.equalsIgnoreCase(attribute)) 
                return true;
        }
        return false;
    }
    private boolean isGeneratorIndex(){
        return lastGenIndex>=0;
    }
    private double matchIndex(ArrayList<SetRestriction> queryConjuntion){
        newIndexMatch =false;
        boolean match = false;
        int indexSet=0;
        ArrayList<String> fieldsMatched = new ArrayList<>();
        for(String atribute : fields){
            match = false;
            for(SetRestriction restriction : queryConjuntion){
                if(restriction.att().equalsIgnoreCase(atribute)){
                    match = true;
                    fieldsMatched.add(atribute);
                    break;
                }
            }
            if(!match)
                return 0;
        }
        
        double querySelectivity=1;
        for(SetRestriction restriction : queryConjuntion){
            if(inFieldSet(restriction.att()))
                querySelectivity = querySelectivity * selectivityClase(restriction);
        }
        boolean ispartial = isPartial();
        if(ispartial){
            boolean matchConj = false;
            for(int i=0;i<restrictionsSet.get(0).size();i++){
                if(matchConjuntions(restrictionsSet.get(0).get(i), queryConjuntion)){
                    matchConj = true;
                    break;
                }
            }
            /*if(!matchConj ){
                if(restrictionsSet.size()==1)
                    restrictionsSet.add(new ArrayList<ArrayList<SetRestriction>>());
                for(int i=0;i<restrictionsSet.get(1).size();i++){
                    if(matchConjuntions(restrictionsSet.get(0).get(i), queryConjuntion)){
                        matchConj = true;
                        break;
                    }
                }
                if(matchConj)
                    newIndexMatch=true;
            }*/
            if(!matchConj){
                if(increment){
                    ArrayList<SetRestriction> rest = new ArrayList<>();
                    for(SetRestriction s : queryConjuntion){
                        for(String f:fieldsMatched){
                            if (s.att().equalsIgnoreCase(f)){
                                rest.add(s);
                            }
                        }
                    }
                    if(!real){
                        indexSet=0;
                        addConjuntion(rest,0);
                        insertConjuntion(rest);
                    }
                    else if(isGeneratorIndex()){
                        indexSet=1;
                        addConjuntion(rest,1);
                    }
                    else return 0;
                }
                else return 0;
            }
        }
        
        double partialSelectivity = 0;
        
        if(ispartial){
            for(ArrayList<SetRestriction> conjuntions : restrictionsSet.get(indexSet)){
                double conjuntionSelectivity =1;
                for(SetRestriction restriction : conjuntions){
                    conjuntionSelectivity = conjuntionSelectivity * selectivityClase(restriction);
                }
                partialSelectivity = partialSelectivity + conjuntionSelectivity - partialSelectivity * conjuntionSelectivity;
            }
        }
        if(indexSet==0)
            newIndexMatch=false;
        else 
            newIndexMatch = true;
        
        if(restrictionsSet.size()>0 && restrictionsSet.get(indexSet).size()>0)
            return querySelectivity * partialSelectivity;
        else
            return querySelectivity;
    }
    private boolean matchConjuntions(ArrayList<SetRestriction> index, ArrayList<SetRestriction> query){
        query = makeInterval(query);
        for(int i=0;i<index.size();i++){
            boolean match =false;
            for(int j=0;j<query.size();j++){
                if(tryMatch(index.get(i), query.get(j))){
                    match = true;
                    break;
                }
            }
            if(!match)
                return false;
        }
        return true;
    }
    private boolean tryMatch(SetRestriction index, SetRestriction query){
        return index.matchRestriction(query);
    }
    private double selectivityClase(SetRestriction restriction){
        if(restriction.selectivity == -1){
            String query = "select * from "+table+" where "+restriction.toString();
            
            PlanPostgreSQL pPlan = new PlanPostgreSQL(getPlanExecutionPostgreSQL(query));
            restriction.selectivity = (double)pPlan.getNumRow()/tuples;
        }
        return restriction.selectivity;
    }
    
    private void loadFields(ArrayList<SetRestriction>restrictions){
        for(int i=0;i<restrictions.size();i++){
            if(restrictions.get(i) instanceof DateIntervalRestriction){
                DateIntervalRestriction d = (DateIntervalRestriction)restrictions.get(i);
                if(!fields.contains(d.gt.date.getAtt()))
                    fields.add(d.gt.date.getAtt());
            }
            else if(restrictions.get(i) instanceof DateUnaryRestriction){
                DateUnaryRestriction d = (DateUnaryRestriction)restrictions.get(i);
                if(!fields.contains(d.date.getAtt()))
                    fields.add(d.date.getAtt());
            }
            else if(restrictions.get(i) instanceof IntervalSetRestriction){
                IntervalSetRestriction d = (IntervalSetRestriction)restrictions.get(i);
                if(!fields.contains(d.getGtORNlt().getAtt()))
                    fields.add(d.getGtORNlt().getAtt());
            }
            else if(restrictions.get(i) instanceof StringRestriction){
                StringRestriction d = (StringRestriction)restrictions.get(i);
                if(!fields.contains(d.getStringConds().get(0).getAtt()))
                    fields.add(d.getStringConds().get(0).getAtt());
            }
            else if(restrictions.get(i) instanceof UnarySetRestriction){
                UnarySetRestriction d = (UnarySetRestriction)restrictions.get(i);
                if(!fields.contains(d.getCondition().getAtt()))
                    fields.add(d.getCondition().getAtt());
            }
        }
    }
    private static boolean isSame(SetRestriction a, SetRestriction b){
        if(a instanceof UnarySetRestriction && b instanceof UnarySetRestriction){
            String aColumn = ((UnarySetRestriction)a).getCondition().getAtt();
            String bColumn = ((UnarySetRestriction)b).getCondition().getAtt();
            return aColumn.equalsIgnoreCase(bColumn);
        }
        else if(a instanceof IntervalSetRestriction && b instanceof IntervalSetRestriction){
            String aColumn = ((IntervalSetRestriction)a).getGtORNlt().getAtt();
            String bColumn = ((IntervalSetRestriction)b).getGtORNlt().getAtt();
            if(!aColumn.equalsIgnoreCase(bColumn))return false;
            aColumn = ((IntervalSetRestriction)a).getLtORNgt().getAtt();
            bColumn = ((IntervalSetRestriction)b).getLtORNgt().getAtt();
            return aColumn.equalsIgnoreCase(bColumn);
        }
        else if(a instanceof DateUnaryRestriction && b instanceof DateUnaryRestriction){
            String aColumn = ((DateUnaryRestriction)a).date.getAtt();
            String bColumn = ((DateUnaryRestriction)b).date.getAtt();
            return aColumn.equalsIgnoreCase(bColumn);
        }
        else if(a instanceof DateIntervalRestriction && b instanceof DateIntervalRestriction){
            String aColumn = ((DateIntervalRestriction)a).att();
            String bColumn = ((DateIntervalRestriction)b).att();
            return aColumn.equalsIgnoreCase(bColumn);
        }
        return false;
    }
    public static ArrayList<SetRestriction> makeInterval(ArrayList<SetRestriction> conjuntion){
        ArrayList<SetRestriction> integred = new ArrayList<SetRestriction>();
        for(int i=0;i<conjuntion.size();i++){
            SetRestriction pivot = conjuntion.get(i);
            if(conjuntion.get(i) instanceof SortableSet){
                int j;
                for(j=i+1;j<conjuntion.size();j++){
                    if(!isSame(conjuntion.get(i), conjuntion.get(j)))
                        break;
                    else{
                        pivot=((SortableSet)pivot).interception((UnarySetRestriction)conjuntion.get(j));
                        if(pivot == null){
                            integred.clear();
                            log.error("Condicion invalida!!!!!");
                            return integred;
                        }
                    }
                }
                i = j-1;
            }
            else if(conjuntion.get(i) instanceof DateUnaryRestriction){
                int j;
                for(j=i+1;j<conjuntion.size();j++){
                    if(!isSame(conjuntion.get(i), conjuntion.get(j)))
                        break;
                    else{
                        pivot=((DateRestriction)pivot).intercept((DateUnaryRestriction)conjuntion.get(j));
                        if(pivot == null){
                            integred.clear();
                            log.error("Condicion invalida!!!!!");
                            return integred;
                        }
                    }
                }
                i = j-1;
            }
            integred.add(pivot);
        }
        return integred;
    }
    public static boolean inCond(String id_Name, String conditionString){
        ConnectionSGBD driver = new ConnectionSGBD();
        boolean isInLM = false;
        //Recupera os índices existentes com o mesmo número de colunas do índeice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getDMLConditionIndexonpostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, id_Name);
            preparedStatement.setString(2, conditionString);
            ResultSet result = driver.executeQuery(preparedStatement);
            isInLM = result.next();
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        return isInLM;
    }
    public static boolean inCol(String id_Name, String colName){
        ConnectionSGBD driver = new ConnectionSGBD();
        boolean isInLM = false;
        //Recupera os índices existentes com o mesmo número de colunas do índeice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getDMLColumnByIndexNamepostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, id_Name);
            preparedStatement.setString(2, colName);
            ResultSet result = driver.executeQuery(preparedStatement);
            isInLM = result.next();
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        return isInLM;
    }
    public static boolean inLM(String id_Name) {
        ConnectionSGBD driver = new ConnectionSGBD();
        boolean isInLM = false;
        //Recupera os índices existentes com o mesmo número de colunas do índeice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getDMLIndexByNameOnPostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, id_Name);
            ResultSet result = driver.executeQuery(preparedStatement);
            isInLM = result.next();
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        return isInLM;
    }
    public void delete(){
        String ddl = "DROP INDEX "+id_Name;
        ConnectionSGBD driver = new ConnectionSGBD();
        PreparedStatement preparedStatement;
        try {
                log.msg("Delte index " + id_Name+ " with script: " + ddl);
                preparedStatement = driver.prepareStatement(ddl);
                driver.executeUpdate(preparedStatement);
                preparedStatement.close();
                log.msg("Finish delete index " + id_Name);
            } catch (Exception ex) {
                //log.error(ex);
                log.msg("Materialized index delete "+ id_Name+ ": Error "+ex.getMessage());
            }
    }
    public boolean checkReindexPartial(){
        String check = "select from agent.tb_candidate_partial_index where cid_index_name = ? "
                + "and cid_status = 'H' and cid_creation_cost < cid_index_profit - cid_acum_index_profit;";
        ConnectionSGBD driver = new ConnectionSGBD();
        PreparedStatement preparedStatement;
        try {
               
                preparedStatement = driver.prepareStatement(check);
                preparedStatement.setString(1, id_Name);
                ResultSet res= driver.executeQuery(preparedStatement);
                if(res.next())
                    return true;
                preparedStatement.close();
            } catch (Exception ex) {
                //log.error(ex);
                log.msg("Materialized index "+ id_Name+ ": Error "+ex.getMessage());
            }
        return false;
    }
    public boolean isContinuosCondition(String attr, ArrayList<SetRestriction> conditions){
        for(SetRestriction rest : restrictionsSet.get(0).get(0)){
                if(rest instanceof DateRestriction || rest instanceof SortableSet){
                    if(rest.att().equalsIgnoreCase(attr)){
                        return true;
                    }
                }
                else if(rest.att().equalsIgnoreCase(attr))
                    return false;
        }
        return false;
    }
    private ArrayList<String> getFiledOrder(){
        ArrayList<String> fieldorder = new ArrayList<>();
        if(isPartial()){
            for(String columnName : fields){
                if(isContinuosCondition(columnName, restrictionsSet.get(0).get(0))){
                    fieldorder.add(columnName);
                }
            }
            for(String columnName : fields){
                if(!isContinuosCondition(columnName, restrictionsSet.get(0).get(0))){
                    fieldorder.add(columnName);
                }
            }
        }
        else{
            for(String columnName : fields) {
                fieldorder.add(columnName);
            }
        }
        return fieldorder;
    }
    public void create(){
        String ddl = "CREATE INDEX "
                    + id_Name
                    + " ON "
                    + table
                    + " USING btree (";
        
        boolean first = true;
        for(String field : getFiledOrder()){
            if(first){
                first =false;
                ddl = ddl + " " + field;
            }
            else{
                ddl = ddl + ", " + field;
            }
        }
        ddl = ddl + ")";
        
        if(isPartial()){
            ddl = ddl +" where (";
            first =true;
            for(ArrayList<SetRestriction> conjuntion : restrictionsSet.get(0)){
                if(first){
                    ddl = ddl + " ("+ conjuntionToString(conjuntion)+") ";
                    first =false;
                }
                else{
                    ddl = ddl + " or " +" ("+ conjuntionToString(conjuntion)+") ";
                }
            }
            ddl = ddl + " )";
        }
        
        System.out.println(ddl);
        ConnectionSGBD driver = new ConnectionSGBD();
        PreparedStatement preparedStatement;
        try {
                log.msg("Creating index " + id_Name+ " with script: " + ddl);
                preparedStatement = driver.prepareStatement(ddl);
                driver.executeUpdate(preparedStatement);
                preparedStatement.close();
                log.msg("Finish create index " + id_Name);
            } catch (Exception ex) {
                //log.error(ex);
                log.msg("Materialized index "+ id_Name+ ": Error "+ex.getMessage());
            }
        if(isPartial()){
            String update1 = "UPDATE agent.tb_candidate_partial_index " +
                    "   SET cid_acum_index_profit=cid_index_profit " +
                            " WHERE cid_index_name = ?;";
            try {
                preparedStatement = driver.prepareStatement(update1);
                preparedStatement.setString(1, id_Name);
                driver.executeUpdate(preparedStatement);
                preparedStatement.close();
                
            } catch (Exception ex) {
                //log.error(ex);
                log.msg("Materialized index "+ id_Name+ ": Error "+ex.getMessage());
            }
        }
    }
    public static ArrayList<IndexCandidate> getRealIndexes(Schema sch){
        ConnectionSGBD driver = new ConnectionSGBD();
        ArrayList<IndexCandidate> candidates = new ArrayList<IndexCandidate>();
        
        try {
            String queryTemp = config.getProperty("getSqlPartialIndexMaterializedAnalizedReactor");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            ResultSet result = driver.executeQuery(preparedStatement);
            while(result.next()){
                candidates.add(getCandidate(result.getString("cid_index_name"), sch));
            }
            
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return candidates;
    }
    public static ArrayList<IndexCandidate> getReadyToCreate(Schema sch){
        if(config==null){
            config= new Configuration();
        }
        ConnectionSGBD driver = new ConnectionSGBD();
        ArrayList<IndexCandidate> candidates = new ArrayList<IndexCandidate>();
        
        try {
            String queryTemp = config.getProperty("getSqlPartialIndexNotAnalizedReactor");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            ResultSet result = driver.executeQuery(preparedStatement);
            while(result.next()){
                candidates.add(getCandidate(result.getString("cid_index_name"), sch));
            }
            
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return candidates;
    }
    public static ArrayList<IndexCandidate> getCandidates(Schema sch){
        if(config==null){
            config= new Configuration();
        }
        ConnectionSGBD driver = new ConnectionSGBD();
        ArrayList<IndexCandidate> candidates = new ArrayList<IndexCandidate>();
        
        try {
            String queryTemp = config.getProperty("getDMLIndexAll");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            ResultSet result = driver.executeQuery(preparedStatement);
            while(result.next()){
                candidates.add(getCandidate(result.getString("cid_index_name"), sch,true));
            }
            
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return candidates;
    }
    private static ArrayList<String> getFields(String id_name){
        ConnectionSGBD driver = new ConnectionSGBD();
        ArrayList<String> cols =new ArrayList<String>();
        //Recupera os índices existentes com o mesmo número de colunas do índeice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getIndexColspostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, id_name);
            ResultSet result = driver.executeQuery(preparedStatement);
            while(result.next()){
                String columnName = result.getString("cic_column_name").trim();
                cols.add(columnName);
            }
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return cols;
    }
    public static IndexCandidate getCandidate(String id_name, Schema sh, boolean allcondtion){
        return getCandidate(id_name, sh, allcondtion, true);
    }
    public static IndexCandidate getCandidate(String id_name, Schema sh, boolean allcondtion, boolean increment){
        if(config==null){
            config= new Configuration();
        }
        ConnectionSGBD driver = new ConnectionSGBD();
        IndexCandidate candidate =null;
        //Recupera os índices existentes com o mesmo número de colunas do índeice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getDMLIndexByNameOnPostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, id_name);
            ResultSet result = driver.executeQuery(preparedStatement);
            result.next(); 
            String table = result.getString("cid_table_name");
            long profit  = result.getLong("cid_index_profit");
            String status = result.getString("cid_status");
            String type = result.getString("cid_type");
            int tuples = result.getInt("cid_table_tuples");
            int pages = result.getInt("cid_table_pages");
            int last = result.getInt("cid_last");
            ArrayList<String> cols = getFields(id_name);
            candidate = new IndexCandidate(id_name, table, profit, type, tuples, pages, cols,status,last);
            
            result.close();  
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        // Recupera las condiciones
        try{
            String queryTemp = config.getProperty("getDMLIndexByNameConditionOnPostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, id_name);
            preparedStatement.setBoolean(2, !allcondtion);
            ResultSet result = driver.executeQuery(preparedStatement);
            
            String condition;
            while(result.next()){
                condition = result.getString("cid_index_condition");
                addConjuntion(condition, candidate, sh);
            }
            
            
            // ----------
            
            result.close();  
        }catch (SQLException e) {
            log.error(e.getMessage());
        }
        candidate.increment = increment;
        return candidate;
    }
    public static IndexCandidate getCandidateAnalise(String id_name, Schema sh){
        return getCandidate(id_name, sh, false,false);
    }
    public static IndexCandidate getCandidate(String id_name, Schema sh){
        return getCandidate(id_name, sh, false);
    }
    private static void addConjuntion(String conditions, IndexCandidate candidate, Schema sh){
        IndexableAttrManager indexableManager = new IndexableAttrManager(sh);
        ArrayList<ArrayList<SetRestriction>> indexedAtt = indexableManager.processQuery(conditions);
        for(ArrayList<SetRestriction> conj : indexedAtt){
            candidate.addConjuntion(makeInterval(conj));
        }
    }
    private void insertField(String colname){
        
        ConnectionSGBD driver = new ConnectionSGBD();
        
        //Integer v = IndexableAttrManager.exploreMapName(colname, IndexableAttrManager.getAttrAsig(null));
        if(!inCol(id_Name, colname)){
            try{
                String queryTemp = config.getProperty("setIndexColspostgresql");
                PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
                
                //cic_column_name 
                preparedStatement.setString(1, colname.toLowerCase());
                //cid_index_name
                preparedStatement.setString(2, id_Name);
                //Executa a inserção
                driver.executeUpdate(preparedStatement);
            }catch(SQLException e){
                //log.error(e.getMessage());
            }
        }
        
    }
    private void updateIndexNumber() {
        //Atualiza o beneficio acumulado do indice
        ConnectionSGBD driver = new ConnectionSGBD();
        try {
            String queryTemp = config.getProperty("setDMLnumberonpostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            //cid_index_profit
            preparedStatement.setLong(1, lastGenIndex);
            //cid_id
            preparedStatement.setString(2, id_Name);
            
            //Executa a inserção
            driver.executeUpdate(preparedStatement);
            
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
    public boolean insertIntoDatabaseNewIndex(){
        if(isGeneratorIndex() && profitNewIndex>0){
            lastGenIndex++;
            
            String idTmp = id_Name;
            id_Name = id_Name + "_N_"+lastGenIndex;
            
            long profitTmp = profit;
            profit=profitNewIndex;
            
            insertIntoDatabase(1);
            
            id_Name = idTmp;
            profit = profitTmp;
            
            updateIndexNumber();
        }
        return true;
    }
    public boolean insertIntoDatabase(){
        return insertIntoDatabase(0);
    }
    public boolean insertIntoDatabase(int index){
        //Inserir indice na LM
        //Beneficio acumulado = 0
        //NQ (numero de consultas que usa o indice) = 0
        //Inserir linha na tabela tb_candidate_index
        //Inserir uma ou mais linhas na tabela tb_candidate_index_column. Uma linha para cada coluna
        //Inserir linha na tabela tb_task_indexes
        
        ConnectionSGBD driver = new ConnectionSGBD();
        
        if(!inLM(id_Name)){
        //Insere o novo índice candidato
            try {
                String queryTemp = config.getProperty("setDMLInsertCandidatePartialIndexonpostgresql");
                PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
                
                //cid_indexname 
                preparedStatement.setString(1, id_Name);
                //cid_table_name
                preparedStatement.setString(2, table);
                //cid_index_profit
                preparedStatement.setInt(3, (int)profit);
                //cid_creation_cost
                preparedStatement.setInt(4, (int)getCreationCost());
                //cid_status
                preparedStatement.setString(5, "H");
                //cid_type
                preparedStatement.setString(6, "S");
                //cid_initial_profit
                preparedStatement.setInt(7, 0);
                //cid_fragmentation_level
                preparedStatement.setInt(8, 0);
                //cid_initial_ratio
                preparedStatement.setInt(9, 0);
                //cid_creation_time (as a real index)
                preparedStatement.setTimestamp(10, null);
                //cid_table_tuples
                preparedStatement.setInt(11, tuples);
                //cid_table_pages
                preparedStatement.setInt(12, pages);
                //cid_last
                if(index==0)
                    preparedStatement.setInt(13, lastGenIndex);
                else 
                    preparedStatement.setInt(13, -1);
                
                //Executa a inserção
                driver.executeUpdate(preparedStatement);
                
                
                
            } catch (SQLException e) {
                //      log.error(e.getMessage());
                return false;
            }
            
            for(String field : fields)
                insertField(field);
        }
        if(restrictionsSet.size()>0 && restrictionsSet.get(0).size()>0){
            for(int i=0;i<restrictionsSet.get(index).size();i++){
                insertConjuntion(restrictionsSet.get(index).get(i));
            }
        }
        return true;
    }
    private String conjuntionToString(ArrayList<SetRestriction> conjuntion){
        String conditions = "" ; 
        
        for(SetRestriction rest : conjuntion){
            if(conditions == ""){
                conditions = rest.toString();
            }
            else{
                conditions = conditions + " AND " +rest.toString();
            }
        }
        return conditions;
    }
    private boolean insertConjuntion(ArrayList<SetRestriction> conjuntion){
        
        ConnectionSGBD driver = new ConnectionSGBD();
        
        String conjuntionString=conjuntionToString(conjuntion);
        
        //Insere o novo índice candidato
        if(!inCond(id_Name, conjuntionString)){
            try {
                String queryTemp = config.getProperty("setDMLInsertConditionIndexOnPostgresql");
                PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
                
                //cid_indexname 
                preparedStatement.setString(1, id_Name);
                //cid_index_condition
                preparedStatement.setString(2, conjuntionString);
                
                //Executa a inserção
                driver.executeUpdate(preparedStatement);
                
            } catch (SQLException e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
    public long getCreationCost(){
        if(isPartial()){
            return pages;
        }
        else
            return 2*pages;
    }
    private long getSeqScanCost(String tableName) {
        ConnectionSGBD driver = new ConnectionSGBD();
        int numberOfTablePages = 0;
        try {
            String queryTemp = config.getProperty("getSeqScanCostpostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, tableName);
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result.next()) {
                numberOfTablePages = result.getInt("relpages");
                result.close();
            } else {
                result.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return numberOfTablePages + (long)(tuples*0.003);
    }
}