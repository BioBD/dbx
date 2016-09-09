/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.algorithms;

import agents.libraries.ConnectionSGBD;
import agents.sgbd.Column;
import agents.sgbd.Filter;
import agents.sgbd.Index;
import agents.sgbd.SQL;
import agents.sgbd.SeqScan;
import agents.sgbd.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class IHSTIS_CI extends Algorithm {

    private ArrayList<SeqScan> sso = null;
    private SeqScan ss = null;
    private ArrayList<Table> tabsSelect = null;
    private ArrayList<Table> tabsGroup = null;
    private ArrayList<Table> tabsOrder = null;
    private ArrayList<Table> allTabs = null;
    private ArrayList<Index> lCandidates = new ArrayList();
    private ArrayList<Filter> filterColumns = null;
    private Filter filterAux = null;
    private Index indexAuxP = null;
    private Index indexAuxS = null;
    private long indexScanCost = 0;
    private long seqScanCost = 0;
    private long wldId = 0;
    private long profit = 0;
    private ConnectionSGBD connection;

    public IHSTIS_CI() {
        this.connection = new ConnectionSGBD();
    }

    public void runAlg(SQL sql) {

        //Recupera o codigo da Task (wld_id)
        wldId = sql.getId();

        if (sql.getPlan() != null) {
            sso = sql.plan().getSeqScanOperations();
        }

        //Percorre as operacoes de SeqScan
        for (int i = 0; i < sso.size(); i++) {
            ss = sso.get(i);

            //Criando um índice composto primário (com todos os atributos do seq scan)
            Index composedIndexP = new Index();
            composedIndexP.setTableName(ss.getTableName());
            composedIndexP.columns = new ArrayList();
            composedIndexP.setIndexType("P");
            composedIndexP.setNumberOfRows(ss.getNumberOfRows());

            //Criando um índice composto primário (com todos os atributos do seq scan)
            Index composedIndexS = new Index();
            composedIndexS.setTableName(ss.getTableName());
            composedIndexS.columns = new ArrayList();
            composedIndexS.setIndexType("S");
            composedIndexS.setNumberOfRows(ss.getNumberOfRows());

            //Percorre cada coluna (filter) do SeqScan
            filterColumns = ss.getFilterColumns();
            for (int j = 0; j < filterColumns.size(); j++) {
                filterAux = filterColumns.get(j);

                //Ajusta o índice composto
                composedIndexP.columns.add(filterAux);
                composedIndexS.columns.add(filterAux);

                if ((composedIndexP.getFilterType() == null) || (!composedIndexP.getFilterType().equals("equi"))) {
                    composedIndexP.setFilterType(filterAux.getFilterType());
                    composedIndexS.setFilterType(filterAux.getFilterType());
                }
                composedIndexP.setHasFilter(true);
                composedIndexS.setHasFilter(true);
                composedIndexP.setCreationCost(2 * getSeqScanCost(ss.getTableName()));
                composedIndexS.setCreationCost(2 * getSeqScanCost(ss.getTableName()));

                //Adiciona indice primario
                //Cria um indice candidato primario para cada coluna do SeqScan
                indexAuxP = new Index();
                indexAuxP.setTableName(ss.getTableName());
                indexAuxP.columns = new ArrayList();
                indexAuxP.columns.add(filterAux);
                indexAuxP.setIndexType("P");
                indexAuxP.setCreationCost(2 * getSeqScanCost(ss.getTableName()));
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
                indexAuxS.setIndexType("S");
                indexAuxS.setCreationCost(2 * getSeqScanCost(ss.getTableName()));
                indexAuxS.setHasFilter(true);
                indexAuxS.setFilterType(filterAux.getFilterType());
                indexAuxS.setNumberOfRows(ss.getNumberOfRows());
                lCandidates.add(indexAuxS);
            }
            //Adiciona o índice composto
            if (filterColumns.size() > 1) {
                lCandidates.add(composedIndexP);
                lCandidates.add(composedIndexS);
            }
        }

        //TODO: Para cada tabela presente em uma cláusula SELECT faça:
        //Criar um indice composto secundário para todos os atributos desta tabela que aparecem na Consulta SQL
        //Usa a comando SQL para pegar os atributos envolvidos nas clausulas SELECT, GROUP e ORDER
        //colsSelect = sql.getFieldsSelect();
        //colsGroup = sql.getFieldsGroup();
        //colsOrder = sql.getFieldsOrder();
        //PS. Um comando SQL pode ter várias cláusulas SELECT
        //É importante saber qual a tabela de uma determinada coluna.
        //Logo, é necessário preencher o campo table da classe Column
        //SELECT nome FROM empregado WHERE salario >= ALL (SELECT salario_base FROM cargo)
        //Devolver um ArrayList de Tabela, onde cada objeto Tabela tem um conjunto de atributos usados em um ou mais cláusulas SELECT
        //Agrupar por tabela
        //SELECT e.enome, d.dnome FROM empregado e, departamento d WHERE e.lotacao=d.codigo
        //Talvez, o mais fácil seja utilizar o Parser ZQL [Thuraisingham et al. 2010].
        //TODO: Juntar os atributos do Case 1 (Filtro) e do Case 2 (Select, Group e Order) para uma mesma Tabela
        //Criar um novo índice secundário com os atributos do índice anterior (Select, Group e Order) c
        //com os atributos de cada um dos índices criados anteriormente (envolvidos em filter)
        //Basta criar um novo índice secundário com todos os atributos presentes nos índices que já estão em lCandidates (removendo os atributos repetidos)
        //Avaliar esse código posteriormente
        /*
         tabsSelect = sql.getFieldsSelect(sql);
         tabsGroup = sql.getFieldsGroup(sql);
         tabsOrder = sql.getFieldsOrder(sql);

         //Juntar todas listas de tabelas numa mesma lista
         for(int i=0; i<tabsSelect.size(); i++){
         allTabs.add(tabsSelect.get(i));
         }
         for(int i=0; i<tabsOrder.size(); i++){
         allTabs.add(tabsOrder.get(i));
         }
         for(int i=0; i<tabsGroup.size(); i++){
         allTabs.add(tabsGroup.get(i));
         }

         //Obter uma lista de tabelas, sem repeticoes, e com seus respectivos atributos utilizados na clausula SQL
         //Tratar as repeticoes de atributos e tabelas
         for(int i=0; i<allTabs.size()-1; i++){
         for(int j=i+1; j<allTabs.size(); j++){
         Table tab1 = allTabs.get(i);
         Table tab2 = allTabs.get(j);
         //Testa se as tabelas s„o iguais
         if(tab1.getName() == tab2.getName()){
         ArrayList<Column> listCols1 = tab1.getFields();
         ArrayList<Column> listCols2 = tab2.getFields();
         //Trata os atributos em comum
         for(int k=0; k<listCols1.size()-1; k++){
         for(int l=0; l<listCols2.size(); l++){
         Column col1 = listCols1.get(k);
         Column col2 = listCols1.get(l);
         if(col1.getName() == col2.getName()){
         listCols2.remove(l);
         }
         }
         }

         //Junta os atributos que est„o em listCols2 e que n„o est„o em listCols1
         for(int k=0; k<listCols2.size(); k++){
         listCols1.add(listCols2.get(k));
         }

         //Atualiza a lista de atributos da tabela atual, pela listCols1
         tab1.setFields(listCols1);

         //remover tab2 da lista allTabs e atualiza a tabela atual
         allTabs.remove(j);
         allTabs.set(i, tab1);
         }
         }
         }

         //Criar um Ìndice secundario, a partir das tabelas obtidas.
         for(int i=0; i<allTabs.size(); i++){
         Index idx = null;
         Table tab = allTabs.get(i);

         idx.setTableName(tab.getName()); //Adicionando a tabela em que o Ìndice ser· criado
         idx.setIndexType("S"); //Adicionando tipo do indice
         idx.setColumns(tab.getFields()); //Adicionando as colunas do indice

         lCandidates.add(idx); //Adicionando o Ìndice na lCandidates
         }
         */
        //Percorrer os Indices Candidatos
        for (Index lCandidate : lCandidates) {
            if (!lCandidate.getTableName().isEmpty()) {
                //Testa se o indice ja existe na metabase local
                if (!inLM(lCandidate)) {
                    //Insere um novo indice candidato na LM
                    insertIndexLM(lCandidate);
                }
                //verificar se o indice ja estah associado ah tarefa corrente
                if (!inTaskIndexes(wldId, lCandidate)) {
                    //Inserir tupla na tabela tb_task_indexes
                    insertTaskIndexes(wldId, lCandidate);
                }

                //Verifica se o indice eh hipotetico (ou seja, nao eh um indice real)
                if (isHypotheticalIndex(lCandidate) && !lCandidate.getTableName().isEmpty()) {
                    //Estimar Custo de Index Scan
                    indexScanCost = getIndexScanCost(lCandidate);
                    //Estima custo do SeqScan
                    seqScanCost = getSeqScanCost(lCandidate.getTableName());
                    //Verifica se o custo de utilizar o indice (Index Scan) eh menor que o custo do SeqScan
                    if (indexScanCost < seqScanCost) {
                        profit = seqScanCost - indexScanCost;
                    }
                    updateProfit(lCandidate, profit);
                }
                //Se o indice eh real e nao foi utilizado deveria ter um beneficio descontado???
            }
        }
    }

    private void updateProfit(Index ind, long profit) {
        //Atualiza o beneficio acumulado do indice
        try {
            String queryTemp = config.getProperty("setDMLUpdateCandidateIndexProfitonpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            //cid_index_profit
            preparedStatement.setLong(1, profit);
            //cid_id
            preparedStatement.setInt(2, getIndexId(ind));

            //Executa a inserção
            connection.executeUpdate(preparedStatement);

        } catch (SQLException e) {
            log.error(e.getMessage());
        }

    }

    private int getIndexId(Index ind) {
        //Verificar se existe indice na ML definido sobre a mesma tabela e memos atributos
        //Sera necessario utilizar as tabelas tb_candidate_index e tb_candidate_index_column
        //Retorna zero se não encontrar o índice
        ArrayList<Integer> ids = new ArrayList();
        int cid;
        int cidId = 0;
        //Recupera os índices existentes com o mesmo número de colunas do índice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getDMLIndexNamesWithConditionpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            preparedStatement.setString(1, ind.getTableName());
            preparedStatement.setString(2, ind.getIndexType());
            preparedStatement.setInt(3, ind.columns.size());
            ResultSet result = connection.executeQuery(preparedStatement);
            while (result.next()) {
                cid = result.getInt("cid_id");
                ids.add(cid);
            }
            result.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        //Percorre os índices identificados, verificando as colunas
        Column column;
        String columnName;

        //Percorre os índices possíveis, com o memso número de colunas
        for (int i = 0; i < ids.size(); i++) {
            //Guarda o id do índice que está sendo verificado
            cidId = ids.get(i);
            //Percorre as colunas
            for (int j = 0; j < ind.columns.size(); j++) {
                column = ind.columns.get(j);
                columnName = column.getName();

                try {
                    String queryTemp = config.getProperty("getDMLIndexColumnWithConditionpostgresql");
                    PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
                    preparedStatement.setInt(1, ids.get(i));
                    preparedStatement.setString(2, columnName);
                    ResultSet result = connection.executeQuery(preparedStatement);
                    if (result.next()) {
                        result.close();
                    } else {
                        cidId = 0;
                        result.close();
                    }
                } catch (SQLException e) {
                    log.error(e.getMessage());
                }

            }

            if (cidId > 0) {
                return cidId;
            }

        }

        return cidId;
    }

    private boolean isHypotheticalIndex(Index ind) {
        //Verificar na tabela tb_candidate_index a coluna cid_status: H -> Hypothetical; R-> Real

        int indId = getIndexId(ind);
        String status = null;

        if (indId > 0) {
            //Recupera o status do índice
            try {
                String queryTemp = config.getProperty("setDMSelectIndexStatusonpostgresql");
                PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
                preparedStatement.setInt(1, indId);
                ResultSet result = connection.executeQuery(preparedStatement);
                if (result.next()) {
                    status = result.getString("cid_status");
                    result.close();
                } else {
                    result.close();
                }
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        if ((status != null) && (status.equals("H"))) {
            return true;
        } else {
            return false;
        }
    }

    private void insertTaskIndexes(long wldId, Index ind) {
        //verificar se o indice ja estah associado ah tarefa na tabela tb_task_indexes

        int indId = getIndexId(ind);

        if (indId > 0) {
            try {
                String queryTemp = config.getProperty("setDMLInsertTaskIndexesonpostgresql");
                PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
                //cid_index_profit
                preparedStatement.setLong(1, wldId);
                //cid_id
                preparedStatement.setInt(2, getIndexId(ind));

                //Executa a inserção
                connection.executeUpdate(preparedStatement);

            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

    }

    private boolean inTaskIndexes(long wldId, Index ind) {
        //verificar se o indice ja estah associado ah tarefa na tabela tb_task_indexes

        //Recupera o id do índice
        int indId = getIndexId(ind);

        boolean isInTaskIndexes = false;

        if (indId > 0) {
            //Verifica se a tupla existe
            try {
                String queryTemp = config.getProperty("setDMSelectTaskIndexesonpostgresql");
                PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
                preparedStatement.setInt(1, indId);
                preparedStatement.setLong(2, wldId);
                ResultSet result = connection.executeQuery(preparedStatement);

                if (result.next()) {
                    isInTaskIndexes = true;
                    result.close();
                } else {
                    result.close();
                }

            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return isInTaskIndexes;
    }

    private boolean inLM(Index ind) {
        //Verificar se existe indice na ML definido sobre a mesma tabela e memos atributos
        //Sera necessario utilizar as tabelas tb_candidate_index e tb_candidate_index_column
        ArrayList<Integer> ids = new ArrayList();
        int cid;
        boolean isInLM = false;
        //Recupera os índices existentes com o mesmo número de colunas do índeice recebido como parâmetro
        try {
            String queryTemp = config.getProperty("getDMLIndexNamesWithConditionpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            preparedStatement.setString(1, ind.getTableName());
            preparedStatement.setString(2, ind.getIndexType());
            preparedStatement.setInt(3, ind.columns.size());
            ResultSet result = connection.executeQuery(preparedStatement);
            while (result.next()) {
                cid = result.getInt("cid_id");
                ids.add(cid);
            }
            result.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        //Percorre os índices identificados, verificando as colunas
        Column column;
        String columnName;

        //Percorre os índices possíveis, com o memso número de colunas
        for (int i = 0; i < ids.size(); i++) {
            //Avalia um determinado índice possível
            isInLM = true;
            //Percorre as colunas
            for (int j = 0; j < ind.columns.size(); j++) {
                column = ind.columns.get(j);
                columnName = column.getName();

                try {
                    String queryTemp = config.getProperty("getDMLIndexColumnWithConditionpostgresql");
                    PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
                    preparedStatement.setInt(1, ids.get(i));
                    preparedStatement.setString(2, columnName);
                    ResultSet result = connection.executeQuery(preparedStatement);
                    if (result.next()) {
                        result.close();
                    } else {
                        isInLM = false;
                        result.close();
                    }
                } catch (SQLException e) {
                    log.error(e.getMessage());
                }
            }

            if (isInLM) {
                return isInLM;
            }

        }
        return isInLM;
    }

    //TODO: Implementar
    private void insertIndexLM(Index ind) {
        //Inserir indice na LM
        //Beneficio acumulado = 0
        //NQ (numero de consultas que usa o indice) = 0
        //Inserir linha na tabela tb_candidate_index
        //Inserir uma ou mais linhas na tabela tb_candidate_index_column. Uma linha para cada coluna
        //Inserir linha na tabela tb_task_indexes

        int maxId = 0;

        //Recupera o maior valor para cid_id
        try {
            String queryTemp = config.getProperty("getDMLMaxIndexIDonpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            ResultSet result = connection.executeQuery(preparedStatement);
            if (result.next()) {
                maxId = result.getInt("maxId");
                result.close();
            } else {
                result.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        //Gera cid_id do novo índice candidato
        maxId++;

        //Insere o novo índice candidato
        try {
            String queryTemp = config.getProperty("setDMLInsertCandidateIndexonpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            //cid_id
            preparedStatement.setInt(1, maxId);
            //cid_table_name
            preparedStatement.setString(2, ind.getTableName());
            //cid_index_profit
            preparedStatement.setInt(3, 0);
            //cid_creation_cost
            preparedStatement.setLong(4, ind.getCreationCost());
            //cid_status
            preparedStatement.setString(5, "H");
            //cid_type
            preparedStatement.setString(6, ind.getIndexType());
            //cid_initial_profit
            preparedStatement.setInt(7, 0);
            //cid_fragmentation_level
            preparedStatement.setInt(8, 0);
            //cid_initial_ratio
            preparedStatement.setInt(9, 0);
            //cid_index_name
            preparedStatement.setString(10, ind.getName());
            //cid_creation_time (as a real index)
            preparedStatement.setTimestamp(11, null);

            //Executa a inserção
            connection.executeUpdate(preparedStatement);

        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        for (int i = 0; i < ind.columns.size(); i++) {
            Column c = ind.columns.get(i);
            insertColumn(maxId, c);
        }

    }

    private void insertColumn(int cidId, Column c) {
        //Insere coluna de índice candidato
        //RPOLIVEIRA: TODO: VERIFICAR SE EXISTE ANTES DE INSERIR
        try {
            String queryTemp = config.getProperty("getDMLSelectCandidateIndexColumnonpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            //cid_id
            preparedStatement.setInt(1, cidId);
            //Executa a inserção
            ResultSet result = connection.executeQuery(preparedStatement);
            if (!result.next()) {
                queryTemp = config.getProperty("setDMLInsertCandidateIndexColumnonpostgresql");
                preparedStatement = connection.prepareStatement(queryTemp);
                //cid_id
                preparedStatement.setInt(1, cidId);
                //cic_column_name
                preparedStatement.setString(2, c.getName());
                //Executa a inserção
                connection.executeUpdate(preparedStatement);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private long getIndexScanCost(Index ind) {
        //Variável que armazena o custo do index scan
        long isCost = 0;
        //Usar função para estimar a altura da árvore B+ referente ao índice hipotético
        //Se o índice for real, pegar a altura da árvore da metabase
        //Verificar se esse método é chamado sempre para índices hipotéticos e nunca para índices reais. Creio que sim.
        long deepTree = 3;

        //Recupera o número de tuplas e o número de páginas da tabela onde o índice seria criado
        int numberOfTablePages = 0;
        int numberOfTableTuples = 0;
        try {
            String queryTemp = config.getProperty("getIndexScanCostpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            preparedStatement.setString(1, ind.getTableName());
            ResultSet result = connection.executeQuery(preparedStatement);
            if (result.next()) {
                numberOfTablePages = result.getInt("relpages");
                numberOfTableTuples = result.getInt("reltuples");
                result.close();
            } else {
                result.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        //Testa se o índice está envolvido em algum seq scan com filtro
        if (ind.getHasFilter()) {
            //Testa se o filtro é do tipo equi (=)
            if (ind.getFilterType().equals("equi")) {
                //Testa se o índice é primário
                if (ind.getIndexType().equals("P")) {
                    isCost = deepTree + 1;
                } //Índice é secundário
                else {
                    //isCost = Hi + pico(nlr/(n/p))
                    //Hi -> Altura da árvore
                    //nlr -> Número de linhas recuperados no seq scan
                    //n -> Número de linhas (tuplas) da tabela
                    //p -> Número de páginas (blocos) da tabela
                    isCost = deepTree + (ind.getNumberOfRows() / (numberOfTableTuples / numberOfTablePages));
                }

            } //Filtro é do tipo theta (>, <, >=, <=)
            else //Testa se o índice é primário
            {
                if (ind.getIndexType().equals("P")) {
                    isCost = deepTree + (ind.getNumberOfRows() / (numberOfTableTuples / numberOfTablePages));;
                } //Índice é secundário
                else {
                    isCost = deepTree + ind.getNumberOfRows();
                }
            }
        }

        return isCost;
    }

    private long getSeqScanCost(String tableName) {
        int numberOfTablePages = 0;
        try {
            String queryTemp = config.getProperty("getSeqScanCostpostgresql");
            PreparedStatement preparedStatement = connection.prepareStatement(queryTemp);
            preparedStatement.setString(1, tableName);
            ResultSet result = connection.executeQuery(preparedStatement);
            if (result.next()) {
                numberOfTablePages = result.getInt("relpages");
                result.close();
            } else {
                result.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return numberOfTablePages;
    }
}
