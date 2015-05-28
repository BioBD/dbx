/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers;

import base.Base;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Driver extends Base {

    public Driver() {
        super();
    }

    protected static Connection connection = null;
    private Statement statement;

    public void closeConnection() throws SQLException {
        Driver.connection.close();
    }

    public PreparedStatement prepareStatement(String query) {
        try {
            return Driver.connection.prepareStatement(query);
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
            return null;
        }
    }

    public void createStatement() {
        try {
            this.statement = Driver.connection.createStatement();
            log.msgPrint("criou statement", this.getClass().toString());
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            if (statement.isClosed()) {
                this.createStatement();
            }
            return statement.executeQuery(query);
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
            return null;
        }
    }

    public void closeStatement() {
    }

    public void executeUpdate(PreparedStatement prepared) {
        try {
            prepared.executeUpdate();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

    public ResultSet executeQuery(PreparedStatement prepared) {
        try {
            return prepared.executeQuery();
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
            return null;
        }
    }

    public Statement getStatement() {
        try {
            if (statement.isClosed()) {
                this.createStatement();
            }
        } catch (SQLException ex) {
            log.errorPrint(ex, this.getClass().toString());
        }
        return statement;
    }
}
