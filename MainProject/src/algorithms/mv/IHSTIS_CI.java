/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms.mv;

import static bib.base.Base.log;
import static bib.base.Base.prop;
import bib.driver.Driver;
import bib.sgbd.Column;
import bib.sgbd.Filter;
import bib.sgbd.Index;
import bib.sgbd.Plan;
import bib.sgbd.SQL;
import bib.sgbd.SeqScan;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrador
 */
public class IHSTIS_CI {
     Plan p = null;
     ArrayList<SeqScan> sso = null;
     SeqScan ss = null;
     SQL query = null;
     ArrayList<Column> colsSelect = null;
     ArrayList<Column> colsGroup = null;
     ArrayList<Column> colsOrder = null;
     ArrayList<Index> lCandidates = new ArrayList();
     ArrayList<Filter> filterColumns = null;
     Filter filterAux = null;
     Index indexAuxP = null;
     Index indexAuxS = null;
     long indexScanCost = 0;
     long seqScanCost = 0;
     long wldId = 0;
     long profit = 0;
        
    public void runAlg(ResultSet resultset){
       
        //Recupera o codigo da Task (wld_id)
        try {
            wldId = resultset.getInt("wld_id");
        } catch (SQLException ex) {
            Logger.getLogger(IHSTIS_CI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Usa o Plano para pegar os atributos envolvidos em filtros
        try {
            p = new Plan(resultset.getString("wld_plan"));
        } catch (SQLException ex) {
            Logger.getLogger(IHSTIS_CI.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (p!=null){
            sso = getSeqScanOperations(p);
        }
        
        //Percorre as operacoes de SeqScan
        for (int i = 0; i < sso.size(); i++) {  
            ss = sso.get(i);  
            
            //Percorre cada coluna (filter) do SeqScan
            filterColumns = ss.getFilterColumns();
            for (int j = 0; j < filterColumns.size(); j++) {  
                filterAux = filterColumns.get(j); 

                //Adiciona indice primario
                //Cria um indice candidato primario para cada coluna do SeqScan
                indexAuxP = new Index();
                indexAuxP.setTableName(ss.getTableName());
                indexAuxP.columns = new ArrayList();
                indexAuxP.columns.add(filterAux);
                indexAuxP.setIndexType("Primary");
                indexAuxP.setCreationCost(2*getSeqScanCost(ss.getTableName()));
                indexAuxP.setHasFilter(true);
                indexAuxP.setFilterType(filterAux.getFilterType());
                indexAuxP.setNumberOfRows(ss.getNumberOfRows());
                lCandidates.add(indexAuxP);

                //Adicionando Indice Secundario
                //Cria um indice candidato secundario para cada coluna do SeqScan
                indexAuxS = new Index();
                indexAuxS.setTableName(ss.getTableName());
                indexAuxS.columns = new ArrayList();
                indexAuxS.columns.add(filterAux);
                indexAuxS.setIndexType("Secundary");
                indexAuxS.setCreationCost(2*getSeqScanCost(ss.getTableName()));
                indexAuxS.setHasFilter(true);
                indexAuxS.setFilterType(filterAux.getFilterType());
                indexAuxS.setNumberOfRows(ss.getNumberOfRows());
                lCandidates.add(indexAuxS);
            }
        } 
        
        //TODO: Criar um indice composto para todos os atributos de um msmo SeqScan
        
        //Usa a comando SQL para pegar os atributos envolvidos nas clausulas SELECT, GROUP e ORDER
        try {
            query = new SQL();
            query.setSql(resultset.getString("wld_sql"));
        } catch (SQLException ex) {
            Logger.getLogger(IHSTIS_CI.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (query!=null){
            colsSelect = getSelectColumns(query);
            colsGroup = getGroupColumns(query);
            colsOrder = getOrderColumns(query);    
        }
        
        //TODO: Refatorar essa parte....
        if (colsSelect.size()>0){
        
        }
        
        //TODO: Juntar os atributos do SELECT, GROUP e ORDER para uma mesma Tabela
         
        //TODO: Juntar os atributos do Case 1 (Filtro) e do Case 2 (Select, Group e Order) para uma mesma Tabela
         
        //Percorrer os Indices Candidatos
         for (Index lCandidate : lCandidates) {
             //Testa se o indice ja existe na metabase local
             if (!inLM(lCandidate)){
                 //Insere um novo indice candidato na LM
                 insertIndexLM(lCandidate);
             }
             else{
                 //verificar se o indice ja estah associado ah tarefa corrente
                 if (!inTaskIndexes(wldId,lCandidate)){
                     //Inserir tupla na tabela tb_task_indexes
                     insertTaskIndexes(wldId,lCandidate);
                 }
             }
            //Verifica se o indice eh hipotetico (ou seja, nao eh um indice real) 
            if (isHypotheticalIndex(lCandidate)) {
                //Estimar Custo de Index Scan
                indexScanCost = getIndexScanCost(lCandidate);
                //Estima custo do SeqScan
                seqScanCost = getSeqScanCost(lCandidate.getTableName());
                //Verifica se o custo de utilizar o indice (Index Scan) eh menor que o custo do SeqScan
                if (indexScanCost<seqScanCost){
                    profit = seqScanCost - indexScanCost;
                }
                updateProfit(lCandidate, profit);
            }
            //Se o indice eh real e nao foi utilizado deveria ter um beneficio descontado???
         }
    }
    
    private void updateProfit(Index ind, long profit){
        //Atualiza o beneficio acumulado do indice
    }
    
    private boolean isHypotheticalIndex(Index ind){
        //Verificar na tabela tb_candidate_index a coluna cid_status: H -> Hypothetical; R-> Real
        return true;
    }
    
    private void insertTaskIndexes(long wldId, Index ind){
        //verificar se o indice ja estah associado ah tarefa na tabela tb_task_indexes
        
    }
    
    private boolean inTaskIndexes(long wldId, Index ind){
        //verificar se o indice ja estah associado ah tarefa na tabela tb_task_indexes
        
        return false;
    }
         
    private boolean inLM(Index ind){
        //Verificar se existe indice na ML definido sobre a mesma tabela e memos atributos
        //Sera necessario utilizar as tabelas tb_candidate_index e tb_candidate_index_column
        
        return false;
    }
    
    //TODO: Implementar
    private void insertIndexLM(Index ind){
        //Inserir indice na LM
        //Beneficio acumulado = 0
        //NQ (numero de consultas que usa o indice) = 0
        //Inserir linha na tabela tb_candidate_index
        //Inserir uma ou mais linhas na tabela tb_candidate_index_column. Uma linha para cada coluna
        //Inserir linha na tabela tb_task_indexes
    }
    
    private ArrayList<SeqScan> getSeqScanOperations(Plan p){
        ArrayList<SeqScan> sso = new ArrayList();
        ArrayList<Column> columns = new ArrayList();
        Column col;
        String plan, name = null;
        String[] cols, attributes;
        long seqScanCost = 0;
 
        //Deixa o texto em caixa baixa e retira os espaços
        plan = ((p.getPlan()).toLowerCase()).replaceAll(" ", "");
        //Gera um vetor, tal que o seu tamanha é o número de SeqScan do plano
        String[] scan = plan.split("seqscanon");
        
        Pattern rest_name = Pattern.compile("(.*).cost");
        Pattern rest_cols = Pattern.compile("filter:(.*)");
        
        for(int i=1; i<scan.length; i++){
            Matcher nameM, str;
            
            nameM = rest_name.matcher(scan[i]);
            while(nameM.find()){
                name = nameM.group();
            }
            
            //Refinando a string
            name = name.substring(0,(name.length()-5)); 
            
            //Matcher filter = rest_cols.matcher(plan);
            str = rest_cols.matcher(scan[i]);  
            while(str.find()){
                scan[i] = str.group();
            }

            //Trata o SeqScan atual
            scan[i] = (scan[i]).replaceAll("[(]|[)]| |filter:", "");
            scan[i] = (scan[i]).replaceAll("::\\w+,*", "");
            scan[i] = (scan[i]).replaceAll("and|or", ",");

            //Cria um array de atributos
            attributes = scan[i].split(",");
            rest_cols = Pattern.compile(".*[|]");

            //Armazena os atributos no ArrayList columns
            for(int j=0; j<attributes.length; j++){
                attributes[j] = attributes[j].replaceFirst("(>|<|=)", "|");
                str = rest_cols.matcher(attributes[j]);
                while(str.find()){
                    attributes[j] = str.group();
                }
                attributes[j]=(attributes[j]).replaceAll("[|]", "");
                col = new Column();
                col.setName(attributes[j]);
                columns.add(col);
            }
             //Monta um objeto e o adiciona no ArrayList<SecScan>
            //TODO: Thayson: Preencher o atributo numberOfRows de SeqScan
            //TODO Thayson: Preencher o atributo filterType de Filter: equi para igualdade e theta para os demais
            seqScanCost = getSeqScanCost(name);
            //TODO: Thayson ajustar aqui
            //SeqScan objSeq = new SeqScan(name, columns, seqScanCost);
            //sso.add(objSeq);
        }
        
        return sso;  
    }
    
    private long getSeqScanCost(String tableName){
        Driver driver = new Driver();
        int numberOfTablePages = 0;
        try {
            String queryTemp = prop.getProperty("getSeqScanCostPostgresql");
            PreparedStatement preparedStatement = driver.prepareStatement(queryTemp);
            preparedStatement.setString(1, tableName);
            ResultSet result = driver.executeQuery(preparedStatement);
            if (result.next()) {
                numberOfTablePages = result.getInt("rel_pages");
                result.close();
            } else {
                result.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        
        return numberOfTablePages;
    }
    
    private long getIndexScanCost(Index ind){
        if (ind.getIndexType().equals("Primary")){
            
        }
        else{
            
        }
        return 0;
    }

    
    //PS. Um comando SQL pode ter várias cláusulas SELECT
    //É importante saber qual a tabela de uma determinada coluna. Logo, é necessário preencher o campo table da classe Column
    //SELECT nome FROM empregado WHERE salario >= ALL (SELECT salario_base FROM cargo)
    //Devolver um ArrayList de Tabela, onde cada objeto Tabela tem um conjunto de atributos usados em um ou mais cláusulas SELECT
    //Agrupar por tabela
    //SELECT e.enome, d.dnome FROM empregado e, departamento d WHERE e.lotacao=d.codigo
    //TODO: Thayson
    //Talvez, o mais fácil seja utilizar o Parser ZQL [Thuraisingham et al. 2010].
    private ArrayList<Column> getSelectColumns(SQL query) {
        ArrayList<Column> cols = new ArrayList();
        String sql = ((query.getSql()).toLowerCase()).replaceAll(" ", "");
        String[] attributes = null;
        String subStr = null;
        Column col;
        
        if(sql.matches("select")){  
            Pattern rest;
            Matcher str;
            //Cria-se uma restrição para seleção de uma parte do comando sql
            //Em seguida essa parte é armazenada na variavel chamada str
            rest = Pattern.compile("select(.*)from");
            str = rest.matcher(sql);

            //Transforma a variável str em uma string chamada substr
            while(str.find()){
                subStr = str.group();
            }
            //Pega apenas os atributos do SELECT
            subStr = subStr.substring(6,(subStr.length()-4)); 
            
            //Verifica se não é um SELECT genérico
            //Adiciona seus atributos em cols
            if(!(subStr=="*")){
                //Verifica se há uma virgula (neste caso há mais de um atributo
                if(subStr.matches(".*[,]*")){
                    attributes = subStr.split(",");
                    
                //Caso contrário há apenas um atributo.
                }else{
                    attributes[0] = subStr;
                }
                
                //Adiciona os atributos do SELECT no ArrayList cols
                for(int i=1 ; i<attributes.length ; i++){
                    col = new Column();
                    col.setName(attributes[i]);
                    cols.add(col);
                }
            }
        }
        return cols;  
    }
    
    private ArrayList<Column> getGroupColumns(SQL query) {
        ArrayList<Column> cols = new ArrayList();
        String sql = ((query.getSql()).toLowerCase()).replaceAll(" ", "");
        String[] attributes = null;
        String subStr = null;
        Column col;
        
        if(sql.matches("select")){  
            Pattern rest;
            Matcher str;
            //Cria-se uma restrição para seleção de uma parte do comando sql
            //Em seguida essa parte é armazenada na variavel chamada str
            rest = Pattern.compile("select(.*)from");
            str = rest.matcher(sql);

            //Transforma a variável str em uma string chamada substr
            while(str.find()){
                subStr = str.group();
            }    
            
            //Verifica se há um GROUP BY no comando sql
            //Adiciona seus atributos em cols
            if(sql.matches("(.*)groupby(.*)")){
                rest = Pattern.compile("groupby(.*)");
                str = rest.matcher(sql);          
                
                //Transforma a variável str em uma string chamada subStr
                while(str.find()){
                    subStr = str.group();
                }
                
                attributes = subStr.split("(groupby)|,");
                
                //Adiciona os atributos do ORDER BY no ArrayList cols            
                for(int i=1 ; i<attributes.length ; i++){
                    col = new Column();
                    col.setName(attributes[i]);
                    cols.add(col);
                }
            }
        }
        return cols;  
    }
    
    private ArrayList<Column> getOrderColumns(SQL query) {
        ArrayList<Column> cols = new ArrayList();
        String sql = ((query.getSql()).toLowerCase()).replaceAll(" ", "");
        String[] attributes = null;
        String subStr = null;
        Column col;
        
        if(sql.matches("select")){  
            Pattern rest;
            Matcher str;
            //Cria-se uma restrição para seleção de uma parte do comando sql
            //Em seguida essa parte é armazenada na variavel chamada str
            rest = Pattern.compile("select(.*)from");
            str = rest.matcher(sql);

            //Transforma a variável str em uma string chamada substr
            while(str.find()){
                subStr = str.group();
            }
             
            //Verifica se há um ORDER BY no comando sql
            //Adiciona seus atributos em cols
            if(sql.matches("(.*)orderby(.*)")){
                rest = Pattern.compile("orderby(.*)");
                str = rest.matcher(sql);          
                
                //Transforma a variável str em uma string chamada subStr
                while(str.find()){
                    subStr = str.group();
                }
                
                attributes = subStr.split("(orderby|asc,*|desc,*)");
                
                //Adiciona os atributos do ORDER BY no ArrayList cols            
                for(int i=1 ; i<attributes.length ; i++){
                    col = new Column();
                    col.setName(attributes[i]);
                    cols.add(col);
                }
            }    
        }
        return cols;  
    }
}
