package main;

import agents.AgentObserverMV;
import agents.AgentPredictorMV;

public class Main {

    public static void main(String[] args) {
        if (args != null) {
            if (args.length > 0 && args[0].equals("1")) {
                AgentObserverMV observer = new AgentObserverMV();
                Thread threadObserver = new Thread(observer);
                threadObserver.start();
            }
            if (args.length > 1 && args[1].equals("1")) {
                AgentPredictorMV predictor = new AgentPredictorMV();
                Thread threadObserver = new Thread(predictor);
                threadObserver.start();
            }
        }
    }
}
