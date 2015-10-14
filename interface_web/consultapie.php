<?php       
    $order=$_GET['order'];
    
    include("conexao.php");
    
    $operacao="SELECT wld_id,wld_sql,$order from agent.tb_workload";
    $consulta=pg_query($conexao,$operacao) or die ('falha na operacao: '.pg_last_error());

    $dados=array();
    while($linha=pg_fetch_array($consulta)){
        $dados[]=array('name'=>$linha['wld_id'],'y'=>(int)$linha[$order],'text'=>$linha['wld_sql']);

    }
    echo json_encode($dados);
?>