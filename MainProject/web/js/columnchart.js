
var contadorFrag = setInterval(atualizaFrag, 10000);

function atualizaFrag(){
   $.post("JsonServlet",{name: "Fragmentation", inicio: "0"}, function(data){
        optionscolumn.xAxis.categories = data['nomes'];
        optionscolumn.series[1].data = data['fragmentation'];     
    });
    var proper = [];
    for(i = 0; i < optionscolumn.series[1].data.length; i++){
       if(isNaN(parseInt($('#proper-frag').val()))){
           proper[i] = 0;
       } else{
           proper[i] = parseInt($('#proper-frag').val());
       }
    }

    optionscolumn.series[0].animation = true;
    optionscolumn.series[1].animation = true;
   
    optionscolumn.series[0].data = proper;
    chart = new Highcharts.Chart(optionscolumn);
    chart.redraw();
    
};

function setProperFrag(){
    var proper = [];
    for(i = 0; i < optionscolumn.series[1].data.length; i++){
        proper[i] = parseInt($('#proper-frag').val());
    }

    optionscolumn.series[0].animation = true;
    optionscolumn.series[1].animation = true;
   
    optionscolumn.series[0].data = proper;
    chart = new Highcharts.Chart(optionscolumn);
    
};

var optionscolumn = {
    chart: {
        renderTo: 'grafico-coluna',
        type: 'column',
        margin: 75,
    },
    title: {
        text: 'Index fragmentation level'
    },
    subtitle: {
        text: ''
    },
    yAxis: {
        title: {
            verticalAlign: 'top',
            text: 'Fragmentation Level'
        },
    },
    xAxis: {
        title: {
            text: 'Index'
        },
    },
    legend: {
        verticalAlign: 'bottom',
    },
    plotOptions: {
        
        column: {
            
            depth: 35
        }
    },
    series: [{
        name:'Proper fragmentation level'
    },{
        name:'Fragmentation level'
    }]
};

$(document).ready(function inicializar(){
    $.post("JsonServlet",{name: "Fragmentation", inicio: "0"}, function(data){
        optionscolumn.xAxis.categories = data['nomes'];
        optionscolumn.series[0].animation = true;
        optionscolumn.series[1].animation = true;
        optionscolumn.series[1].data = data['fragmentation'];
        chart = new Highcharts.Chart(optionscolumn);
    });
});

