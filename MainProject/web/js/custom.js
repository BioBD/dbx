function showRow(rowId){
    $(rowId).fadeToggle();
    return false;
};

$(document).ready(function(){
    $.get("WorkloadTableServlet", function(data){
        $("#corpo-tabela").html(data);
    });
});