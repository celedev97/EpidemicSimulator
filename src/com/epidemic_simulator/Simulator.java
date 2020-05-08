package com.epidemic_simulator;

import java.util.ArrayList;

public class Simulator {

    public ArrayList<SimulatorCallBack> callBacks;

    //#region Simulation parameters
    private final int startingPopulation;
    private int resources;//Numero risorse disponibili

    public int getResources() {
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

    //simulation status
    public ArrayList<Person> population;
    public ArrayList<Person> alivePopulation;

    public double r0;

    protected int day = 0;

    public boolean firstRed = false;

    //constructor
    public Simulator(int startingPopulation, int resources, int testPrice, int averageEncountersPerDay, int infectionRate, int symptomsRate, int deathRate, int diseaseDuration) throws InvalidSimulationException {
        //Condizioni necessarie per verificare la validità dei dati inseriti in funzione del requisito.
        if (resources >= (10 * (long)startingPopulation * testPrice))
            throw new InvalidSimulationException("Condition not met: R < 10 * P ∗ C");
        if (resources >= ((long)startingPopulation * diseaseDuration))
            throw new InvalidSimulationException("Condition not met: R < P ∗ D");

        //Dati popolazione/stato
        this.startingPopulation = startingPopulation; //Numero popolazione iniziale/al lancio del simulatore
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

        population = new ArrayList<>();//Lista persona

        //Aggiunta di persona alla lista
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }
        //Creazione della prima persona infetta che va in giro,il suo canMove=true resta invariato->Un infetto per il momento giallo
        population.get(0).infect(symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, this.diseaseDuration);
        population.get(0).canInfect = true;

        //All'inizio abbiamo tanti soggetti vivi quante sono le persone inserite.
        alivePopulation = (ArrayList<Person>) population.clone();
    }

    public int getDay() {
        return day;
    }

    //Possibili finali della simulazione
    public enum Outcome {
        NOTHING,
        ALL_HEALED,
        ALL_DEAD,
        ECONOMIC_COLLAPSE
    }

    public Outcome executeDay() {//Fino al raggiungimento di un 'finale' eseguiamo 'n' giorni,e per ognuno di essi sperimentiamo degli esiti tra incontri e consumi
        day++;//Variabile contatrice dei giorni

        //Vd calculation
        int canMoveCount = (int) alivePopulation.stream().filter(person -> person.canMove).count();
        double encountersThisDay = averageEncountersPerDay * canMoveCount / population.size();
        int intEncountersThisDay = encountersThisDay == (int) encountersThisDay ? (int) encountersThisDay : (int) encountersThisDay + 1;

        //R0 calculation
        r0 = encountersThisDay * diseaseDuration * doubleInfectionRate;
        if (r0 < 1)
            System.out.println("Desease has been eradicated");

        //Per ogni giorno prendiamo tutte le 'n' persone VIVE
        for (Person person : population) {
            if (!person.alive) continue;//Se è un morto passiamo avanti alla prossima...

            //#region movimento
            if (!person.canMove || person.symptoms) {
                //Se non è abilitata a muoversi o presenta sintomi il soggetto consuma una delle risorse disponibili
                resources--;
            } else {
                //Se la persona è abilitata al movimento,vuol dire che giornalmente incontra 'n' altre persone Random
                for (int i = 0; i < intEncountersThisDay; i++) {
                    Person randomPerson = null;
                    if (alivePopulation.size() == 1)
                        break;//Se rimane un solo soggetto in vita non può incontrare nessuno...

                    while (randomPerson == null || randomPerson == person) {
                        randomPerson = alivePopulation.get(Utils.random(alivePopulation.size()));//Estraiamo un'altra persona...che non sia se stessa
                    }
                    //Sperimentiamo l'incontro
                    encounter(person, randomPerson);
                }
            }
            //#endregion

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
        if (alivePopulation.size() == 0){
            outcome = Outcome.ALL_DEAD;
        }else if (alivePopulation.stream().filter(person -> person.infected).count() == 0) {
            outcome =  Outcome.ALL_HEALED;
        }else if (resources <= 0) {
            outcome =  Outcome.ECONOMIC_COLLAPSE;
        }
        //#endregion

        Outcome finalOutcome = outcome;
        callBacks.forEach(simulatorCallBack -> simulatorCallBack.afterExecuteDay(finalOutcome));
        return outcome;
    }

    //Funzione per la simulazione dell'incontro tra due persone
    private void encounter(Person person1, Person person2) {
        if (person1.canInfect && !person2.infected) {//Se persona1 è un giallo/infetto e persona2 è un verde/sano,simuliamo come cambierà il fato col metodo tryInfect di persona2
            person2.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration);
        }
        if (person2.canInfect && !person1.infected) {//Se persona2 è un giallo/infetto e persona1 è un verde/sano,simuliamo come cambierà il fato col metodo tryInfect di persona1
            person1.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, diseaseDuration);
        }
        callBacks.forEach(simulatorCallBack -> simulatorCallBack.registerEncounter(person1, person2));
    }

    public boolean testVirus(Person person) {
        resources -= testPrice;
        if (person.canInfect) return true;
        return false;
    }

}