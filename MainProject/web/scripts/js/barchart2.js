
var urlbar2 ='';

var contadorBar2 = setInterval(atualizarBar2, 10000);

var inicioBar2 = 0;

function somaInicioBar2(){
    inicioBar2=inicioBar2+4;
    url2(inicioBar2);
     $.get(urlbar2, function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = true;
        optionsbar2.series[1].animation = true;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
};

function subtraiInicioBar2(){
    inicioBar2 = inicioBar2 - 4;
    if(inicioBar2 < 0){
        url2(0);
        inicioBar2 = 0;
    }else{
        url2(inicioBar2);
    }
     $.get(urlbar2, function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = true;
        optionsbar2.series[1].animation = true;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
   
};

function url2(inicio){
    var parametros = [$('input[name=status]:checked', '#barForm-2').val(), $('input[name=order]:checked', '#barForm-2').val()];
    //Stops the timer and updates the url
    clearInterval(contadorBar2);
    var prefixo = "JsonServlet?";
    var name = "name=MaterializedViews";
    var status = "status="+parametros[0];
    var order = "order="+parametros[1];
    urlbar2 = prefixo+name+"&"+status+"&"+order+"&"+"inicio="+inicio;
    //Sets a new timer with a new url
    contadorBar2 = setInterval(atualizarBar2, 10000);
}

function atualizarBar2(){
    url2(inicioBar2);
    $.get(urlbar2, function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = false;
        optionsbar2.series[1].animation = false;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
}


var optionsbar2 = {
        chart:{
            renderTo:'grafico-barras-2',
            type:'bar'
        },
        title:{
            text: 'Materialized Views'
        },
        xAxis:{
            categories:[]
        },
        series:[{name: 'Materialized View Profit'},{name:'Materialized View Creation Cost'}]
};

$('#botao-barra-2').click(function botao(){  
    url2(0);
    $.get(urlbar2, function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = true;
        optionsbar2.series[1].animation = true;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
});




$(document).ready(function inicializar(){
    $.post("JsonServlet",{name:"MaterializedViews", status: "true",type: "true"
        , order: "cmv_profit", inicio: "0"},function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = true;
        optionsbar2.series[1].animation = true;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
});


