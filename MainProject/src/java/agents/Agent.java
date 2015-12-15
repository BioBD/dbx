/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.sgbd.SQL;
import agents.sgbd.Schema;
import java.util.ArrayList;
import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.libraries.Log;

/**
 *
 * @author Rafael
 */
public abstract class Agent implements Runnable {

    public final Configuration config;
    public final Log log;
    public final ConnectionSGBD connection;
    public ArrayList<SQL> SQListToBeProcessed;
    public Schema schema;

    public Agent() {
        this.config = new Configuration();
        this.log = new Log(this.config);
        this.connection = new ConnectionSGBD();
        this.SQListToBeProcessed = new ArrayList<>();
    }

}
