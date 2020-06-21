package com.epidemic_simulator;

import com.epidemic_simulator.exceptions.InvalidSimulationException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Epidemic Simulator core class, it does all the Simulation-related stuff
 * by handling the prosecution of days, the encounters and the disease status of every {@link Person}.
 */
public final class Simulator {

    //#region Fields/Getters

    //#region callbacks
    private Strategy strategy = null;
    private final ArrayList<SimulatorCallBack> callBacks;

    /**
     * Add a callback to the list of callbacks that will have their methods called in the right situations.
     *
     * @throws RuntimeException if you try to add a {@link Strategy} as a callback to a Simulator that already has one.
     * @param callBack the call back to add
     */
    public void addCallBack(SimulatorCallBack callBack) {
        if(Strategy.class.isAssignableFrom(callBack.getClass())){
            if (strategy != null) throw new RuntimeException("You can't add multiple strategies to the same simulator");
            setStrategy((Strategy)callBack);
        }else {
            callBacks.add(callBack);
        }
    }

    /**
     * Remove a callback to the list of callbacks that will have their methods called in the right situations.
     *
     * @param callBack the call back to remove
     */
    public void removeCallBack(SimulatorCallBack callBack) {
        if (callBack == strategy) {
            strategy = null;
        }
        callBacks.remove(callBack);
    }

    /**
     * Get the {@link Strategy} currently linked to this Simulator.
     *
     * @return the strategy
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Set the {@link Strategy} currently linked to this Simulator.
     *
     * @param strategy the strategy to link
     */
    public void setStrategy(Strategy strategy) {
        callBacks.remove(this.strategy);
        this.strategy = strategy;
        if (strategy != null)
            callBacks.add(strategy);
    }
    //#endregion

    //#region State data
    /**
     * The starting population (P).
     */
    public final int startingPopulation;

    private long resources;
    /**
     * The initial resources (R).
     */
    public final long initialResources;

    /**
     * Gets the current resources that this Simulator has.
     *
     * @return the resources
     */
    public long getResources() {
        return resources;
    }


    /**
     * The test price (C).
     */
    public final int testPrice;
    /**
     * The cost for curing a person for a day (3 * C)
     */
    public final int cureCost;

    /**
     * The average encounters per {@link Person} per day when everyone can move (V).
     */
    public final double averageEncountersPerDay;
    //#endregion


    //#region Disease data
    /**
     * The infection rate (I).
     */
    public final int infectionRate;
    private final double doubleInfectionRate;//this is only used for speeding up r0 calculation

    /**
     * The Symptoms rate (S).
     */
    public final int symptomsRate;//Percentuale di sintomaticità

    /**
     * The Death rate (L).
     */
    public final int deathRate;//Percentuale di letalità

    /**
     * The Disease duration (D).
     */
    public final int diseaseDuration;
    /**
     * Days necessary for an infected green to turn yellow
     */
    public final int canInfectDay;
    /**
     * The last day since the start of the disease in which a yellow could turn red.
     */
    public final int developSymptomsMaxDay;
    //#endregion

    //#region Simulation running status
    private int day = 0;

    /**
     * Gets the number of days since the beginning of the simulation.
     * It can be useful for contact-tracing or for day-based activations of {@link Strategy}
     *
     * @return the day
     */
    public int getDay() {
        return day;
    }

    //#region Population Arrays
    private final ArrayList<Person> population;
    private final ArrayList<Person> alivePopulation;

    private final List<Person> readOnlyPopulation;
    private final List<Person> readOnlyAlivePopulation;

    private final ArrayList<Person> notQuarantinedPersons;

    /**
     * Return a readOnly instance of the population list.<BR>
     * Trying to edit the list in illegal ways will result in a RuntimeException<BR>
     * (ex: deleting a red person so you don't need to heal them)
     *
     * @return the population
     */
    public List<Person> getPopulation() {
        return readOnlyPopulation;
    }

    /**
     * Return a readOnly instance of the alive population list.<BR>
     * Trying to edit the list in illegal ways will result in a RuntimeException<BR>
     * (ex: deleting a red person so you don't need to heal them)
     * @return the alive population
     */
    public List<Person> getAlivePopulation() {
        return readOnlyAlivePopulation;
    }
    //#endregion

    private double r0;

    /**
     * Gets the R0 factor.<BR>
     * Mostly useless but it can be used for output logs.
     *
     * @return the r0
     */
    public double getR0() {
        return r0;
    }

    //flag used to activate the Strategy callbacks
    private boolean firstRed = false;

    //#region status counters
    //healthy = not infected
    private int healthy;
    //infected = not healthy
    private int infected;

    private int greenCount;
    private int yellowCount;
    private int redCount;
    private int blueCount;
    private int blackCount;


    /**
     * Gets the number of healthy people.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of healthy people
     */
    public int getHealthy() {
        Utils.negateStrategyAccess();
        return healthy;
    }

    /**
     * Gets the number of infected people.<BR>
     * This includes green that have been infected.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of infected people
     */
    public int getInfected() {
        Utils.negateStrategyAccess();
        return infected;
    }

    /**
     * Gets the number of green people.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of green people
     */
    public int getGreenCount() {
        Utils.negateStrategyAccess();
        return greenCount;
    }

    /**
     * Gets the number of yellow people.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of yellow people
     */
    public int getYellowCount() {
        Utils.negateStrategyAccess();
        return yellowCount;
    }

    /**
     * Gets the number of red people.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of red people
     */
    public int getRedCount() {
        Utils.negateStrategyAccess();
        return redCount;
    }

    /**
     * Gets the number of blue people.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of blue people
     */
    public int getBlueCount() {
        Utils.negateStrategyAccess();
        return blueCount;
    }

    /**
     * Gets the number of black people.<BR>
     * It is illegal for strategies to access this.
     *
     * @return the number of black people
     */
    public int getBlackCount() {
        return blackCount;
    }

    //#endregion

    //#endregion
    //#endregion

    /**
     * Enum for the possible outcomes of a day execution.
     */
    public enum Outcome {
        /**
         * This is the outcome for when nothing relevant happens.
         */
        NOTHING,
        /**
         * This is the outcome for when there are no more infected.
         */
        ALL_HEALED,
        /**
         * The outcome for when the whole population dies.
         */
        ALL_DEAD,
        /**
         * The outcome for when resources become 0 (or lower).
         */
        ECONOMIC_COLLAPSE
    }

    /**
     * Create a simulator according to the parameters given.
     *
     * @param startingPopulation      (P) The population of the simulation at the start.
     * @param resources               (R) The economic resources available for this simulation.
     * @param testPrice               (C) The cost of a test for this disease.
     * @param averageEncountersPerDay (V) The number of encounters that a person does in a day if everyone is allowed to move.
     * @param infectionRate           (I) The percentage of possibility that an infected person can infect another one if they meet.
     * @param symptomsRate            (S) The percentage of possibility that an infected person can develop symptoms.
     * @param deathRate               (L) The percentage of possibility that a symptomatic person can die.
     * @param diseaseDuration         (D) The number of days that the disease takes to heal;
     * @throws InvalidSimulationException If the parameters are not in line with the rules of the simulation this exception will be thrown, please refer to the exception message for more details.
     */
    public Simulator(int startingPopulation, long resources, int testPrice, double averageEncountersPerDay, int infectionRate, int symptomsRate, int deathRate, int diseaseDuration) throws InvalidSimulationException {
        //#region Parameters validity check
        //Condizioni necessarie per verificare la validità dei dati inseriti in funzione del requisito.
        if (resources >= (10 * (long) startingPopulation * testPrice))
            throw new InvalidSimulationException("Condition not met: R < 10 * P ∗ C");
        if (resources >= ((long) startingPopulation * diseaseDuration))
            throw new InvalidSimulationException("Condition not met: R < P ∗ D");
        //#endregion

        //#region Initializing simulation parameter

        //#regionDati popolazione/stato
        this.startingPopulation = startingPopulation;
        this.resources = this.initialResources = resources;
        this.averageEncountersPerDay = averageEncountersPerDay;
        this.testPrice = testPrice;

        //Dati derivati
        this.cureCost = testPrice * 3;
        //#endregion

        //#region Dati sanitari
        this.infectionRate = infectionRate;
        this.symptomsRate = symptomsRate;
        this.deathRate = deathRate;

        //Dati derivati
        this.doubleInfectionRate = (infectionRate / 100.0);

        this.r0 = averageEncountersPerDay * diseaseDuration * doubleInfectionRate;
        //#endregion

        //Dati evoluzione della malattia
        this.diseaseDuration = diseaseDuration;
        this.canInfectDay = diseaseDuration / 6;
        this.developSymptomsMaxDay = diseaseDuration / 3;

        //#endregion

        //#region Preparing population lists
        population = new ArrayList<>();//Lista persona

        //Aggiunta di persona alla lista
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }

        //Creazione della prima persona infetta che va in giro,il suo canMove=true resta invariato->Un infetto per il momento giallo
        Person fistInfected = population.get(Utils.random(startingPopulation));
        fistInfected.infect(symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, this.diseaseDuration);
        fistInfected.canInfect = true;

        //cloning the population to get alive population
        alivePopulation = (ArrayList<Person>) population.clone();

        //creating a preallocated empty list for not quarantined persons
        notQuarantinedPersons = new ArrayList<>(alivePopulation.size());

        //creating read only view for population and alive population (for strategies)
        readOnlyPopulation = Collections.unmodifiableList(population);
        readOnlyAlivePopulation = Collections.unmodifiableList(alivePopulation);

        //#endregion

        //Initializing callback lists
        callBacks = new ArrayList<>();

        //initializing counters
        greenCount = healthy = startingPopulation - 1;
        yellowCount = infected = 1;
        redCount = 0;
        blueCount = 0;
        blackCount = 0;
    }

    /**
     * Execute a day in the simulator.<BR>
     * This method will:<BR>
     * <ul>
     *     <li>Make the people that can move, meet other people</li>
     *     <li>Make the disease status proceed for every one that has the disease</li>
     *     <li>Adjust resources according to who moved and who got cures</li>
     *
     * @see Outcome
     * @return the outcome of this day execution.
     */
    public synchronized Outcome executeDay() {
        day++;

        //#region Encounters
        //updating the list of not quarantined persons for encounters
        notQuarantinedPersons.clear();
        alivePopulation.stream().filter(p -> p.canMove).collect(Collectors.toCollection(() -> notQuarantinedPersons));

        //Vd calculation
        int canMoveCount = notQuarantinedPersons.size();
        double encountersPerPersonThisDay = averageEncountersPerDay * canMoveCount / population.size();

        //R0 calculation
        r0 = encountersPerPersonThisDay * diseaseDuration * doubleInfectionRate;

        int encounterToDoThisDay = (int) (encountersPerPersonThisDay * canMoveCount);

        if (canMoveCount > 1) {
            for (int i = 0; i < encounterToDoThisDay; i++) {
                Person p1 = notQuarantinedPersons.get(Utils.random(notQuarantinedPersons.size()));
                Person p2 = null;

                while (p2 == null || p2 == p1) {
                    p2 = notQuarantinedPersons.get(Utils.random(notQuarantinedPersons.size()));//Estraiamo un'altra persona...che non sia se stessa
                }

                encounter(p1, p2);
            }
        }
        //#endregion

        //depleting resources
        resources -= (alivePopulation.size() - canMoveCount);

        //#region disease calculations
        //loop over all the alive and infected persons
        population.stream().filter(person -> person.alive && person.infected).forEach(person -> {
            //increasing the number of days passed since the day of the infection
            person.daysSinceInfection++;

            //if this person is green and today is the day they become yellow
            if (!person.canInfect && person.daysSinceInfection == canInfectDay) {
                yellowCount++;
                greenCount--;
                person.canInfect = true;
            }

            //if this person is yellow and today is the day they become red
            if (person.daysSinceInfection == person.symptomsDevelopmentDay) {
                person.symptoms = true;
                person.canMove = false;

                //adjusting counters
                yellowCount--;
                redCount++;

                //this flag enables the strategies
                firstRed = true;

                //Execute all the simulation operation on the Person
                callBacks.forEach(simulatorCallBack -> {
                    synchronized (simulatorCallBack) {
                        simulatorCallBack.personHasSymptoms(person);
                    }
                });
            }

            //if the person has symptoms i need to cure them
            if (person.symptoms)
                resources -= cureCost;

            //if this is the day this person die
            if (person.daysSinceInfection == person.deathDay) {
                synchronized (alivePopulation) {
                    alivePopulation.remove(person);
                }
                person.alive = false;

                //adjusting counters
                blackCount++;
                redCount--;
                infected--;

                return;//this return is just to be safe and avoid a people dying and then healing itself
            }

            //if this is the day this person heals
            if (person.daysSinceInfection == diseaseDuration) {
                boolean hadSymptoms = person.symptoms;

                //curing person
                person.immune = true;
                person.infected = false;
                person.canInfect = false;
                person.symptoms = false;
                person.canMove = true;
                //adjusting counter
                blueCount++;
                infected--;

                //the strategy get told of the fact that he's immune only if it knew he was sick.
                if (!hadSymptoms) {
                    yellowCount--;
                } else {
                    redCount--;
                    person.canMove = true;
                    callBacks.forEach(simulatorCallBack -> {
                        if (simulatorCallBack != strategy || firstRed) {
                            synchronized (simulatorCallBack) {
                                simulatorCallBack.personClean(person);
                            }
                        }
                    });
                }
            }

        });

        //#endregion

        //#region simulation status return
        Outcome outcome = Outcome.NOTHING;

        if (alivePopulation.isEmpty()) {
            outcome = Outcome.ALL_DEAD;
        } else if (infected == 0) {
            outcome = Outcome.ALL_HEALED;
        } else if (resources <= 0) {
            outcome = Outcome.ECONOMIC_COLLAPSE;
        }

        //#endregion

        //calling the callbacks for the end of the day
        Outcome finalOutcome = outcome;
        callBacks.forEach(simulatorCallBack -> {
            if (simulatorCallBack != strategy || firstRed) {
                simulatorCallBack.afterExecuteDay(finalOutcome);
            }
        });

        return outcome;
    }

    /**
     * This method makes two people meet,
     * and try to infect one in case the other one is at a disease state for which he can infect other people.
     *
     * @param person1 the first person for this encounter
     * @param person2 the second person for this encounter
     */
    private void encounter(Person person1, Person person2) {
        if ((person1.canInfect && person2.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration))
                || (person2.canInfect && person1.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration))) {
            infected++;
            healthy--;
        }
        callBacks.forEach(simulatorCallBack -> {
            if (simulatorCallBack != strategy || firstRed) {
                simulatorCallBack.registerEncounter(person1, person2);
            }
        });
    }

    /**
     * This method test the virus inside a {@link Person}.<BR>
     * The virus will only be detected if the Person it's already at a stage at which he can infect other people.<BR>
     * <BR>
     * <b>NOTE:</b> This method usually consume resources,
     * but if the person to test already has {@link Person#symptoms} it will not waste resources.
     *
     * @param person the person to test
     * @return true if the virus is found, false if it's not found
     */
    public synchronized boolean testVirus(Person person) {
        if (person.symptoms) return true;

        resources -= testPrice;
        return person.canInfect;
    }


    /**
     * Unlink this Simulator from any data it has.<BR>
     * On certain JVM versions it can allow for faster Garbage collection.<BR>
     * It allows for lower RAM usages on multiple simulations, at the expenses of performances.
     */
    public synchronized void dispose() {
        //#region strategies
        //unlinking each strategy from this simulator
        if (strategy != null) {
            strategy.dispose();
        }

        //clearing my references to the callbacks
        callBacks.clear();
        //now the gc should be able to kick in and clear this Simulator and its callbacks
        //#endregion
    }
}