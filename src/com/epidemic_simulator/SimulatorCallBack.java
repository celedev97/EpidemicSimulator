package com.epidemic_simulator;

public interface SimulatorCallBack {

    void personHasSymptoms(Person person);

    void personClean(Person person);

    void registerEncounter(Person person1, Person person2);

    void afterExecuteDay(Simulator.Outcome outcome);

}
