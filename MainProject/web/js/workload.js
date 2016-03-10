var contadorWorkload = setInterval(atualizaWorkload, 10000);

function atualizaWorkload(){
    $.post("JsonServlet", {name: "Workload", order: "wld_capture_count"}, function(data){
        optionspie.series[0].data = data;
        optionspie.series[0].animation = false;
        chart = new Highcharts.Chart(optionspie);
        
    });
};

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
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b><br/>'
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
    var parametro = $('input[name=order]:checked', '#pieForm').val();
   
    $.post("JsonServlet", {name: "Workload", order: parametro}, function(data){   
        optionspie.series[0].data = data;
        optionspie.series[0].animation = true;
        chart = new Highcharts.Chart(optionspie);
    });
});




$(document).ready(function inicializar(){
    $.post("JsonServlet", {name: "Workload", order: "wld_capture_count"}, function(data){
        optionspie.series[0].data = data;
        optionspie.series[0].animation = true;
        chart = new Highcharts.Chart(optionspie);   
    });
});
