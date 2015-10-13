<?php

        $status=$_GET['status'];
        $order=$_GET['order'];
        $inicio=(int)$_GET['inicio'];
        
        include("conexao.php");
        
        if(!empty($status) and !empty($order) and $status != "undefined"){
            $operacao="SELECT cmv_id,cmv_cost,cmv_profit from  agent.tb_candidate_view WHERE cmv_status!='$status' ORDER BY $order";
        }else{
            $operacao="SELECT cmv_id,cmv_cost,cmv_profit from  agent.tb_candidate_view";
        }
        
        $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());

        $dados=array('nomes'=>array(),'profit'=>array(),'cost'=>array());
        while($linha=pg_fetch_array($consulta)){
           //$dados[]=array('id'=>"'".$linha['cmv_id']."'",'cost'=>$linha['cmv_cost'],'profit'=>$linha['cmv_profit']);
            $nome= $linha['cmv_id'];
           array_push($dados['nomes'],$nome);
           array_push($dados['profit'],(int)$linha['cmv_profit']);
            array_push($dados['cost'],(int)$linha['cmv_cost']);

        }
        $i = $inicio;
        $dados2=array('nomes'=>array(),'profit'=>array(),'cost'=>array());
        while($i < $inicio+4 and $i < sizeof($dados['nomes'])){
            $dados2['nomes'][] =$dados['nomes'][$i]; 
            $dados2['profit'][] = $dados['profit'][$i];  
            $dados2['cost'][] = $dados['cost'][$i];  
            $i++;
        }
        echo json_encode($dados2);
?>