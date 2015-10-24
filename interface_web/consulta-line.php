<?php
         include("conexao.php");
        
        $operacao="SELECT wlog_id,wlog_time,wlog_duration,wlog_sql FROM agent.tb_workload_log ORDER BY wlog_id  ";
        $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());
        
        $dados=array();
        $dados2=array();
        while($linha=pg_fetch_array($consulta)){
            $date = date('H:i:s', strtotime($linha['wlog_time']));
            //echo $date;
            $id=(int)$linha['wlog_id'];
            $time=strtotime($linha['wlog_time']) *1000;
            $duration=(int)$linha['wlog_duration'];
            $name=$linha['wlog_sql'];
            $dados[]=array('id'=>$id,'time'=> $time,'duration'=>$duration,'name'=>$name);
           
        }
 
        for($j=0;$j<sizeof($dados);$j++){
            $v=false;
            for($i=0;$i<=sizeof($dados2);$i++){	
                if($dados[$j]['name']==$dados2[$i]['name']){
                    $v=true;
                    break;
                }
            }
            if($v){
                array_push($dados2[$i]['data'],array($dados[$j]['time'],$dados[$j]['duration']));
            }
            else{
                $dados2[]=array('id'=>$dados[$j]['id'],'data'=>array(array($dados[$j]['time'],$dados[$j]['duration'])),'name'=>$dados[$j]['name']);
            }
        }
       
        echo json_encode($dados2);
?>