/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.factory;

import agents.postgresql.PredictorMVPostgreSQLMV;
import base.Base;
import static base.Base.log;

/**
 *
 * @author Rafael
 */
public class PredictorFactory extends Base implements Runnable {

    public void run() {
        switch (Integer.parseInt(this.propertiesFile.getProperty("database"))) {
            case 2:
                if (Integer.parseInt(this.propertiesFile.getProperty("executionMode")) == 2) {
                    PredictorMVPostgreSQLMV pr = new PredictorMVPostgreSQLMV();
                    pr.run();
                }
                break;
            default:
                log.errorPrint("Banco de dados configurado incorreamente.", this.getClass().toString());
        }
    }

}
