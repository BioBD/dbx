urlcolumn="consultafragmentation.php";

function setProperFrag(){
    var proper = [];
    for(i = 0; i < optionscolumn.series[1].data.length; i++){
        proper[i] = parseInt($('#proper-frag').val());
    }
    //alert("antes");
    //optionscolumn.xAxis.categories = 
    optionscolumn.series[0].animation = true;
    optionscolumn.series[1].animation = true;
    //optionscolumn.series[1].data = 
    optionscolumn.series[0].data = proper;
    chart = new Highcharts.Chart(optionscolumn);
    //alert(optionscolumn.series[0].data);
    //alert("depois");
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
    $.getJSON(urlcolumn, function(data){
        optionscolumn.xAxis.categories = data['nomes'];
        optionscolumn.series[0].animation = true;
        optionscolumn.series[1].animation = true;
        optionscolumn.series[1].data = data['fragmentation'];
        chart = new Highcharts.Chart(optionscolumn);
    });
});
