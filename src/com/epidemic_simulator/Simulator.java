package com.epidemic_simulator;

import com.epidemic_simulator.exceptions.InvalidSimulationException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Simulator {

    //#region Fields/Getters

    //#region callbacks
    private Strategy strategy = null;
    private final ArrayList<SimulatorCallBack> callBacks;

    public void addCallBack(SimulatorCallBack callBack) {
        if (strategy != null && Strategy.class.isAssignableFrom(callBack.getClass()))
            throw new RuntimeException("You can't add multiple strategies to the same simulator");
        callBacks.add(callBack);
    }

    public void removeCallBack(SimulatorCallBack callBack) {
        if (callBack == strategy) {
            strategy = null;
        }
        callBacks.remove(callBack);
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        callBacks.remove(this.strategy);
        this.strategy = strategy;
        callBacks.add(strategy);
    }
    //#endregion

    //#region State data
    //P
    public final int startingPopulation;

    //R
    private long resources;
    public final long initialResources;

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

    //R0
    private double r0;

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


    public int getHealthy() {
        Utils.negateStrategyAccess();
        return healthy;
    }

    public int getInfected() {
        Utils.negateStrategyAccess();
        return infected;
    }

    public int getGreenCount() {
        Utils.negateStrategyAccess();
        return greenCount;
    }

    public int getYellowCount() {
        Utils.negateStrategyAccess();
        return yellowCount;
    }

    public int getRedCount() {
        Utils.negateStrategyAccess();
        return redCount;
    }

    public int getBlueCount() {
        Utils.negateStrategyAccess();
        return blueCount;
    }

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
        //parallel loop over all the alive and infected persons
        population.parallelStream().filter(person -> person.alive && person.infected).forEach(person -> {
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

        if(yellowCount <0 || redCount <0 || (greenCount+yellowCount+redCount+blueCount+blackCount) < startingPopulation){
            int realGreens = (int)population.stream().filter(person -> person.getColor() == Color.GREEN).count();
            int realYellows = (int)population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
            int realReds = (int)population.stream().filter(person -> person.getColor() == Color.RED).count();
            int realBlues = (int)population.stream().filter(person -> person.getColor() == Color.BLUE).count();
            int realBlacks = (int)population.stream().filter(person -> person.getColor() == Color.BLACK).count();

            if((greenCount+yellowCount+redCount+blueCount+blackCount)< startingPopulation){
                throw new RuntimeException("what the actual fucking fuck?");
            }
            throw new RuntimeException("diobestia");
        }

        return outcome;
    }

    /**
     * This method makes two people meet, and try to infect one in case the other one is at a disease state for which he can infect other people.
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
     * This method test the virus inside a Person, the virus will only be detected if the person it's already at a stage at which he can infect other people.
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
     * TODO: JAVADOC
     */
    public synchronized void dispose() {
        //#region strategies
        //unlinking each strategy from this simulator
        if(strategy != null){
            strategy.dispose();
        }

        //clearing my references to the callbacks
        callBacks.clear();
        //now the gc should be able to kick in and clear this Simulator and its callbacks
        //#endregion
    }
}