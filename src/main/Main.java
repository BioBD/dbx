package main;

import agents.AgentObserverMV;

public class Main {

    public static void main(String[] args) {
        if (args != null) {
            if (args[0].equals("1")) {
                AgentObserverMV observer = new AgentObserverMV();
                Thread threadObserver = new Thread(observer);
                threadObserver.start();
            }
        }

    }
}
