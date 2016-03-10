package iqt;

import br.com.iqt.exception.HeuristicsSelectedException;
import iqt.exception.SqlInputException;
import br.com.iqt.util.Indenter;
import br.com.iqt.zql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/**
 * Possui métodos responsáveil por reescrever consultas com um plano de execução não
 * ótimo para outras semânticamente equivaletes com um plano de melhor desempenho.
 * 
 * @author Arlino Henrique Magalhães de Araújo
 */
public class AgentRewriter {    
    private Collection<Transformation> transformationList = null;//Lista transformações de reescrita
    private boolean rewrited;//Indica se houve reescrita ou não.
    private boolean error;
    private int count = 0;//Quantidade de reescritas realizadas.
    private int idSql;//Número da consulta.  Serve como orientação nas transformações executadas.
    private HeuristicSet heuristicSet;
    private double time = 0;
    //Responsavel pela sintonia assistida, caso seja utilizada
    private boolean allSql = false, //Se verdadeiro usa apenas uma conf. de heuristica para todas as SQL.
                    customSql = false;//Se verdadeira, usa conf. de heuristicas personalidas para cada SQL.
    private HeuristicsSelected heuristicsSelectedForAll;//Heuristica utilizada para todas as SQL
    private ArrayList customHeuristicsSelectedList;//Lista de heurísticas personalizadas para SQLs.
    
    private final static String fileNameHeuristicsForSql = "HeuristicsForSql.properties";
    private final static String fileNameHeuristicsForAllSql = "HeuristicsForAllSql.properties";

    public AgentRewriter(int database) {
        heuristicSet = new HeuristicSet(database);
    }

    public AgentRewriter(Dbms dbms) {
        heuristicSet = new HeuristicSet(dbms);
    }
    
    /**
     * @return
     * Retorna se o parser encontrou algum erro na sintaxe sql.
     */
    public boolean isError() {
        return error;
    }
    
    /**
     * @return
     * Retorna o tempo decorrido para realizar nas reescritas em nanosegundos.
     */
    public double getTimeNano() {
        return time;
    }

    /**
     * @return
     * Retorna o tempo decorrido para realizar nas reescritas em segundos.
     */
    public double getTimeSecond() {
        return getTimeNano()/1000000000;
    }
    
    /**
     * @return
     * Retorna o tempo decorrido para realizar nas reescritas em milisegundos.
     */
    public double getTimeMillis() {
        return getTimeNano()/1000000;
    }
    
    /**
     * Retorna a quantidade de reescritas realizadas.
     * @return
     * Retorna um valor inteiro correspondendo a quantidade de reescritas realizadas.
     */
    public int getCount() {
        return count;
    }
    
    /**
     * Informa se ocorreu alguma reescrita.
     * @return
     * Retorna <i>true</i> se ocorreu alguma reescrita e <i>false</i> caso contrário.
     */
    public boolean isRewrited() {
        return rewrited;
    }
    
    public void setInteractivity(boolean allSql, boolean customSql){
        this.allSql = allSql;
        this.customSql = customSql;
        if(customSql)
            customHeuristicsSelectedList = AgentRewriter.getHeuristicsForSqlList();
        if(allSql)
            heuristicsSelectedForAll = AgentRewriter.getHeuristicsForAllSql();
    }
    
    /**
     * Armazena infromações sobre uma reescrita executada: heurística realizada,
     * descrição da heurística, sql a ser reescrita, sql após a reescrita e número de
     * identificação de cada sql nas trasformações realizadas.
     * @param heuristic
     * Heurística executada.
     * @param description
     * Descrição da heurística.
     * @param sql1
     * SQL original.
     * @param sql2
     * SQL após a reescrita.
     * @param idSql1
     * Número de identificação da SQL original.
     * @param idSql2
     * Número de identificação da SQL após a reescrita.
     * 
     */
    private void addTransformation(String heuristic, String description, String sql1, String sql2, int idSql1, int idSql2){
        if(transformationList == null)
            transformationList = new Vector();
        Transformation t = new Transformation(heuristic, description, sql1, sql2, idSql1, idSql2);
        transformationList.add(t);
    }
    
     /**
     * Retorna uma lista de informações sobre cada reescria realizada.
     * @return
     * Retorna um <b>Vector</b> de <b>Transformation</b> com as informações de
     * reescrita.
     */
    public Collection<Transformation> getTransformationList() {
        return transformationList;
    }
    
    /**
     * Retorna a lista transformações sofridas pela SQL, desde a original até a 
     * última reescrita.
     * @return
     * Retorna um <b>Vector</b> de <b>String</b>.
     */
    public Collection getSqlsTransformationList(){
        Vector list = new Vector();
        for (Object object : transformationList) {
            Transformation t = (Transformation)object;
            list.add(t.getSql1());
        }
        Transformation last = ((Transformation)((Vector)transformationList).lastElement());
        list.add(last.getSql2());
        return list;
    }
    
    /**
     * Analisa uma SQL procurando por potenciais reescritas e as aplica, se encontrar alguma.
     * As heuristicas utilizadas na reescrita são todas as possíveis.
     * @param sqlInput
     * String da SQL a ser ajustada.
     * @return
     * Retorna uma String com a SQL ajustada, se for encontrada alguma reescrita,
     * ou a String da própria SQL dada em sqlImput, se não for encontrada nenhuma reeescrita.
     */
    public String analyseStatement(String sqlInput) throws ParseException, SqlInputException{
        return analyseStatement(sqlInput, null);
    }
    
    /**
     * Analisa uma SQL procurando por potenciais reescritas e as aplica, se encontrar alguma.
     * As heuristicas utilizadas na reescrita são escolhidas através de configurações em 
     * <i>heuristicsSelect</i>.
     * @param sqlInput
     * String da SQL a ser ajustada.
     * @param heuristicsSelect
     * Classe HeuristicsSelected que seta as heurísticas que devem ser usadas no processo 
     * de reescrita.
     * @return
     * Retorna uma String com a SQL ajustada, se for encontrada alguma reescrita,
     * ou a String da própria SQL dada em sqlImput, se não for encontrada nenhuma reeescrita.
     */
    public String analyseStatement(String sqlInput, HeuristicsSelected heuristicsSelected) throws ParseException, SqlInputException{
        long beginTime = System.nanoTime();
        this.time = 0;
        
        if(sqlInput == null){
            long endTime = System.nanoTime();
            this.time = endTime - beginTime;
            throw new SqlInputException("A string da SQL de entrada não pode ser um objeto nulo.");
        }
        if(sqlInput.equals("")){
            long endTime = System.nanoTime();
            this.time = endTime - beginTime;
            throw new SqlInputException("A string da SQL de entrada não pode ser vazia.");
        }
        
        String sqlInputTemp = sqlInput.trim();
        if(sqlInputTemp.charAt(sqlInputTemp.length()-1) !=';')
            sqlInput = sqlInputTemp + ";";
        /*
        ArrayList<String[]> replacementList = Replace.replaceCommands(sqlInput);
        int lastIndex = replacementList.size()-1;
        String[] last = replacementList.get(lastIndex);
        sqlInput = last[0];
        replacementList.remove(lastIndex);
        * 
        */
        
        //Responsável pela parte de sintonia interativa
        if(this.customSql){
            String sqlInputAux = Indenter.unindent(sqlInput).toUpperCase();
            for (Iterator<HeuristicsForSql> it = customHeuristicsSelectedList.iterator(); it.hasNext();) {
                HeuristicsForSql heuristicsForSql = it.next();
                if(heuristicsForSql.getSql().equals(sqlInputAux)){
                    heuristicsSelected = heuristicsForSql.getHeuristicsSelected();
                    break;
                }
            }
        }
        if(this.allSql && (!this.customSql || heuristicsSelected==null))
            heuristicsSelected = this.heuristicsSelectedForAll;
        
        if(heuristicsSelected == null){
            heuristicsSelected = new HeuristicsSelected();
            heuristicsSelected.setAllSelected();
        }
        
        String sqlOutput = sqlInput;
        ZqlParser p = null;
        ZStatement st = null;
        this.idSql = 1;
        this.transformationList = null;
        this.count = 0;
        this.rewrited = false;
        this.error = false;
        
        heuristicsSelected.setAllToNotExecuted();
        
        //manipula select do parametro sql
        try{
            p = new ZqlParser();
            p.initParser(new ByteArrayInputStream(sqlInput.getBytes()));
            st = p.readStatement();
            sqlOutput = st.toString();
            
            //Verifica a heuristica para remover tabela temporária
            if(heuristicsSelected.isTemporaryTableToSubQuerySelected())
                if(st instanceof ZQuery){
                    ZQuery query = (ZQuery) st;
                    //Reescreve SQLs que utilizam tabela temporária
                    st = this.analyseTemporaryTable(query, p);
                    sqlOutput = st.toString();
                }
            //Reescreve uma consulta
            if(st instanceof ZQuery){
                //Tenta reescrever a consulta enviada
                ZQuery query = (ZQuery) st;
                this.analyseQuery(query, heuristicsSelected);
                sqlOutput = query.toString();
            }else
                //Reescreve uma consulta
                if(st instanceof ZUpdate){
                    ZUpdate update = (ZUpdate) st;
                    this.rewrited = this.analyseUpdate(update, heuristicsSelected);
                    sqlOutput = update.toString();
                }else
                    //Reescreve um delete
                    if(st instanceof ZDelete){
                        ZDelete delete = (ZDelete) st;
                        this.rewrited = this.analyseDelete(delete, heuristicsSelected);
                        sqlOutput = delete.toString();
                    }
            
            long endTime = System.nanoTime();
            this.time = endTime - beginTime;
        }catch(Exception e){
            long endTime = System.nanoTime();
            this.time = endTime - beginTime;
            this.error = true;
            System.out.println(" Erro (AgentRewriter.analyseStatement()) : " + e.getMessage());
            //throw new ParseException(e.getMessage());
        }
        //sqlOutput = Replace.rereplaceCommands(replacementList, sqlOutput);
        return sqlOutput;
    }
    
    /**
     * Analisa uma SQL procurando por tabelas temporárias. Se encontrar alguma
     * consulta que crie uma tabela temporária, reescreve essa consulta juntamente
     * com a SQL que usa a tabela temporária para uma nova SQL.
     * @param query
     * Classe ZQuery instanciada com a consulta a ser analisado se há a criação de
     * uma tabela temporária.
     * @param p
     * Classe ZqlParser instanciada com a consulta que faz uso da tabela temporária.
     * @return
     * Se for feita uma reescrita, retorna uma ZStatement instanciada com a SQL ajustada 
     * com a remoção da criação de tabela temporária. Ou instanciada com a própria SQL 
     * dada em query, se não for feita reeescrita.
     */
    private ZStatement analyseTemporaryTable(ZQuery query, ZqlParser p){
        String queryImput, queryOutput;//Consultas de entrada e saida em uma reescrita.
        ZStatement st1 = query, st2 = null;
        
        //Verifica se a quere produz tabela temporária
        String into = query.getInto();
        if(into != null){
            //Extrai sql que usa a tabela temporária, que pode ser: consulta, update ou delete
            try{
                st2 = p.readStatement();
            }catch(Exception e){
                //NÃO DEVE SER CHAMADA EXCESSÃO POR CONTA DE NÃO EXISTIR UMA SEGUNDA SQL E GERAR UM ERRO
            }
            if(st2 != null){
                //Reescreve a consulta que produz tabela temorária para um join com a consulta da segunda sql
                if(st2 instanceof ZQuery){
                    ZQuery query2 = (ZQuery)st2;
                    queryImput = query.toString() + "; " + query2.toString();
                    st1 = heuristicSet.temporaryTableToSubQuery(query, query2);
                    queryOutput = st1.toString();
                    String description = "Remove tabela temporária.";
                    this.addTransformation("temporaryTableToSubQuery", description, queryImput, queryOutput, idSql, ++idSql);
                    this.rewrited = true;
                    ++this.count;
                    
                }else{
                    //Reescreve a consulta que produz tabela temorária tabela temorária para um join com o update da segunda sql
                    if(st2 instanceof ZUpdate){
                        ZUpdate update = (ZUpdate)st2;
                        queryImput = query.toString() + "; " + update.toString();
                        st1 = heuristicSet.temporaryTableToSubQueryWithUpdate(query, update);
                        queryOutput = st1.toString();
                        String description = "Remove tabela temporária em Update.";
                        this.addTransformation("temporaryTableToSubQuery", description, queryImput, queryOutput, idSql, ++idSql);
                        this.rewrited = true;
                        ++this.count;
                        
                    }else{
                        //Reescreve a consulta que produz tabela temorária tabela temorária para um join com o delete da segunda sql
                        if(st2 instanceof ZDelete){
                            ZDelete delete = (ZDelete)st2;
                            queryImput = query.toString() + "; " + delete.toString();
                            st1 = heuristicSet.temporaryTableToSubQueryWithDelete(query, delete);
                            queryOutput = st1.toString();
                            String description = "Remove tabela temporária em Delete.";
                            this.addTransformation("temporaryTableToSubQuery", description, queryImput, queryOutput, idSql, ++idSql);
                            this.rewrited = true;
                            ++this.count;
                        }
                    }
                }
                
                //Faz uma chamada recursiva para verificar uso de mais tabelas temporárias
                if(st1 instanceof ZQuery)
                    st1 = this.analyseTemporaryTable((ZQuery)st1, p);
            }
        }
        
        return st1;
    }
    
    /**
     * Analisa uma consulta procurando por oportunidade de reescritas e as reescreve,
     * se encontrar alguma.
     * @param query
     * Classe ZQuery instanciada com a consulta a ser realizada a possível reescrita.
     * @return
     * Retorna <i>true</i> se tiver acontecido alguma reescrita e <i>false</i>, caso contrário.
     */
    private boolean analyseQuery(ZQuery query, HeuristicsSelected heuristicsSelect){
        String sqlImput, sqlOutput;//Consultas de entrada e saida em uma reescrita.
        boolean querRewrited = false;//Retorna se a consulta foi reescrita
        
        //Verifica uso de havingToWhere, que não é uma heuristica mas ajudar na realização das demais.
        if(heuristicsSelect.isHavingToWhereSelected()){
            sqlImput = query.toString();
            if(heuristicSet.havingToWhere(query)){
                sqlOutput = query.toString();
                String description = "Remove cláusulas de HAVING desnecessário para o WHERE.";
                this.addTransformation("havingToWhere", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setHavingToWhereExecuted(true);
            }
        }
        
        //Verifica uso da heurística groupbyToWhere
        if(heuristicsSelect.isRemoveGroupbySelected()){
            sqlImput = query.toString();
            if(heuristicSet.removeGroupby(query)){
                sqlOutput = query.toString();
                String description = "Remove GROUP BY desencessário.";
                this.addTransformation("groupbyToWhere", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setRemoveGroupbyExecuted(true);
            }
        }
        
        //Verifica uso da heurística moveFunction
        if(heuristicsSelect.isMoveFunctionSelected()){
            sqlImput = query.toString();
            if(heuristicSet.moveFunction(query.getWhere(),query.getFrom())){
                sqlOutput = query.toString();
                String description = "Remove função de uma coluna com índice.";
                this.addTransformation("moveFunction", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setMoveFunctionExecuted(true);
            }
        }
        
        //Verifica uso da heurística orToUnion
        if(heuristicsSelect.isOrToUnionSelected()){
            sqlImput = query.toString();
            if(heuristicSet.orToUnion(query)){//O operador mais externo deve ser o OR
                sqlOutput = query.toString();
                String description = "";
                this.addTransformation("orToUnion", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setOrToUnionExecuted(true);
            }
        }
        
        //Verifica uso da heurística allToSubquery
        if(heuristicsSelect.isAllToSubquerySelected()){
            sqlImput = query.toString();
            if(heuristicSet.allToSubquery(query.getWhere())){
                sqlOutput = query.toString();
                String description = "Remove ALL.";
                this.addTransformation("allToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setAllToSubqueryExecuted(true);
            }
        }
        
        //Verifica uso da heurística anyToSubquery
        if(heuristicsSelect.isAnyToSubquerySelected()){
            sqlImput = query.toString();
            if(heuristicSet.anyToSubquery(query.getWhere())){
                sqlOutput = query.toString();
                String description = "Remove ANY.";
                this.addTransformation("anyToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setAnyToSubqueryExecuted(true);
            }
        }
        
        //Verifica uso da heurística someToSubquery
        if(heuristicsSelect.isSomeToSubquerySelected()){
            sqlImput = query.toString();
            if(heuristicSet.someToSubquery(query.getWhere())){
                sqlOutput = query.toString();
                String description = "Remove SOME.";
                this.addTransformation("someToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setSomeToSubqueryExecuted(true);
            }
        }
        
        //Verifica uso da heurística inToJoin
        if(heuristicsSelect.isInToJoinSelected()){
            sqlImput = query.toString();
            if(heuristicSet.inToJoin(query)){
                sqlOutput = query.toString();
                String description = "Transforma IN em uma junção.";
                this.addTransformation("inToJoin", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setInToJoinExecuted(true);
            }
        }
        
        //Verifica uso da heurística moveAtithmetcExpression
        if(heuristicsSelect.isMoveAtithmetcExpressionSelected()){
            sqlImput = query.toString();
            if(heuristicSet.moveAtithmetcExpression(query.getWhere(), query.getFrom())){
                sqlOutput = query.toString();
                String description = "Move expressão aritmética associada a uma coluna com índice.";
                this.addTransformation("moveAtithmetcExpression", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setMoveAtithmetcExpressionExecuted(true);
            }
        }
        
        //Verifica uso da heurística removeDistinct
        if(heuristicsSelect.isRemoveDistinctSelected()){
            sqlImput = query.toString();
            if(heuristicSet.removeDistinct(query)){
                sqlOutput = query.toString();
                String description = "Remove DISTINCT desnecessário.";
                this.addTransformation("removeDistinct", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
                heuristicsSelect.setRemoveDistinctExecuted(true);
            }
        }
        
        //Verifica se as sub-consultas da consulta enviada podem ser reescritas
        sqlImput = query.toString();
        int actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        if(this.analyseSubqueryInWhere(query.getWhere(), heuristicsSelect)){
            sqlOutput = query.toString();
            String description = "Reescrita de sub-consulta.";
            description = "Reescrita de sub-consulta(s) na cláusula Where da SQL(" + actualId +
                    "). A reescrita está compreendida na(s) transformações(s) "
                    + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
            this.addTransformation("analyseSubquery", description, sqlImput, sqlOutput, actualId, ++idSql);
            this.rewrited = true;
            querRewrited = true;
        }else if(actualId != 0)  --idSql;
        
        //Verifica se as sub-consultas em forma de tabela da consulta enviada podem ser reescritas
        sqlImput = query.toString();
        actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        Vector from = query.getFrom();
        if(from != null){
            if(this.analyseSubqueryInFrom(from, heuristicsSelect)){
                sqlOutput = query.toString();
                String description = "Reescrita de sub-consulta em forma de tabela.";
                description = "Reescrita de sub-consulta(s) na cláusula From da SQL(" + actualId +
                        "). A reescrita está compreendida na(s) transformações(s) "
                        + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
                this.addTransformation("analyseQueryInFrom", description, sqlImput, sqlOutput, actualId, ++idSql);
                this.rewrited = true;
                querRewrited = true;
            }else if(actualId != 0)  --idSql;
        }else if(actualId != 0)  --idSql;
        
        //Verifica se as sub-consultas em colunas retornadas pela consulta enviada podem ser reescritas
        sqlImput = query.toString();
        actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        Vector select = query.getSelect();
        if(select != null){
            if(this.analyseSubqueryInColumns(select, heuristicsSelect)){
                sqlOutput = query.toString();
                String description = "Reescrita de sub-consulta no lugar de uma coluna selecionada.";
                description = "Reescrita de sub-consulta(s) na cláusula From da SQL(" + actualId +
                        "). A reescrita está compreendida na(s) transformações(s) "
                        + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
                this.addTransformation("analyseSubqueryInColumns", description, sqlImput, sqlOutput, actualId, ++idSql);
                this.rewrited = true;
                querRewrited = true;
            }else if(actualId != 0)  --idSql;
        }else if(actualId != 0)  --idSql;
        
        //Verifica se as sub-consultas em colunas retornadas pela consulta enviada podem ser reescritas
        sqlImput = query.toString();
        actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        ZGroupBy groupBy = query.getGroupBy();
        if(groupBy != null){
            ZExp having = groupBy.getHaving();
            if(having != null){
                if(this.analyseHaving(having, query.getFrom(), heuristicsSelect)){
                    sqlOutput = query.toString();
                    String description = "Reescrita de sub-consulta em expressão Having.";
                    description = "Reescrita de sub-consulta(s) na cláusula From da SQL(" + actualId +
                            "). A reescrita está compreendida na(s) transformações(s) "
                            + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
                    this.addTransformation("analyseSubqueryInColumns", description, sqlImput, sqlOutput, actualId, ++idSql);
                    this.rewrited = true;
                    querRewrited = true;
                }else if(actualId != 0)  --idSql;
            }else if(actualId != 0)  --idSql;
        }
        
        //Verifica a reescrita das outras consultas de um conjunto(UNION, MINUS, INTERSECT), se existir
        sqlImput = query.toString();
        actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        if(this.analyseQuerySet(query, heuristicsSelect)){
            sqlOutput = query.toString();
            String description = "Reescrita da segunda consulta na operação de conjunto " +
                    "da SQL(" + actualId + "). A reescrita está compreendida na(s) transformações(s) "
                    + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
            this.addTransformation("analyseQuerySet", description, sqlImput, sqlOutput, actualId, ++idSql);
            this.rewrited = true;
            querRewrited = true;
        }else if(actualId != 0)  --idSql;
        
        return querRewrited;
    }
    
    /**
     * Analisa uma consulta procurando por oportunidades de reescritas em SQLs de uma 
     * operação de conjunto (UNION, INTERSECT e MINUS) e as reescreve, se encontrar alguma.
     * @param query
     * Classe ZQuery instanciada com a consulta a ser realizada a possível reescrita.
     * @return
     * Retorna <i>true</i> se tiver acontecido alguma reescrita e <i>false</i>, caso contrário.
     */
    private boolean analyseQuerySet(ZQuery query, HeuristicsSelected heuristicsSelect){
        ZExpression set = query.getSet();
        boolean querySetRewrited = false;
               
        if(set != null){
            ZQuery operandQuery = (ZQuery)set.getOperand(0);
            querySetRewrited = this.analyseQuery(operandQuery, heuristicsSelect);
        }
        return querySetRewrited;
    }
    
    /**
     * Analisa uma SQL Update procurando por oportunidade de reescritas e as reescreve,
     * se encontrar alguma.
     * @param update
     * Classe ZUpdate instanciada com a SQL a ser realizada a possível reescrita.
     * @return
     * Retorna <i>true</i> se tiver acontecido alguma reescrita e <i>false</i>, caso contrário.
     */
    private boolean analyseUpdate(ZUpdate update, HeuristicsSelected heuristicsSelect){
        String sqlImput, sqlOutput;//Consultas de entrada e saida em uma reescrita.
        boolean updateRewrited = false;
        ZExpression where = (ZExpression)update.getWhere();
        
        //Verifica uso da heurística moveFunction
        sqlImput = update.toString();
        if(heuristicSet.moveFunction(update.getWhere(),update.getFrom())){
            sqlOutput = update.toString();
            String description = "Remove função de uma coluna com índice em SQL Update.";
            this.addTransformation("moveFunction", description, sqlImput, sqlOutput, idSql, ++idSql);
            this.rewrited = true;
            updateRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística allToSubquery
        sqlImput = update.toString();
        if(heuristicSet.allToSubquery(update.getWhere())){
            sqlOutput = update.toString();
            String description = "Remove ALL em SQL Update.";
            this.addTransformation("allToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
            updateRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística anyToSubquery
        sqlImput = update.toString();
        if(heuristicSet.anyToSubquery(update.getWhere())){
            sqlOutput = update.toString();
            String description = "Remove ANY em SQL Update.";
            this.addTransformation("anyToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
            updateRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística someToSubquery
        sqlImput = update.toString();
        if(heuristicSet.someToSubquery(update.getWhere())){
            sqlOutput = update.toString();
            String description = "Remove SOME em SQL Update.";
            this.addTransformation("someToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
            updateRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística inToJoin
        sqlImput = update.toString();
        if(heuristicSet.inToJoinWithUpdate(update)){
            sqlOutput = update.toString();
            String description = "Transforma IN em uma junção em SQL Update.";
            this.addTransformation("inToJoin", description, sqlImput, sqlOutput, idSql, ++idSql);
            updateRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística moveAtithmetcExpression
        sqlImput = update.toString();
        //Se não possuir tabela em from, cria um vetor de tabelas com a tabela de atualização
        Vector from = update.getFrom();
        if(from == null){
            String table = update.getTable();
            String alias = update.getAlias();
            ZFromItem fi = new ZFromItem(table);
            fi.setAlias(alias);
            from = new Vector();
            from.add(fi);
        }
        if(heuristicSet.moveAtithmetcExpression(update.getWhere(), from)){
            sqlOutput = update.toString();
            String description = "Move expressão aritmética associada a uma coluna com índice.";
            this.addTransformation("moveAtithmetcExpression", description, sqlImput, sqlOutput, idSql, ++idSql);
            this.rewrited = true;
            updateRewrited = true;
            ++this.count;
        }
        
        //Verifica se as sub-consultas do update podem ser reescritas
        sqlImput = update.toString();
        int actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        where = (ZExpression)update.getWhere();
        if(this.analyseSubqueryInWhere(where, heuristicsSelect)){
            sqlOutput = update.toString();
            String description = "Reescrita de sub-consulta.";
            description = "Reescrita de sub-consulta(s) na cláusula Where da SQL(" + actualId +
                    "). A reescrita está compreendida na(s) transformações(s) "
                    + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
            this.addTransformation("analyseSubquery", description, sqlImput, sqlOutput, actualId, ++idSql);
            this.rewrited = true;
            updateRewrited = true;
        }else if(actualId != 0)  --idSql;
        
        //Verifica se as sub-consultas em forma de tabela do update podem ser reescritas
        sqlImput = update.toString();
        actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        from = update.getFrom();
        if(from != null){
            if(this.analyseSubqueryInFrom(from, heuristicsSelect)){
                sqlOutput = update.toString();
                String description = "Reescrita de sub-consulta em forma de tabela.";
                description = "Reescrita de sub-consulta(s) na cláusula From da SQL(" + actualId +
                        "). A reescrita está compreendida na(s) transformações(s) "
                        + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
                this.addTransformation("analyseQueryInFrom", description, sqlImput, sqlOutput, actualId, ++idSql);
                this.rewrited = true;
                updateRewrited = true;
            }else if(actualId != 0)  --idSql;
        }else if(actualId != 0)  --idSql;
        
        return updateRewrited;
    }
    
    /**
     * Analisa uma SQL Delete procurando por oportunidade de reescritas e as reescreve,
     * se encontrar alguma.
     * @param delete
     * Classe ZDelete instanciada com a consulta a ser realizada a possível reescrita.
     * @return
     * Retorna <i>true</i> se tiver acontecido alguma reescrita e <i>false</i>, caso contrário.
     */
    private boolean analyseDelete(ZDelete delete, HeuristicsSelected heuristicsSelect){
        String sqlImput, sqlOutput;//Consultas de entrada e saida em uma reescrita.
        boolean deleteRewrited = false;
        ZExpression where = (ZExpression)delete.getWhere();
        
        //Verifica uso da heurística moveFunction
        sqlImput = delete.toString();
        if(heuristicSet.moveFunction(delete.getWhere(),delete.getUsing())){
            sqlOutput = delete.toString();
            String description = "Remove função de uma coluna com índice em SQL Update.";
            this.addTransformation("moveFunction", description, sqlImput, sqlOutput, idSql, ++idSql);
            this.rewrited = true;
            deleteRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística allToSubquery
        sqlImput = delete.toString();
        if(heuristicSet.allToSubquery(delete.getWhere())){
            sqlOutput = delete.toString();
            String description = "Remove ALL em SQL Delete.";
            this.addTransformation("allToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
            deleteRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística anyToSubquery
        sqlImput = delete.toString();
        if(heuristicSet.anyToSubquery(delete.getWhere())){
            sqlOutput = delete.toString();
            String description = "Remove ANY em SQL Delete.";
            this.addTransformation("allToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
            deleteRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística someToSubquery
        sqlImput = delete.toString();
        if(heuristicSet.someToSubquery(delete.getWhere())){
            sqlOutput = delete.toString();
            String description = "Remove SOME em SQL Delete.";
            this.addTransformation("someToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
            deleteRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística inToJoin
        sqlImput = delete.toString();
        if(heuristicSet.inToJoinWithDelete(delete)){
            sqlOutput = delete.toString();
            String description = "Transforma IN em uma junção em SQL Delete.";
            this.addTransformation("inToJoin", description, sqlImput, sqlOutput, idSql, ++idSql);
            deleteRewrited = true;
            ++this.count;
        }
        
        //Verifica uso da heurística moveAtithmetcExpression
        sqlImput = delete.toString();
        //Se não possuir tabela em from, cria um vetor de tabelas com a tabela de atualização
        Vector using = delete.getUsing();
        if(using == null){
            String table = delete.getTable();
            String alias = delete.getAlias();
            ZFromItem fi = new ZFromItem(table);
            fi.setAlias(alias);
            using = new Vector();
            using.add(fi);
        }
        if(heuristicSet.moveAtithmetcExpression(delete.getWhere(), using)){
            sqlOutput = delete.toString();
            String description = "Move expressão aritmética associada a uma coluna com índice.";
            this.addTransformation("moveAtithmetcExpression", description, sqlImput, sqlOutput, idSql, ++idSql);
            this.rewrited = true;
            deleteRewrited = true;
            ++this.count;
        }
        
        //Verifica se as sub-consultas do delete podem ser reescritas
        sqlImput = delete.toString();
        int actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        where = (ZExpression)delete.getWhere();
        if(this.analyseSubqueryInWhere(where, heuristicsSelect)){
            sqlOutput = delete.toString();
            String description = "Reescrita de sub-consulta.";
            description = "Reescrita de sub-consulta(s) na cláusula Where da SQL(" + actualId +
                    "). A reescrita está compreendida na(s) transformações(s) "
                    + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
            this.addTransformation("analyseSubquery", description, sqlImput, sqlOutput, actualId, ++idSql);
            this.rewrited = true;
            deleteRewrited = true;
        }else if(actualId != 0)  --idSql;
        
        //Verifica se as sub-consultas em forma de tabela do delete podem ser reescritas
        sqlImput = delete.toString();
        actualId = idSql;
        if(idSql == 1)//O id da consulta será 0 se ela não sofrer alterações, mas suas subconsultas sim
            actualId = 0;
        else ++idSql;
        using = delete.getUsing();
        if(using != null){
            if(this.analyseSubqueryInFrom(using, heuristicsSelect)){
                sqlOutput = delete.toString();
                String description = "Reescrita de sub-consulta em forma de tabela.";
                description = "Reescrita de sub-consulta(s) na cláusula From da SQL(" + actualId +
                        "). A reescrita está compreendida na(s) transformações(s) "
                        + "iniciando em SQL(" + (actualId+1) + ") até SQL(" + idSql + ").";
                this.addTransformation("analyseQueryInFrom", description, sqlImput, sqlOutput, actualId, ++idSql);
                this.rewrited = true;
                deleteRewrited = true;
            }else if(actualId != 0)  --idSql;
        }else if(actualId != 0)  --idSql;
        
        return deleteRewrited;
    }
    
    /**
     * Analisa sub-consultas de uma SQL na expressão Where de uma SQL procurando por oportunidade de 
     * reescritas e as reescreve, se encontrar alguma.
     * @param exp
     * Classe ZExp instanciada com a expressão Where onde serão procuradas sub-consultas para 
     * realizada a possível reescrita.
     * @return
     * Retorna <i>true</i> se tiver acontecido reescrita em alguma sub-consulta e 
     * <i>false</i>, caso contrário.
     */
    private boolean analyseSubqueryInWhere(ZExp exp, HeuristicsSelected heuristicsSelect){
        boolean result = false;
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            Vector operands = expression.getOperands();
            for (int i = 0; i < operands.size(); i++) {
                Object op = operands.get(i);
                
                //Se a sub-expressão(op) for uma sub-consulta então verifica possiveis reescritas
                if(op instanceof ZQuery){
                    ZQuery query = (ZQuery)op;
                    boolean result2 = this.analyseQuery(query, heuristicsSelect);
                    if(result2) result = true;
                    //Se a sub-consulta tiver um where verifica sua reescirta
                    ZExp where = query.getWhere();
                    if(where != null)
                        this.analyseSubqueryInWhere(where, heuristicsSelect);
                }else{
                    //Faz chamada recursiva para procurar sub-consultas em sub-expressões da sub-expressão
                    boolean result2 = this.analyseSubqueryInWhere((ZExp)op, heuristicsSelect);
                    if(result2) result = true;
                }
            }
        }
        return result;
    }
    
    /**
     * Analisa Having e suas sub-consultas procurando por oportunidade de 
     * reescritas e as reescreve, se encontrar alguma.
     * @param exp
     * Classe ZExp instanciada com a expressão Where onde serão procuradas sub-consultas para 
     * realizada a possível reescrita.
     * @param tables
     * Vetor de tabelas da SQL da expressão Where ou Having. As tabelas são necessárias
     * para a heurística de remoção de expressão aritmética de uma coluna que possui índice.
     * @return
     * Retorna <i>true</i> se tiver acontecido reescrita em alguma sub-consulta e 
     * <i>false</i>, caso contrário.
     */
    private boolean analyseHaving(ZExp exp, Vector from, HeuristicsSelected heuristicsSelect){
        boolean result = false;
        String sqlImput, sqlOutput;
            //Verifica uso da heurística allToSubquery
            sqlImput = exp.toString();
            if(heuristicSet.allToSubquery(exp)){
                sqlOutput = exp.toString();
                String description = "Remove ALL em expressão HAVING.";
                this.addTransformation("allToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                result = true;
                ++this.count;
            }
            
            //Verifica uso da heurística anyToSubquery
            sqlImput = exp.toString();
            if(heuristicSet.anyToSubquery(exp)){
                sqlOutput = exp.toString();
                String description = "Remove ANY em expressão HAVING.";
                this.addTransformation("anyToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                result = true;
                ++this.count;
            }
            
            //Verifica uso da heurística someToSubquery
            sqlImput = exp.toString();
            if(heuristicSet.someToSubquery(exp)){
                sqlOutput = exp.toString();
                String description = "Remove SOME em expressão HAVING.";
                this.addTransformation("someToSubquery", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                result = true;
                ++this.count;
            }
            /*
            //Verifica uso da heurística inToJoin
            if(this.verifyInToJoin(exp)){
                sqlImput = exp.toString();
                
                        this.inToJoin(exp);
                
                sqlOutput = exp.toString();
                String description = "Transforma IN em uma junção.";
                this.addTransformation("inToJoin", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                querRewrited = true;
                ++this.count;
            }
             * 
             */
            
            //Verifica uso da heurística moveAtithmetcExpression
            sqlImput = exp.toString();
            if(heuristicSet.moveAtithmetcExpression(exp, from)){
                sqlOutput = exp.toString();
                String description = "Move expressão aritmética associada a uma coluna com índice em expressão HAVING.";
                this.addTransformation("moveAtithmetcExpression", description, sqlImput, sqlOutput, idSql, ++idSql);
                this.rewrited = true;
                result = true;
                ++this.count;
            }
        
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            Vector operands = expression.getOperands();
            for (int i = 0; i < operands.size(); i++) {
                Object op = operands.get(i);
                
                //Se a sub-expressão(op) do havig for uma sub-consulta então verifica possiveis reescritas
                if(op instanceof ZQuery){
                    ZQuery query = (ZQuery)op;
                    boolean result2 = this.analyseQuery(query, heuristicsSelect);
                    if(result2) result = true;
                    //Se a sub-consulta tiver um having verifica sua reescirta
                    ZGroupBy groupBy = query.getGroupBy();
                    if(groupBy != null){
                        ZExp having = groupBy.getHaving();
                        if(having != null)
                            result2 = this.analyseHaving(having, from, heuristicsSelect);
                        if(result2) result = true;
                    }
                }else{
                    //Faz chamada recursiva para procurar sub-consultas em sub-expressões da sub-expressão
                    boolean result2 = this.analyseHaving((ZExp)op, from, heuristicsSelect);
                    if(result2) result = true;
               }
            }
        }
        return result;
    }
    
    /**
     * Analisa sub-consultas em na cláusula From de uma SQL procurando por oportunidade de 
     * reescritas e as reescreve, se encontrar alguma.
     * @param from
     * Classe Vector instanciada com classes ZQuery ou ZFromItem.
     * @return
     * Retorna <i>true</i> se tiver acontecido alguma reescrita e <i>false</i>, caso contrário.
     */
    private boolean analyseSubqueryInFrom(Vector from, HeuristicsSelected heuristicsSelect){
        boolean result = false;
        if(from != null)
            for (Object tb : from) {
                
                //Se a tabela(tb) for uma sub-consulta então verifica possiveis reescritas
                if(tb instanceof ZQuery){
                    ZQuery tableQuery = (ZQuery)tb;
                    boolean result2 = this.analyseQuery(tableQuery, heuristicsSelect);
                    if(result2) result = true;
                    
                    //Analisa as tabelas da sub-consulta a procura de outras tabelas em forma de sub-consulta
                    Vector from1 = tableQuery.getFrom();
                    result2 = analyseSubqueryInFrom(from1, heuristicsSelect);
                    if(result2)
                        result = true;
                }
            }
        return result;
    }
    
    /**
     * Analisa sub-consultas nas colunas seleciodas de uma consulta procurando por oportunidade de 
     * reescritas e as reescreve, se encontrar alguma.
     * @param select
     * Classe Vector instanciada com classes ZQuery ou ZSelectItem.
     * @return
     * Retorna <i>true</i> se tiver acontecido alguma reescrita e <i>false</i>, caso contrário.
     */
    private boolean analyseSubqueryInColumns(Vector select, HeuristicsSelected heuristicsSelect){
        boolean result = false;
        if(select != null)
            for (Object cols : select) {//Procura por consulta lista de colunas
                ZSelectItem col = (ZSelectItem)cols;
                
                //Se a coluna(col) for uma sub-consulta então verifica possiveis reescritas
                if(col.isQuery()){
                    ZQuery subquery = col.getQuery();
                    boolean result2 = this.analyseQuery(subquery, heuristicsSelect);
                    if(result2){//Atualiza a sub-consulta se tiver sido reescrita
                        col.setExpression(subquery);
                        result = true;
                    }
                    
                    //Verifica se as colunas retornadas da sub-consulta também possuem sub-consultas
                    Vector select1 = subquery.getSelect();
                    result2 = analyseSubqueryInColumns(select1, heuristicsSelect);
                    if(result2)
                        result = true;
                }
            }
        return result;
    }
    
    public static int getKey(String sql) throws FileNotFoundException, IOException{
        File file = new File(fileNameHeuristicsForSql);
        FileInputStream fis = new FileInputStream(file);
        
        Properties props = new Properties();
        //lê os dados que estão no arquivo
        props.load(fis);
        fis.close();
        
        int count = Integer.parseInt(props.getProperty("counter"));
        for (int i = 1; i <= count; i++) {
            String key = "sql" + i;
            String value = props.getProperty(key);
            if(value.equals(sql))
                return Integer.parseInt(key.substring(key.length()-1));
        }
        
        return -1;
    }
    
    public static void addHeuristicsForSql(String sql, HeuristicsSelected heuristicsSelected) throws FileNotFoundException, IOException, SqlInputException, HeuristicsSelectedException{
        if(sql == null)
            throw new SqlInputException("A SQL original não pode ser um objeto nulo.");
        
        if(sql.equals(""))
            throw new SqlInputException("A SQL original não pode ser vazia.");
        
        if(heuristicsSelected == null)
            throw new HeuristicsSelectedException();
        
        sql = Indenter.unindent(sql).toUpperCase();
        
        File file = new File(fileNameHeuristicsForSql);
        Properties props = new Properties();
        
        //lê os dados que estão no arquivo
        FileInputStream fis = new FileInputStream(file);
        props.load(fis);
        fis.close();
        int counter = Integer.parseInt(props.getProperty("counter"));
        
        int counterAux = AgentRewriter.getKey(sql);
        System.out.println(counterAux);
        if(counterAux > 0)
            counter = counterAux;
        else
            counter++;
        
        //grava propreidades  no arquivo
        FileOutputStream fos = new FileOutputStream(file);
        if(counterAux < 0)
            props.setProperty("counter", Integer.toString(counter));
        props.setProperty("sql" + counter, sql);
        props.setProperty("havingToWhereSelected" + counter, Boolean.toString(heuristicsSelected.isHavingToWhereSelected()));
        props.setProperty("removeGroupbySelected" + counter, Boolean.toString(heuristicsSelected.isRemoveGroupbySelected()));
        props.setProperty("moveFunctionSelected" + counter, Boolean.toString(heuristicsSelected.isMoveFunctionSelected()));
        props.setProperty("orToUnionSelected" + counter, Boolean.toString(heuristicsSelected.isOrToUnionSelected()));
        props.setProperty("allToSubquerySelected" + counter, Boolean.toString(heuristicsSelected.isAllToSubquerySelected()));
        props.setProperty("anyToSubquerySelected" + counter, Boolean.toString(heuristicsSelected.isAnyToSubquerySelected()));
        props.setProperty("someToSubquerySelected" + counter, Boolean.toString(heuristicsSelected.isSomeToSubquerySelected()));
        props.setProperty("inToJoinSelected" + counter, Boolean.toString(heuristicsSelected.isInToJoinSelected()));
        props.setProperty("moveAtithmetcExpressionSelected" + counter, Boolean.toString(heuristicsSelected.isMoveAtithmetcExpressionSelected()));
        props.setProperty("removeDistinctSelected" + counter, Boolean.toString(heuristicsSelected.isRemoveDistinctSelected()));
        props.setProperty("temporaryTableToSubQuerySelected" + counter, Boolean.toString(heuristicsSelected.isTemporaryTableToSubQuerySelected()));
        props.store(fos, "SQLs e as heurísticas que devem ser aplicadas a elas.");
        fos.close();
    }
    
    public static HeuristicsSelected getHeuristicsForSql(String sql){
        File file = new File(fileNameHeuristicsForAllSql);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            return null;
        }
        
        Properties props = new Properties();
        try {
            //lê os dados que estão no arquivo
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            return null;
        }
        
        int counter;
        try {
            counter = AgentRewriter.getKey(sql);
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
        if(counter < 0)
            return null;
        
        HeuristicsSelected heuristicsSelected = new HeuristicsSelected();
        //lê proriedades do arquivo
        heuristicsSelected.setHavingToWhereSelected(Boolean.parseBoolean(props.getProperty("havingToWhereSelected" + counter)));
        heuristicsSelected.setRemoveGroupbySelected(Boolean.parseBoolean(props.getProperty("removeGroupbySelected" + counter)));
        heuristicsSelected.setMoveFunctionSelected(Boolean.parseBoolean(props.getProperty("moveFunctionSelected" + counter)));
        heuristicsSelected.setOrToUnionSelected(Boolean.parseBoolean(props.getProperty("orToUnionSelected" + counter)));
        heuristicsSelected.setAllToSubquerySelected(Boolean.parseBoolean(props.getProperty("allToSubquerySelected" + counter)));
        heuristicsSelected.setAnyToSubquerySelected(Boolean.parseBoolean(props.getProperty("anyToSubquerySelected" + counter)));
        heuristicsSelected.setSomeToSubquerySelected(Boolean.parseBoolean(props.getProperty("someToSubquerySelected" + counter)));
        heuristicsSelected.setInToJoinSelected(Boolean.parseBoolean(props.getProperty("inToJoinSelected" + counter)));
        heuristicsSelected.setMoveAtithmetcExpressionSelected(Boolean.parseBoolean(props.getProperty("moveAtithmetcExpressionSelected" + counter)));
        heuristicsSelected.setRemoveDistinctSelected(Boolean.parseBoolean(props.getProperty("removeDistinctSelected" + counter)));
        heuristicsSelected.setTemporaryTableToSubQuerySelected(Boolean.parseBoolean(props.getProperty("temporaryTableToSubQuerySelected" + counter)));
        
        return heuristicsSelected;
    }
    
    public static ArrayList getHeuristicsForSqlList(){
        File file = new File(fileNameHeuristicsForSql);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            return null;
        }
        
        Properties props = new Properties();
        try {
            //lê os dados que estão no arquivo
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            return null;
        }
        
        int count = Integer.parseInt(props.getProperty("counter"));
        if(count == 0)
            return null;
        
        ArrayList list = new ArrayList();
        for (int i = 1; i <= count; i++) {
            HeuristicsSelected heuristicsSelected = new HeuristicsSelected();
            //lê proriedades do arquivo
            heuristicsSelected.setHavingToWhereSelected(Boolean.parseBoolean(props.getProperty("havingToWhereSelected" + i)));
            heuristicsSelected.setRemoveGroupbySelected(Boolean.parseBoolean(props.getProperty("removeGroupbySelected" + i)));
            heuristicsSelected.setMoveFunctionSelected(Boolean.parseBoolean(props.getProperty("moveFunctionSelected" + i)));
            heuristicsSelected.setOrToUnionSelected(Boolean.parseBoolean(props.getProperty("orToUnionSelected" + i)));
            heuristicsSelected.setAllToSubquerySelected(Boolean.parseBoolean(props.getProperty("allToSubquerySelected" + i)));
            heuristicsSelected.setAnyToSubquerySelected(Boolean.parseBoolean(props.getProperty("anyToSubquerySelected" + i)));
            heuristicsSelected.setSomeToSubquerySelected(Boolean.parseBoolean(props.getProperty("someToSubquerySelected" + i)));
            heuristicsSelected.setInToJoinSelected(Boolean.parseBoolean(props.getProperty("inToJoinSelected" + i)));
            heuristicsSelected.setMoveAtithmetcExpressionSelected(Boolean.parseBoolean(props.getProperty("moveAtithmetcExpressionSelected" + i)));
            heuristicsSelected.setRemoveDistinctSelected(Boolean.parseBoolean(props.getProperty("removeDistinctSelected" + i)));
            heuristicsSelected.setTemporaryTableToSubQuerySelected(Boolean.parseBoolean(props.getProperty("temporaryTableToSubQuerySelected" + i)));
            HeuristicsForSql HeuristicsForSql = new HeuristicsForSql(props.getProperty("sql" + i), heuristicsSelected);
            list.add(HeuristicsForSql);
        }
        
        return list;
    }
    
    public static void defineHeuristicsForAllSql(HeuristicsSelected heuristicsSelected, boolean allSql,boolean customSql) throws FileNotFoundException, IOException{
        File file = new File(fileNameHeuristicsForAllSql);
        FileOutputStream fos = new FileOutputStream(file);
        
        Properties props = new Properties();
        //grava propreidades  no arquivo
        props.setProperty("havingToWhereSelected", Boolean.toString(heuristicsSelected.isHavingToWhereSelected()));
        props.setProperty("removeGroupbySelected", Boolean.toString(heuristicsSelected.isRemoveGroupbySelected()));
        props.setProperty("moveFunctionSelected", Boolean.toString(heuristicsSelected.isMoveFunctionSelected()));
        props.setProperty("orToUnionSelected", Boolean.toString(heuristicsSelected.isOrToUnionSelected()));
        props.setProperty("allToSubquerySelected", Boolean.toString(heuristicsSelected.isAllToSubquerySelected()));
        props.setProperty("anyToSubquerySelected", Boolean.toString(heuristicsSelected.isAnyToSubquerySelected()));
        props.setProperty("someToSubquerySelected", Boolean.toString(heuristicsSelected.isSomeToSubquerySelected()));
        props.setProperty("inToJoinSelected", Boolean.toString(heuristicsSelected.isInToJoinSelected()));
        props.setProperty("moveAtithmetcExpressionSelected", Boolean.toString(heuristicsSelected.isMoveAtithmetcExpressionSelected()));
        props.setProperty("removeDistinctSelected", Boolean.toString(heuristicsSelected.isRemoveDistinctSelected()));
        props.setProperty("temporaryTableToSubQuerySelected", Boolean.toString(heuristicsSelected.isTemporaryTableToSubQuerySelected()));
        
        props.setProperty("allSql",Boolean.toString(allSql));
        props.setProperty("customSql",Boolean.toString(customSql));
        
        props.store(fos, "Heurísticas que devem ser aplicadas a todas as SQLs.");
        fos.close();
    }
    
    public static HeuristicsSelected getHeuristicsForAllSql(){
        File file = new File(fileNameHeuristicsForAllSql);
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            return null;
        }
        
        Properties props = new Properties();
        //lê os dados que estão no arquivo
        try {
            //lê os dados que estão no arquivo
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            return null;
        }
        
        HeuristicsSelected heuristicsSelected = new HeuristicsSelected();
        //lê proriedades do arquivo
        heuristicsSelected.setHavingToWhereSelected(Boolean.parseBoolean(props.getProperty("havingToWhereSelected")));
        heuristicsSelected.setRemoveGroupbySelected(Boolean.parseBoolean(props.getProperty("removeGroupbySelected")));
        heuristicsSelected.setMoveFunctionSelected(Boolean.parseBoolean(props.getProperty("moveFunctionSelected")));
        heuristicsSelected.setOrToUnionSelected(Boolean.parseBoolean(props.getProperty("orToUnionSelected")));
        heuristicsSelected.setAllToSubquerySelected(Boolean.parseBoolean(props.getProperty("allToSubquerySelected")));
        heuristicsSelected.setAnyToSubquerySelected(Boolean.parseBoolean(props.getProperty("anyToSubquerySelected")));
        heuristicsSelected.setSomeToSubquerySelected(Boolean.parseBoolean(props.getProperty("someToSubquerySelected")));
        heuristicsSelected.setInToJoinSelected(Boolean.parseBoolean(props.getProperty("inToJoinSelected")));
        heuristicsSelected.setMoveAtithmetcExpressionSelected(Boolean.parseBoolean(props.getProperty("moveAtithmetcExpressionSelected")));
        heuristicsSelected.setRemoveDistinctSelected(Boolean.parseBoolean(props.getProperty("removeDistinctSelected")));
        heuristicsSelected.setTemporaryTableToSubQuerySelected(Boolean.parseBoolean(props.getProperty("temporaryTableToSubQuerySelected")));
        
        return heuristicsSelected;
    }
    
    
}