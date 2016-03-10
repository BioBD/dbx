<%-- 
    Document   : index
    Created on : 29/02/2016, 16:51:06
    Author     : Italo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>DBX</title>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="img/favicon.png" rel="icon">
		
        <!--JS core 
        ==================================================================-->
	<script src="js/jquery-2.1.4.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <script src="js/highcharts.js"></script>
        <script src="js/exporting.js"></script>
        
        
	<!-- CSS core 
        ==================================================================-->
	<link href="css/bootstrap.css" rel="stylesheet">
	<link href="css/custom.css" rel="stylesheet">
        
    </head>
    <body>
        <header class="navbar navbar-default navbar-fixed">
			<div class="container">
				<div class="navbar-header">
					<a class="navbar-brand" href="#"><img src="img/logo.png" alt="DBX"></a>
				</div>
                <div class="navbar-left">
                    <p>To tune or not to tune</p>
                </div>
            </div>
        </header>
        
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
                        <!-- Div onde será renderizado o grafico -->
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
                    <!-- Div onde será renderizado o grafico -->
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
                        <tbody id="corpo-tabela">
                           
                        </tbody>    
                    </table>
                    <!-- Fim Tabela Worload
                    ============================================================================================-->
                </div>
            </div>
        </div>
        
        <div class="row">
            <!--Inicio do Grafico: Index Fragmentation 
            =========================================================================================-->
            <div class="col-md-12">
                <div id="grafico-coluna">
                    <!--Div onde será renderizado o grafico -->
                </div>
                
                <!-- Inicio: Formulario para receber a Proper Frag. 
                ============================================================================================-->    
                <div class="col-md-offset-9 col-md-3">
                    <div class="input-group">
                        <input id="proper-frag" type="text" class="form-control" placeholder="Enter Proper Fragmentation">
                        <span class="input-group-btn">
                            <button class="btn btn-default" type="button" onclick="setProperFrag()"><b>Ok!</b></button>
                        </span>
                    </div><!-- /input-group -->
                </div>
                <!-- Inicio: Formulario para receber a Proper Frag. 
                ============================================================================================--> 
            
            </div>
            
            <!--Fim do Grafico: Index Fragmentation  
            =========================================================================================-->
        </div>
        
        <div class="row">
            <div class="col-md-12">
                <div id="grafico-linha">
                </div>
            </div>    
        </div>
                
                
        <!-- Inclusão dos arquivos .js 
        =========================================================================================--> 
        <script>
            <%@include file="js/custom.js"%>
            <%@include file="js/barchart.js"%>
            <%@include file="js/barchart2.js"%>
            <%@include file="js/columnchart.js"%>
            <%@include file="js/line.js"%>
            <%@include file="js/workload.js"%>
        </script>
        </div>
    </body>
</html>
