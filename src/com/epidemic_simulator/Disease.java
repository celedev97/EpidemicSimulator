package com.epidemic_simulator;

public class Disease {
    //la probabilità (maggiore di 0) che un individuo sano venga infettato a seguito di un incontro con un contagiato asintomatico o sintomatico;
    public double infectivity;
    //la probabilità  (maggiore di 0) che un contagiato sviluppi sintomi
    public double symptomaticity;
    //la probabilità  (maggiore di 0) che un malato sintomatico muoia
    public double lethality;
    //il numero di giorni che intercorrono fra il momento del contagio e quello della guarigione
    public int duration;
}
