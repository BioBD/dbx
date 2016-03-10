package interfaceweb.model.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import interfaceweb.model.javabeans.ManagedIndexes;

/**
 *
 * @author Karlos
 */


public class ManagedIndexesDAO {

    private String url, ColInd = "";//Coluna_Indice
    private Statement stmt, stmt2;
    private ResultSet Cursor, Cursor2;
    private final Gson gsonObj = new GsonBuilder().disableHtmlEscaping().create();
    private final Connection connection;
    
    public ManagedIndexesDAO(Connection connection){
        this.connection = connection;
    }
    
    public String getConsultaManagedIndexes(String status,String type,String order,int inicio) {
       
        try {

           
            stmt = connection.createStatement();
            /*CONSULTA MANAGED INDEXES*/
            Cursor = stmt.executeQuery("SELECT cid_id,cid_table_name,cid_index_profit,cid_creation_cost FROM agent.tb_candidate_index WHERE cid_status!= '"+ status +"' AND cid_type!= '"+ type +"' ORDER BY "+order);
            ManagedIndexes ObjetoManInd = new ManagedIndexes();
            stmt2 = connection.createStatement();
            while (Cursor.next()) {
                ColInd += Cursor.getString(2) + "(";
                Cursor2 = stmt2.executeQuery("SELECT cic_column_name from agent.tb_candidate_index_column WHERE cid_id=" + Cursor.getInt(1));
                while (Cursor2.next()) {
                    ColInd += (Cursor2.getString(1)).trim() + ",";
                }
                ColInd = ColInd.substring(0, ColInd.length() - 1);
                ColInd += ")";
                ObjetoManInd.addNome(ColInd);
                ObjetoManInd.addProfit(Cursor.getInt(3));
                ObjetoManInd.addCost(Cursor.getInt(4));
                ColInd = "";
            }
            inicio=inicio>ObjetoManInd.getNomesSize() ? ObjetoManInd.getNomesSize():inicio;
            ObjetoManInd.replaceNomes( ObjetoManInd.getNomes().subList( inicio, (inicio + 4) > ObjetoManInd.getNomesSize() ? ObjetoManInd.getNomesSize() : inicio + 4));
            ObjetoManInd.replaceProfit(ObjetoManInd.getProfit().subList(inicio, (inicio + 4) > ObjetoManInd.getProfitSize() ? ObjetoManInd.getProfitSize() :inicio + 4));
            ObjetoManInd.replaceCost(ObjetoManInd.getCost().subList(inicio, ( inicio + 4) > ObjetoManInd.getCostSize() ? ObjetoManInd.getCostSize() : inicio + 4));
            String jsonManInd = gsonObj.toJson(ObjetoManInd);

            stmt.close();
            stmt2.close();
            return jsonManInd;
            
        } catch (SQLException e1) {
            System.out.println(e1.getMessage());
        }
        return null;
    }
}
