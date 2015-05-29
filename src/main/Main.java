package main;

import agents.factory.ObserverFactory;
import agents.factory.PredictorFactory;

public class Main {

    public static void main(String[] args) {
        ObserverFactory observer = new ObserverFactory();
        Thread threadObserver = new Thread(observer);
        threadObserver.start();

        PredictorFactory predictor = new PredictorFactory();
        Thread threadPredictor = new Thread(predictor);
        threadPredictor.start();
//
//        ReactorFactory reactor = new ReactorFactory();
//        Thread threadReactor = new Thread(reactor);
//        threadReactor.start();
    }

}
