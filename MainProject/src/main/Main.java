package main;

import agents.AgentObserver;
import agents.AgentPredictorIndex;
import agents.AgentPredictorMV;
import agents.AgentReactorIndex;
import agents.AgentReactorMV;

public class Main {

    public static void main(String[] args) {
        if (args != null) {
            if (args.length > 0 && args[0].equals("1")) {
                AgentObserver observer = new AgentObserver();
                Thread threadObserver = new Thread(observer);
                threadObserver.start();

            }
            if (args.length > 1 && args[1].equals("1")) {
                AgentPredictorMV predictor = new AgentPredictorMV();
                Thread threadObserver = new Thread(predictor);
                threadObserver.start();
            }
            if (args.length > 2 && args[2].equals("1")) {
                AgentReactorMV predictor = new AgentReactorMV();
                Thread threadObserver = new Thread(predictor);
                threadObserver.start();
            }

            if (args.length > 3 && args[3].equals("1")) {
                AgentPredictorIndex predictor = new AgentPredictorIndex();
                Thread threadObserver = new Thread(predictor);
                threadObserver.start();
            }
            if (args.length > 4 && args[4].equals("1")) {
                AgentReactorIndex reactor = new AgentReactorIndex();
                Thread threadObserver = new Thread(reactor);
                threadObserver.start();
            }
        }
    }
}
