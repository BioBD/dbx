var urlpie ='consultapie.php?order=wld_capture_count';

var optionspie = {
        chart:{
            renderTo:'grafico-pizza',
            type:'pie',
             margin: [25, 25, 25, 25],
            spacingTop: 0,
            spacingBottom: 0,
            spacingLeft: 0,
            spacingRight: 0
        },
        xAxis:{
            categories:[]
        },
         title: {
            text: 'Workload'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b><br/>{point.text}'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                depth: 35,
                dataLabels: {
                    enabled: false,
                    format: '{point.name}'
                },
                    showInLegend: true
            }
        },
        series:[{}]
};


$('#botao-pizza').click(function botao(){  
    var parametro = "order="+$('input[name=order]:checked', '#pieForm').val();
    var prefixo = "consultapie.php?";
    urlpie = prefixo+parametro;
    
    $.getJSON(urlpie, function(data){   
        optionspie.series[0].data = data;
        chart = new Highcharts.Chart(optionspie);
    });
});




$(document).ready(function inicializar(){
    $.getJSON(urlpie, function(data){
        optionspie.series[0].data = data;
        chart = new Highcharts.Chart(optionspie);
        
    });
});
