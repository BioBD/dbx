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
                DriverSQLServer.connection = DriverManager.getConnection(this.propertiesFile.getProperty("urlSQLServer") + "databaseName=" + this.propertiesFile.getProperty("databaseName") + ";", this.propertiesFile.getProperty("userSQLServer"), this.propertiesFile.getProperty("pwdSQLServer"));
                log.msgPrint("Conectado ao bd " + this.propertiesFile.getProperty("urlSQLServer"), this.getClass().toString());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }

}
