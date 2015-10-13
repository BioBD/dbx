<?php
        
        $status=$_GET['status'];
        $type=$_GET['type'];
        $order=$_GET['order'];
        $inicio=(int)$_GET['inicio'];

        include("conexao.php");
        
        if(!empty($status) and !empty($type) and !empty($order) and $status != "undefined"){
            $operacao="SELECT cid_id,cid_table_name,cid_index_profit,cid_creation_cost from  agent.tb_candidate_index WHERE cid_status!='$status' AND cid_type!='$type' ORDER BY $order";
        }else{
            $operacao="SELECT cid_id,cid_table_name,cid_index_profit,cid_creation_cost from  agent.tb_candidate_index";
        }
        
        $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());

        $dados=array('nomes'=>array(),'profit'=>array(),'cost'=>array());
        while($linha=pg_fetch_array($consulta)){
            $nome="'".$linha['cid_table_name'].'(';
           
            $operacao2="SELECT cic_column_name from  agent.tb_candidate_index_column WHERE cid_id=".$linha['cid_id'];
            $consulta2=pg_query($conexao,$operacao2) or die ('falha na operacao: '.pg_last_error());
        while($linha2=pg_fetch_array($consulta2)){
            $nome.=" ".trim($linha2['cic_column_name']);

        }
            $nome.=")'";
            //echo $nome;
            array_push($dados['nomes'],$nome);
            array_push($dados['profit'],(int)$linha['cid_index_profit']);
            array_push($dados['cost'],(int)$linha['cid_creation_cost']);

        //$dados[$i]['nome'].=')';
        }
        
        $i = $inicio;
        $dados2=array('nomes'=>array(),'profit'=>array(),'cost'=>array());
        while($i < $inicio+4 and $i < sizeof($dados['nomes'])){
            $dados2['nomes'][] =$dados['nomes'][$i]; 
            $dados2['profit'][] = $dados['profit'][$i];  
            $dados2['cost'][] = $dados['cost'][$i];  
            $i++;
        }
        //print_r($dados['nomes'][$i]);

        echo json_encode($dados2);
?>