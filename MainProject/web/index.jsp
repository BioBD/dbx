<%--
    Document   : index
    Created on : 15/12/2015, 12:15:44
    Author     : Rafael
--%>
<%@page import="servlets.Memory"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% Memory memory = new Memory();%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta charset="utf-8" />
        <title>DBX</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body class="blurBg-false" style="background-color:#EBEBEB">
        <link rel="stylesheet" href="scripts/formoid-metro-cyan.css" type="text/css" charset="UTF-8"/>
        <script type="text/javascript" src="scripts/jquery.min.js" charset="UTF-8"></script>

        <form class="formoid-metro-cyan" style="background-color:#FFFFFF;font-size:14px;font-family:'Trebuchet MS',Helvetica,sans-serif;color:#666666;max-width:800px;min-width:150px" method="post">
            <div class="title">
                <h2>DBX - interface temporária para testes</h2>
            </div>
            <div class="element-input">
                <style type="text/css">
                    .tg  {border-collapse:collapse;border-spacing:0;border-color:#ccc;margin:0px auto;}
                    .tg td{font-family:Arial, sans-serif;font-size:14px;padding:17px 20px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#fff;}
                    .tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:17px 20px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;border-color:#ccc;color:#333;background-color:#f0f0f0;}
                    .tg .tg-0vih{background-color:#f9f9f9;font-weight:bold;text-align:center;vertical-align:top}
                    .tg .tg-amwm{font-weight:bold;text-align:center;vertical-align:top}
                    .tg .tg-yw4l{vertical-align:top}
                    .tg .tg-b7b8{background-color:#f9f9f9;vertical-align:top}
                    @media screen and (max-width: 767px) {.tg {width: auto !important;}.tg col {width: auto !important;}.tg-wrap {overflow-x: auto;-webkit-overflow-scrolling: touch;margin: auto 0px;}}</style>
                <div class="tg-wrap">
                    <br>
                    <table class="tg">
                        <tr>
                            <td class="tg-amwm"><br>Observer</td>
                            <td class="tg-yw4l">
                                <% if (memory.isNotRunning("ObserverAgent")) {%>
                                <a href="ServletAgents?cmd=startObserverAgent">Start Observer Agent</a>
                                <% } else {%>
                                Observer Agent is running.
                                <% }%>
                            </td>
                        </tr>
                        <tr>
                            <td class="tg-0vih"><br>Predictor</td>
                            <td class="tg-b7b8">
                                <% if (memory.isNotRunning("PredictorMVAgent")) {%>
                                <a href="ServletAgents?cmd=startPredictorMVAgent">Start PredictorMV Agent</a>
                                <% } else {%>
                                PredictorMV Agent is running.
                                <% }%><br/>

                                <% if (memory.isNotRunning("PredictorIndexAgent")) {%>
                                <a href="ServletAgents?cmd=startPredictorIndexAgent">Start PredictorIndex Agent</a>
                                <% } else {%>
                                PredictorIndex Agent is running.
                                <% }%><br/>
                            </td>
                        </tr>
                        <tr>
                            <td class="tg-amwm"><br>Reactor</td>
                            <td class="tg-yw4l">
                                <% if (memory.isNotRunning("ReactorMVAgent")) {%>
                                <a href="ServletAgents?cmd=startReactorMVAgent">Start ReactorMV Agent</a>
                                <% } else {%>
                                ReactorMV Agent is running.
                                <% }%><br/>

                                <% if (memory.isNotRunning("ReactorIndexAgent")) {%>
                                <a href="ServletAgents?cmd=startReactorIndexAgent">Start ReactorIndex Agent</a>
                                <% } else {%>
                                ReactorIndex Agent is running.
                                <% }%><br/>
                            </td>
                        </tr>
                    </table>
                    <br><br>
                </div>
                <div class="submit">
                    <h3>BIOBD (PUC-RIO) e DC (UFC)</h3>
                </div>
        </form>
        <script type="text/javascript" src="scripts/formoid-metro-cyan.js" charset="UTF-8"></script>
    </body>
</html>
