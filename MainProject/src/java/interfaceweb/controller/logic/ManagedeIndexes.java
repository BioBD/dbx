package interfaceweb.controller.logic;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import interfaceweb.model.dao.ManagedIndexesDAO;

/**
 *
 * @author Administrador
 */

public class ManagedeIndexes implements ILogic{
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response){
        
         Connection connection = (Connection) request.getAttribute("connection");
         
         String status = (String) request.getParameter("status");
         String type = (String) request.getParameter("type");
         String order = (String) request.getParameter("order");
         int inicio = Integer.parseInt(request.getParameter("inicio"));
         
         status = status.equals("undefined") ? "true" : status;
         type = type.equals("undefined") ? "true" : type;
         order = order.equals("undefined") ? "cid_index_profit" : order;
         
         ManagedIndexesDAO dao = new ManagedIndexesDAO(connection);
         
         String json = dao.getConsultaManagedIndexes(status, type, order, inicio);
         
         return json;
    }
}
