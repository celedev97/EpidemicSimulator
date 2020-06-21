package com.epidemic_simulator;

/**
 * This interface declares the methods necessary for the {@link com.epidemic_simulator.Simulator} to issue ecallbacks to an object.<BR>
 * <b>NOTE:</b> The Simulator only calls the methods of the SimulatorCallBacks that are registered.<BR>
 * <b>NOTE:</b> The Simulator <b>never</b> calls methods of a Strategy before the first red person has been found.<BR>
 *
 * @see com.epidemic_simulator.Simulator
 * @see com.epidemic_simulator.Person
 */
public interface SimulatorCallBack {

    /**
     * This method is called
     * every time a {@link com.epidemic_simulator.Person} develop {@link com.epidemic_simulator.Person#symptoms}.
     *
     * @param person the person that developed symptoms
     */
    void personHasSymptoms(Person person);

    /**
     * This method is called
     * every time a {@link com.epidemic_simulator.Person} become {@link com.epidemic_simulator.Person#immune}
     * after they had {@link com.epidemic_simulator.Person#symptoms}.
     *
     * @param person the person that healed from having symptoms
     */
    void personClean(Person person);

    /**
     * This method is called every time two {@link com.epidemic_simulator.Person}s meets,
     * it can be used for keeping a log of every meeting.
     *
     * @param person1 One of the two Persons
     * @param person2 The other Person
     */
    void registerEncounter(Person person1, Person person2);

    /**
     * This method is called every time the {@link com.epidemic_simulator.Simulator} finishes the simulation of a day.<BR>
     * It can be useful for activating a strategy every day,
     * or for running the Simulator in another {@link java.lang.Thread}, and having callbacks.
     *
     * @param outcome the outcome
     */
    void afterExecuteDay(Simulator.Outcome outcome);

}
