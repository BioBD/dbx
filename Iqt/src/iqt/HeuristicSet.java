package iqt;

import iqt.util.DatabaseFunctions;
import br.com.iqt.util.SqlFunctions;
import br.com.iqt.zql.*;
import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author Arlino
 */
public class HeuristicSet {
    
    private int dbms;//SGBD para qual as heurísticas serão utilizadas. (Seu uso é necessário pois algumas heurísticas
                     //  já são feitas pelo SGBD e não são necessárias.)
    private Dbms dbmsObject;//Objeto com informações para conexão com o SGBD.
    
    //Constantes que indicam o sgbd que irá utilizar as heurísticas
    /*public final static int POSTGRESQL = 1;
    //public final static int MYSQL = 2;//OBS: esse sgbd não foi investigado ainda
    public final static int SQLSERVER = 3;
    public final static int ORACLE = 4;
     * 
     */

    /**
     * Construtor da classe HeuristicSet que possui um conjunto de heurísticas 
     * responsáveis por fazer reescrita de consultas. Esse construtor não permite
     * conexão com o SGBD, não fazendo, dessa forma, a execução de algumas heutísticas que
     * necessitam informações sobre o banco de dados, como a existência de índices,
     * exemplo.
     * @param dbms
     * Seleciona as heurísticas de reescrita consultas para um bando de dados
     * específico. O banco de dados deve ser escolhido utilizando uma das constantes:
     * <b>HeuristicSet</b>.(POSTGRESQL | SQLSERVER | ORACLE)
     */
    public HeuristicSet(int dbms) {
        this.dbms = dbms;
        this.dbmsObject = null;
    }
    
    /**
     * Construtor da classe HeuristicSet que possui um conjunto de heurísticas 
     * responsáveis por fazer reescrita de consultas.
     * @param dbms
     * Objeto Dbms com informações sobre o SGBD selecionado e informações de 
     * conexão com o banco.
     */
    public HeuristicSet(Dbms dbms) {
        this.dbms = dbms.getDbms();
        this.dbmsObject = dbms;
        
    }
    
    /**
     * Produz uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * operada por <b>ALL</b>. A consulta retornada removerá a expressão ALL e usará uma 
     * comparação com uma sub-consulta simples com agregação.
     * @param exp
     * Classe ZExp instanciada com a restrição WHERE da consulta a ser reescrita.
     * @return
     * Retorna <i>true</i> se a consulta foi reescrita e <i>false</i> caso contrário.
     */
    public boolean allToSubquery(ZExp exp){
        boolean result = false;
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER){
            if(exp instanceof ZExpression){
                ZExpression expression = (ZExpression)exp;
                String operator = expression.getOperator();
                if( (operator.equals(">")) || (operator.equals(">="))
                        || (operator.equals("<")) || (operator.equals("<=")) ){
                    ZExp operand2 = expression.getOperand(1);
                    //Verifica se é expressão ALL
                    if(operand2.toString().toUpperCase().startsWith("ALL")){
                        ZExpression expAll = (ZExpression)operand2;
                        ZQuery subqueryALL = (ZQuery)expAll.getOperand(0);
                        
                        //Seta agregação na sub-consulta do ALL
                        if( operator.equals(">") || operator.equals(">=") )
                            ((ZSelectItem)subqueryALL.getSelect().elementAt(0)).setAggregate("MAX");
                        else
                            ((ZSelectItem)subqueryALL.getSelect().elementAt(0)).setAggregate("MIN");
                        
                        ZExp operand1 = expression.getOperand(0);
                        ZExpression newExpression = new ZExpression(operator, operand1, subqueryALL);
                        ((ZExpression)exp).setOperands(newExpression.getOperands());
                        result = true;
                    }
                }
                Vector operands = expression.getOperands();
                for (int i = 0; i < operands.size(); i++) {
                    ZExp opExp = (ZExp)operands.get(i);
                    boolean result2 = this.allToSubquery(opExp);
                    if(result2 )
                        result = true;
                }
            }
        }
        return result;
    }
    
    /**
     * Retorna uma consulta a partir da reescria de outra que tenha uma sub-consulta 
     * operada por <b>ANY</b>. A consulta retornada removerá a expressão ANY e usará uma 
     * comparação com uma sub-consulta simples com agregação.
     * @param exp
     * Classe ZExp instanciada com a restrição WHERE da consulta a ser reescrita.
     * @return
     * Retorna <i>true</i> se a consulta foi reescrita e <i>false</i> caso contrário.
     */
    public boolean anyToSubquery(ZExp exp){
        boolean result = false;
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER){
            if(exp instanceof ZExpression){
                ZExpression expression = (ZExpression)exp;
                String operator = expression.getOperator();
                if( (operator.equals(">")) || (operator.equals(">="))
                        || (operator.equals("<")) || (operator.equals("<=")) ){
                    ZExp operand2 = expression.getOperand(1);
                    //Verifica se é expressão ANY
                    
                    if(operand2.toString().toUpperCase().startsWith("ANY")){
                        ZExpression expAny = (ZExpression)operand2;
                        ZQuery subqueryAny = (ZQuery)expAny.getOperand(0);
                        
                        //Seta agregação na sub-consulta do ANY
                        if( operator.equals(">") || operator.equals(">=") )
                            ((ZSelectItem)subqueryAny.getSelect().elementAt(0)).setAggregate("MIN");
                        else
                            ((ZSelectItem)subqueryAny.getSelect().elementAt(0)).setAggregate("MAX");
                        
                        ZExp operand1 = expression.getOperand(0);
                        ZExpression newExpression = new ZExpression(operator, operand1, subqueryAny);
                        ((ZExpression)exp).setOperands(newExpression.getOperands());
                        result = true;
                    }
                }
                Vector operands = expression.getOperands();
                for (int i = 0; i < operands.size(); i++) {
                    ZExp opExp = (ZExp)operands.get(i);
                    boolean result2 = this.anyToSubquery(opExp);
                    if(result2 )
                        result = true;
                }
            }
        }
        return result;
    }
    
    /**
     * Retorna uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * o operador <b>SOME</b>. A consulta retornada removerá a expressão SOME e usará uma 
     * comparação com uma sub-consulta simples com agregação.
     * @param exp
     * Classe ZExp instanciada com a restrição WHERE da consulta a ser reescrita.
     * @return
     * Retorna <i>true</i> se a consulta foi reescrita e <i>false</i> caso contrário.
     */
    public boolean someToSubquery(ZExp exp){
        boolean result = false;
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER){
            if(exp instanceof ZExpression){
                ZExpression expression = (ZExpression)exp;
                String operator = expression.getOperator();
                if( (operator.equals(">")) || (operator.equals(">="))
                        || (operator.equals("<")) || (operator.equals("<=")) ){
                    ZExp operand2 = expression.getOperand(1);
                    
                    //Verifica se é expressão SOME
                    if(operand2.toString().toUpperCase().startsWith("SOME")){
                        ZExpression expSome = (ZExpression)operand2;
                        ZQuery subquerySome = (ZQuery)expSome.getOperand(0);
                        
                        //Seta agregação na sub-consulta do SOME
                        if( operator.equals(">") || operator.equals(">=") )
                            ((ZSelectItem)subquerySome.getSelect().elementAt(0)).setAggregate("MIN");
                        else
                            ((ZSelectItem)subquerySome.getSelect().elementAt(0)).setAggregate("MAX");
                        
                        ZExp operand1 = expression.getOperand(0);
                        ZExpression newExpression = new ZExpression(operator, operand1, subquerySome);
                        ((ZExpression)exp).setOperands(newExpression.getOperands());
                        result = true;
                    }
                }
                Vector operands = expression.getOperands();
                for (int i = 0; i < operands.size(); i++) {
                    ZExp opExp = (ZExp)operands.get(i);
                    boolean result2 = this.someToSubquery(opExp);
                    if(result2 )
                        result = true;
                }
            }
        }
        return result;
    }
    
    /**
     * Retorna uma consulta reescrita a partir de outra que usa <b>HAVING</b> desnecessário. 
     * Essa reescrita move a(s) restrição(ões) que não necessitam estar no HAVING para o WHERE. 
     * @param st1
     * Classe ZQuery instanciada com a consulta a ser reescrita.
     * @return
     * Retorna uma ZQuery instanciada com a consulta reescrita.
     */
    /*
     * NÃO CONSISTE EM UMA HEURÍSTICA mas pode possibilitar novas oportunidades de reescrita
     * no WHERE através das outras heurísticas, além de organizar melhor o código.
     */
    public boolean havingToWhere(ZQuery st1){
        boolean result = false;
        
        if(verifyHavingToWhere(st1)){
            //Extrai cláusulas where, colunas do goup by e cláusulas do having
            ZExpression where1 =((ZExpression) st1.getWhere());
            ZGroupBy groupBy1 = st1.getGroupBy();
            ZExp having1 = groupBy1.getHaving();
            
            //Constroi a nova expressão do Where
            if(where1 != null)
                where1 = new ZExpression("AND", where1, having1);
            else where1 = (ZExpression)having1;
            
            //Elimina having desnecessário
            groupBy1.setHaving(null);
            st1.addWhere(where1);
            result = true;
        }

        return result;
    }
    
    /**
     * Verifica se um consulta deve ser aplicada no método <b>havingToWhere</b>,
     * que é resposável por reescrever uma consulta com Having desnecessário.
     * @param query
     * Classe ZQuery instanciada com a consulta a ser verificada.
     * @return
     * Retorna <i>true</i> se a consulta deve ser reescrita utilizando o método
     * havingToWhere e <i>false</i> caso contrário.
     */
    private boolean verifyHavingToWhere(ZQuery query){
        ZGroupBy groupBy = query.getGroupBy();
        if(groupBy != null){
            ZExp having = groupBy.getHaving();
            if(having != null){
                return !this.findInExpression(having, "AGGREGATION");
            }
        }
        return false;
    }
    
    /**
     * Encontra algum tipo de elemento expecífico em uma expressão.
     * @param exp
     * Classe ZExp instanciada com a texpressão a ser verificada.
     * @param type
     * Nome do tipo de elemento a ser encontrado: AGGREGATION, ALL, ANY, SOME.
     * @param searchInSubqueries
     * Se for <i>true</i> procura o tipo de elemento também em suas sub-consultas,
     * se for <i>false</i> não procura nas sub-consultas.
     * @return
     * Retorna <i>true</i> se encontrar algum elemento do tipo especificado e 
     * <i>false</i> caso contrário.
     */
    private boolean findInExpression(ZExp exp, String type){
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            //System.out.println("exp: " + exp);
            String operator = expression.getOperator();
            //System.out.println("operator: " + operator);
            
            //Procura por uma agregação
            if(type.equals("AGGREGATION"))
                if(SqlFunctions.isAggregation(operator))
                    return  true;
            /*
            //Procura por um comando ALL
            if(type.equals("ALL"))
                if(operator.equalsIgnoreCase("ALL"))
                    return  true;
            
            //Procura por um comando ANY
            if(type.equals("ANY"))
                if(operator.equalsIgnoreCase("ANY"))
                    return  true;
            
            //Procura por um comando SOME
            if(type.equals("SOME"))
                if(operator.equalsIgnoreCase("SOME"))
                    return  true;
            
            //Procura por um comando IN
            if(type.equals("IN"))
                if(operator.equalsIgnoreCase("IN"))
                    return  true;
            */
            //Chamada recursiva para procurar em sub-expressões
            Vector operands = expression.getOperands();
            for (Object op : operands) {
                boolean result = this.findInExpression((ZExp)op, type);
                if(result)
                    return true;
            }
        }
        /*
        //Procura também em sub-consultas da expressão(se searchInSubqueries=true)
        if(searchInSubqueries)
            if(exp instanceof ZQuery){
                ZQuery query = (ZQuery)exp;
                boolean result = this.findInSubqueryExpression(query, type);
            }
         * 
         */
        
        return false;
    }
    
    /**
     * Produz uma consulta reescrita a partir de outra que usa <b>GROUP BY</b> empregado de 
     * forma desnecessária. Se a consulta não requerer que os dados seja agrupados, GROUP BY 
     * será removido.
     * @param st1
     * Classe ZQuery instanciada com a consulta a ser reescrita.
     * @return
     * Retorna uma ZQuery instanciada com a consulta reescrita.
     */
    public boolean removeGroupby(ZQuery st1){
        boolean result = false;
        
        if(verifyRemoveGroupby(st1)){
            st1.addGroupBy(null);
            result = true;
        }    
        return result;
    }
    
    /**
     * Verifica se uma consulta pode ser aplicada no método <b>groupbyUnnecessary</b>.
     * @param query
     * Classe ZQuery instanciada com a consulta a ser verificada.
     * @return
     * Retorna <i>true</i> se a consulta puder ser aplicada em groupbyToWhere e <i>false</i>
     * caso contrário.
     */
     /* COMO A VERIFICAÇÃO FUNCIONA:
     *  1 - Verificar se não exite HAVING.
     *  2 - Verificar se todas as colunas do SELECT possuem agregações ou se todas não possuem
     *      agregações, tornando não necessário declarar o nome de uma coluna em GROUP BY.
     */
    private boolean verifyRemoveGroupby(ZQuery query){
        //Verificação 1
        ZGroupBy groupBy = query.getGroupBy();
        if(groupBy != null){
            ZExp having = groupBy.getHaving();
            if(having == null){
                //Verificação 2
                //Conta a quantidade de colunas com e sem agregação
                int columnsWithAggregations = 0, columnsWithoutAggregations =0;
                Vector cols = query.getSelect();
                for (Object col : cols) {
                    ZSelectItem c = ((ZSelectItem)col);
                    if(this.isAggregation(c.getExpression()))
                        columnsWithAggregations++;
                    else
                        columnsWithoutAggregations++;
                }
                if((columnsWithAggregations == 0) || (columnsWithoutAggregations == 0))
                    return true;
            }
        }
        return false;
    }
    
    private boolean isAggregation(ZExp exp){
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            String operator = expression.getOperator();
            if(operator.equalsIgnoreCase("sum"))
                return true;
            else
                if(operator.equalsIgnoreCase("count"))
                    return true;
                else 
                    if(operator.equalsIgnoreCase("max"))
                        return true;
                    else 
                        if(operator.equalsIgnoreCase("min"))
                            return true;
                        else 
                            if(operator.equalsIgnoreCase("avg"))
                                return true;
            Vector operands = expression.getOperands();
            for (Object e : operands) {
                if(e instanceof ZExpression){
                    boolean result = isAggregation((ZExp)e);
                    if(result)
                        return true;
                }
            }
        }
            return false;
    }
    
    /**
     * Produz uma consulta reescrita a partir de outra com disjunções no WHERE. 
     * Essa reescrita faz uma união de consultas, cada uma com um termo da disjunção
     * da consula original.
     * @param st1
     * Classe ZQuery instanciada com a consulta a ser reescrita.
     * @return
     * Retorna uma classe <b>ZQuery</b> instanciada com a consulta reescrita.
     */
    public boolean orToUnion(ZQuery st1){
        boolean result = false;
        
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER || this.dbms == Dbms.ORACLE){
            ZExpression where = (ZExpression)st1.getWhere();
            Vector tables = st1.getFrom();
            if(verifyOrToUnion(where, tables)){
                //Extrai colunas do select, distinct, tabelas, cláusulas where, colunas do goup by e colunas do order by
                Vector cols1 = st1.getSelect();
                boolean distinct1 = st1.isDistinct();
                Vector from1 = st1.getFrom();
                ZExpression where1 =((ZExpression) st1.getWhere());
                ZGroupBy groupBy1 = st1.getGroupBy();
                Vector orderBy1 = st1.getOrderBy();
                ZExpression set1 = st1.getSet();
                
                Vector queries = new Vector();
                for (int i=0; i<where1.nbOperands();i++) {
                    ZQuery q = new ZQuery();
                    q.addSelect(cols1);
                    q.setDistinct(distinct1);
                    q.addFrom(from1);
                    q.addWhere(((ZExpression)where1).getOperand(i));
                    q.addGroupBy(groupBy1);
                    q.addOrderBy(orderBy1);
                    q.addSet(set1);
                    
                    queries.add(q);
                }
                
                //Seta o Where da consulta com apenas a primeira clausla e a coloca no vetor de consultas
                st1.addWhere(((ZQuery)queries.get(0)).getWhere());
                queries.set(0, st1);
                
                for (int i=queries.size()-1; i>0;i--) {
                    ZQuery q1 = (ZQuery)queries.elementAt(i);
                    ZQuery q2 = (ZQuery)queries.elementAt(i-1);
                    
                    ZExpression e = new ZExpression("UNION", q1);
                    q2.addSet(e);
                    
                    queries.set(i-1, q2);
                }
                result = true;
            }
        }
        
        return result;
    }
    
    /**
     * Reescreve uma expressão (Where ou Having) apartir de outra expressão dada que contenha 
     * coluna com índice associada a uma expressão aritmética em uma equivalência. 
     * A expressão aritmética será movida para o outro lado da equivalência deixando 
     * a coluna com índice sozinha no lado de origem. Uma expressão aritmética associada
     * a uma coluna com índice não permite que o SGBD utilize esse índice.<br>
     * <b>Exemplo:</b><br>
     * Suponha que columnWithIndex tem um índice. Com isso, a método irá agir da seguinte
     * forma sobre a expressão abaixo:<br>
     * (columnWithIndex * a) + b = c -> columnWithIndex = (c - b) / a
     * @param exp
     * Classe ZExp instanciada com a expressão Where ou Having onde será(ão) procurada(s) 
     * coluna(s) com índice assiada(s) a expressão aritmética.
     * @param tables
     * Vetor de tabelas da SQL da expressão Where ou Having dada em <b>exp</b>. 
     * As tabelas são necessárias para que a coluna seja econtrada no SGBD e verificado 
     * se ela possui índice.
     * @return
     * Retorna <i>true</i> se conseguir fazer alguma reescrita e <i>false</i> caso contrário.
     */
    public boolean verifyOrToUnion(ZExp exp, Vector tables){
        if(exp == null)
            return false;
        
        if(this.dbmsObject == null)
            return false;
        
        ZExpression expression = (ZExpression) exp;
        String operator = expression.getOperator();
        if(operator.equalsIgnoreCase("OR")){
            ZConstant column = this.getColumnWithIndex(exp, tables);
            if(column != null)
                return true;
        }
        return false;
    }
    
    /**
     * Produz uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * o operador <b>IN</b>. A consulta retornada removerá a expressão In e produzirá
     * uma junção entre a consulta e a sub-consulta da expressão IN.
     * @param st1
     * Classe ZQuery instanciada com a consulta a ser reescrita.
     * @return
     * Retorna uma ZQuery instanciada com a consulta reescrita.
     */
    public boolean inToJoin(ZQuery st1){
        boolean result = false;
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER){
            ZExpression where = (ZExpression)st1.getWhere();
            if(where != null){
                if(this.verifyInToJoin(where)){
                    //Extrai tabelas e clausulas do where
                    Vector from1 = st1.getFrom();
                    //ZExpression where1 =((ZExpression) st1.getWhere());
                    
                    ZExpression inExpression;
                    //ZExpression where = (ZExpression)st1.getWhere();
                    if(where.getOperator().equalsIgnoreCase("IN")){//Obtem e remove expressão IN caso ela seja unica no Where
                        inExpression = where;
                        st1.addWhere(null);
                    }else//Obtem e remove expressão IN caso existam outros termos no Where
                        inExpression = (ZExpression)this.removeInExpression(st1.getWhere());
                    
                    //Obtem operando e sub-consulta (juntamente com sua expressão where e colunas) da expressão IN
                    ZConstant inOperand1 = (ZConstant)inExpression.getOperand(0);
                    ZQuery inSubquery = (ZQuery)inExpression.getOperand(1);
                    ZExp whereInSubquery = inSubquery.getWhere();
                    Vector cols2 = inSubquery.getSelect();
                    ZSelectItem colInSubquery = (ZSelectItem)cols2.elementAt(0);
                    boolean inSubqueryDistinct = inSubquery.isDistinct();
                    
                    ZJoin join1 = st1.getJoin();
                    ZJoin join2 = inSubquery.getJoin();
                    //System.out.println("join1: " + join1);
                    //System.out.println("join1: " + join2);
                    
                    //inner IN (inner) INICIO ***
                    if(join1 != null && join2 != null){
                        System.out.println("1");
                        //Adiciona os dois inner join em um novo inner join auxiliar ( join1 INNER JOIN join2)
                        Vector join1Vector = new Vector();
                        join1Vector.add(join1);
                        //joinAux.setFirstTable(join1Vector);
                        Vector join2Vector = new Vector();
                        join2Vector.add(join1);
                        ZJoin joinAux = new ZJoin();
                        joinAux.setFirstTable(join2Vector);
                        joinAux.setNestedJoin(join2);
                        
                        joinAux.setJoinType("INNER");
                        joinAux.setOnExpression(whereInSubquery);
                        
                        //Nova expressão ON
                        ZExpression onExp = new ZExpression("=", inOperand1, ((ZSelectItem)cols2.get(0)).getExpression());
                        joinAux.setOnExpression(onExp);
                        
                        st1.setJoin(joinAux);
                        
                        ZExp where1 = st1.getWhere();
                        //Constroi nova expressão where
                        if(where1!=null && whereInSubquery!=null){
                            ZExpression newWhereExpression = new ZExpression("AND");
                            where1 =((ZExpression) st1.getWhere());
                            newWhereExpression.addOperand(where1);
                            newWhereExpression.addOperand(whereInSubquery);
                            
                            st1.addWhere(newWhereExpression);
                        }else
                            if(whereInSubquery!=null)
                                st1.addWhere(whereInSubquery);
                    }
                    //tables IN (inner) FIM ***
                    
                    //tables IN (inner) INICIO ***
                    if(join1 == null && join2 != null){
                        System.out.println("2");
                        Vector from = st1.getFrom();
                        Vector firstTable = join2.getFirstTable();
                        firstTable.addAll(0, from);
                        st1.setJoin(join2);
                        
                        //Constroi junção
                        ZExpression joinExpression = new ZExpression("=", inOperand1,colInSubquery.getExpression());
                        
                        ZExp where1 = st1.getWhere();
                        //Constroi nova expressão where
                        if(where1!=null && whereInSubquery!=null){
                            ZExpression newWhereExpression = new ZExpression("AND");
                            newWhereExpression.addOperand(where1);
                            newWhereExpression.addOperand(whereInSubquery);
                            newWhereExpression.addOperand(joinExpression);
                            
                            st1.addWhere(newWhereExpression);
                        }else
                            if(whereInSubquery!=null)
                                st1.addWhere(whereInSubquery);
                    }
                    //inner IN (tables) fim ***
                    
                    //inner IN (tables)
                    if(join1 != null && join2 == null){
                        
                        //System.out.println("in3");
                    }
                    
                    //tables IN(tables) INICIO ***
                    if(join1 == null && join2 == null){
                        //System.out.println("4");
                        //Concatena a(s) tabela(s) da consulta com a(s) de sua sub-consulta IN, caso já esteja na consulta
                        Vector from2 = inSubquery.getFrom();
                        for (Object tb2 : from2) {
                            boolean equals = false;
                            for (Object tb1 : from1)
                                if(tb1.toString().equalsIgnoreCase(tb2.toString()))
                                    equals = true;
                            if(!equals)
                                from1.add(tb2);
                        }
                        //Constroi junção
                        ZExpression join = new ZExpression("=", inOperand1,
                                new ZConstant(colInSubquery.getColumn(), ZConstant.COLUMNNAME));
                        
                        //Constroi nova expressão where
                        ZExpression newWhereExpression = new ZExpression("AND");
                        ZExpression where1 =((ZExpression) st1.getWhere());
                        if(where1 != null)
                            newWhereExpression.addOperand(where1);
                        if(whereInSubquery != null)
                            newWhereExpression.addOperand(whereInSubquery);
                        if((where1 != null) || (whereInSubquery != null))
                            newWhereExpression.addOperand(join);
                        else
                            newWhereExpression = join;
                        
                        st1.addWhere(newWhereExpression);
                        
                        //Constroi expressão having
                        ZGroupBy groupBySubquery = inSubquery.getGroupBy();
                        if(groupBySubquery != null){
                            ZExp havingSubquery = groupBySubquery.getHaving();
                            if(havingSubquery != null){
                                ZGroupBy groupBy = st1.getGroupBy();
                                if(groupBy != null){
                                    ZExp having = groupBy.getHaving();
                                    ZExpression newHavingExpression;
                                    if(having != null)
                                        newHavingExpression = new ZExpression("AND",
                                                having, havingSubquery);
                                    else
                                        newHavingExpression = (ZExpression)havingSubquery;
                                    groupBy.setHaving(newHavingExpression);
                                }else{
                                    st1.addGroupBy(groupBySubquery);
                                    Vector groupByElementes = groupBySubquery.getGroupBy();
                                    groupByElementes.clear();
                                    Vector select = st1.getSelect();
                                    for (Object object : select) {
                                        ZSelectItem zsi = (ZSelectItem)object;
                                        ZConstant cons = new ZConstant(zsi.toString(), ZConstant.COLUMNNAME);
                                        groupByElementes.add((ZExp)cons);
                                    }
                                }
                            }
                        }
                    }
                    //tables IN(tables) FIM ***
                    
                    if(inSubqueryDistinct){
                        Vector select = st1.getSelect();
                        select.add(0, colInSubquery);
                        st1.setDistinct(true);
                    }
                    
                    result = true;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Retorna uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * o operador <b>IN</b>. A consulta retornada usará uma sub-consulta simples com
     * agregação ao invés de IN.
     * @param st1
     * Classe ZUpdate instanciad
     * a com a SQL update a ser reescrita.
     * @return
     * Retorna uma ZUpdate instanciada com a SQL update reescrita.
     */
    public boolean inToJoinWithUpdate(ZUpdate st1){
        boolean result = false;
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER){
            ZExp where = st1.getWhere();
            if(this.verifyInToJoin(where)){
                //Extrai tabelas e clausulas do where
                Vector from1 = st1.getFrom();
                ZExpression where1 =((ZExpression) st1.getWhere());
                
                ZExpression inExpression;
                //ZExpression where = (ZExpression)st1.getWhere();
                if(where1.getOperator().equalsIgnoreCase("IN")){//Remove expressão IN caso ela seja unica no Where
                    inExpression = where1;
                    st1.addWhere(null);
                }else//Remove expressão IN caso ela seja aninhada no Where
                    inExpression = (ZExpression)this.removeInExpression(st1.getWhere());
                
                ZConstant inOperand1 = (ZConstant)inExpression.getOperand(0);
                
                ZQuery inSubquery = (ZQuery)inExpression.getOperand(1);
                ZExp whereInSubquery = inSubquery.getWhere();
                Vector cols2 = inSubquery.getSelect();
                ZSelectItem colsInSubquery = (ZSelectItem)cols2.elementAt(0);
                
                //Concatena a(s) tabela(s) do update com a(s) de sua sub-consulta IN
                Vector from2 = inSubquery.getFrom();
                if(from1 == null)
                    from1 = new Vector();
                from1.addAll((Collection)from2);
                
                //Constroi junção
                ZExpression join = new ZExpression("=", inOperand1,
                        new ZConstant(colsInSubquery.getColumn(), ZConstant.COLUMNNAME));
                
                //Constroi nova expressão where
                ZExpression newWhereExpression = new ZExpression("AND");
                where1 =((ZExpression) st1.getWhere());
                if(where1 != null)
                    newWhereExpression.addOperand(where1);
                if(whereInSubquery != null)
                    newWhereExpression.addOperand(whereInSubquery);
                if((where1 != null) || (whereInSubquery != null))
                    newWhereExpression.addOperand(join);
                else
                    newWhereExpression = join;
                
                st1.addFrom(from1);
                st1.addWhere(newWhereExpression);
                result = true;
            }
        }
        
        return result;
    }
    
    /**
     * Retorna uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * o operador <b>IN</b>. A consulta retornada usará uma sub-consulta simples com
     * agregação ao invés de IN.
     * @param st1
     * Classe ZDelete instanciada com a SQL delete a ser reescrita.
     * @return
     * Retorna uma ZDelete instanciada com a SQL delete reescrita.
     */
    public boolean inToJoinWithDelete(ZDelete st1){
        boolean result = false;
        
        if(this.dbms == Dbms.POSTGRESQL || this.dbms == Dbms.SQLSERVER){
            ZExp where = st1.getWhere();
            if(this.verifyInToJoin(where)){
                //Extrai colunas, tabelas e clausulas do where da consulta do parâmetro sql
                Vector using1 = st1.getUsing();
                ZExpression where1 =((ZExpression) st1.getWhere());
                
                ZExpression inExpression;
                //ZExpression where = (ZExpression)st1.getWhere();
                if(where1.getOperator().equalsIgnoreCase("IN")){//Remove expressão IN caso ela seja unica no Where
                    inExpression = where1;
                    st1.addWhere(null);
                }else//Remove expressão IN caso ela seja aninhada no Where
                    inExpression = (ZExpression)this.removeInExpression(st1.getWhere());
                
                ZConstant inOperand1 = (ZConstant)inExpression.getOperand(0);
                
                ZQuery inSubquery = (ZQuery)inExpression.getOperand(1);
                ZExp whereInSubquery = inSubquery.getWhere();
                Vector cols2 = inSubquery.getSelect();
                ZSelectItem colsInSubquery = (ZSelectItem)cols2.elementAt(0);
                
                //Concatena a(s) tabela(s) do delete com a(s) de sua sub-consulta IN
                Vector from2 = inSubquery.getFrom();
                if(using1 == null)
                    using1 = new Vector();
                using1.addAll((Collection)from2);
                
                //Constroi junção
                ZExpression join = new ZExpression("=", inOperand1,
                        new ZConstant(colsInSubquery.getColumn(), ZConstant.COLUMNNAME));
                
                //Constroi nova expressão where
                ZExpression newWhereExpression = new ZExpression("AND");
                where1 =((ZExpression) st1.getWhere());
                if(where1 != null)
                    newWhereExpression.addOperand(where1);
                if(whereInSubquery != null)
                    newWhereExpression.addOperand(whereInSubquery);
                if((where1 != null) || (whereInSubquery != null))
                    newWhereExpression.addOperand(join);
                else
                    newWhereExpression = join;
                
                st1.addUsing(using1);
                st1.addWhere(newWhereExpression);
                result = true;
            }
        }
        
        return result;
    }
    
    /**
     * Verifica a presença do comando <b>IN</b> em uma expressão Where.
     * @param exp
     * Classe ZExp instanciada com a restrição do WHERE da consulta onde IN será
     * procurado.
     * @return
     * Retorna <i>true</i> se encontra o comando IN e <i>false</i> caso contrário.
     */
    private boolean verifyInToJoin(ZExp exp){
        boolean result = false;
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            String operator = expression.getOperator();
            if( operator.equalsIgnoreCase("IN")  ){
                ZExp operand2 = expression.getOperand(1);
                if(operand2 instanceof ZQuery)
                    return true;
            }
            Vector operands = expression.getOperands();
            for (int i = 0; i < operands.size(); i++) {
                ZExp opExp = (ZExp)operands.get(i);
                boolean result2 = this.verifyInToJoin(opExp);
                if(result2 )
                    return true;
            }
        }
        return result;
    }
    
    /**
     * Remove a sub-expressão com operador <b>IN</b>, se existir, de uma expressão maior.
     * @param exp
     * Classe Zexp instanciada com uma expressão.
     * @return
     * Retorna uma classe ZExp com a expressão IN removida.
     */
    private ZExp removeInExpression(ZExp exp){
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            String operator = expression.getOperator();
            if( operator.equals("IN")  ){
                //Não funciona bem! Não remove caso não entre na recursão.
                return expression;//Retorna expressão IN
            }
            Vector operands = expression.getOperands();
            for (int i = 0; i < operands.size(); i++) {
                ZExp opExp = (ZExp)operands.get(i);
                ZExp result2 = this.removeInExpression(opExp);
                if(result2 != null){
                    String operatorIn = ((ZExpression)opExp).getOperator();
                    if(operatorIn.equalsIgnoreCase("IN")){//Verifica se a expessão retorna da é a IN
                        operands.remove(i);//Remove expressão IN
                        
                        //Conserta o bug de um operador binário ficar com apenas um operando após
                        //a remoção da expressão IN. Exemplo: "AND (c=d)"
                        if( (expression.getOperator().equalsIgnoreCase("AND")|| 
                                expression.getOperator().equalsIgnoreCase("OR"))
                                && (expression.nbOperands() == 1) ){
                            ZExpression operand0 = (ZExpression)expression.getOperand(0);
                            expression.setOperator(operand0.getOperator());
                            expression.setOperands(operand0.getOperands());
                        }
                        //Fim do conserto.
                    }
                    return result2;
                }
            }
        }
        return null;
    }
    
    /**
     * Reescreve uma expressão (Where ou Having) apartir de outra expressão dada que contenha 
     * coluna com índice associada a uma expressão aritmética em uma equivalência. 
     * A expressão aritmética será movida para o outro lado da equivalência deixando 
     * a coluna com índice sozinha no lado de origem. Uma expressão aritmética associada
     * a uma coluna com índice não permite que o SGBD utilize esse índice.<br>
     * <b>Exemplo:</b><br>
     * Suponha que columnWithIndex tem um índice. Com isso, a método irá agir da seguinte
     * forma sobre a expressão abaixo:<br>
     * (columnWithIndex * a) + b = c -> columnWithIndex = (c - b) / a
     * @param exp
     * Classe ZExp instanciada com a expressão Where ou Having onde será(ão) procurada(s) 
     * coluna(s) com índice assiada(s) a expressão aritmética.
     * @param tables
     * Vetor de tabelas da SQL da expressão Where ou Having dada em <b>exp</b>. 
     * As tabelas são necessárias para que a coluna seja econtrada no SGBD e verificado 
     * se ela possui índice.
     * @return
     * Retorna <i>true</i> se conseguir fazer alguma reescrita e <i>false</i> caso contrário.
     */
    public boolean moveAtithmetcExpression(ZExp exp, Vector tables){
        boolean result = false;
        if(this.dbmsObject == null)
            return false;
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression) exp;
            String operator = expression.getOperator();
            if(operator.equals("=") || operator.equals(">=") || operator.equals(">") 
                    || operator.equals("<=") || operator.equals("<")){//se a expressão for uma equivalencia procura por uma coluna com índice
                ZConstant column = this.getColumnWithIndex(exp, tables);
                if(column != null){ //se encontrar uma coluna com índice, tenta mover expressao aritmética para o outro lado da expressão
                    ZExp leftSubExpression = expression.getOperand(0);
                    ZExp rightSubExpression = expression.getOperand(1);
                    
                    //Verifica se uma das duas sub-expressões acima já são a própria coluna com índice, 
                    //  não necessitando, por esse motivo, acontecer reescrita.
                    boolean expressionEqualsColumn = false;
                    if( (this.findColumnInExpression(column, leftSubExpression))
                            && !(leftSubExpression instanceof ZConstant))//Verifica se a expressao da esquerda já é a coluna com índice
                        expressionEqualsColumn = true;
                    if(!expressionEqualsColumn)
                        if( (this.findColumnInExpression(column, rightSubExpression))
                                && !(rightSubExpression instanceof ZConstant))//Verifica se a expressao da direita já é a coluna com índice
                            expressionEqualsColumn = true;
                    
                    //Se a coluna com índice não for a expressão da esquerda ou direita (ou seja, está misturada com 
                    // expressão aritimética), efetua a reescrita.
                    if(expressionEqualsColumn){
                        ZExp newExp = this.moveOperandsToRight(expression, column);
                        if(newExp != null){
                            //Atualiza a expressão com a nova expressão aritmética reescrita
                            expression.setOperands(((ZExpression)newExp).getOperands());
                            result = true;
                        }
                    }
                }
            }else{
                //Faz chamadas recursivas para sub-expressões da expressão dada em busca de mais colunas com índices
                Vector operands = expression.getOperands();
                for (Object op : operands) {
                    ZExp e = (ZExp)op;
                    boolean result2 = this.moveAtithmetcExpression(e, tables);
                    if(result2) result = true;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Dada uma equivalência e uma coluna, o método faz com que a coluna fique sozinha
     * em um dos lados da equivalência, movendo todos os elmentos da expressão aritmética
     * para o outro lado.
     * @param expresion
     * Classe Zexpression instanciada com uma expressão de equivalência.
     * @param column
     * Classe ZConstant instanciada com uma columa presente na expressão dada.
     * @return
     * Retorna uma ZExp instanciada com uma expressão de equivalência equivalente a expressão
     * <b>expression</b> com a coluna <b>column</b> sozinha em um dos lados da expressão.
     * Se a coluna não existir na expressão, então será retornado nulo. Se a expressão não for
     * uma equivalência, será retornado nulo.
     */
    private ZExp moveOperandsToRight(ZExpression expresion, ZConstant column){
        //Retorna nulo se a expressão vinher nula
        if(expresion == null)
            return null;
        
        //Retorna nulo se a coluna vinher nula
        if(column == null)
            return null;
        
        //Retorna nulo se não existir operador na expressão ou se não for o operador =
        String operatorExpression = expresion.getOperator();
        if(operatorExpression == null)
            return null;
        ZExp leftExpression = expresion.getOperand(0);
        ZExp rightExpression = expresion.getOperand(1);
        
        //Verifica em que lado da expressão está a coluna(column)
        boolean columnExists = false;
        if(this.findColumnInExpression(column, leftExpression)){//procura column na expressão da esquerda
            columnExists = true;
        }
        if(!columnExists)
            if(this.findColumnInExpression(column, rightExpression)){//procura column na expressão da direita
                //troca valor da expressão da esquerda com o da direita
                ZExp expressionAux = rightExpression;
                rightExpression = leftExpression;
                leftExpression = expressionAux;
                columnExists = true;
            }
        
        //Retorna nulo se column não existir em nenhum dos lados da expressão
        if(!columnExists)
            return null;
        
        //Se a expressão da esquerda já for a própria column, então não precisa mover alguma parte da expressão
        if(leftExpression instanceof ZConstant)
                return new ZExpression("=", leftExpression, rightExpression);
        
        //Move partes da expressão(leftExpression) que não seja ou não contenha column da esquerda para direita de operatorExpression
        // de acordo com o operador(operator) da expressão.
        String operator = ((ZExpression)leftExpression).getOperator();
        ZExp le = ((ZExpression)leftExpression).getOperand(0);//expressão a esquerda do operador(operator)
        ZExp lr = ((ZExpression)leftExpression).getOperand(1);//expressão a direita do operador(operator)
        if(operator.equals("+")){
            if(this.findColumnInExpression(column, le)){//column+a = b  ->  column = b-a
                rightExpression = new ZExpression("-", rightExpression, lr);
                leftExpression = le;
            }else{//a+column = b  ->  column = b-a
                rightExpression = new ZExpression("-", rightExpression, le);
                leftExpression = lr;
            }
        }else
            if(operator.equals("-")){
                if(lr == null){//-column = b  ->  column = -b
                    rightExpression = new ZExpression("-", rightExpression);
                    leftExpression = le;
                }else
                    if(this.findColumnInExpression(column, le)){//column-a = b  ->  column = b+a
                        rightExpression = new ZExpression("+", rightExpression, lr);
                        leftExpression = le;
                    }else{//a-column = b  ->  column = a-b
                        rightExpression = new ZExpression("-", le, rightExpression);
                        leftExpression = lr;
                    }
            }else
                if(operator.equals("*")){
                    if(this.findColumnInExpression(column, le)){//column*a = b  ->  column = b/a
                        rightExpression = new ZExpression("/", rightExpression, lr);
                        leftExpression = le;
                    }else{//a*column = b  ->  column = b/a
                        rightExpression = new ZExpression("/", rightExpression, le);
                        leftExpression = lr;
                    }
                }else
                    if(operator.equals("/")){
                        if(this.findColumnInExpression(column, le)){//column/a = b  ->  column = b*a
                            rightExpression = new ZExpression("*", rightExpression, lr);
                            leftExpression = le;
                        }else{//a/column = b  ->  column = a/b
                            rightExpression = new ZExpression("/", le, rightExpression);
                            leftExpression = lr;
                        }
                    }else//operador não identificado
                        return null;
        
        return this.moveOperandsToRight(new ZExpression("=", leftExpression, rightExpression), column);
    }
    
    /**
     * Encontra uma coluna <b>column</b> uma expressão <b>exp</b>.
     * @param column
     * Classe ZConstant instaciada com a coluna a ser encontrada.
     * @param exp
     * Classe Zexp instanciada com uma expressão Where ou Having onde a columa 
     * será procurada.
     * @return
     * Retorna <i>true</i> se encontrar a coluna <b>exp</b> na expressão <b>exp</b>
     * e <i>false</i> caso contrário.
     */
    private boolean findColumnInExpression(ZConstant column, ZExp exp){
        //
        if(exp instanceof ZConstant){
            ZConstant constant = (ZConstant)exp;
            if(constant.getType() == ZConstant.COLUMNNAME){
                if(constant.getValue().equalsIgnoreCase(column.getValue()))
                    return true;
            }
        }
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression) exp;
            Vector operands = expression.getOperands();
            for (Object op : operands) {
                ZExp e = (ZExp)op;
                boolean result = this.findColumnInExpression(column, e);
                if(result)
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Encontra uma coluna associada a um índice em uma expressão.
     * @param exp
     * Classe Zexp instanciada com uma expressão Where ou Having onde a columa com 
     * índice será procurada.
     * @param tables
     * Vetor de tabelas da consulta da expressão exp.
     * @return
     * Retorna uma ZConstant instanciada com a coluna que tem um índice associado.
     */
    private ZConstant getColumnWithIndex(ZExp exp, Vector tables){
        if(exp instanceof ZConstant){
            ZConstant constant = (ZConstant)exp;
            if(constant.getType() == ZConstant.COLUMNNAME){
                if(DatabaseFunctions.isColumnWithIndex(dbmsObject, tables, constant.getValue()))
                    return constant;
            }
        }
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression) exp;
            Vector operands = expression.getOperands();
            for (Object op : operands) {
                ZExp e = (ZExp)op;
                ZConstant c = this.getColumnWithIndex(e, tables);
                if(c != null)
                    return c;
            }
        }
        
        return null;
    }
    
    /**
     * Produz uma consulta reescrita a partir de outra que possua o comando
     * <b>DISTINCT</b> empregado de forma desnecessária. Se uma das restrições
     * das cláusulas no WHERE fizer restrição usando o valor de uma chave-primária, 
     * o resultado retornardo conterá apenas tuplas distintas. A consulta resultante 
     * terá o comando DISTINCT removido se for constatado que ele é desnecessário.
     * @param st1
     * Classe ZQuery instanciada com a consulta a ser reescrita.
     * @return
     * Retorna uma ZQuery instanciada com a consulta reescrita.
     */
    public boolean removeDistinct(ZQuery st1){
        if(this.verifyRemoveDistinct(st1)){
            st1.setDistinct(false);
            return true;
        }
        return false;
    }
    
    /**
     * Verifica se uma consulta pode ser aplicada no método <b>removeDistinct</b>.
     * @param query
     * Classe ZQuery instanciada com a consulta a ser reescrita.
     * @return
     * Retorna <i>true</i> se a consulta puder ser aplicada em removeDistinct e <i>false</i>
     * caso contrário.
     */
     /* 
     * COMO A VERIFICAÇÃO FUNCIONA:
     * 1 - Verifica se o comando DISTINCT exste.
     * 2 - Verica se em uma consulta que utiliza apenas uma tabela o distinct é 
     *     aplicado a uma chave-primária.
     * 3 - Se o item 2 não for verdadeiro, verifica se existe uma chave-primária 
     *     igulada á um valor que pode ser uma constante (número, string, ...) ou 
     *     a um valor que pode vir de uma sub-consulta. A chave não pode ser igulada 
     *     a outro campo. A cláusula da chave deve ser aplicada á uma disjunção ou 
     *     não retornará apenas tuplas distintas.
     */
    private boolean verifyRemoveDistinct(ZQuery query){
        
        //Verificação 1
        if(query.isDistinct()){
            Vector from = query.getFrom();
            
            //Verificação 2
            if(from.size() == 1){
                Vector cols = query.getSelect();
                ZSelectItem col = (ZSelectItem)cols.elementAt(0);
                return DatabaseFunctions.isColumnKey(dbmsObject, from, col.getColumn());
            }
            
            //Verificação 3
            Vector cols = query.getSelect();
            ZSelectItem column = (ZSelectItem)cols.elementAt(0);
            ZExpression where = (ZExpression)query.getWhere();
            if(where != null)
                return this.findKey(where, column, from);
        }
        return false;
    }
    
    /**
     * Verifica se uma expressão possui o par <i>&lt;chave-primária&gt =  &lt;valor&gt</i>.
     * Onde <i>&lt;chave-primária&gt</i> deve ser uma chave-primária <b>column</b> de uma das tabelas da consulta 
     * do argumento <b>query</b>. <i>&lt;valor&gt</i> não pode ser outra coluna, deve ser um valor 
     * constante (número, string, ...) ou deve ser o valor obtido de uma sub-consulta.
     * @param exp
     * Classe ZExp instanciada com uma expressão WHERE onde <i>&lt;chave-primária&gt =  &lt;valor&gt</i>
     * será procurado.
     * @param query
     * Classe ZQuery instanciada com a consulta que possui as tabelas que podem possuir a chave-primária.
     * @return
     * Retorna <i>true</i> se econtrar  o par <i>&lt;chave-primária&gt =  &lt;valor&gt</i> e <i>false</i> calso contrário.
     */
    private boolean findKey(ZExp exp, ZSelectItem column, Vector from){
        if(exp instanceof ZExpression){
            
            ZExpression expression = (ZExpression)exp;
            String operator = expression.getOperator();
            /*
             * Verifica se a expressão consiste no par '<campo> =  <valor>'.
             * <valor> não pode ser outra coluna, deve ser um valor constante (número, string, ...)
             * ou deve vir de uma consulta.
             * Depois verifica se <campo> é uma chave primária de alguma das tabelas da culsulta.
             */
            if(operator.equals("=")){
                ZExp operand1 = expression.getOperand(0);
                ZExp operand2 = expression.getOperand(1);
                //Verifica se o primeiro operando é uma coluna igualada a outro elemento que não seja uma coluna.
                //Exemplos: (c1=12), (c1='e') ou (c=(SELECT ...))
                if(operand1 instanceof ZConstant){
                    ZConstant constant1 = (ZConstant)operand1;
                    if((constant1.getType() == ZConstant.COLUMNNAME) && //Verifica se o primeiro operador é uma coluna.
                            (constant1.getValue().equalsIgnoreCase(column.getColumn()))){//Verifica se o primeiro operador tem o mesmo nome de column.
                        if(operand2 instanceof ZConstant){//Verifica se o segundo operador não é uma coluna
                            ZConstant constant2 = (ZConstant)operand2;
                            if(constant2.getType() != ZConstant.COLUMNNAME){
                                return DatabaseFunctions.isColumnKey(dbmsObject, from, constant1.getValue());//verifica se constant1 é chave-primária em algumas das tabelas
                            }
                        }else//O segundo operador pode ser uma consulta
                            if(operand2 instanceof ZQuery){
                                return DatabaseFunctions.isColumnKey(dbmsObject, from, constant1.getValue());//verifica se constant1 é chave-primária em algumas das tabelas de query
                            }
                    }
                }
                //Verifica se o segundo operando é uma coluna igualada a outro elemento que não seja uma coluna.
                //Exemplos: (12=c1) ou ('e'=c1)
                if(operand2 instanceof ZConstant){
                    ZConstant constant2 = (ZConstant)operand2;
                    if((constant2.getType() == ZConstant.COLUMNNAME) && //Verifica se o primeiro operador é uma coluna.
                            (constant2.getValue().equalsIgnoreCase(column.getColumn()))){//Verifica se o segundo operador tem o mesmo nome de column.
                        if(operand1 instanceof ZConstant){//Verifica se o primeiro operador não é uma coluna
                            ZConstant constant1 = (ZConstant)operand1;
                            if(constant1.getType() != ZConstant.COLUMNNAME){
                                return DatabaseFunctions.isColumnKey(dbmsObject, from, constant2.getValue());//verifica se constant2 é chave-primária em algumas das tabelas de query
                            }
                        }
                    }
                }
            }else{
                /*
                 * Verifica se o par <chave-primária>=<valor> tem como operador uma conjunção, pois a consulta não 
                 * retorna tuplas distintas com disjunção. Depois faz chamdas recursivas para seus operandos.
                 */
                if(operator.equalsIgnoreCase("AND")){
                    Vector operands = expression.getOperands();
                    for (Object op : operands) {
                        //System.out.println("exp: " + op);
                        //if(this.isAggregation(op.toString()))
                        //   return  true;
                        boolean result = this.findKey((ZExp)op, column, from);
                        if(result)
                            return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Produz uma consulta a partir da reescria de outras duas que usem tabela
     * temporária. Uma das consultas cria uma tabela temporária e a outra utiliza
     * essa tabela. A consulta retornada será uma junção das duas consultas
     * passadas com parâmetro.
     * @param st1
     * Classe ZQuery instanciada com a consulta que produz uma tabela temporária.
     * @param st2
     * Classe ZQuery instanciada com a consulta que utiliza a tabela temporária gerada
     * por st1.
     * @return
     * Retorna uma ZQuery instanciada com a consulta reescrita.
     */
    public ZQuery temporaryTableToSubQuery(ZQuery st1, ZQuery st2){
        
        //Extrai o nome da tabela temporári da primeira consulta e removo INTO
        String temporaryTable = st1.getInto();
        st1.removeInto();
        
        //Remove a tabela temporária temporaryTable da segunda consulta
        String alias2 = null;
        Vector from2 = st2.getFrom();
        for (int i = 0; i < from2.size(); i++) {
            Object object = from2.get(i);
            if(object instanceof ZFromItem){
                ZFromItem tb = (ZFromItem)object;
                if(tb.getTable().equalsIgnoreCase(temporaryTable)){
                    alias2 = tb.getAlias();//Extrai alias da tabela temporária
                    from2.remove(i);
                    break;
                }
            }
        }
        
        //Atribui o alias da tabela temporária (alias2) à nova tabela em forma de consulta.
        boolean exit;
        String newAlias;
        if(alias2 != null)
            newAlias = alias2;
        else{ // Se alias2 não existir, cria um novo
            do{
                exit = true;
                Random rnd = new Random();
                int number = rnd.nextInt(1000);
                newAlias = "tb" + Integer.toString(number) ;//Nome do alias da nova tabela
                //Verifica se o alias gerado concide com o nome de alguma tabela ou alias já existente
                for (int i = 0; i < from2.size(); i++) {
                    Object object = from2.get(i);
                    if(object instanceof ZFromItem){
                        ZFromItem tb = (ZFromItem)object;
                        if(tb.getTable().equalsIgnoreCase(newAlias)){
                            exit = false;
                            break;
                        }
                        if(tb.getAlias() != null)
                            if(tb.getAlias().equalsIgnoreCase(newAlias)){
                                exit = false;
                                break;
                            }
                    }
                }
            }while(!exit);
        }
        
        //Seta o alias a consulta
        st1.setAlias(newAlias);
        
        //Adiciona a primeira consulta como uma tabela da segunda consulta
        from2.add(st1);
        
        return st2;
    }
    
    /**
     * Produz uma consulta a partir da reescria de duas SQLs que usem tabela
     * temporária. Uma das SQL é uma consulta que cria uma tabela temporária e a outra 
     * é um update que utiliza essa tabela. A SQL retornada será uma junção das 
     * duas SQLs passadas com parâmetro.
     * @param st1
     * Classe ZQuery instanciada com a consulta que produz uma tabela temporária.
     * @param st2
     * Classe ZUpdate instanciada com a SQL update que utiliza a tabela temporária gerada
     * por st1.
     * @return
     * Retorna uma ZUpdate instanciada com a SQL update reescrita.
     */
    public ZUpdate temporaryTableToSubQueryWithUpdate(ZQuery st1, ZUpdate st2){
        
        //Extrai o nome da tabela temporári da consulta e remove o INTO
        String temporaryTable = st1.getInto();
        st1.removeInto();
        
        //Remove a tabela temporária temporaryTable do update
        String alias2 = null;
        Vector from2 = st2.getFrom();
        for (int i = 0; i < from2.size(); i++) {
            Object object = from2.get(i);
            if(object instanceof ZFromItem){
                ZFromItem tb = (ZFromItem)object;
                if(tb.getTable().equalsIgnoreCase(temporaryTable)){
                    alias2 = tb.getAlias();//Extrai alias da tabela temporária
                    from2.remove(i);
                    break;
                }
            }
        }
        
        //Atribui o alias da tabela temporária (alias2) à nova tabela em forma de consulta.
        boolean exit;
        String newAlias;
        if(alias2 != null)
            newAlias = alias2;
        else{ // Se alias2 não existir, cria um novo
            do{
                exit = true;
                Random rnd = new Random();
                int number = rnd.nextInt(1000);
                newAlias = "tb" + Integer.toString(number) ;//Nome do alias da nova tabela
                //Verifica se o alias gerado concide com o nome de alguma tabela ou alias já existente
                for (int i = 0; i < from2.size(); i++) {
                    Object object = from2.get(i);
                    if(object instanceof ZFromItem){
                        ZFromItem tb = (ZFromItem)object;
                        if(tb.getTable().equalsIgnoreCase(newAlias)){
                            exit = false;
                            break;
                        }
                        if(tb.getAlias() != null)
                            if(tb.getAlias().equalsIgnoreCase(newAlias)){
                                exit = false;
                                break;
                            }
                    }
                }
            }while(!exit);
        }
        
        //Seta o alias a consulta
        st1.setAlias(newAlias);
        
        //Adiciona a primeira consulta com uma tabela do update
        from2.add(st1);
        
        return st2;
    }
    
    /**
     * Produz uma consulta a partir da reescria de duas SQLs que usem tabela
     * temporária. Uma das SQL é uma consulta que cria uma tabela temporária e a outra 
     * é um delete que utiliza essa tabela. A SQL retornada será uma junção das 
     * duas SQLs passadas com parâmetro.
     * @param st1
     * Classe ZQuery instanciada com a consulta que produz uma tabela temporária.
     * @param st2
     * Classe ZDelete instanciada com a SQL update que utiliza a tabela temporária gerada
     * por st1.
     * @return
     * Retorna uma ZDelete instanciada com a SQL delete reescrita.
     */
    public ZDelete temporaryTableToSubQueryWithDelete(ZQuery st1, ZDelete st2){
        
        //Extrai o nome da tabela temporári da primeira consulta e remove INTO
        String temporaryTable = st1.getInto();
        st1.removeInto();
        
        //Remove a tabela temporária temporaryTable delete
        String alias2 = null;
        Vector using2 = st2.getUsing();
        for (int i = 0; i < using2.size(); i++) {
            Object object = using2.get(i);
            if(object instanceof ZFromItem){
                ZFromItem tb = (ZFromItem)object;
                if(tb.getTable().equalsIgnoreCase(temporaryTable)){
                    alias2 = tb.getAlias();//Extrai alias da tabela temporária
                    using2.remove(i);
                    break;
                }
            }
        }
        
        //Atribui o alias da tabela temporária (alias2) à nova tabela em forma de consulta.
        boolean exit;
        String newAlias;
        if(alias2 != null)
            newAlias = alias2;
        else{ // Se alias2 não existir, cria um novo
            do{
                exit = true;
                Random rnd = new Random();
                int number = rnd.nextInt(1000);
                newAlias = "tb" + Integer.toString(number) ;//Nome do alias da nova tabela
                //Verifica se o alias gerado concide com o nome de alguma tabela ou alias já existente
                for (int i = 0; i < using2.size(); i++) {
                    ZFromItem tb = (ZFromItem)using2.get(i);
                    if(tb.getTable().equalsIgnoreCase(newAlias)){
                        exit = false;
                        break;
                    }
                    if(tb.getAlias() != null)
                        if(tb.getAlias().equalsIgnoreCase(newAlias)){
                            exit = false;
                            break;
                        }
                }
            }while(!exit);
        }
        
        //Seta o alias a consulta
        st1.setAlias(newAlias);
        
        //Adiciona a primeira consulta com uma tabela delete
        using2.add(st1);
        
        return st2;
    }
    
    
    /**
     * Produz uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * operada por <b>ALL</b>. A consulta retornada removerá a expressão ALL e usará uma 
     * comparação com uma sub-consulta simples com agregação.
     * @param exp
     * Classe ZExp instanciada com a restrição WHERE da consulta a ser reescrita.
     * @return
     * Retorna <i>true</i> se a consulta foi reescrita e <i>false</i> caso contrário.
     */
    public boolean moveFunction(ZExp exp, Vector tables){
        boolean result = false;
        if(dbmsObject == null)
            return false;
        
        if(exp instanceof ZExpression){
            ZExpression expression = (ZExpression)exp;
            String operator = expression.getOperator();
            
            //Pocura por função em um dois operandos de uma equivalencia
            if(operator.equals("=")){
                String function = null;
                ZExp operandLeft = expression.getOperand(0);
                ZExp operandRight = expression.getOperand(1);
            
                //Procura função no operando da esquerda
                if(!(operandLeft instanceof ZConstant)){
                    if(((ZExpression)operandLeft).getType() == ZExpression.FUNCTION)
                        function = ((ZExpression)operandLeft).getOperator();
                }else{
                //Procura função no operando da direita
                    if(!(operandRight instanceof ZConstant) && !(operandRight instanceof ZQuery))
                    if(((ZExpression)operandRight).getType() == ZExpression.FUNCTION){
                        function = ((ZExpression)operandRight).getOperator();
                        ZExp operandAux = operandLeft;
                        operandLeft = operandRight;
                        operandRight = operandAux;
                    }
                }
                
                //Se encontrar uma função em um dos operandos, procura agora por indices em seus parametros
                if(function != null){
                    //Verifica se alguma das colunas prestentes na função possui índice
                    Vector operands = ((ZExpression)operandLeft).getOperands();
                    for (Object op : operands) {
                        if(op instanceof ZConstant)
                            if(DatabaseFunctions.isColumnWithIndex(dbmsObject, tables, ((ZConstant)op).getValue())){
                                //faz nova expressão com a função do lado esquerdo da equivalencia
                                ZExpression newExpression = new ZExpression("=", operandLeft, operandRight);
                                RewritableFunctions rf = new RewritableFunctions(dbms);
                                newExpression = rf.rewriteFunction(expression, tables);
                                //Atribui a expressão a sua reescrita, se houver uma nova
                                if(newExpression != null){ 
                                    expression.setOperands(newExpression.getOperands());
                                    expression.setOperator(newExpression.getOperator());
                                    result = true;
                                }
                                break;
                            }
                    }
                }
            }
            
            //Faz chamada recursiva para verificar sub-expressões da expressão   
            Vector operands = expression.getOperands();
            for (int i = 0; i < operands.size(); i++) {
                ZExp opExp = (ZExp)operands.get(i);
                boolean result2 = this.moveFunction(opExp, tables);
                if(result2 )
                    result = true;
            }
        }
        
        return result;
    }
        
    /*
     * FUNÇÕES QUE DEVEM SER ANALIZADAS PARA SEREM IMPLEMENTADAS
     */
    
    /**
     * Retorna uma consulta a partir da reescria de outra que tenha uma sub-consulta com
     * o operador <b>EXISTS</b>. A consulta retornada usará <b>JOIN</b> ao invés de <b>EXISTS</b>.
     * @param sql
     * Consulta a ser reescrita.
     * @return
     * Retorna a String da consulta reescrita.
     */
    public String joinToExists(String sql){
        
        
        //VERIFICAR INSTANCIAS DE ZFROMINTEM QUE PODEM VIR COMO ZQUERY
        
        
        try{
            //manipula select do parametro sql
            ZqlParser p = new ZqlParser();
            p.initParser(new ByteArrayInputStream(sql.getBytes()));
            ZQuery st1 = (ZQuery)p.readStatement();
            
            //Extrai colunas do select do parametro sql
            Vector cols1 = st1.getSelect();
            
            //Extrai tabelas do select do parametro sql
            Vector from1 = st1.getFrom();
            
            //Extrai a tabela que contém a coluna da consulta
            ZFromItem firstTable = null;
            for (Object tb : from1) {
                if(this.tableContainsColumn(tb.toString(), cols1.elementAt(0).toString())){
                    firstTable = (ZFromItem)tb;//obtem tabela realcionada a coluna da culsuta
                    from1.remove(tb);//deleta a tabela obtida da lista
                    break;
                }
            }
            
            //Constroi sub-consulta do Exists
            ZQuery queryExists = new ZQuery();
            ZSelectItem zSelectItem = new ZSelectItem("*");
            Vector newCols = new Vector();//
            newCols.add(zSelectItem);
            queryExists.addSelect(newCols);
            queryExists.addFrom(from1);
            queryExists.addWhere(st1.getWhere());
            //Constroir expressão da clausula Exists
            ZExpression expressionExists = new ZExpression("Exists", queryExists);
            
            //Constroi nova sql retornada
            ZQuery query = new ZQuery();
            query.addSelect(cols1);
            Vector newTable = new Vector();
            newTable.add(firstTable);
            query.addFrom(new Vector(newTable));
            query.addWhere(expressionExists);
            
            return query.toString();
        }catch(Exception e){
            //** Criar excessao
            System.out.println("Erro=" + e.getMessage());
        }
        return "";
    }
    
    /**
     * Verifica se uma tabela contém uma coluna específica.
     * A verificação é feita através de comparações entre as estruturas
     * <i>[schema.]table [alias]</i> de <b>tableName</b>
     * e <i>[[schema.]table|alias.]column</i> de <b>columnName</b>, necessáriamente.
     * @param tableName
     * Nome da tabela onde a coluna é procurada.
     * @param columnName
     * Nome coluna procurada.
     * @return
     * Retorna <i>true</i> se <b>columnName</b> pertence a <b>tableName</b> e
     * <i>false</i> caso contrário.
     */
    private boolean tableContainsColumn(String tableName, String columnName){
        String schema1="", table1="", alias1="";//partes de tableName
        String schema2="", table2="", column2="";//partes de columnName
        
        //divide tableName em schema1 e table1
        StringTokenizer st1 = new StringTokenizer(tableName, ".");
        switch(st1.countTokens()) {
            case 1:
                table1 = new String(st1.nextToken());
                break;
            case 2:
                schema1 = new String(st1.nextToken());
                table1 = new String(st1.nextToken());
        }
        //divide table1 em table1 e alias1
        StringTokenizer st3 = new StringTokenizer(table1, " ");
        switch(st3.countTokens()) {
            case 1:
                break;
            case 2:
                table1 = new String(st3.nextToken());
                alias1 = new String(st3.nextToken());
        }
        
        //retira agregação de columnName, se houver
        int begin = columnName.indexOf("(");
        int end = columnName.indexOf(")");
        if((begin!=-1)&&(end!=-1))
            columnName = columnName.substring(begin + 1, end);
        //divide columnName em schema1, table1 e column1
        StringTokenizer st2 = new StringTokenizer(columnName, ".");
        switch(st2.countTokens()) {
            case 1:
                column2 = new String(st2.nextToken());
                break;
            case 2:
                table2 = new String(st2.nextToken());
                column2 = new String(st2.nextToken());
                break;
            case 3:
                schema2 = new String(st2.nextToken());
                table2 = new String(st2.nextToken());
                column2 = new String(st2.nextToken());
                break;
        }
        
        //verifica se a tabela contem a coluna
        if(!table1.isEmpty())
            if(table1.equalsIgnoreCase(table2))
                return true;
        if(!alias1.isEmpty())
            if(alias1.equalsIgnoreCase(table2))
                return true;
        return false;
    }
    
    /*
     * AS FUNÇÕES ABAIXO NAO FORAM TERMINADAS
     */
    
    
    /**
     * Produz uma consulta a partir da reescria de outra que tenha sub-consulta simples.
     * A consulta retornada usará junção entre as tabelas da consulta e sua sub-consulta.
     * @param sql
     * Consulta a ser reescrita.
     * @return
     * Retorna a String da consulta reescrita.
     */
    public String subQueryToJoin(String sql){
        try{
            //manipula select do parametro sql
            ZqlParser p = new ZqlParser();
            p.initParser(new ByteArrayInputStream(sql.getBytes()));
            ZQuery st1 = (ZQuery)p.readStatement();
            
            //Extrai colunas do select do parametro sql
            Vector cols1 = st1.getSelect();
            
            //Extrai tabelas do select do parametro sql
            Vector from1 = st1.getFrom();
            
            //Extrai clausulas do where do select do parametro sql
            ZExpression where1 =((ZExpression) st1.getWhere());
            
            //Extrai sub consulta
            String subQuery="";//armazena sub consulta
            String column1="", column2;//colunas utilizadas para fazer a junção
            if(where1.getOperator().equals("=")){//caso so exista a sub consulta no where
                column1 = where1.getOperand(0).toString();
                subQuery = where1.getOperand(1).toString()+ ";";
                //where1.getOperands().removeAllElements();//não funciona muito bem
                where1 = null;
                where1 = new ZExpression("");
            }
            else{
                for(int i=0; i<where1.nbOperands();i++){//lista cada clausula do Where
                    if(((ZExpression)where1.getOperand(i)).getOperator().equals("=")){
                        //verifica se subQuery é um select
                        subQuery = ((ZExpression)where1.getOperand(i)).getOperand(1).toString() + ";" ;
                        if(subQuery.length()>=6)
                            if(subQuery.substring(0, 6).equalsIgnoreCase("SELECT")){
                                column1 = ((ZExpression)where1.getOperand(i)).getOperand(0).toString();
                                where1.getOperands().remove(i);//remove a sub-consulta
                                break;
                            }
                    }
                }
            }
            
            //manipula select da clausula da sub-consulta
            ZqlParser p2 = new ZqlParser();
            p2.initParser(new ByteArrayInputStream(subQuery.getBytes()));
            ZQuery st2 = (ZQuery)p2.readStatement();
            
            //Extrai colunas do select da sub consulta
            Vector cols2 = st2.getSelect();
            column2 = cols2.elementAt(0).toString();
            
            //Extrai tabelas do select da sub consulta
            Vector from2 = st2.getFrom();
            
            //juntas as tabelas das duas sql
            for (int i=0; i<from2.size(); i++)
                from1.addElement(from2.elementAt(i));
            
            //Constroi a nova sql
            ZQuery query = new ZQuery();
            query.addSelect(cols1);
            query.addFrom(from1);
            
            //faz expressão da junção
            ZExpression c1 = new ZExpression("=",
                    new ZConstant(column1, ZConstant.COLUMNNAME),
                    new ZConstant(column2, ZConstant.COLUMNNAME));
            //verifica se há clausulas do where do In a serem adicinadas
            if(((ZExpression)st2.getWhere())!=null)
                c1 = new ZExpression("AND",c1,st2.getWhere());
            where1.addOperand(c1);
            query.addWhere(where1);
            return query.toString();
        }catch(Exception e){
            //** Criar excessao
            System.out.println("Erro=" + e.getMessage());
        }
        return "";
    }
    
        /**
     *
     * @param sql
     * @return
     */
    public String subQueryToJoinInUpdate(String sql){
        String sqlOutput="";
        
        return sqlOutput;
    }
    
    /**
     *
     * @param sql
     * @return
     */
    public String subQueryToJoinInDelete(String sql){
        String sqlOutput="";
        
        return sqlOutput;
    }
    
}


