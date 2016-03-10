var contadorBar = setInterval(atualizarBar, 10000);
var urlbar = '';
var inicioBar = 0;

function somaInicioBar(){
    inicioBar=inicioBar+4;
    url(inicioBar);
     $.get(urlbar, function(data){   
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
     $.get(urlbar, function(data){   
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
    //Stops the timer and updates the url
    clearInterval(contadorBar);
    var prefixo = "JsonServlet?";
    var name = "name=ManagedeIndexes";
    var status = "status="+parametros[0];
    var type = "type="+parametros[1];
    var order = "order="+parametros[2];
    urlbar = prefixo+name+"&"+status+"&"+type+"&"+order+"&"+"inicio="+inicioBar;
    //sets a new timer with a new url
    contadorBar = setInterval(atualizarBar, 10000);
}

function atualizarBar(){
    url(inicioBar);
    $.get(urlbar, function(data){   
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
    $.get(urlbar, function(data){   
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = true;
        optionsbar.series[1].animation = true;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
    });
});




$(document).ready(function inicializar(){
    $.post("JsonServlet",{name: "ManagedeIndexes", inicio: "0", status: "true", 
       type: "true", order: "cid_index_profit"}, function(data){
        optionsbar.xAxis.categories = data['nomes'];
        optionsbar.series[0].animation = true;
        optionsbar.series[1].animation = true;
        optionsbar.series[0].data = data['profit'];
        optionsbar.series[1].data = data['cost'];
        chart = new Highcharts.Chart(optionsbar);
        
    });
});


