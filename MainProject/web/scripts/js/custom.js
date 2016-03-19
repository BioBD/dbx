function showRow(rowId){
    $(rowId).fadeToggle();
    return false;
};

$(document).ready(function(){
    $.get("WorkloadTableServlet", function(data){
        $("#corpo-tabela").html(data);
    });
});

function trim(str) {
    return str.replace(/^\s+|\s+$/g,"");
};

function selectRow(rowId){

    var queryText = ($(rowId).text());
    document.getElementById("form-main-query-original-body").value = trim(queryText);
    document.getElementById("runQueryOriginal").value = trim(queryText);
    document.getElementById("rewriteQueryOriginal").value = trim(queryText);

};
            
$(document).ready(function(){

    $('#executarSqlOriginal').click(function(){
        $.post( "ServletIqt", {executar: $("#form-main-query-original-body").val()}, function( data ) {
            alert(data);
        });
    });
    
    $('#reescreverSqlOriginal').click(function(){
        $.post( "ServletIqt", { reescrever: $("#form-main-query-original-body").val(),
                                temporaryTable: $("#1").is(":checked"),
                                havingToWhere: $("#2").is(":checked"),
                                removeGroupby: $("#3").is(":checked"),
                                moveFunction: $("#4").is(":checked"),
                                orToUnion: $("#5").is(":checked"),
                                allToSubquery: $("#6").is(":checked"),
                                anyToSubquery: $("#7").is(":checked"),
                                someToSubquery: $("#8").is(":checked"),
                                inToJoin: $("#9").is(":checked"),
                                atithmetcExpression: $("#10").is(":checked"),
                                removeDistinct: $("#11").is(":checked")}, function( data ) {
            $("#form-main-query-result-body").html(data);
            if($('#result').hasClass("select") == true){
                $('#result').toggleClass('select');
                $('#result').slideDown('slow');
                elemento.style.display="block";
            }         
        });
    });
    $('#planOriginal').click(function(){
        $.post("ServletIqt", {plano: $("#form-main-query-original-body").val()}, function( data ) {
            $('#form-main-plan-original-body').html(data);
            $('#form-main-plan-original').toggleClass('invisible');
        });
    });
    
    $('#planResult').click(function(){
        $.post( "ServletIqt", {plano: $("#form-main-query-result-body").text()}, function( data ) {
            $('#form-main-plan-result-body').html(data);
            $('#form-main-plan-result').toggleClass('invisible');
        });
    });
    
    $('#executarSqlReescrita').click(function(){
        $.post( "ServletIqt", {executar: $("#form-main-query-original-body").val()}, function( data ) {
            alert(data);
        });
    });
    
    $("input:file").change(function (){
        
        var fileName = $(this).val();
        $.post( "ServletIqt", {file: fileName }, function( data ) {
             $("#form-main-table-sqls").html(data);
        });
     });
     
    $('#btn-wload').click(function(){
        $.post( "ServletIqt", {workload: "workload"}, function( data ) {
             $("#form-main-table-sqls").html(data);
        });
    });    
});
$(document).ready(function(){
   $("#menu-heuristics").click(function() {
        if($('#element-menu-heuristics').hasClass("select") == true){
            $('#element-menu-heuristics').toggleClass('select');
            $('#limit-menu-heuristics').slideDown('slow');
            elemento.style.display="block";
        } else {
            $('#limit-menu-heuristics').slideUp('slow');
            $('#element-menu-heuristics').toggleClass('select');
        }
    });
});