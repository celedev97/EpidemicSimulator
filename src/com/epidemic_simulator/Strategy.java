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

    public void personClean(Person person) {
        //if the person is now clean from the virus i allow him to move again
        person.setCanMove(true);
    }

    public void personHasSymptoms(Person person){
        person.setCanMove(false);
    }


}
