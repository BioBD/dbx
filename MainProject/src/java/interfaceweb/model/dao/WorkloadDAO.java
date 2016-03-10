package interfaceweb.model.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import interfaceweb.model.javabeans.Workload;


public class WorkloadDAO {

    private String url, ColInd = "";//Coluna_Indice
    private Connection conn;
    private Statement stmt, stmt2;
    private ResultSet Cursor, Cursor2;
    private final Gson gsonObj = new GsonBuilder().disableHtmlEscaping().create();
    private final Connection connection;
    
    public WorkloadDAO(Connection connection){
        this.connection = connection;
    }
    
    public String getJsonWorkload(String order) {
       
        try {
            /*CONSULTA WORKLOAD*/
            
            stmt = connection.createStatement();

            Cursor = stmt.executeQuery("SELECT wld_id,"+ order +",wld_sql from agent.tb_workload");
            
            List<Workload> ListaObjetoWorkload = new ArrayList<>();
            
            while (Cursor.next()) {
                //System.out.println(""+Cursor.getString(3));
                ListaObjetoWorkload.add(new Workload(Cursor.getString(1), Cursor.getInt(2), Cursor.getString(3)));
            }
            String jsonWorkload = gsonObj.toJson(ListaObjetoWorkload);

            stmt.close();
            
            return jsonWorkload;
        } catch (SQLException e1) {
            System.out.println(e1.getMessage());
        }
            return null;
    }

}
