<html>
	<head>
		<meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Visualização 1</title>
		
		<!--JS core -->
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.4/jquery.min.js"></script>
        <script src="js/bootstrap.js"></script>
		<script src="http://code.highcharts.com/highcharts.js"></script>
		
		<!-- CSS core -->
		<link href="css/bootstrap.css" rel="stylesheet">
		<link href="css/custom.css" rel="stylesheet">
		
	</head>
	<body>
			<div class="container">
            <div class="row">
				<!--Inicio do Grafico: Managed Indexes  
                ==============================================================================================-->
                <div class="col-md-6">
                    <div id="grafico-barras">
                        <!-- Div onde será renderizado o grafico -->
                    </div>
                    <div id = "formulario">
                        <form id="barForm" class="form-inline" onsubmit="return false">
                            <div class="form-group">
                                <div class="checkbox">
                                    <h4>Status</h4>
                                    <label><input type="radio" name="status" value="true"> All</label>
                                    <label><input type="radio" name="status" value="H"> Real</label>
                                    <label><input type="radio" name="status" value="R"> Hypothetical</label>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="checkbox">
                                    <h4>Type</h4>
                                    <label><input type="radio" name="type" value="true"> All</label>
                                    <label><input type="radio" name="type" value="S"> Primary</label>
                                    <label><input type="radio" name="type" value="P"> Secundary</label>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="checkbox fix-order">
                                    <h4>Order by</h4>
                                    <label><input type="radio" name="order" value="cid_index_profit"> Profit</label>
                                    <label><input type="radio" name="order" value="cid_creation_cost"> Creation Cost</label>
                                </div>
                            </div>
                            <div class="form-group">
                                <div id="botoes">
                                    <button id='botao-barra' class="btn btn-default">Filter</button>
                                    <button id='prev' class="btn btn-default" onClick="subtraiInicioBar()">Prev</button>
                                    <button id='next' class="btn btn-default" onClick="somaInicioBar()">Next</button>
                                </div>
                            </div>    
                        </form>
                        
                    </div>
                </div>
                <!--Fim do Grafico: Managed Indexes  
                =========================================================================================-->
                
                 <!--Inicio do Grafico: Managed Materialized Views
                =========================================================================================--> 
                <div class="col-md-6">
                    <div id="grafico-barras-2">
                    </div>
                    <div id = "formulario">
                        <form id="barForm-2" class="form-inline" onsubmit="return false">
                            <div class="form-group">
                                <div class="checkbox">
                                    <h4>Status</h4>
                                    <label><input type="radio" name="status" value="true"> All</label>
                                    <label><input type="radio" name="status" value="H"> Real</label>
                                    <label><input type="radio" name="status" value="R"> Hypothetical</label>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="checkbox fix-order">
                                    <h4>Order by</h4>
                                    <label><input type="radio" name="order" value="cmv_profit"> Profit</label>
                                    <label><input type="radio" name="order" value="cmv_cost"> Creation Cost</label>
                                </div>
                            </div>
                           <div class="form-group">
                                <div id="botoes">
                                    <button id='botao-barra-2' class="btn btn-default">Filter</button>
                                    <button id='prev' class="btn btn-default" onClick="subtraiInicioBar2()">Prev</button>
                                    <button id='next' class="btn btn-default" onClick="somaInicioBar2()">Next</button>
                                </div>
                            </div>    
                        </form>
                        	
                    </div>
                </div>
                <!--Fim do Grafico: Managed Materialized Views
                =========================================================================================--> 
               
               
			</div>
        
        <div id="teste" class="row">
             <!--Inicio do Grafico: Workload  
            =========================================================================================-->
            <div id="teste-div" class="col-md-6">
                <div id="grafico-pizza">
                </div>
                <div id = "formulario">
                    <form id="pieForm" class="form-inline" onsubmit="return false">
                        <div class="form-group">
                            <div class="checkbox">
                                <h4>Order by</h4>
                                <label><input type="radio" name="order" value="wld_capture_count"> Capture count</label>
                                <label><input type="radio" name="order" value="wld_relevance"> Relevance</label>
                            </div>
                        </div>
                        <button id='botao-pizza' class="btn btn-default">Filter</button>
                    </form>

                </div>
            </div>
            <!--Fim do Grafico: Workload  
            =========================================================================================-->
            <?php include("tabela.php");?>
            <div id="teste-div" class="col-md-6">
                <div class="tabela">
                    <!-- Inicio Tabela Worload
                    ============================================================================================-->
                    <table class="table table-bordered table-hover">
                        <thead>
                            <tr>
                                <td>Id</td>
                                <td>Number of executions</td>
                                <td>Type</td>
                                <td>Relevance</td>
                            </tr>
                        </thead>    
                        <?php 
                            $dados = consulta();
                            for($i = 0; $i < sizeof($dados['id']); $i++){
                        ?>
                        <tbody>
                            <tr class="clickable" id="start-row" onclick="showRow(texto<?php echo $dados['id'][$i]?>)">
                                <td><?php echo $dados['id'][$i]?></td>
                                <td><?php echo $dados['count'][$i]?></td>
                                <td><?php echo $dados['type'][$i]?></td>
                                <td><?php echo $dados['relevance'][$i]?></td>
                            </tr>
                            <td colspan="4" class="hidden-row table-bordered" id="texto<?php echo $dados['id'][$i]?>">
                                <table class="hidden-table">
                                    <tr>
                                        <td><b>Sql</b></td>
                                    </tr>
                                    <tr>
                                        <td><?php echo $dados['sql'][$i]?></td>
                                    </tr>
                                    <tr>
                                        <td><b>Execution Plan</b></td>
                                    </tr>
                                    <tr>
                                        <td><?php echo $dados['plan'][$i]?></td>
                                    </tr>
                                    <tr>
                                        <td><b>Related index</b></td> 
                                    </tr>
                                    <tr>
                                        <td><?php echo $dados['indexes'][$i]?></td>
                                    </tr>
                                </table>
                            </td>
                        </tbody>    
                        <?php    
                            }
                        ?>
                    </table>
                    <!-- Fim Tabela Worload
                    ============================================================================================-->
                </div>
            </div>
                
        </div>
        
        <div id="teste-div-2" class="row">
            <!--Inicio do Grafico: Index Fragmentation 
            =========================================================================================-->
            <div class="col-md-12">
                <div id="grafico-coluna">
                </div>
                <div class="col-md-offset-9 col-md-3">
                    <div class="input-group">
                        <input id="proper-frag" type="text" class="form-control" placeholder="Enter Proper Fragmentation">
                        <span class="input-group-btn">
                            <button class="btn btn-default" type="button" onclick="setProperFrag()"><b>Ok!</b></button>
                        </span>
                    </div><!-- /input-group -->
                </div>    
            </div>
            <!--Fim do Grafico: Index Fragmentation  
            =========================================================================================-->
        </div>
        
        <!--Inclusão dos arquivos .js
        =========================================================================================--> 
        <script type="text/javascript">
            <?php
                include("js/custom.js");
                include("barchart.js");
                include("piechart.js");
                include("columnchart.js");    
                include("barchart2.js");
            ?>
        </script>
        </div>
	</body>
</html>