
var urlbar ='consultabar.php?inicio=0';

var contadorBar = setInterval(atualizarBar, 10000);

var inicioBar = 0;

function somaInicioBar(){
    inicioBar=inicioBar+4;
    url(inicioBar);
     $.getJSON(urlbar, function(data){   
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = true;
        optionsbar.series[1].animation = true;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
    });
};

function subtraiInicioBar(){
    inicioBar = inicioBar - 4;
    if(inicioBar < 0){
        url(0);
        inicioBar = 0;
    }else{
        url(inicioBar);
    }
     $.getJSON(urlbar, function(data){   
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = true;
        optionsbar.series[1].animation = true;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
    });
   
};

function url(inicioBar){
    var parametros = [$('input[name=status]:checked', '#barForm').val(), $('input[name=type]:checked', '#barForm').val(), $('input[name=order]:checked', '#barForm').val()];
    //para o timer e atualiza o grafico
    clearInterval(contadorBar);
    var prefixo = "consultabar.php?";
    var status = "status="+parametros[0];
    var type = "type="+parametros[1];
    var order = "order="+parametros[2];
    urlbar = prefixo+status+"&"+type+"&"+order+"&"+"inicio="+inicioBar;
    contadorBar = setInterval(atualizarBar, 10000);
}

function atualizarBar(){
    $.getJSON(urlbar, function(data){   
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = false;
        optionsbar.series[1].animation = false;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
    });
}


var optionsbar = {
        chart:{
            renderTo:'grafico-barras',
            type:'bar'
        },
        title:{
            text: 'Managed Indexes'
        },
        xAxis:{
            categories:[]
        },
        series:[{name: 'Index Profit'},{name:'Index Creation Cost'}]
};


$('#botao-barra').click(function botao(){  
    url(0);
    $.getJSON(urlbar, function(data){   
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = true;
        optionsbar.series[1].animation = true;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
    });
});




$(document).ready(function inicializar(){
    $.getJSON(urlbar, function(data){
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = true;
        optionsbar.series[1].animation = true;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
        
    });
});


