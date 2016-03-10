package interfaceweb.controller.logic;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import interfaceweb.model.dao.WorkloadLogDAO;


public class WorkloadLog implements ILogic{
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response){
        
        Connection connection = (Connection) request.getAttribute("connection");
        
        WorkloadLogDAO dao = new WorkloadLogDAO(connection);
      
        String json = dao.getConsultaLine();
        
        return json; 
    }
}
