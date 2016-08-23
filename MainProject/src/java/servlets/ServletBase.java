package servlets;

import agents.libraries.Configuration;
import agents.libraries.Log;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Rafael
 */
@WebServlet(name = "ServletBase", urlPatterns = {"/ServletBase"})
public class ServletBase extends HttpServlet {

    protected HttpServletRequest request;
    HttpServletResponse response;
    protected String output = "";
    protected String param = "";
    protected Log log;
    protected Properties config;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.request = request;
        this.response = response;
        this.output = "";
        this.param = "";
        this.config = Configuration.getProperties();
        this.log = new Log(config);
        this.response.setContentType("text/html;charset=UTF-8");
        if (this.request != null && this.request.getParameterMap().containsKey("cmd") && !this.request.getParameter("cmd").isEmpty()) {
            try {
                Method method = this.getClass().getMethod(this.request.getParameter("cmd"));
                method.invoke(this);
                this.request.getRequestDispatcher(this.request.getParameter("cmd") + ".jsp" + param).forward(this.request, this.response);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ServletBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
