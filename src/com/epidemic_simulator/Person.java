package com.epidemic_simulator;

import java.awt.*;
import java.util.Random;

public class Person {
    //#region DISEASE STATUS
    protected boolean alive        = true;
    protected boolean infected     = false;
    protected boolean canInfect    = false;
    protected boolean symptoms     = false;
    protected boolean immune       = false;

    //disease evolution
    protected int diseaseDays = 0;
    protected int willHaveSymptomps =-1;
    protected int willDie =-1;
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


    protected void tryInfect(int infectivity, int symptomaticity, int lethality, int incubation, int duration){
        if(immune) return;
        //check infectivity
        if((Simulator.random.nextInt(100)+1) > infectivity) return;
        infected = true;
        //check symptomaticity
        if((Simulator.random.nextInt(100)+1) > symptomaticity) return;
        willHaveSymptomps = incubation + Simulator.random.nextInt(duration - incubation);
        //check death
        if((Simulator.random.nextInt(100)+1) > lethality) return;
        System.out.println(100);
        willDie = willHaveSymptomps + Simulator.random.nextInt((duration+1) - willHaveSymptomps);
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
