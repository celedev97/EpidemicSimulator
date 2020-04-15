package com.epidemic_simulator;

import java.awt.*;

public class Person {
    //#region DISEASE STATUS
    public boolean alive        = true;
    public boolean infected     = false;
    public boolean canInfect    = false;
    public boolean symptoms     = false;
    public boolean immune       = false;
    //#endregion

    public boolean canMove;

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

    public void encounter(Person person2) {
        if(this.canInfect && !person2.infected && !person2.immune){
            person2.infected = true;
        }
        if(person2.canInfect && !this.infected && !this.immune){
            this.infected = true;
        }
    }

}
