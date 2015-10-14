--SCRIPT DE TESTE DA FERRAMENTA DBX

--Limpa Metabase

SELECT agent.limpa_estatisticas()

SELECT agent.clearMaterializedView() 

SELECT agent.clearAllIndex() 

SELECT agent.clearIndexNotPrimary() 

/*AGENT_DBX*/SELECT * FROM  agent.tb_workload

/*AGENT_DBX*/SELECT * FROM agent.tb_candidate_index

/*AGENT_DBX*/SELECT * FROM agent.tb_task_indexes

/*AGENT_DBX*/SELECT * FROM agent.tb_candidate_index_column

/*AGENT_DBX*/SELECT * FROM agent.tb_workload_log



-- Inicio dos Testes

SELECT * FROM lineitem WHERE l_orderkey= 100

SELECT * FROM lineitem WHERE l_orderkey< 100

SELECT * FROM lineitem WHERE l_orderkey=100 and l_partkey=46150

SELECT * FROM lineitem WHERE l_orderkey=100 and l_partkey>38024

SELECT * FROM lineitem WHERE l_orderkey> 100 -- Beneficio vai ser negativo

SELECT l_orderkey, l_partkey FROM lineitem WHERE l_orderkey= 100

