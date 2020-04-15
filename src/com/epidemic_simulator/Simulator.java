package com.epidemic_simulator;

import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private int startingPopulation;
    public int getStartingPopulation(){return startingPopulation;}

    public int resources;

    public int testPrice;

    public int averageEncounterRate;

    private List<Person> population;

    //constructor
    public Simulator(int startingPopulation, int resources, int testPrice, int averageEncounterRate) throws InvalidSimulationException {
        if(resources >= (testPrice*startingPopulation)) throw new InvalidSimulationException();

        this.startingPopulation = startingPopulation;
        this.resources = resources;
        this.testPrice = testPrice;
        this.averageEncounterRate = averageEncounterRate;

        population = new ArrayList<>();
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }
    }

    public void executeDay(){
        for (Person person : population){
            if (!person.canMove || person.symptoms) {
                //not abilitated to move or has syntomphs, so they stand still and waste resources.
                resources--;
            }else if(person.alive){
                //person abilitated to move (and not dead or sick)
            }
            //encounters???
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
