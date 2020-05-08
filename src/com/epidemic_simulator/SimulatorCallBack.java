package com.epidemic_simulator;

public interface SimulatorCallBack {

    public void personHasSymptoms(Person person);

    public void personClean(Person person);

    public void registerEncounter(Person person1, Person person2);

    public void afterExecuteDay();

}
