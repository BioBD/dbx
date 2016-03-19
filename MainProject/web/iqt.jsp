<!--
    Iqt - Interactive Query Tuning
    Document   : iqt
    Created on : 03/03/2016, 10:30
-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    
    <head>
        <meta charset="UTF-8">
        <link href="scripts/img/favicon.png" rel="icon">
        <script src="js/jquery-2.1.4.min.js"></script>
        <script src="scripts/js/jquery-2.1.4.min.js" type="text/javascript"></script>
        <link href="scripts/css/style.css" rel="stylesheet" type="text/css"/>
        <link href="scripts/css/bootstrap.css" rel="stylesheet" type="text/css"/>
        <script src="scripts/js/custom.js" type="text/javascript"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>DBx-Iqt</title>
    </head>
    <body>
        <div class="form-horizontal" id="form-main-iqt">   
            <center>
                <h3>DBx-Iqt</h3>
            </center>

                <button id="menu-heuristics" type="button" class="btn btn-default" aria-label="Left Align">
                    <span class="glyphicon glyphicon-align-justify" aria-hidden="true"></span>
                </button>

                <div id="limit-menu-heuristics">
                    <div id="element-menu-heuristics"  class="select">
                        <b>Heurísticas para Reescrita</b><br />
                        <input type="checkbox" id="1" > Eliminar Tabelas Temporárias<br />
                        <input type="checkbox" id="2" > Eliminar Having desnecessário<br />
                        <input type="checkbox" id="3"> Eliminar Group by e/ou Having desnecessários<br />
                        <input type="checkbox" id="4"> Remover Função assossiada a coluna com índice<br />
                        <input type="checkbox" id="5"> Trocar conectivo OR por União de seleções<br /> 
                        <input type="checkbox" id="6"> Substituir operação All por sub-consulta<br />
                        <input type="checkbox" id="7"> Substituir operação Any por sub-consulta<br />
                        <input type="checkbox" id="8"> Substituir operação Some por sub-consulta<br />
                        <input type="checkbox" id="9"> Substituir operação In por junção<br />
                        <input type="checkbox" id="10"> Remover Expressão Aritmética de coluna com índice<br />
                        <input type="checkbox" id="11"> Remover Distinct desnecessário<br />
                        <%--
                        <hr>
                        <b>Definir Reescrita Padrão</b><br />
                        <input type="radio" name="opcaoReescrita"> Utilizar as heurísticas selecionadas para todas as SQLs<br />
                        <input type="radio" name="opcaoReescrita"> Utilizar as heurísticas selecionadas para todas as SQLs, excetos para as com Reescrita Personalizada<br />
                        <input type="radio" name="opcaoReescrita"> Escolher automaticamente as heurísticas para todas as SQLs, excetos para as com Reescrita Personalizada<br />
                        <input type="radio" name="opcaoReescrita"> Escolher automaticamente as heurísticas para todas as SQLs<br />
                        <button type="submit" class="btn btn-success btn-xs">Salvar</button>
                        --%>
                    </div>
                </div>
            <center>  
                <table id="btns-table">
                    <tr>
                        <td><button id="btn-wload" type="button" class="btn btn-primary btn-xs">Obter Workload</button></td>
                        <td><input id="btn-file" class="fileSQLs" type="file" class="btn btn-primary btn-xs" name="arquivo" value="Obter Arquivo"></td>
                    </tr>
                </table>
                <div id="form-main-table">
                    <div id="form-main-table-bar">
                        <b>Id | Consulta</b>
                    </div>
                    <div id="form-main-table-sqls">
                    </div>
                </div>
                
                <div id="original">
                    <div id="table-buttons">
                        <button type="submit" id="executarSqlOriginal" name="opcao" value="executar" class="btn btn-success btn-xs">Executar</button>
                        <button type="submit" id="reescreverSqlOriginal" name="opcao" value="reescrever" class="btn btn-success btn-xs">Reescrever</button>
                        <input type="checkbox" id="planOriginal"/> Ver Plano de Execução
                    </div>
                    <div id="form-main-query-original">
                        <div id="form-main-query-original-title">
                            <b>Consulta Original</b>
                        </div>
                        <textarea id="form-main-query-original-body" class="form-control" name="txt-query-original"></textarea>
                    </div>
                    <div id="form-main-plan-original" class="invisible">
                        <div id="form-main-plan-original-title">
                            <b>Plano Original</b>
                        </div>
                        <div id="form-main-plan-original-body"></div>
                    </div>
                </div>
                <div id="result" class="select">
                    <div id="table-buttons">
                        <input type="hidden" name="opcao" value="executar">
                        <input type="hidden" name="opcao" value="reescrever">

                        <button type="submit" id="executarSqlReescrita" class="btn btn-success btn-xs">Executar</button>
                        <input type="checkbox" id="planResult"/> Ver Plano de Execução
                    </div>
                    <div id="form-main-query-result">
                        <div id="form-main-query-result-title">
                            <b>Consulta Reescrita</b>
                        </div>
                        <div id="form-main-query-result-body"></div>
                    </div>
                    <div id="form-main-plan-result" class="invisible">
                        <div id="form-main-plan-result-title">
                            <b>Plano Reescrita</b>
                        </div>
                        <div id="form-main-plan-result-body"></div>
                    </div>
                </div>
            </center>           
        </div>
    </body>
    
</html>
