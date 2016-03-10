package interfaceweb.controller.logic;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import interfaceweb.model.dao.MaterializedViewsDAO;


public class MaterializedViews implements ILogic{
    
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response){
        
        Connection connection = (Connection) request.getAttribute("connection");
        
        String status = request.getParameter("status");
        String order = request.getParameter("order");
        
        
        status = status.equals("undefined") ? "true" : status;
        order = order.equals("undefined") ? "cmv_profit" : order;
        
        int inicio = Integer.parseInt(request.getParameter("inicio"));
        
        MaterializedViewsDAO dao = new MaterializedViewsDAO(connection);
        
        String json = dao.getConsultaMaterializedViews(status, order, inicio);
        
       
        
        return json;
    }
}
