package com.epidemic_simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Simulator {

    //#region Fields/Getters
    public ArrayList<SimulatorCallBack> callBacks;

    //#region Simulation parameters
    private final int startingPopulation; //TODO: serve effettivamente a qualcosa?
    private long resources;//Numero risorse disponibili

    public long getResources() {
        return resources;
    }

    public final int testPrice;//Costi per le cure
    private final int cureCost;
    private final double averageEncountersPerDay;//Velocità di incontro media per individuo/numero medio di individui che giornalmente una persona incontra
    //#endregion

    //#region Disease data
    public final int infectionRate;//Percentuale di infettività
    private final double doubleInfectionRate;
    public final int symptomsRate;//Percentuale di sintomaticità
    public final int deathRate;//Percentuale di letalità

    public final int diseaseDuration;
    public final int canInfectDay;//Numero giorni di incubazione
    public final int developSymptomsMaxDay;
    //#endregion

    //#region Simulation running status
    private int day = 0;

    public int getDay() {
        return day;
    }

    final private ArrayList<Person> population;
    final private List<Person> alivePopulation;

    public List<Person> getPopulation() {
        return population;
    }

    public List<Person> getAlivePopulation() {
        return alivePopulation;
    }

    public double r0;

    private boolean firstRed = false;

    public void dispose() {
        //unlinking each strategy from this simulator
        callBacks.stream()
                .filter(callBack -> Strategy.class.isAssignableFrom(callBack.getClass()))
                .forEach(callBack -> ((Strategy) callBack).simulator = null);
        //clearing my references to the callbacks
        callBacks.clear();
        //setting the array to null (just to be safe)
        callBacks = null;
        //now the gc should be able to kick in and clear this Simulator and its callbacks
    }

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
        this.startingPopulation = startingPopulation;
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
        Person infected = population.get(Utils.random(startingPopulation));
        infected.infect(symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, this.diseaseDuration);
        infected.canInfect = true;

        //cloning the
        alivePopulation = (ArrayList<Person>) population.clone();
        //#endregion

        //Initializing callback lists
        callBacks = new ArrayList<>();

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
    public Outcome executeDay() {
        //TODO: find a decent place for this
        List<Person> notQuarantinedPersons = alivePopulation.stream().filter(p -> p.canMove).collect(Collectors.toList());

        day++;

        //Vd calculation
        int canMoveCount = notQuarantinedPersons.size();
        double encountersThisDay = averageEncountersPerDay * canMoveCount / population.size();
        int intEncountersThisDay = encountersThisDay == (int) encountersThisDay ? (int) encountersThisDay : (int) encountersThisDay + 1;

        //R0 calculation TODO: is this really necessary?
        r0 = encountersThisDay * diseaseDuration * doubleInfectionRate;
        if (r0 < 1)
            System.out.println("Disease has been eradicated");

        int encounterToDo = (int) (encountersThisDay * canMoveCount);

        if(canMoveCount > 1){
            for (int i = 0; i < encounterToDo; i++) {
                Person p1 = notQuarantinedPersons.get(Utils.random(notQuarantinedPersons.size()));
                Person p2 = null;

                while (p2 == null || p2 == p1) {
                    p2 = notQuarantinedPersons.get(Utils.random(notQuarantinedPersons.size()));//Estraiamo un'altra persona...che non sia se stessa
                }

                encounter(p1,p2);
            }
        }

        resources -= (alivePopulation.size() - canMoveCount);

        //Per ogni giorno prendiamo tutte le 'n' persone VIVE
        for (Person person : population) {
            if (!person.alive) continue;//Se è un morto passiamo avanti alla prossima...

            //#region prosecuzione malattia
            if (!person.infected) continue;//Controlliamo se la persona è stata effettivamente infettata

            person.daysSinceInfection++;//Altrimenti comincio a contare i giorni entro cui,anche se infetta,l'individuo non può infettare

            if (!person.canInfect && person.daysSinceInfection == canInfectDay) {
                //è verde ma è infetto, e sono i passati i giorni dell'incubazione, quindi diventa giallo
                person.canInfect = true;
            }

            if (person.daysSinceInfection == person.symptomsDevelopmentDay) {
                //è giallo ed oggi è il giorno in cui sviluppa sintomi
                person.symptoms = true;
                firstRed = true; //flag per il primo sintomatico per iniziare a tracciare gli incontri
                callBacks.forEach(simulatorCallBack -> simulatorCallBack.personHasSymptoms(person));
            }

            if (person.symptoms)
                resources -= cureCost;

            if (person.daysSinceInfection == person.deathDay) {
                //è rosso può morire
                alivePopulation.remove(person);//Rimuovo l'individuo dalla lista delle persone vive
                person.alive = false;
                continue;
            }

            if (person.daysSinceInfection == diseaseDuration) {
                boolean hadSymtomps = person.symptoms;

                person.immune = true;
                person.infected = false;
                person.canInfect = false;
                person.symptoms = false;
                //person.canMove=true; DISABLED, THE STRATEGY SHOULD MANAGE THIS!!!

                if (hadSymtomps)
                    callBacks.forEach(simulatorCallBack -> simulatorCallBack.personClean(person));
            }
            //#endregion
        }


        //#region simulation status return
        Outcome outcome = Outcome.NOTHING;
        if (alivePopulation.isEmpty()) {
            outcome = Outcome.ALL_DEAD;
        } else if (alivePopulation.stream().filter(person -> person.infected).count() == 0) {
            outcome = Outcome.ALL_HEALED;
        } else if (resources <= 0) {
            outcome = Outcome.ECONOMIC_COLLAPSE;
        }
        //#endregion

        Outcome finalOutcome = outcome;
        callBacks.forEach(simulatorCallBack -> simulatorCallBack.afterExecuteDay(finalOutcome));
        return outcome;
    }

    /**
     * This method makes two people meet, and try to infect one in case the other one is at a disease state for which he can infect other people.
     *
     * @param person1 the first person for this encounter
     * @param person2 the second person for this encounter
     */
    private void encounter(Person person1, Person person2) {
        if (person1.canInfect && !person2.infected) {//Se persona1 è un giallo/infetto e persona2 è un verde/sano,simuliamo come cambierà il fato col metodo tryInfect di persona2
            person2.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration);
        }
        if (person2.canInfect && !person1.infected) {//Se persona2 è un giallo/infetto e persona1 è un verde/sano,simuliamo come cambierà il fato col metodo tryInfect di persona1
            person1.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration);
        }
        callBacks.forEach(simulatorCallBack -> simulatorCallBack.registerEncounter(person1, person2));
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

    public boolean getFirstRed() {
        return firstRed;
    }

}