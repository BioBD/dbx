
var urlbar2 ='consulta-materialized-views.php?inicio=0';

var contadorBar2 = setInterval(atualizarBar2, 10000);

var inicioBar2 = 0;

function somaInicioBar2(){
    inicioBar2=inicioBar2+4;
    url2(inicioBar2);
     $.getJSON(urlbar2, function(data){   
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
     $.getJSON(urlbar2, function(data){   
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
    //para o timer e atualiza o grafico
    var prefixo = "consulta-materialized-views.php?";
    var status = "status="+parametros[0];
    var order = "order="+parametros[1];
    urlbar2 = prefixo+status+"&"+order+"&"+"inicio="+inicio;
    contadorBar2 = setInterval(atualizarBar2, 10000);
}

function atualizarBar2(){
     $.getJSON(urlbar2, function(data){   
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
     $.getJSON(urlbar2, function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = true;
        optionsbar2.series[1].animation = true;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
});




$(document).ready(function inicializar(){
$.getJSON(urlbar2, function(data){   
        optionsbar2.xAxis.categories = data['nomes'];
        optionsbar2.series[0].animation = true;
        optionsbar2.series[1].animation = true;
        optionsbar2.series[0].data = data['profit'];
        optionsbar2.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar2);
    });
});


