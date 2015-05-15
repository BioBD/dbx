/*
 * Automatic Creation Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package drivers.sqlserver;

import static base.Base.log;
import drivers.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Rafael
 */
public final class DriverSQLServer extends Driver {

    public DriverSQLServer() {
        super();
        connect();
    }

    private void connect() {
        try {
            if (DriverSQLServer.connection == null) {
                DriverSQLServer.connection = DriverManager.getConnection(this.propertiesFile.getProperty("urlSqlServer") + this.propertiesFile.getProperty("databaseName"), this.propertiesFile.getProperty("userSqlServer"), this.propertiesFile.getProperty("pwdSqlServer"));
                log.msgPrint("Conectado ao bd!", this.getClass().toString());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}
