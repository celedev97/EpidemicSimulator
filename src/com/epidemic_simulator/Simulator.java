package com.epidemic_simulator;

import java.awt.*;
import java.util.ArrayList;

public class Simulator {
    //#region Simulation parameters
    private final int startingPopulation;
    protected Strategy strategy;//Oggetto Strategy per i vari richiami sulle strategie
    private int resources;//Numero risorse disponibili

    public int getResources() {
        return resources;
    }

    private final int testPrice;//Costi per le cure
    private final int averageEncountersPerDay;//Velocità di incontro media per individuo/numero medio di individui che giornalmente una persona incontra
    //#endregion

    //#region Disease data
    public final int infectionRate;//Percentuale di infettività
    public final int symptomsRate;//Percentuale di sintomaticità
    public final int deathRate;//Percentuale di letalità

    public final int healDay;
    public final int canInfectDay;//Numero giorni di incubazione
    public final int developSymptomsMaxDay;
    //#endregion

    //simulation status
    public ArrayList<Person> population;
    private ArrayList<Person> alivePopulation;

    private int day = 0;

    //constructor
    public Simulator(int startingPopulation, int resources, int testPrice, int averageEncountersPerDay, int infectionRate, int symptomsRate, int deathRate, int diseaseDuration) throws InvalidSimulationException {
        //Condizioni necessarie per verificare la validità dei dati inseriti in funzione del requisito.
        if(resources >= (startingPopulation*testPrice)) throw new InvalidSimulationException("Condition not met: R < P ∗ C\nThe resources are enough to test the whole population!");
        if(resources >= (startingPopulation*diseaseDuration)) throw new InvalidSimulationException("Condition not met: R < P ∗ D");

        //Dati popolazione/stato
        this.startingPopulation = startingPopulation; //Numero popolazione iniziale/al lancio del simulatore
        this.resources = resources;
        this.testPrice = testPrice;
        this.averageEncountersPerDay = averageEncountersPerDay;
        //Dati sanitari
        this.infectionRate = infectionRate;
        this.symptomsRate = symptomsRate;
        this.deathRate = deathRate;
        //Dati evoluzione della malattia
        this.healDay                = diseaseDuration;
        this.canInfectDay           = diseaseDuration/6;
        this.developSymptomsMaxDay  = diseaseDuration/3;

        population = new ArrayList<>();//Lista persona

        //Aggiunta di persona alla lista
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }
        //Creazione della prima persona infetta che va in giro,il suo canMove=true resta invariato->Un infetto per il momento giallo
        Person p= population.get(0);
        p.canInfect=true;
        p.infected=true;
        p.infect(symptomsRate,deathRate,canInfectDay,developSymptomsMaxDay,healDay);
        alivePopulation = (ArrayList<Person>) population.clone(); //All'inizio abbiamo tanti soggetti vivi quante sono le persone inserite.
        System.out.println(canInfectDay+" "+developSymptomsMaxDay);
    }

    public int getDay() {
        return day;
    }

    //Richiamo il metodo 'heal/guarigione' su una Person rossa passata
    public void heal(Person person) {
        heal(person,true); //Se payHealing=True la Persona è guarita con un costo
    }

    private void heal(Person person, boolean payHealing){ //Se payHealing=False la Persona è guarita da sola smaltendo il virus
        if(payHealing) resources -= 3 * testPrice;

        boolean hadSymtomps = person.symptoms;

        //La persona è guarita
        person.immune = true;
        person.infected = false;
        person.canInfect = false;
        person.symptoms = false;
        //person.canMove=true;

        if(hadSymtomps && strategy != null) strategy.personClean(person);
    }

    //Possibili finali della simulazione
    public enum Outcomes{
        NOTHING,
        ALL_HEALED,
        ALL_DEAD,
        ECONOMIC_COLLAPSE
    }

    public Outcomes executeDay(){//Fino al raggiungimento di un 'finale' eseguiamo 'n' giorni,e per ognuno di essi sperimentiamo degli esiti tra incontri e consumi
        day++;//Variabile contatrice dei giorni
        int black   = (int) population.stream().filter(person -> person.getColor() == Color.BLACK).count();
        int green   = (int) population.stream().filter(person -> person.getColor() == Color.GREEN).count();
        int yellow  = (int) population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
        int red     = (int) population.stream().filter(person -> person.getColor() == Color.RED).count();
        int blue    = (int) population.stream().filter(person -> person.getColor() == Color.BLUE).count();

        System.out.println("green : "+green );
        System.out.println("yellow: "+yellow);
        System.out.println("red   : "+red   );
        System.out.println("blue  : "+blue  );
        System.out.println("black : "+black );
        System.out.println("resources : "+resources );

        //Per ogni giorno prendiamo tutte le 'n' persone VIVE
        for (Person person : population){
            if(!person.alive) continue;//Se è un morto passiamo avanti alla prossima...

            //#region movimento
            if (!person.canMove || person.symptoms) {
                //Se non è abilitata a muoversi o presenta sintomi il soggetto consuma una delle risorse disponibili
                resources--;
            }else {
                //Se la persona è abilitata al movimento,vuol dire che giornalmente incontra 'n' altre persone Random
                for (int i = 0; i < averageEncountersPerDay; i++) {
                    Person randomPerson = null;
                    if(alivePopulation.size() == 1) break;//Se rimane un solo soggetto in vita non può incontrare nessuno...

                    while(randomPerson == null || randomPerson == person){
                        randomPerson = alivePopulation.get(Utils.random(alivePopulation.size()));//Estraiamo un'altra persona...che non sia se stessa
                    }
                    //Sperimentiamo l'incontro
                    encounter(person, randomPerson);
                }
            }
            //#endregion

            //#region prosecuzione malattia
            if(!person.infected) continue;//Controlliamo se la persona è stata effettivamente infettata

            person.daysSinceInfection++;//Altrimenti comincio a contare i giorni entro cui,anche se infetta,l'individuo non può infettare

            if(!person.canInfect && person.daysSinceInfection >= canInfectDay){
                //è verde ma è infetto, e sono i passati i giorni dell'incubazione, quindi diventa giallo
                person.canInfect = true;
            }

            if(person.daysSinceInfection == person.symptomsDevelopmentDay){
                //è giallo ed oggi è il giorno in cui sviluppa sintomi
                person.symptoms = true;
                if(strategy != null) strategy.personHasSymptoms(person);
            }

            if(person.daysSinceInfection == person.deathDay){
                //è rosso può morire
                alivePopulation.remove(person);//Rimuovo l'individuo dalla lista delle persone vive
                person.alive = false;
                continue;
            }

            if(person.daysSinceInfection == healDay){
                //se è rosso/giallo può guarire
                heal(person,false);
            }
            //#endregion
        }

        if(strategy!=null) strategy.afterExecuteDay(); //Eseguiamo il giorno sulla strategia
        //#region simulation status return
        int alive = alivePopulation.size(); //Controlliamo quante persone vive rimangono al giorno 'x'
        if(alive == 0) return Outcomes.ALL_DEAD; //AlivePopulation==0-->Outcomes=All Dead(Not big surprise)
        if(alivePopulation.stream().filter(person -> person.infected).count() == 0) return Outcomes.ALL_HEALED; //Se di tutte le persone in vita gli infetti sono 0-->Outcames=All Healed
        if(resources <= 0) return  Outcomes.ECONOMIC_COLLAPSE;//Se le risorse raggiungono lo 0-->Outcomes=Economic Collapse
        return Outcomes.NOTHING; //Altrimenti ritorniamo NOTHING per continuare l'esecuzione...(il ciclo di continuazione è nel Main)
        //#endregion
    }

    //Funzione per la simulazione dell'incontro tra due persone
    private void encounter(Person person1, Person person2) {
        if(person1.canInfect && !person2.infected){//Se persona1 è un giallo/infetto e persona2 è un verde/sano,simuliamo come cambierà il fato col metodo tryInfect di persona2
            person2.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, healDay);
        }
        if(person2.canInfect && !person1.infected){//Se persona2 è un giallo/infetto e persona1 è un verde/sano,simuliamo come cambierà il fato col metodo tryInfect di persona1
            person1.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, healDay);
        }
    }

    public boolean testVirus(Person person){
        //if it has symptoms then it has the virus, no need to waste resources.
        if(person.symptoms) return true;

        //otherwise i do the test
        resources -= testPrice;
        if(person.canInfect) return true;
        return false;
    }

}
