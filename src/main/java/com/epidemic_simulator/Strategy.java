package com.epidemic_simulator;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An object that represent a Strategy.<BR>
 * A Strategy works alongside with a {@link Simulator}, in a similar way of how a {@link SimulatorCallBack} would do.<BR>
 * The main difference between a Strategy and a SimulatorCallBack is that the Strategy methods are not called before the first red is found<BR>
 * The difference is only in the fact that a strategy keeps an inner log of every encounter between every person.
 */
public abstract class Strategy implements SimulatorCallBack {
    /**
     * The {@link Simulator} attached to this strategy, this should just be used to issue methods from the simulator.<BR>
     * <b>Setting this variable to something else will not unlink this strategy from the old Simulator and it won't either link it to the new one</b>
     * @see Simulator#setStrategy
     * @see Simulator#addCallBack
     */
    protected Simulator simulator;
    /**
     * The starting number for the {@link Simulator} resources when this Strategy was attached to the Simulator.
     */
    protected final long originalResources;
    private String log = "";

    /**
     * The data structure holding all the info about every encounter.<BR>
     * It is a {@link HashMap} that link every day number to another HashMap (used as a dictionary).<BR>
     * The second HashMap links every {@link Person} to a List of the Persons they met.<BR>
     * Example conceptual usage: encounters[12][mario] get all the encounters that mario did at the day 12.
     */
    protected HashMap<Integer, HashMap<Person, List<Person>>> encounters;

    /**
     * ParameterData is an {@link Annotation} used to specify the data of a numeric parameter of a Strategy.<BR>
     * It is used by the {@link com.epidemic_simulator.gui.SimulatorSettings} for building the GUI dynamically.
     */
    //#region Annotations
    @Documented @Target(ElementType.PARAMETER) @Retention(RUNTIME)
    public @interface ParameterData {
        /**
         * The default value of this parameter.
         *
         * @return the value
         */
        float value();

        /**
         * The minimum value of this parameter.
         *
         * @return the min
         */
        float min() default Integer.MIN_VALUE;

        /**
         * The maximum value of this parameter.
         *
         * @return the max
         */
        float max() default Integer.MAX_VALUE;


        /**
         * The step of the value for this parameter.<BR>
         * <b>NOTE:</b> If it's set to 1 this parameter will act as an integer.
         *
         * @return the max
         */
        float step() default 1;
    }
    //#endregion


    /**
     * Instantiates a new Strategy.<br>
     * Link this Strategy to the {@link Simulator} passed as a parameter,
     * set the {@link #originalResources}
     * and initialize the {@link #encounters} dictionary.
     *
     * @param simulator The Simulator that should be linked to this strategy
     */
    public Strategy(Simulator simulator) {
        this.simulator = simulator;
        simulator.setStrategy(this);
        originalResources = simulator.getResources();

        encounters = new HashMap<>();
    }

    /**
     * Unlink this strategy from any data it has.<BR>
     * On certain JVM versions it can allow for faster Garbage collection.<BR>
     * It allows for lower RAM usages on multiple simulations, at the expenses of performances.
     */
    public void dispose() {
        encounters.clear();
        encounters = null;
        simulator = null;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
    }

    @Override
    public void personClean(Person person) {
    }

    @Override
    public void personHasSymptoms(Person person) {
    }

    /**
     * Output a log string to temporary output that TODO: complete
     *
     * @param text the text
     */
    public void output(String text) {
        System.out.println(text);
        if (log.length() == 0)
            this.log = text;
        else {
            this.log += "\n" + text;
        }
    }

    /**
     * Clear output string.TODO: complete
     *
     * @return the string
     */
    public String clearOutput(){
        String temp = this.log;
        this.log = "";
        return temp;
    }

    /**
     * Register that the person1 and the person2 met today
     * <b></b>NOTE: This should ONLY be used by the Simulator!!!
     *
     * @param person1 The first person
     * @param person2 The second person
     */
    @Override
    public final void registerEncounter(Person person1, Person person2) {
        int day = simulator.getDay();

        //get this day encounter dictionary
        HashMap<Person, List<Person>> encounterDictionary = findEncounters(day);

        //adding person2 to the list of the encounters of person1
        findEncounters(encounterDictionary, person1).add(person2);

        //adding person1 to the list of the encounters of person2
        findEncounters(encounterDictionary, person2).add(person1);
    }


    /**
     * TODO: complete
     * Find the list of people that a person has met in the last days
     *
     * @param person       The person that should be used for the research
     * @param previousDays The number of days that should be looked up (NOTE: if days = 1 you will only search today)
     * @return The list of the people that that person has met over the last days
     */
    protected final List<Person> findEncounters(Person person, int previousDays) {
        ArrayList<Person> outputList = new ArrayList<>();

        int currentDay = simulator.getDay();
        int limitDay = currentDay - previousDays;
        for (int day = currentDay; day > limitDay; day--) {
            //find the encounter dictionary for the day, then find the list of the Persons for this person, then add them all to the list
            findEncounters(findEncounters(day), person).stream().collect(Collectors.toCollection(() -> outputList));
        }

        return outputList;
    }


    //#region private overload of findEncounters for internal use

    /**
     * TODO: complete
     * Find the encounters of a person from the encounter dictionary of a day
     * NOTE: It's equal to encounterDictionary.get(person), but it performs some additional error checking
     *
     * @param encounterDictionary The encounter dictionary in which the person should be searched
     * @param person              The person to search
     * @return The list of the people that this person has met
     */
    private List<Person> findEncounters(HashMap<Person, List<Person>> encounterDictionary, Person person) {
        if (!encounterDictionary.containsKey(person)) {
            ArrayList<Person> temp = new ArrayList<>();
            encounterDictionary.put(person, temp);
            return temp;
        }
        return encounterDictionary.get(person);
    }

    /**
     * TODO: complete
     * Find the dictionary of the encounters for a single day
     * NOTE: this is the same as doing encounters.get(dayNum), but it performs some additional error checking
     *
     * @param dayNum the number of the day that should be looked up
     * @return The dictionary of the encounters for that day
     */
    protected final HashMap<Person, List<Person>> findEncounters(int dayNum) {
        //if this day doesn't exists in the encounters dictionary i add it
        if (!encounters.containsKey(dayNum)) {
            HashMap<Person, List<Person>> temp = new HashMap<>();
            encounters.put(dayNum, temp);
            return temp;
        }
        return encounters.get(dayNum);
    }

    //#endregion

}
