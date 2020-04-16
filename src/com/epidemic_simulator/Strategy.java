package com.epidemic_simulator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class Strategy {
    private ArrayList<Person> population;

    public Strategy(Simulator simulator) {
        population = simulator.population;
    }

}
