package com.epidemic_simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Strategy implements SimulatorCallBack {
    protected Simulator simulator;
    private ArrayList<Person> population;
    protected long originalResources;

    protected HashMap<Integer, HashMap<Person, List<Person>>> encounters;

    public Strategy(Simulator simulator) {
        this.simulator = simulator;
        simulator.callBacks.add(this);
        originalResources = simulator.getResources();

        encounters = new HashMap<>();
    }

    public void dispose() {
        encounters.clear();
        encounters = null;
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
     * Register that the person1 and the person2 met today
     * NOTE: This should ONLY be used by the Simulator!!!
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
     * Find the dictionary of the encounters for a single day
     * NOTE: this is the same as doing encounters.get(dayNum), but it performs some additional error checking
     *
     * @param dayNum the number of the day that should be looked up
     * @return
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
