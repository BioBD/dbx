/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.interfaces.IReactor;
import static bib.base.Base.log;
import static java.lang.Thread.sleep;

/**
 *
 * @author Rafael
 */
public abstract class AgentReactor extends Agent implements IReactor {

    @Override
    public void run() {
        while (true) {
            try {
                this.getLastTuningActionsNotAnalyzed();
                this.executeTuningActions();
                this.updateStatusTuningActions();
                sleep(4000);
            } catch (InterruptedException e) {
                log.errorPrint(e);
            }
        }
    }
}
