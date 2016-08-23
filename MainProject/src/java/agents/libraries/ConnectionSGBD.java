/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.libraries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionSGBD {

    private final Properties config;
    private final Log log;

    public ConnectionSGBD() {
        this.config = Configuration.getProperties();
        this.log = new Log(this.config);
        connect();
    }

    protected static Connection connection = null;

    private void connect() {
        try {
            if (connection == null) {
                System.out.println(config.getProperty("sgbd"));
                switch (config.getProperty("sgbd")) {
                    case "sqlserver":
                        connection = DriverManager.getConnection(config.getProperty("urlSQLServer") + "databaseName=" + config.getProperty("databaseName") + ";", config.getProperty("userSQLServer"), config.getProperty("pwdSQLServer"));
                        log.msg("Conectado ao bd " + config.getProperty("urlSQLServer") + ":" + config.getProperty("databaseName"));
                        break;
                    case "postgresql":
                        Class.forName("org.postgresql.Driver");
                        connection = DriverManager.getConnection(config.getProperty("urlPostgres") + config.getProperty("databaseName"), config.getProperty("userPostgres"), config.getProperty("pwdPostgres"));
                        log.msg("Conectado ao bd " + config.getProperty("urlPostgres") + ":" + config.getProperty("databaseName"));
                        break;
                    case "oracle":
                        connection = DriverManager.getConnection(config.getProperty("urlOracle") + config.getProperty("databaseName"), config.getProperty("userOracle"), config.getProperty("pwdOracle"));
                        log.msg("Conectado ao bd " + config.getProperty("urlOracle") + config.getProperty("databaseName"));
                        break;
                    default:
                        throw new UnsupportedOperationException("Atributo SGBD do arquivo de parâmetros (.properties) nao foi atribuido corretamente.");
                }
                log.msg("Aplicação versão: " + config.getProperty("versao"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            log.error(e);
        }
    }

    public void closeConnection() throws SQLException {
        ConnectionSGBD.connection.close();
    }

    public PreparedStatement prepareStatement(String query) {
        try {
            return ConnectionSGBD.connection.prepareStatement(config.getProperty("signature") + " " + query);
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            Statement statement = ConnectionSGBD.connection.createStatement();
            statement.closeOnCompletion();
            return statement.executeQuery(config.getProperty("signature") + " " + query);
        } catch (SQLException e) {
            log.msg(query);
            log.error(e);
            return null;
        }
    }

    public void executeUpdate(PreparedStatement prepared) {
        try {
            prepared.executeUpdate();
        } catch (SQLException e) {
            log.error(e);
        } finally {
            try {
                prepared.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    public void executeUpdate(String query) {
        PreparedStatement prepared = this.prepareStatement(query);
        try {
            prepared.executeUpdate();
        } catch (SQLException e) {
            log.msg("Query com erro:" + query);
            log.error(e);
        } finally {
            try {
                prepared.close();
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
    }

    public ResultSet executeQuery(PreparedStatement prepared) {
        try {
            return prepared.executeQuery();
        } catch (SQLException e) {
            log.error(e);
            return null;
        }
    }

    public Statement getStatement() {
        try {
            Statement statement = ConnectionSGBD.connection.createStatement();
            statement.closeOnCompletion();
            return statement;
        } catch (SQLException ex) {
            log.error(ex);
            return null;
        }
    }

}
