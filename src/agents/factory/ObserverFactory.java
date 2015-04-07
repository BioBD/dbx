/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.factory;

import agents.postgresql.ObserverPostgreSQL;
import base.Base;

/**
 *
 * @author Rafael
 */
public class ObserverFactory extends Base implements Runnable {

    public void run() {
        switch (Integer.parseInt(this.propertiesFile.getProperty("database"))) {
            case 2:
                ObserverPostgreSQL wo = new ObserverPostgreSQL();
                wo.run();
                break;
            default:
                log.errorPrint("Banco de dados configurado incorreamente.", this.getClass().toString());
        }
    }
}
