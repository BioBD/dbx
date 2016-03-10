$(function () {
    $(document).ready(function () {
        Highcharts.setOptions({
            global: {
                useUTC: false
            }
        });

        $('#grafico-linha').highcharts('StockChart',{
            chart: {
                type: 'spline',
                animation: Highcharts.svg, // don't animate in old IE
                marginRight: 10,
                events: {
                    load: function () {
                        setInterval(function(){
                            $.post("JsonServlet",{name: "WorkloadLog"}, function(data){
                                 var chart =  $('#grafico-linha').highcharts('StockChart');
                                
                                for(i=0;i<data.length;i++){ 
                                    
                                    var v= true;
                                    for(j=0;j<chart.series.length;j++){
                                        if(data[i]['name'] == chart.series[j]['name']){
                                            v=false;
                                            save = j;     
                                        }
                                    }
                                    if(v){
                                        
                                        chart.addSeries(data[i]);
                                    }
                                    else{
                                        for(x=chart.series[save].data.length;x<data[i].data.length;x++){
                                            chart.series[save].addPoint(data[i].data[x]);
                                        }
                                      
                                    }
                               
                                    
                                }
                            });
                        },3000);
                        
                    }
                }
            },
            rangeSelector: {
            buttons: [{
                count: 1,
                type: 'minute',
                text: '1M'
            }, {
                count: 5,
                type: 'minute',
                text: '5M'
            }, {
                type: 'all',
                text: 'All'
            }],
            inputEnabled: false,
            selected: 0
        },
            title: {
                text: 'Live random data'
            },
            xAxis: {
                 type: 'datetime',
                dateTimeLabelFormats: {
                second: '%Y-%m-%d<br/>%H:%M:%S',
                minute: '%Y-%m-%d<br/>%H:%M',
                hour: '%Y-%m-%d<br/>%H:%M',
                day: '%Y<br/>%m-%d',
                week: '%Y<br/>%m-%d',
                month: '%Y-%m',
                year: '%Y'
                },
                tickPixelInterval:150
            },
            yAxis: {
                title: {
                    text: 'Value'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            legend:{enabled:true},
            tooltip: {
                
            },
            exporting: {
                enabled: false
            },
            series: []
        });
    });
});


function converterData(data){
    
    var dataArray = data.split(":");
    
    var horas = 3600 * parseInt(dataArray[0]);
    var minutos = 60 * parseInt(dataArray[1]);
    var segundos = parseInt(dataArray[2]);
    
   return horas+minutos+segundos;

}