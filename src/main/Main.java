package main;

import agents.factory.ObserverFactory;
import agents.factory.PredictorFactory;
import agents.factory.ReactorFactory;

public class Main {

    public static void main(String[] args) {
        if (args != null) {
            if (args[0].equals("1")) {
                ObserverFactory observer = new ObserverFactory();
                Thread threadObserver = new Thread(observer);
                threadObserver.start();
            }
            if ((args.length > 1) && (args[1].equals("1"))) {
                PredictorFactory predictor = new PredictorFactory();
                Thread threadPredictor = new Thread(predictor);
                threadPredictor.start();
            }
            if ((args.length > 2) && (args[2].equals("1"))) {
                ReactorFactory reactor = new ReactorFactory();
                Thread threadReactor = new Thread(reactor);
                threadReactor.start();
            }
        }
    }

}
