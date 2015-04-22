/*
 * Automatic Create Materialized Views
 * Authors: rpoliveira@inf.puc-rio.br, sergio@inf.puc-rio.br  *
 */
package agents.postgresql;

import agents.PredictorMV;
import drivers.postgresql.DriverPostgreSQL;
import drivers.postgresql.QueriesPostgreSQL;
import java.util.ArrayList;

/**
 *
 * @author Rafael
 */
public class PredictorMVPostgreSQL extends PredictorMV {

    public PredictorMVPostgreSQL() {
        super();
        driver = new DriverPostgreSQL();
        this.queries = new QueriesPostgreSQL();
        this.itemsBag = new ArrayList<>();
        this.idDDLForMaterialization = new ArrayList<>();
    }

}
