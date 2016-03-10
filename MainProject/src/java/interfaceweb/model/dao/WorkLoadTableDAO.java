package interfaceweb.model.dao;

import interfaceweb.model.javabeans.WorkloadTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Italo
 */
public class WorkLoadTableDAO {
    private final Connection connection; 
    
    public WorkLoadTableDAO(Connection connection){
        this.connection = connection;
    }
    
    public List<WorkloadTable> read(){
        String sql = "SELECT wld_id, wld_sql, wld_plan, wld_type,wld_capture_count, wld_relevance FROM agent.tb_workload ORDER BY wld_id ASC";
        
        try{

            List<WorkloadTable> list = new ArrayList<>();
            
            Statement statement,statement2,statement3,statement4,statement5,statement6 ;
            statement=this.connection.createStatement();
            statement2=this.connection.createStatement();
            statement3=this.connection.createStatement();
            statement4=this.connection.createStatement();
            statement5=this.connection.createStatement();
            statement6 = this.connection.createStatement();

            ResultSet resultado,resultado2,resultado3,resultado4,resultado5,resultado6;
            resultado = statement.executeQuery(sql);
            
            
            String index,vm;

            if(!resultado.isBeforeFirst())
                return null;
            
            while(resultado.next()){
                WorkloadTable linha = new WorkloadTable();
                
                linha.setId(resultado.getInt("wld_id"));
                linha.setNumberOfExecutions(resultado.getInt("wld_capture_count"));
                linha.setPlan(resultado.getString("wld_plan"));
                linha.setRelevance(resultado.getInt("wld_relevance"));
                linha.setSql(resultado.getString("wld_sql"));
                linha.setType(resultado.getString("wld_type"));

                index = vm = "";

                resultado2=statement2.executeQuery("SELECT cid_id FROM agent.tb_task_indexes WHERE wld_id="+resultado.getInt("wld_id"));
                while(resultado2.next()){

                    resultado3=statement3.executeQuery("SELECT cid_index_name FROM agent.tb_candidate_index WHERE cid_id="+resultado2.getInt("cid_id"));
                    
                    while(resultado3.next()){
                        index+=resultado3.getString("cid_index_name")+"(";
                        resultado4=statement4.executeQuery("SELECT cic_column_name from  agent.tb_candidate_index_column WHERE cid_id="+resultado2.getInt("cid_id"));
                        while(resultado4.next()){
                            index+=resultado4.getString("cic_column_name").trim()+",";
                        }
                        index=index.substring(0,index.length()-1);
                        index+=") <br/>";
                        resultado4.close();
                    }
                    resultado3.close();

                }
                resultado2.close();
                
                resultado5=statement5.executeQuery("SELECT cmv_id FROM agent.tb_task_views WHERE wld_id="+resultado.getInt("wld_id"));
                while(resultado5.next()){

                    vm+=resultado5.getString("cmv_id")+"(";
                    resultado6=statement6.executeQuery("SELECT cmv_ddl_create from agent.tb_candidate_view WHERE cmv_id="+resultado5.getInt("cmv_id"));
                    while(resultado6.next()){
                        vm+=resultado6.getString("cmv_ddl_create").trim()+",";
                    }
                    resultado6.close();
                    vm=vm.substring(0,vm.length()-1);
                    vm+=") <br/>";

                }
                resultado5.close();
                linha.setIndexes(index);                
                linha.setVms(vm);

                list.add(linha);
            }
            
            resultado.close();
           

            statement.close();
            statement2.close();
            statement3.close();
            statement4.close();
            statement5.close();
            statement6.close();
             
            return list;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }
}