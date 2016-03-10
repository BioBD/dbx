package interfaceweb.model.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import interfaceweb.model.javabeans.Fragmentation;

public class FragmentationDAO {
    
    private String url, ColInd = "";//Coluna_Indice
    private final Connection connection;
    private Statement stmt, stmt2;
    private ResultSet Cursor, Cursor2;
    private final Gson gsonObj = new GsonBuilder().disableHtmlEscaping().create();
    
    public FragmentationDAO(Connection connection){

        this.connection = connection;
    }
    
    public String getConsultaFragmentation(int inicio) {
        try {

            stmt = connection.createStatement();

            /*CONSULTA FRAGMENTATION*/
            Cursor = stmt.executeQuery("SELECT cid_id,cid_table_name,cid_fragmentation_level FROM agent.tb_candidate_index WHERE cid_status!= 'R' ORDER BY cid_index_profit");
            Fragmentation ObjetoFrag = new Fragmentation();
            stmt2 = connection.createStatement();
            while (Cursor.next()) {
                ColInd += Cursor.getString(2) + "(";
                Cursor2 = stmt2.executeQuery("SELECT cic_column_name from agent.tb_candidate_index_column WHERE cid_id=" + Cursor.getInt(1));
                while (Cursor2.next()) {
                    ColInd += (Cursor2.getString(1)).trim() + ",";
                }
                ColInd = ColInd.substring(0, ColInd.length() - 1);
                ColInd += ")";
                ObjetoFrag.addNome(ColInd);
                ObjetoFrag.addFragmentation(Cursor.getDouble(3));
                ColInd = "";
            }
            inicio=inicio>ObjetoFrag.getNomesSize()? ObjetoFrag.getNomesSize():inicio;
            ObjetoFrag.setNomes(ObjetoFrag.getNomes().subList(inicio, (inicio + 4) > ObjetoFrag.getNomesSize() ? ObjetoFrag.getNomesSize() : inicio + 4));
            ObjetoFrag.setFragmentation(ObjetoFrag.getFragmentation().subList(inicio, (inicio + 4) > ObjetoFrag.getFragmentationSize() ? ObjetoFrag.getFragmentationSize() : inicio + 4));
            String jsonFrag = gsonObj.toJson(ObjetoFrag);
            
            stmt.close();
            stmt2.close();
            
            return jsonFrag;

        } catch (SQLException e1) {
            System.out.println(e1.getMessage());
        }
        return null;
    }
}
