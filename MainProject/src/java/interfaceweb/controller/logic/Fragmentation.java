
package interfaceweb.controller.logic;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import interfaceweb.model.dao.FragmentationDAO;

public class Fragmentation implements ILogic{
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response){
        
        Connection connection = (Connection) request.getAttribute("connection");
        
        int inicio = Integer.parseInt(request.getParameter("inicio"));
        
        FragmentationDAO dao = new FragmentationDAO(connection);
        
        String json = dao.getConsultaFragmentation(inicio);
        
        return json;
    }
}
