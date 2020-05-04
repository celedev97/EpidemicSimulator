package com.epidemic_simulator.gui;

import dev.federicocapece.jdaze.Engine;
import dev.federicocapece.jdaze.GameObject;
import dev.federicocapece.jdaze.Vector;

import java.awt.*;

public class Person extends GameObject {
    private com.epidemic_simulator.Person innerPerson;

    public Person(com.epidemic_simulator.Person innerPerson) {
        super();
        this.innerPerson = innerPerson;
    }


    @Override
    protected void update() {
        position.sumUpdate(new Vector(10,10).multiply(Engine.deltaTime));
    }

    @Override
    protected void draw(Graphics graphics, int x, int y) {
        graphics.setColor(innerPerson.getColor());
        graphics.fillOval(x-5,y-5,10,10);
    }
}
