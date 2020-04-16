package com.epidemic_simulator;

import jdk.jshell.execution.Util;

import java.util.ArrayList;

public class Simulator {
    //#region Simulation parameters
    private final int startingPopulation;
    private int resources;
    private final int testPrice;
    private final int averageEncountersPerDay;
    //#endregion

    //#region Disease data
    public final int infectionRate;
    public final int symptomsRate;
    public final int deathRate;

    public final int healDay;
    public final int canInfectDay;
    public final int developSymptomsMaxDay;
    //#endregion

    //simulation status
    public ArrayList<Person> population;
    private ArrayList<Person> alivePopulation;
    private int day = 0;

    public int getDay() {
        return day;
    }

    public enum Outcomes{
        NOTHING,
        ALL_HEALED,
        ALL_DEAD,
        ECONOMIC_COLLAPSE
    }

    //constructor
    public Simulator(int startingPopulation, int resources, int testPrice, int averageEncountersPerDay, int infectionRate, int symptomsRate, int deathRate, int diseaseDuration) throws InvalidSimulationException {
        if(resources >= (startingPopulation*testPrice)) throw new InvalidSimulationException("Condition not met: R < P ∗ C\nThe resources are enough to test the whole population!");
        if(resources >= (startingPopulation*diseaseDuration)) throw new InvalidSimulationException("Condition not met: R < P ∗ D");

        //population/state data
        this.startingPopulation = startingPopulation;
        this.resources = resources;
        this.testPrice = testPrice;
        this.averageEncountersPerDay = averageEncountersPerDay;
        //rateos
        this.infectionRate = infectionRate;
        this.symptomsRate = symptomsRate;
        this.deathRate = deathRate;
        //disease evolution data
        this.healDay                = diseaseDuration;
        this.canInfectDay           = diseaseDuration/6;
        this.developSymptomsMaxDay = diseaseDuration/3;

        population = new ArrayList<>();
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }
        //infecting first person
        population.get(0).infect(symptomsRate,deathRate,canInfectDay,developSymptomsMaxDay,healDay);

        alivePopulation = (ArrayList<Person>) population.clone();
    }

    public Outcomes executeDay(){
        day++;
        for (Person person : population){
            if(!person.alive) continue;

            //#region movimento
            if (!person.canMove || person.symptoms) {
                //not abilitated to move or has syntomphs, so they stand still and waste resources.
                resources--;
            }else {
                //person abilitated to move (and not dead or sick)
                for (int i = 0; i < averageEncountersPerDay; i++) {
                    Person randomPerson = null;
                    if(alivePopulation.size() == 1) break;

                    while(randomPerson == null || randomPerson == person){
                        randomPerson = alivePopulation.get(Utils.random(alivePopulation.size()));
                    }

                    encounter(person, randomPerson);
                }
            }
            //#endregion

            //#region prosecuzione malattia
            if(!person.infected) continue;

            person.daysSinceInfection++;

            if(!person.canInfect && person.daysSinceInfection >= canInfectDay){
                //è verde ma è infetto, e sono i passati i giorni dell'incubazione, quindi diventa giallo
                person.canInfect = true;
            }

            if(person.daysSinceInfection == person.symptomsDevelopmentDay){
                //è giallo ed oggi è il giorno in cui sviluppa sintomi
                person.symptoms = true;
            }

            if(person.daysSinceInfection == person.deathDay){
                //è rosso può morire
                alivePopulation.remove(person);
                person.alive = false;
                continue;
            }

            if(person.daysSinceInfection == healDay){
                //se è rosso/giallo può guarire
                person.immune = true;
                person.canMove = true;
                person.infected = false;
                person.canInfect = false;
                person.symptoms = false;
            }

            //#endregion
        }
        int alive = alivePopulation.size();
        if(alive == 0) return Outcomes.ALL_DEAD;
        if(alivePopulation.stream().filter(person -> person.immune).count() == alive) return Outcomes.ALL_HEALED;
        if(resources <= 0) return  Outcomes.ECONOMIC_COLLAPSE;
        return Outcomes.NOTHING;
    }

    private void encounter(Person person1, Person person2) {
        if(person1.canInfect && !person2.infected){
            person2.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, healDay);
        }
        if(person2.canInfect && !person1.infected){
            person1.tryInfect(infectionRate, symptomsRate, deathRate, canInfectDay, developSymptomsMaxDay, healDay);
        }
    }

    public boolean testVirus(Person person){
        //if it has symptoms then it has the virus, no need to waste resources.
        if(person.symptoms) return true;

        //otherwise i do the test
        //TODO: ROBA
        resources -= testPrice;
        if(person.canInfect) return true;
        return false;
    }

}
