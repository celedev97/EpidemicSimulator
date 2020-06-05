package com.epidemic_simulator;

import java.awt.*;

public class Person {
    public boolean canMove = true; //All'inizio tutti gli individui possono muoversi

    //#region DISEASE FLAGS
    protected boolean alive        = true;
    protected boolean infected     = false;
    protected boolean canInfect    = false;
    protected boolean symptoms     = false;
    protected boolean immune       = false;

    //#region DISEASE FLAGS GETTERS
    public boolean isAlive() {
        return alive;
    }
    public boolean isInfected() {
        Utils.negateStrategyAccess();
        return infected;
    }
    public boolean hasSymptoms() {
        return symptoms;
    }
    //#endregion

    //#endregion

    //#region DISEASE STATUS
    protected int daysSinceInfection = 0; //Numero giorni passati dal contagio.
    protected int symptomsDevelopmentDay =-1; //Numero giorni entro cui eventualmente presenterà sintomi,-1 dato che la persona potrebbe non svilupparli.
    protected int deathDay =-1; //Numero giorni entro cui eventualmente la persona in questione morirà,-1 dato che la persona potrebbe anche non morire.
    //#endregion

    public Color getColor(){
        //Se l'individuo ha status-alive=false è sicuramente nero/morto.
        if(!alive) return Color.BLACK;
        //blu un individuo guarito
        if(immune) return Color.BLUE;

        //verde, che rappresenta un individuo sano o con carica batterica non rilevabile e comunque non contagioso con CanInfect=false
        if(!canInfect) return Color.GREEN;
        //giallo, che indica un individuo contagioso ma asintomatico con symptoms=false

        if(!symptoms) return Color.YELLOW;

        //rosso un individuo sintomatico
        return Color.RED;
    }

    //Una volta avvenuto un'incontro tra un individuo sano e uno infetto in movimento,la seguente funzione verrà richiamata per vedere se il soggetto sano può essere stato infetto o meno.
    protected synchronized boolean tryInfect(int infectionRate, int symptomsRate, int deathRate, int incubation, int maxDayForSymptomsDevelopment, int healDay){
        //if this person is already immune or infected than i can't infect it again
        if(immune || infected) return false;

        //check sull'infettività
        if(Utils.randomBool(infectionRate)){//Data la percentuale di infezione(25% ad esempio)Util.randomBool ritornerà true se rientra in quella percentuale di infezione.
            infect(symptomsRate, deathRate, incubation, maxDayForSymptomsDevelopment, healDay);//Se entriamo nell'if abbiamo un'infezione avvenuta
            return true;
        }
        return false;
    }

    //Una volta che l'infezione è avvenuta verifichiamo con la funzione come il soggetto reagirà alla malattia.
    protected void infect(int symptomsRate, int deathRate, int incubation, int maxDayForSymptomsDevelopment, int healDay) {
        infected = true;//La persona passerà a questo punto il suo attributo 'infected' a true.

        //check symptomaticity
        if(!Utils.randomBool(symptomsRate)) return; /*Se il soggetto non rientra nel range di sintomaticità(anche questa in percentuale)
        allora resterà giallo fino alla sua guarigione ed usciamo...*/
        symptomsDevelopmentDay = Utils.random(incubation, maxDayForSymptomsDevelopment);//Altrimenti calcoliamo un giorno randomico entro cui una persona può presentare sintomi
        //check death-->Controlliamo con lo stesso principio se il soggetto rientra nella percentuale di letalità.
        if(!Utils.randomBool(deathRate)) return;
        deathDay = Utils.random(symptomsDevelopmentDay, healDay-1);//Calcolo di un giorno entro cui la persona morirà.
    }

}
