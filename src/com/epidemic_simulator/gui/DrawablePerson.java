package com.epidemic_simulator.gui;

import com.epidemic_simulator.Person;
import dev.federicocapece.jdaze.Engine;
import dev.federicocapece.jdaze.GameObject;
import dev.federicocapece.jdaze.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class DrawablePerson extends GameObject {
    private static final Random rand = new Random();

    private final int SIZE = 10;
    private final int SPEED = 400+rand.nextInt(60);
    private final int POSITION_PRECISION = 2;

    protected Boolean moving = false;

    private final Vector startingPosition;
    private Person innerPerson;

    protected ArrayList<DrawablePerson> target;

    public DrawablePerson(Person innerPerson){
        this(innerPerson,0,0);
    }

    public DrawablePerson(Person innerPerson, float x, float y) {
        super(x,y);
        target = new ArrayList<>();
        this.innerPerson = innerPerson;
        startingPosition = new Vector(position);
    }


    @Override
    protected void update() {
        synchronized (moving){
            moving = true;
            Vector offset, movement;
            if(target.size()>0){
                //if my target is faster than me i'll just wait
                //if(target.get(0).SPEED >= SPEED && target.get(0).target.size()>0) return;

                //(target.position-position).normalized * deltaTime
                offset = target.get(0).position.sub(this.position);
                movement = offset.normalized().multiplyUpdate(SPEED * Engine.deltaTime);
                if (movement.magnitude() < offset.magnitude()){
                    //System.out.println("MOVING: "+this+" to "+target.get(0)+"MOVE:"+ movement);
                    position.sumUpdate(movement);
                }else{
                    //i'm really close to the other person, i'm gonna consider that i've met him.
                    target.remove(0);
                }
            }else if((offset = startingPosition.sub(position)).magnitude() > POSITION_PRECISION){

                movement = startingPosition.sub(this.position).normalize().multiplyUpdate(SPEED * Engine.deltaTime);
                if (movement.magnitude() < offset.magnitude()){
                    //System.out.println("GOING BACK: "+position+" to "+startingPosition+"MOVE:"+ movement);
                    position.sumUpdate(movement);
                }else{
                    position.set(startingPosition);
                    moving = false;
                }
            }else{
                moving = false;
            }
            //position.sumUpdate(new Vector(10,10).multiply(Engine.deltaTime));
        }

    }

    @Override
    protected void draw(Graphics graphics, int x, int y, float scale) {
        graphics.setColor(innerPerson.getColor());
        int scaledSize = (int)(SIZE*scale);
        int halfScaledSize = scaledSize/2;
        graphics.fillOval(x-halfScaledSize,y-halfScaledSize,scaledSize,scaledSize);
    }

}
