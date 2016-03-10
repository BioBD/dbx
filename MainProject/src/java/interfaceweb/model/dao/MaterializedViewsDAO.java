package interfaceweb.model.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import interfaceweb.model.javabeans.MaterializedViews;

public class MaterializedViewsDAO {
    
    private String url, ColInd = "";//Coluna_Indice
    private final Connection connection;
    private Statement stmt, stmt2;
    private ResultSet Cursor, Cursor2;
    private final Gson gsonObj = new GsonBuilder().disableHtmlEscaping().create();
    
    
    public MaterializedViewsDAO(Connection connection){
        this.connection = connection;
    }
    
    public String getConsultaMaterializedViews(String status,String order,int inicio) {
      
        try {
            
            stmt = connection.createStatement();

            /*CONSULTA MATERIALIZED VIEWS*/
            Cursor = stmt.executeQuery("SELECT cmv_id,cmv_cost,cmv_profit FROM agent.tb_candidate_view WHERE cmv_status != '"+ status +"' ORDER BY "+order);
            MaterializedViews ObjetoMatViews = new MaterializedViews();

            while (Cursor.next()) {
                ObjetoMatViews.addNome(Cursor.getString(1));
                ObjetoMatViews.addProfit(Cursor.getInt(3));
                ObjetoMatViews.addCost(Cursor.getInt(2));
            }
            inicio=inicio>ObjetoMatViews.nomes.size() ? ObjetoMatViews.nomes.size():inicio;
            ObjetoMatViews.replaceNomes(ObjetoMatViews.getNomes().subList(inicio, (inicio + 4) > ObjetoMatViews.getNomesSize() ? ObjetoMatViews.getNomesSize() : inicio + 4));
            ObjetoMatViews.replaceProfit(ObjetoMatViews.getProfit().subList(inicio, (inicio + 4) > ObjetoMatViews.getProfitSize() ? ObjetoMatViews.getProfitSize() : inicio + 4));
            ObjetoMatViews.replaceCost(ObjetoMatViews.getCost().subList(inicio, (inicio + 4) > ObjetoMatViews.getCostSize()?ObjetoMatViews.getCostSize() : inicio + 4));
            String jsonMatViews = gsonObj.toJson(ObjetoMatViews);

            stmt.close();
       
            return jsonMatViews;
            
        } catch (SQLException e1) {
            System.out.println(e1.getMessage());
        }
        return null;
    }
}   
