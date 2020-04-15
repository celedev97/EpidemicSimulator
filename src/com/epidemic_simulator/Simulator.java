package com.epidemic_simulator;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulator {

    public Random random;

    //#region Simulation data
    private int startingPopulation;
    public int getStartingPopulation(){return startingPopulation;}

    public int resources;

    public int testPrice;

    public int averageEncounterRate;

    private List<Person> population;
    //#endregion

    //#region Disease data
    public int infectivity;
    public int symptomaticity;
    public int lethality;
    public int duration;
    public int incubation;
    //#endregion


    //constructor
    public Simulator(int startingPopulation, int resources, int testPrice, int averageEncounterRate, int infectivity, int symptomaticity, int lethality, int duration, int incubation) throws InvalidSimulationException {
        this.startingPopulation = startingPopulation;
        this.resources = resources;
        this.testPrice = testPrice;
        this.averageEncounterRate = averageEncounterRate;
        this.infectivity = infectivity;
        this.symptomaticity = symptomaticity;
        this.lethality = lethality;
        this.duration = duration;
        this.incubation = incubation;

        if(resources >= (testPrice*startingPopulation)) throw new InvalidSimulationException();

        population = new ArrayList<>();
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }
        population.get(0).infected = true;

        random = new Random(LocalDateTime.now().getNano());
    }

    public void executeDay(){
        for (Person person : population){
            //#region movimento
            if (!person.canMove || person.symptoms) {
                //not abilitated to move or has syntomphs, so they stand still and waste resources.
                resources--;
            }else {
                //person abilitated to move (and not dead or sick)
                for (int i = 0; i < averageEncounterRate; i++) {

                    Person randomPerson = null;
                    while(randomPerson == null || randomPerson == person){
                        //TODO: va in loop infinito se sono quasi tutti morti
                        population.get(random.nextInt(population.size()));
                    }

                    person.encounter(randomPerson);
                }
            }
            //#endregion

            //#region
            if(!person.infected) continue;

            if(!person.canInfect){

            }

            //#endregion
        }
    }

    public boolean testVirus(Person person){
        //if it has symptoms then it has the virus, no need to waste resources.
        if(person.symptoms) return true;

        //otherwise i do the test
        resources--;
        if(person.canInfect) return true;
        return false;
    }

}
