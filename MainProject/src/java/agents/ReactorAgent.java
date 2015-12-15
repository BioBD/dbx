/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package agents;

import agents.interfaces.IReactor;
import static java.lang.Thread.sleep;

/**
 *
 * @author Rafael
 */
public abstract class ReactorAgent extends Agent implements IReactor {

    @Override
    public void run() {
        log.msg("Execute agent " + this.getClass());
        while (true) {
            try {
                this.getLastTuningActionsNotAnalyzed();
                this.executeTuningActions();
                this.updateStatusTuningActions();
                sleep(4000);
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }
}
