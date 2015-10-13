<?php 
    $conexao=pg_connect("host=localhost port=5432 dbname=agent user=postgres password=admin") or die('erro ao conectar: '.pg_last_error());
?> 