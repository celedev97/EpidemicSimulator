package com.epidemic_simulator;

import java.awt.*;

/**
 * /**
 * An object that represent a person and their health status.
 */
public class Person {
    /**
     * If this is set to true the person is allowed to move, it's true by default.
     */
    public boolean canMove = true;

    //#region DISEASE FLAGS
    /**
     * If this is set to true the person is alive, it's true by default.
     */
    protected boolean alive = true;
    /**
     * If this is set to true the person is infected and its disease status will be updated everyday.
     */
    protected boolean infected = false;
    /**
     * If this is set to true the person has enough virus to infect other people in case they meet.
     */
    protected boolean canInfect = false;
    /**
     * If this is set to true the person is showing symptoms of the disease.
     * <br>By default a person that has symptoms will not be able to move, and will consume more resources.
     */
    protected boolean symptoms = false;
    /**
     * If this is set to true the person is immune to the disease and can't be infected.
     * <br><b>NOTE:</b> The fact that a person is immune doesn't necessarily mean that they're allowed to move.
     */
    protected boolean immune = false;

    //#region DISEASE FLAGS GETTERS

    /**
     * Get the {@link #alive} flag.
     *
     * @return the flag value
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Get the {@link #infected} flag.
     *
     * @return the flag value
     */
    public boolean isInfected() {
        Utils.negateStrategyAccess();
        return infected;
    }

    /**
     * Get the {@link #symptoms} flag.
     *
     * @return the flag value
     */
    public boolean hasSymptoms() {
        return symptoms;
    }
    //#endregion

    //#endregion

    //#region DISEASE STATUS
    /**
     * The days since when this person was infected.
     */
    protected int daysSinceInfection = 0;
    /**
     * The number of the day in which this person will develop symptoms.<br>
     * If this person will not develop symptoms it is is {@code -1} by default.
     *
     * @see #symptoms
     */
    protected int symptomsDevelopmentDay = -1;
    /**
     * The number of the day in which this person will die.<br>
     * If this person will not die it is {@code -1} by default.
     *
     * @see #alive
     */
    protected int deathDay = -1;
    //#endregion

    /**
     * Get the Color representing the health status of this person according to the project specs.
     *
     * @return the Color
     * @see java.awt.Color
     */
    public Color getColor() {
        //Se l'individuo ha status-alive=false è sicuramente nero/morto.
        if (!alive) return Color.BLACK;
        //blu un individuo guarito
        if (immune) return Color.BLUE;

        //verde, che rappresenta un individuo sano o con carica batterica non rilevabile e comunque non contagioso con CanInfect=false
        if (!canInfect) return Color.GREEN;
        //giallo, che indica un individuo contagioso ma asintomatico con symptoms=false

        if (!symptoms) return Color.YELLOW;

        //rosso un individuo sintomatico
        return Color.RED;
    }

    /**
     * This method is called every time an {@link #infected} person and a not infected one meets.<br>
     * This method extract a boolean based on {@code infectionRate}, if it's true then it proceed to call {@link #infect}
     *
     * @param infectionRate                The infection rate.
     * @param symptomsRate                 The symptoms rate.
     * @param deathRate                    The death rate.
     * @param incubation                   The number of incubation days.
     * @param maxDayForSymptomsDevelopment The max number of days for symptoms development.
     * @param healDay                      The number of days in which the disease will cure itself.
     * @return true if this person gets infected, false otherwise
     * @see #infect
     */
    protected boolean tryInfect(int infectionRate, int symptomsRate, int deathRate, int incubation, int maxDayForSymptomsDevelopment, int healDay) {
        //if this person is already immune or infected than i can't infect it again
        if (immune || infected) return false;

        //check sull'infettività
        if (Utils.randomBool(infectionRate)) {//Data la percentuale di infezione(25% ad esempio)Util.randomBool ritornerà true se rientra in quella percentuale di infezione.
            infect(symptomsRate, deathRate, incubation, maxDayForSymptomsDevelopment, healDay);//Se entriamo nell'if abbiamo un'infezione avvenuta
            return true;
        }
        return false;
    }

    /**
     * This method effectively infect this Person.<br>
     * The person is infected by setting their {@link #infected} to {@code true}.<br>
     * <br>
     * This method also decides the future development of the disease for this person.<br>
     * It will extract a booleans based on the {@code symptomsRate}
     * and if it's true it will decide the day on which this person will develop {@link #symptoms}.
     * If the extracted boolean is true it will also extract another boolean based on the {@code deathRate}
     * and if it's true it will decide the day on which this person will die {@link #alive}.
     *
     * @param symptomsRate                 the symptoms rate
     * @param deathRate                    the death rate
     * @param incubation                   the incubation
     * @param maxDayForSymptomsDevelopment the max day for symptoms development
     * @param healDay                      the heal day
     */
    protected void infect(int symptomsRate, int deathRate, int incubation, int maxDayForSymptomsDevelopment, int healDay) {
        infected = true;//La persona passerà a questo punto il suo attributo 'infected' a true.

        //check symptomaticity
        if (!Utils.randomBool(symptomsRate)) return; /*Se il soggetto non rientra nel range di sintomaticità(anche questa in percentuale)
        allora resterà giallo fino alla sua guarigione ed usciamo...*/
        symptomsDevelopmentDay = Utils.random(incubation, maxDayForSymptomsDevelopment);//Altrimenti calcoliamo un giorno randomico entro cui una persona può presentare sintomi
        //check death-->Controlliamo con lo stesso principio se il soggetto rientra nella percentuale di letalità.
        if (!Utils.randomBool(deathRate)) return;
        deathDay = Utils.random(symptomsDevelopmentDay, healDay - 1);//Calcolo di un giorno entro cui la persona morirà.
    }

}
