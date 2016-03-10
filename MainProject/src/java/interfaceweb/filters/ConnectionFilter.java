package interfaceweb.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import interfaceweb.util.ConnectionFactory;

/**
 *
 * @author Administrador
 */
public class ConnectionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
        throws IOException, ServletException{

        try {
            //Open database connection.
            Connection connection = new ConnectionFactory().getConnection();

            //Sets the database connection  as an request`s attribute.
            request.setAttribute("connection", connection);
			
            fc.doFilter(request, response);

            //Closes the connection after the request processing 
            connection.close();

        } catch(SQLException e) {
            throw new ServletException(e);
	}
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException {}
	
    @Override
    public void destroy() {}
}
