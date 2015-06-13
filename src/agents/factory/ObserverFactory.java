/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.factory;

import agents.postgresql.ObserverPostgreSQLMV;
import agents.sqlserver.ObserverSQLServerMV;
import base.Base;

/**
 *
 * @author Rafael
 */
public class ObserverFactory extends Base implements Runnable {

    public void run() {
        switch (Integer.parseInt(this.prop.getProperty("database"))) {
            case 2:
                ObserverPostgreSQLMV woPG = new ObserverPostgreSQLMV();
                woPG.run();
                break;
            case 3:
                ObserverSQLServerMV woSQL = new ObserverSQLServerMV();
                woSQL.run();
                break;
            default:
                log.errorPrint("Banco de dados configurado incorreamente.", this.getClass().toString());
        }
    }
}
