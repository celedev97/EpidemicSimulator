package com.epidemic_simulator.gui.visual;

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
    //MOVEMENT SPEED/PRECISION

    private final int BASE_SPEED = 1000;
    private final float SPEED_VARIANT = .5f;
    private final int SPEED = BASE_SPEED - (int)(BASE_SPEED*SPEED_VARIANT)+rand.nextInt((int)(BASE_SPEED*SPEED_VARIANT));

    private final int POSITION_PRECISION = 2;

    //GUI CONTROL
    protected PersonManager personManager;
    protected Boolean doMove = false;

    private final Vector startingPosition;
    private Person thisPerson;

    protected ArrayList<DrawablePerson> target;
    private Color color = Color.CYAN;

    public DrawablePerson(PersonManager personManager, Person thisPerson){
        this(personManager, thisPerson,0,0);
    }

    public DrawablePerson(PersonManager personManager, Person thisPerson, float x, float y) {
        super(x,y);
        target = new ArrayList<>();
        this.personManager = personManager;
        this.thisPerson = thisPerson;
        startingPosition = new Vector(position);
    }


    @Override
    protected void update() {
        //this Person isn't allowed to move by the GUI
        if(!doMove) return;

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
                doMove = false;
                personManager.doneMoving(this);
            }
        }else{
            doMove = false;
            personManager.doneMoving(this);
        }
        //position.sumUpdate(new Vector(10,10).multiply(Engine.deltaTime));
    }

    @Override
    protected void draw(Graphics graphics, int x, int y, float scale) {
        graphics.setColor(color);
        int scaledSize = (int)(SIZE*scale);
        int halfScaledSize = scaledSize/2;
        graphics.fillOval(x-halfScaledSize,y-halfScaledSize,scaledSize,scaledSize);
    }

    public void updateColor() {
        color = thisPerson.getColor();
    }
}
