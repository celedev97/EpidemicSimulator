package com.epidemic_simulator;

import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Simulator {
    public static Random random;

    //#region Simulation data
    private int startingPopulation;
    public int getStartingPopulation(){return startingPopulation;}
    public int resources;
    public int testPrice;
    public int averageEncounterRate;

    public ArrayList<Person> population;

    private ArrayList<Person> alivePopulation;
    //#endregion

    //#region Disease data
    public int infectivity;
    public int symptomaticity;
    public int lethality;

    public int duration;
    public int incubation;
    //#endregion

    //constructor
    public Simulator(int startingPopulation, int resources, int testPrice, int averageEncounterRate, int infectivity, int symptomaticity, int lethality, int duration) throws InvalidSimulationException {
        this.startingPopulation = startingPopulation;
        this.resources = resources;
        this.testPrice = testPrice;
        this.averageEncounterRate = averageEncounterRate;
        this.infectivity = infectivity;
        this.symptomaticity = symptomaticity;
        this.lethality = lethality;
        this.duration = duration;
        this.incubation = duration/6;

        if(resources >= (testPrice*startingPopulation)) throw new InvalidSimulationException();

        population = new ArrayList<>();
        for (int i = 0; i < startingPopulation; i++) {
            population.add(new Person());
        }
        //creating first person
        population.get(0).infected = true;
        population.get(0).willHaveSymptomps = duration/2;
        population.get(0).willDie = (int)(duration * .75);

        alivePopulation = (ArrayList<Person>) population.clone();

        random = new Random(LocalDateTime.now().getNano());
    }

    public void executeDay(){
        for (Person person : population){
            if(!person.alive) continue;

            //#region movimento
            if (!person.canMove || person.symptoms) {
                //not abilitated to move or has syntomphs, so they stand still and waste resources.
                resources--;
            }else {
                //person abilitated to move (and not dead or sick)
                for (int i = 0; i < averageEncounterRate; i++) {
                    Person randomPerson = null;
                    if(alivePopulation.size() == 1) break;

                    while(randomPerson == null || randomPerson == person){
                        randomPerson = alivePopulation.get(random.nextInt(alivePopulation.size()));
                    }

                    encounter(person, randomPerson);
                }
            }
            //#endregion

            //#region
            if(!person.infected) continue;

            person.diseaseDays++;

            if(!person.canInfect && person.diseaseDays >= incubation){
                //è verde ma è infetto, e sono i passati i giorni dell'incubazione, quindi diventa giallo
                person.canInfect = true;
            }

            if(person.diseaseDays == person.willHaveSymptomps){
                //è giallo ed oggi è il giorno in cui sviluppa sintomi
                person.symptoms = true;
            }

            if(person.diseaseDays == person.willDie){
                //è rosso può morire
                alivePopulation.remove(person);
                person.alive = false;
            }

            if(person.diseaseDays == duration){
                //se è rosso/giallo può guarire
                person.immune = true;
                person.canMove = true;
                person.infected = false;
                person.canInfect = false;
                person.symptoms = false;
            }

            //#endregion
        }

        System.out.println("");
    }

    private void encounter(Person person1, Person person2) {
        if(person1.canInfect && !person2.infected){
            person2.tryInfect(infectivity, symptomaticity, lethality, incubation, duration);
        }
        if(person2.canInfect && !person1.infected){
            person1.tryInfect(infectivity, symptomaticity, lethality, incubation, duration);
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
