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
import interfaceweb.model.javabeans.WorkloadLog;

public class WorkloadLogDAO {
    
    private String url, ColInd = "";//Coluna_Indice
    private Connection connection;
    private Statement stmt;
    private ResultSet Cursor, Cursor2;
    private final Gson gsonObj = new GsonBuilder().disableHtmlEscaping().create();
    
    public WorkloadLogDAO(Connection connection){
        this.connection = connection;
    }
    
    public String getConsultaLine() {
        
        try {
            
            stmt = connection.createStatement();
            /*CONSULTA GRAFICO LINHA*/
            Cursor = stmt.executeQuery("SELECT wlog_id,wlog_time,wlog_duration,wlog_sql FROM agent.tb_workload_log ORDER BY wlog_time");
            String jsonLine;
            List<WorkloadLog> ListaObjetoLine = new ArrayList<>();
            WorkloadLog ObjetoLine = new WorkloadLog();
            boolean existeData = false, existe = false;
            while (Cursor.next()) {
                // System.err.println(Cursor.getString(1));
                for (WorkloadLog CL : ListaObjetoLine) {
                    if (CL.getName().equals(Cursor.getString(4))) {
                        existe = true;
                        for (Object[] time : CL.getData()) {
                            if ((long) time[0] == Cursor.getTimestamp(2).getTime()) {
                                existeData = true;
                            }
                            //System.out.println("ALALAL");
                        }
                        //System.out.println("TEM IGUAL");
                        if (!existeData) {
                            CL.addData(new Object[]{Cursor.getTimestamp(2).getTime(), Cursor.getDouble(3)});
                        }
                    }
                }
                if (!existe) {
                    ObjetoLine.setId(Cursor.getInt(1));
                    ObjetoLine.setName(Cursor.getString(4));
                    ObjetoLine.addData(new Object[]{Cursor.getTimestamp(2).getTime(), Cursor.getDouble(3)});
                    ListaObjetoLine.add(ObjetoLine);
                    ObjetoLine = new WorkloadLog();
                }
                existeData = false;
                existe = false;
            }
            jsonLine = gsonObj.toJson(ListaObjetoLine);

            stmt.close();
            
            return jsonLine;

        } catch (SQLException e1) {
            System.out.println(e1.getMessage());
        }
        
        return null;
    }
}
