package com.epidemic_simulator;
import java.util.ArrayList;

public abstract class Strategy {
    protected Simulator simulator;
    private ArrayList<Person> population;
    protected int originalResources;

    public Strategy(Simulator simulator) {
        this.simulator = simulator;
        simulator.strategy = this;
        originalResources = simulator.getResources();
    }

    protected void initialLockDown(int lockDownPercentage) {
        for (int i = (int)(simulator.population.size() * (1-(lockDownPercentage/100.0))); i < simulator.population.size(); i++) {
            simulator.population.get(i).setCanMove(false);
        }
    }

    public abstract void afterExecuteDay();

    public abstract void personClean(Person person);

    public abstract void personHasSymptoms(Person person);
}
