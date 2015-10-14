<?php
    function consulta(){
        $conexao=pg_connect("host=localhost port=5432 dbname=pg_tpch_1gb user=postgres2 password=gagasenha") or die('erro ao conectar: '.pg_last_error());
        
        $operacao="SELECT wld_id, wld_sql, wld_plan, wld_type,wld_capture_count, wld_relevance FROM agent.tb_workload ORDER BY wld_id ASC";
        $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());
        
        $dados=array('id'=>array(),'sql'=>array(),'plan'=>array(),'type'=>array(),'indexes'=>array(),'count'=>array(),'relevance'=>array());
        while($linha=pg_fetch_array($consulta)){
            $id=$linha['wld_id'];
            $sql=$linha['wld_sql'];
            $plan=$linha['wld_plan'];
            $type=$linha['wld_type'];
            $count=$linha['wld_capture_count'];
            $relevance=$linha['wld_relevance'];
            $indexes=null;
            $operacao2="SELECT cid_id FROM agent.tb_task_indexes WHERE wld_id=".$id;
            $consulta2=pg_query($conexao,$operacao2) or die ('falha na operacao: '.pg_last_error());
            while($linha2=pg_fetch_array($consulta2)){
                $id2=$linha2['cid_id'];

                $operacao3="SELECT cid_table_name FROM agent.tb_candidate_index WHERE cid_id=".$id2;
                $consulta3=pg_query($conexao,$operacao3) or die ('falha na operacao: '.pg_last_error());
                while($linha3=pg_fetch_array($consulta3)){
                    $indexes.=$linha3['cid_table_name'].'(';

                    $operacao4="SELECT cic_column_name from  agent.tb_candidate_index_column WHERE cid_id=".$id2;
                    $consulta4=pg_query($conexao,$operacao4) or die ('falha na operacao: '.pg_last_error());
                    while($linha4=pg_fetch_array($consulta4)){
                        $indexes.=trim($linha4['cic_column_name']).",";
                    }
                    $indexes=trim($indexes,',');
                    $indexes.=") <br/>";
                }
            }
            array_push($dados['id'],$id);
            array_push($dados['sql'],$sql);
            array_push($dados['plan'],$plan);
            array_push($dados['type'],$type);
            array_push($dados['indexes'],$indexes);
            array_push($dados['count'],$count);
            array_push($dados['relevance'],$relevance);
        }
        return $dados;
    }

?>