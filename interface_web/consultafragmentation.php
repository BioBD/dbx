<?php
        $inicio=(int)$_GET['inicio'];
        include("conexao.php");
        
        $operacao="SELECT cid_id,cid_table_name,cid_fragmentation_level from  agent.tb_candidate_index where cid_status = 'R' ORDER BY cid_index_profit";
        
        $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());

        $dados=array('nomes'=>array(),'fragmentation'=>array());
        while($linha=pg_fetch_array($consulta)){
            $nome="'".$linha['cid_table_name'].'(';
           
            $operacao2="SELECT cic_column_name from  agent.tb_candidate_index_column WHERE cid_id=".$linha['cid_id'];
            $consulta2=pg_query($conexao,$operacao2) or die ('falha na operacao: '.pg_last_error());
        while($linha2=pg_fetch_array($consulta2)){
            $nome.=" ".trim($linha2['cic_column_name']);

        }
            $nome.=")'";
           
            array_push($dados['nomes'],$nome);
            array_push($dados['fragmentation'],(int)$linha['cid_fragmentation_level']);

        //$dados[$i]['nome'].=')';
        }
        $i = $inicio;
        $dados2=array('nomes'=>array(),'fragmentation'=>array());
        while($i < $inicio+4 and $i < sizeof($dados['nomes'])){
            $dados2['nomes'][] =$dados['nomes'][$i]; 
            $dados2['fragmentation'][] = $dados['fragmentation'][$i];  
            $i++;
        }

        echo json_encode($dados);
?>