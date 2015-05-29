package drivers.postgresql;

import drivers.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DriverPostgreSQL extends Driver {

    public DriverPostgreSQL() {
        super();
        connect();
    }

    private void connect() {
        try {
            if (DriverPostgreSQL.connection == null) {
                DriverPostgreSQL.connection = DriverManager.getConnection(this.propertiesFile.getProperty("urlPostgres") + this.propertiesFile.getProperty("databaseName"), this.propertiesFile.getProperty("userPostgres"), this.propertiesFile.getProperty("pwdPostgres"));
                log.msgPrint("Conectado ao bd " + this.propertiesFile.getProperty("urlPostgres"), this.getClass().toString());
            }
        } catch (SQLException e) {
            log.errorPrint(e, this.getClass().toString());
        }
    }
}
