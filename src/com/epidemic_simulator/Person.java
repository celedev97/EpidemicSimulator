package com.epidemic_simulator;

import java.awt.*;

public class Person {
    //#region DISEASE STATUS
    protected boolean alive        = true;
    protected boolean infected     = false;
    protected boolean canInfect    = false;
    protected boolean symptoms     = false;
    protected boolean immune       = false;

    //disease evolution
    protected int daysSinceInfection = 0;
    protected int symptomsDevelopmentDay =-1;
    protected int deathDay =-1;
    //#endregion

    protected boolean canMove = true;

    public Color getColor(){
        //if he's not alive then he's dead (Captain obvious to the rescue!)
        if(!alive) return Color.BLACK;
        //blu un individuo guarito
        if(immune) return Color.BLUE;

        //verde, che rappresenta un individuo sano o con carica batterica non rilevabile e comunque non contagioso
        if(!canInfect) return Color.GREEN;
        //giallo, che indica un individuo contagioso ma asintomatico
        if(!symptoms) return Color.YELLOW;
        //rosso un individuo sintomatico
        return Color.RED;
    }

    protected void tryInfect(int infectionRate, int symptomsRate, int deathRate, int incubation, int maxDayForSymptomsDevelopment, int healDay){
        if(immune) return;

        //check infectivity
        if(Utils.randomBool(infectionRate)){
            infect(symptomsRate, deathRate, incubation, maxDayForSymptomsDevelopment, healDay);
        }
    }

    protected void infect(int symptomsRate, int deathRate, int incubation, int maxDayForSymptomsDevelopment, int healDay) {
        infected = true;

        //check symptomaticity
        if(!Utils.randomBool(symptomsRate)) return;
        symptomsDevelopmentDay = Utils.random(incubation, maxDayForSymptomsDevelopment);

        //check death
        if(!Utils.randomBool(deathRate)) return;
        deathDay = Utils.random(symptomsDevelopmentDay, healDay-1);
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean getCanMove() {
        return canMove;
    }

    public boolean getImmune(){
        return immune;
    }

}
