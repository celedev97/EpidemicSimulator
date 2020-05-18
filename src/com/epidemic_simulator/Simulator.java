package com.epidemic_simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Simulator {

    //#region Fields/Getters

    //callbacks
    public ArrayList<SimulatorCallBack> callBacks;

    //#region State data
    //R
    private long resources;
    public long getResources() {
        return resources;
    }

    //C
    public final int testPrice;//Costi per le cure
    public final int cureCost;

    //V
    public final double averageEncountersPerDay;
    //#endregion

    //#region Disease data
    //I
    public final int infectionRate;
    private final double doubleInfectionRate;//this is only used for speeding up r0 calculation

    //S
    public final int symptomsRate;//Percentuale di sintomaticità

    //L
    public final int deathRate;//Percentuale di letalità

    //D
    public final int diseaseDuration;
    public final int canInfectDay;//Days for a infected green to turn yellow
    public final int developSymptomsMaxDay;//The last day on wich a yellow could turn red
    //#endregion

    //#region Simulation running status

    //DAY
    private int day = 0;
    public int getDay() {
        return day;
    }

    //#region Population Arrays
    private final ArrayList<Person> population;
    private final ArrayList<Person> alivePopulation;

    private final List<Person> readOnlyPopulation;
    private final List<Person> readOnlyAlivePopulation;

    private final ArrayList<Person> notQuarantinedPersons;

    public List<Person> getPopulation() {
        return readOnlyPopulation;
    }

    public List<Person> getAlivePopulation() {
        return readOnlyAlivePopulation;
    }
    //#endregion

    public double r0;

    //flag used to activate the Strategy callbacks
    private boolean firstRed = false;

    //#region status counters
    private int healthy;
    private int infected;
    private int immunes;
    private int deads;


    public int getHealthy() {
        negateStrategyAccess();
        return healthy;
    }

    public int getInfected() {
        negateStrategyAccess();
        return infected;
    }

    public int getImmunes() {
        negateStrategyAccess();
        return immunes;
    }

    public int getDeads() {
        return deads;
    }

    void negateStrategyAccess(){
        try {
            Class callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if(Strategy.class.isAssignableFrom(callerClass)){
                throw new RuntimeException("Strategies cannot see this.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //#endregion

    //#endregion
    //#endregion

    /**
     * Enum for the possible outcomes of a day execution.
     */
    public enum Outcome {
        NOTHING,
        ALL_HEALED,
        ALL_DEAD,
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

        //checking that rateos are all between 0 and 100
        if (infectionRate > 100 || infectionRate < 0)
            throw new InvalidSimulationException("Infectivity should be between 0 and 100");
        if (symptomsRate > 100 || symptomsRate < 0)
            throw new InvalidSimulationException("Symptomaticity should be between 0 and 100");
        if (deathRate > 100 || deathRate < 0)
            throw new InvalidSimulationException("Lethality should be between 0 and 100");
        //#endregion

        //#region Initializing simulation parameter
        //Dati popolazione/stato
        this.resources = resources;
        this.testPrice = testPrice;
        this.cureCost = testPrice * 3;
        this.averageEncountersPerDay = averageEncountersPerDay;

        //Dati sanitari
        this.infectionRate = infectionRate;
        this.doubleInfectionRate = (infectionRate / 100.0);
        this.symptomsRate = symptomsRate;
        this.deathRate = deathRate;

        this.r0 = averageEncountersPerDay * diseaseDuration * doubleInfectionRate;

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
        healthy  = startingPopulation - 1;
        infected = 1;
        immunes  = 0;
        deads    = 0;
    }

    /**
     * Execute a day in the simulator.
     * This method will:
     * -make the people that have to move move and meet other people
     * -adjust resources
     * -make the disease status proceed for every one that has the disease
     *
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

        if(canMoveCount > 1){
            //TODO: could this be made parallel?
            //Probably not, this would require synchronization on persons, and could cause a deadlock
            for (int i = 0; i < encounterToDoThisDay; i++) {
                Person p1 = notQuarantinedPersons.get(Utils.random(notQuarantinedPersons.size()));
                Person p2 = null;

                while (p2 == null || p2 == p1) {
                    p2 = notQuarantinedPersons.get(Utils.random(notQuarantinedPersons.size()));//Estraiamo un'altra persona...che non sia se stessa
                }

                encounter(p1,p2);
            }
        }
        //#endregion

        //depleting resources
        resources -= (alivePopulation.size() - canMoveCount);


        //#region disease calculations
        //parallel loop over all the alive and infected persons
        population.parallelStream().filter(person -> person.alive && person.infected).parallel().forEach(person -> {
            //increasing the number of days passed since the day of the infection
            person.daysSinceInfection++;

            //if this person is green and today is the day they become yellow
            if (!person.canInfect && person.daysSinceInfection == canInfectDay)
                person.canInfect = true;

            //if this person is yellow and today is the day they become red
            if (person.daysSinceInfection == person.symptomsDevelopmentDay) {
                person.symptoms = true;
                person.canMove = false;

                //this flag enables the strategies
                firstRed = true;

                //Execute all the simulation operation on the Person
                callBacks.forEach(simulatorCallBack -> {
                    synchronized (simulatorCallBack){
                        simulatorCallBack.personHasSymptoms(person);
                    }
                });
            }

            //if the person has symptoms i need to cure them
            if (person.symptoms)
                resources -= cureCost;

            //if this is the day this person die
            if (person.daysSinceInfection == person.deathDay) {
                synchronized (alivePopulation){
                    alivePopulation.remove(person);
                }
                person.alive = false;
                deads++;
                infected--;

                return;//this return is just to be safe and avoid a people dying and then healing itself
            }

            //if this is the day this person heals
            if (person.daysSinceInfection == diseaseDuration) {
                boolean hadSymptoms = person.symptoms;

                person.immune = true;
                person.infected = false;
                person.canInfect = false;
                person.symptoms = false;

                //the strategy get told of the fact that he's immune only if it knew he was sick.
                if (hadSymptoms) {
                    person.canMove=true;
                    callBacks.forEach(simulatorCallBack -> {
                        if(!Strategy.class.isAssignableFrom(simulatorCallBack.getClass()) || firstRed){
                            synchronized (simulatorCallBack){
                                simulatorCallBack.personClean(person);
                            }
                        }
                    });
                }

                immunes++;
                infected--;

            }
        });


        //#endregion

        //#region simulation status return
        Outcome outcome = Outcome.NOTHING;

        int infectedCount;//TODO: you could increase a counter instead of counting them at every cycle???
        synchronized (alivePopulation){
            infectedCount = (int) alivePopulation.parallelStream().filter(person -> person.infected).count();
        }

        if (alivePopulation.isEmpty()) {
            outcome = Outcome.ALL_DEAD;
        } else if (infectedCount == 0) {
            outcome = Outcome.ALL_HEALED;
        } else if (resources <= 0) {
            outcome = Outcome.ECONOMIC_COLLAPSE;
        }

        //#endregion

        //calling the callbacks for the end of the day
        Outcome finalOutcome = outcome;
        callBacks.forEach(simulatorCallBack -> {
            if(!Strategy.class.isAssignableFrom(simulatorCallBack.getClass()) || firstRed){
                synchronized (simulatorCallBack){
                    simulatorCallBack.afterExecuteDay(finalOutcome);
                }
            }
        });
        return outcome;
    }

    /**
     * This method makes two people meet, and try to infect one in case the other one is at a disease state for which he can infect other people.
     *
     * @param person1 the first person for this encounter
     * @param person2 the second person for this encounter
     */
    private void encounter(Person person1, Person person2) {
        if (person1.canInfect && person2.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration)){
            infected++;
            healthy--;
        }
        if (person2.canInfect && person1.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration)){
            infected++;
            healthy--;
        }
        callBacks.forEach(simulatorCallBack -> {
            if(!Strategy.class.isAssignableFrom(simulatorCallBack.getClass()) || firstRed){
                synchronized (simulatorCallBack){
                    simulatorCallBack.registerEncounter(person1, person2);
                }
            }
        });
    }

    /**
     * This method test the virus inside a Person, the virus will only be detected if the person it's already at a stage at which he can infect other people.
     *
     * @param person the person to test
     * @return true if the virus is found, false if it's not found
     */
    public boolean testVirus(Person person) {
        if(person.symptoms) return true;

        resources -= testPrice;
        return person.canInfect;
    }


    /**
     * TODO: JAVADOC
     */
    public synchronized void dispose() {
        //#region strategies
        //unlinking each strategy from this simulator
        Stream<SimulatorCallBack> strategies = callBacks.stream().filter(callBack -> Strategy.class.isAssignableFrom(callBack.getClass()));
        strategies.forEach(strategy -> {
            ((Strategy) strategy).simulator = null;
            ((Strategy) strategy).dispose();
        });

        //clearing my references to the callbacks
        callBacks.clear();
        //now the gc should be able to kick in and clear this Simulator and its callbacks
        //#endregion

        notQuarantinedPersons.clear();
    }

    //TODO: THE SIMULATOR SHOULD BE SYNCHRONIZED, THE ENGINE AND THE GUI ARE ON SEPARATE THREADS!!!
    //ARE YOU REALLY SURE ABOUT THIS? IF THE GETTERS AREN'T SYNCHRONIZED IT SHOULD BE FINE.

}