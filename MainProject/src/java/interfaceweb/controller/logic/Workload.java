package interfaceweb.controller.logic;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import interfaceweb.model.dao.WorkloadDAO;
/**
 *
 * @author Italo
 */
public class Workload implements ILogic {
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response){
        
        Connection connection = (Connection) request.getAttribute("connection");
        
        String order = (String) request.getParameter("order");
        
        WorkloadDAO dao = new WorkloadDAO(connection);
        
        String json = dao.getJsonWorkload(order);
        
        return json;
    }
}
