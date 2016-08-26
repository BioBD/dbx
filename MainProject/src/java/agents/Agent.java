/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.libraries.Configuration;
import agents.libraries.ConnectionSGBD;
import agents.libraries.Log;
import agents.sgbd.SQL;
import agents.sgbd.Schema;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Rafael
 */
public abstract class Agent implements Runnable {

    public final Properties config;
    public final Log log;
    public final ConnectionSGBD connection;
    public ArrayList<SQL> SQListToBeProcessed;
    public Schema schema;

    public Agent() {
        this.config = Configuration.getProperties();
        this.log = new Log(this.config);
        this.connection = new ConnectionSGBD();
        this.SQListToBeProcessed = new ArrayList<>();
    }

}
