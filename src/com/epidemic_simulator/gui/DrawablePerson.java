package com.epidemic_simulator.gui;

import com.epidemic_simulator.Person;
import dev.federicocapece.jdaze.Engine;
import dev.federicocapece.jdaze.GameObject;
import dev.federicocapece.jdaze.Vector;

import java.awt.*;

public class DrawablePerson extends GameObject {
    private com.epidemic_simulator.Person innerPerson;

    public DrawablePerson(Person innerPerson){
        this(innerPerson,0,0);
    }

    public DrawablePerson(Person innerPerson, float x, float y) {
        super(x,y);
        this.innerPerson = innerPerson;
    }


    @Override
    protected void update() {
        //position.sumUpdate(new Vector(10,10).multiply(Engine.deltaTime));
    }

    @Override
    protected void draw(Graphics graphics, int x, int y, float scale) {
        graphics.setColor(innerPerson.getColor());
        int scaledSize = (int)(10*scale);
        int halfScaledSize = scaledSize/2;
        graphics.fillOval(x-scaledSize,y-scaledSize,scaledSize,scaledSize);
    }
}
