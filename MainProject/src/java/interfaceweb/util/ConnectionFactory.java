package interfaceweb.util;

import agents.libraries.Configuration;
import iqt.Dbms;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Italo
 */
public class ConnectionFactory {
    public Connection getConnection() {
        Configuration config = new Configuration();

        String url = "jdbc:postgresql://"+config.getProperty("serverPostgres")+":"+config.getProperty("portPostgres")+"/"+config.getProperty("databaseName")+"?currentSchema=agent";
        String usuario = config.getProperty("userPostgres");
        String senha = config.getProperty("pwdPostgres");
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, usuario, senha);
            
        } catch(SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
	}
        return connection;
    }
}
