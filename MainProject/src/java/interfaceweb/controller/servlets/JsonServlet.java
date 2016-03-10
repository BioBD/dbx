/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaceweb.controller.servlets;

import interfaceweb.controller.logic.ILogic;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author Administrador
 */
@WebServlet(name = "JsonServlet", urlPatterns = {"/JsonServlet"})
public class JsonServlet extends HttpServlet {

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        String graphName = (String) request.getParameter("name");
         
        
        String className = "interfaceweb.controller.logic." + graphName;
        
      
        try (PrintWriter out = response.getWriter()) {
          
            Class<?> jsonClass = Class.forName(className);
            
            ILogic logic = (ILogic) jsonClass.newInstance();
            
            String json = logic.execute(request, response);
            
            out.println(json);
            
        } catch (Exception e) {
           throw new ServletException(e);
        }
    }

}
