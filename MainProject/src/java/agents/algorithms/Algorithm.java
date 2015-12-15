/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents.algorithms;

import agents.libraries.Configuration;
import agents.libraries.Log;

/**
 *
 * @author Rafael
 */
public class Algorithm {

    public final Configuration config;
    public final Log log;

    public Algorithm() {
        this.config = new Configuration();
        this.log = new Log(this.config);
    }

}
