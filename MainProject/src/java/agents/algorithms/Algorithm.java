/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.algorithms;

import agents.libraries.Configuration;
import agents.libraries.Log;
import java.util.Properties;

/**
 *
 * @author Rafael
 */
public class Algorithm {

    public final Properties config;
    public final Log log;

    public Algorithm() {
        this.config = Configuration.getProperties();
        this.log = new Log(this.config);
    }

}
