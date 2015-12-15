/*
 * DBX - Database eXternal Tuning
 * BioBD Lab - PUC-Rio && DC - UFC  *
 */
package servlets;

import agents.ObserverAgent;
import agents.PredictorIndexAgent;
import agents.PredictorMVAgent;
import agents.ReactorIndexAgent;
import agents.ReactorMVAgent;
import javax.servlet.annotation.WebServlet;

/**
 *
 * @author Rafael
 */
@WebServlet(name = "ServletAgents", urlPatterns = {"/ServletAgents"})
public class ServletAgents extends ServletBase {

    public void startObserverAgent() {
        Memory memory = new Memory();
        if (memory.isNotRunning("ObserverAgent")) {
            ObserverAgent agent = new ObserverAgent();
            Thread threadObserver = new Thread(agent);
            threadObserver.start();
            memory.addAgent("ObserverAgent");
            log.msg("Observer agent started");
        }
    }

    public void startPredictorMVAgent() {
        Memory memory = new Memory();
        if (memory.isNotRunning("PredictorMVAgent")) {
            PredictorMVAgent agent = new PredictorMVAgent();
            Thread threadObserver = new Thread(agent);
            threadObserver.start();
            memory.addAgent("PredictorMVAgent");
            log.msg("PredictorMV agent started");
        }
    }

    public void startReactorMVAgent() {
        Memory memory = new Memory();
        if (memory.isNotRunning("ReactorMVAgent")) {
            ReactorMVAgent agent = new ReactorMVAgent();
            Thread threadObserver = new Thread(agent);
            threadObserver.start();
            memory.addAgent("ReactorMVAgent");
            log.msg("ReactorMVAgent agent started");
        }
    }

    public void startPredictorIndexAgent() {
        Memory memory = new Memory();
        if (memory.isNotRunning("PredictorIndexAgent")) {
            PredictorIndexAgent agent = new PredictorIndexAgent();
            Thread threadObserver = new Thread(agent);
            threadObserver.start();
            memory.addAgent("PredictorIndexAgent");
            log.msg("PredictorIndex agent started");
        }
    }

    public void startReactorIndexAgent() {
        Memory memory = new Memory();
        if (memory.isNotRunning("ReactorIndexAgent")) {
            ReactorIndexAgent agent = new ReactorIndexAgent();
            Thread threadObserver = new Thread(agent);
            threadObserver.start();
            memory.addAgent("ReactorIndexAgent");
            log.msg("ReactorIndexAgent agent started");
        }
    }

}
