<?php 
    $conexao=pg_connect("host=localhost port=5432 dbname=pg_tpch_1gb user=postgres password=gagasenha") or die('erro ao conectar: '.pg_last_error());
?> 