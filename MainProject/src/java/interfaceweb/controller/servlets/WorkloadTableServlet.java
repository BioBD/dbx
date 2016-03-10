/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaceweb.controller.servlets;

import interfaceweb.model.javabeans.WorkloadTable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import interfaceweb.model.dao.WorkLoadTableDAO;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Italo
 */

@WebServlet(name = "WorkloadTableServlet", urlPatterns = {"/WorkloadTableServlet"})
public class WorkloadTableServlet extends HttpServlet {

   
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try{
        Connection connection = (Connection) request.getAttribute("connection");
        
        WorkLoadTableDAO dao = new WorkLoadTableDAO(connection);
        
        List<WorkloadTable> linhas = dao.read();
        
        try(PrintWriter out = response.getWriter()){
            for(WorkloadTable l : linhas){
                out.println("<tr class=\"clickable\" id=\"start-row\" onclick=\"showRow(texto"+l.getId()+")\">");
                out.println("<td>"+l.getId()+"</td>");
                out.println("<td>"+l.getNumberOfExecutions()+"</td>");
                out.println("<td>"+l.getType()+"</td>");
                out.println("<td>"+l.getRelevance()+"</td>");
                out.println("</tr>");
                out.println("<td colspan=\"4\" class=\"hidden-row table-bordered\" id=\"texto"+l.getId()+"\">");
                out.println("<table class=\"hidden-table\">");
                out.println("<tr>\n" +
                                "<td><b>Sql</b></td>\n" +
                            "</tr>\n" +
                            "<tr>\n" +
                                "<td>"+l.getSql()+"</td>"+
                            "</tr>"+
                            "<tr>\n" +
                                "<td><b>Indexes</b></td>"+
                            "</tr>"+
                            "<tr>\n" +
                                "<td>"+l.getIndexes()+"</td>"+
                            "</tr>"+
                            "<tr>\n" +
                                "<td><b>Vms</b></td>"+
                            "</tr>"+
                            "<tr>\n" +
                                "<td>"+l.getVms()+"</td>"+
                            "</tr>");
                out.println("</table>");
                out.println("</td>");
            } 
        }
        } catch (Exception exx){
            exx.printStackTrace();
        }  
    }

}
