package com.epidemic_simulator.gui;

import com.epidemic_simulator.Person;
import dev.federicocapece.jdaze.Engine;
import dev.federicocapece.jdaze.GameObject;
import dev.federicocapece.jdaze.Vector;
import dev.federicocapece.jdaze.collider.Collider;

import java.awt.*;

public class DrawablePerson extends GameObject {
    private Person innerPerson;

    private DrawablePerson target = null;

    public DrawablePerson(Person innerPerson){
        this(innerPerson,0,0);
    }

    public DrawablePerson(Person innerPerson, float x, float y) {
        super(x,y);
        this.innerPerson = innerPerson;
    }


    @Override
    protected void update() {
        if(target != null){
            //(target.position-position).normalized * speed * deltaTime
            move(target.position.sub(this.position).normalize().multiply(10*Engine.deltaTime));
        }
        //position.sumUpdate(new Vector(10,10).multiply(Engine.deltaTime));
    }

    @Override
    protected void onCollisionEnter(Collider collider) {
        if(collider.gameObject == target){
            target = null;
            extrapolate(collider);
        }
    }

    @Override
    protected void draw(Graphics graphics, int x, int y, float scale) {
        graphics.setColor(innerPerson.getColor());
        int scaledSize = (int)(10*scale);
        int halfScaledSize = scaledSize/2;
        graphics.fillOval(x-halfScaledSize,y-halfScaledSize,scaledSize,scaledSize);
    }
}
