package interfaceweb.controller.logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Italo
 */
public interface ILogic {
    
    String execute(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
}
