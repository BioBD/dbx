<?php
    function consulta(){
        include("conexao.php");
        
        $operacao="SELECT wld_id, wld_sql, wld_plan, wld_type,wld_capture_count, wld_relevance FROM agent.tb_workload ORDER BY wld_id ASC";
        $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());
        
        $dados=array('id'=>array(),'sql'=>array(),'plan'=>array(),'type'=>array(),'indexes'=>array(),'vms'=>array(),'count'=>array(),'relevance'=>array());
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

                $operacao3="SELECT cid_index_name FROM agent.tb_candidate_index WHERE cid_id=".$id2;
                $consulta3=pg_query($conexao,$operacao3) or die ('falha na operacao: '.pg_last_error());
                while($linha3=pg_fetch_array($consulta3)){
                    $indexes.=$linha3['cid_index_name'].'(';

                    $operacao4="SELECT cic_column_name from  agent.tb_candidate_index_column WHERE cid_id=".$id2;
                    $consulta4=pg_query($conexao,$operacao4) or die ('falha na operacao: '.pg_last_error());
                    while($linha4=pg_fetch_array($consulta4)){
                        $indexes.=trim($linha4['cic_column_name']).",";
                    }
                    $indexes=trim($indexes,',');
                    $indexes.=") <br/>";
                }
            }
			$vms=null;
			$operacao5="SELECT cmv_id FROM agent.tb_task_views WHERE wld_id=".$id;
            $consulta5=pg_query($conexao,$operacao5) or die ('falha na operacao: '.pg_last_error());
            while($linha5=pg_fetch_array($consulta5)){
				$id3=$linha5['cmv_id'];
				$vms.=$linha5['cmv_id'].'(';
							$operacao6="SELECT cmv_ddl_create from agent.tb_candidate_view WHERE cmv_id=".$id3;
							$consulta6=pg_query($conexao,$operacao6) or die ('falha na operacao: '.pg_last_error());
							while($linha6=pg_fetch_array($consulta6)){
								$vms.=trim($linha6['cmv_ddl_create']).",";
							}
							$vms=trim($vms,',');
							$vms.=") <br/>";
			}
				
				
            array_push($dados['id'],$id);
            array_push($dados['sql'],$sql);
            array_push($dados['plan'],$plan);
            array_push($dados['type'],$type);
            array_push($dados['indexes'],$indexes);
			array_push($dados['vms'],$vms);
            array_push($dados['count'],$count);
            array_push($dados['relevance'],$relevance);
        }
        return $dados;
    }

?>