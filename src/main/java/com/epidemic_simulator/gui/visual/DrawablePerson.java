package com.epidemic_simulator.gui.visual;

import com.epidemic_simulator.Person;
import dev.federicocapece.jdaze.Engine;
import dev.federicocapece.jdaze.GameObject;
import dev.federicocapece.jdaze.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * The type Drawable person.
 */
public class DrawablePerson extends GameObject {
    private static final Random rand = new Random();

    private final int SIZE = 10;

    //MOVEMENT SPEED/PRECISION
    private final float speed;

    private final int POSITION_PRECISION = 2;


    //GUI CONTROL
    /**
     * The simulatorGUI that will be called after this Person has completed their encounters.
     */
    protected SimulatorGUI simulatorGUI;
    /**
     * This flag will be set from the simulatorGUI to coordinate all the movements
     * in order to emulate the encounters of the {@link com.epidemic_simulator.Simulator}
     */
    protected Boolean doMove = false;

    private final Vector startingPosition;
    private Person thisPerson;

    /**
     * A {@link java.util.List} of DrawablePersons that this person has to meet in this day.
     */
    protected ArrayList<DrawablePerson> target;

    private Color color = Color.GREEN;
    private boolean innerYellow = false, canMove = false;

    /**
     * Instantiates a new Drawable person.
     *
     * @param simulatorGUI The simulatorGUI that will be called after this Person has completed their encounters.
     * @param thisPerson   the Person linked to this DrawablePerson.
     * @param x            the x coordinate of this DrawablePerson.
     * @param y            the y coordinate of this DrawablePerson.
     * @param speed        the speed at which this DrawablePerson will move.
     */
    public DrawablePerson(SimulatorGUI simulatorGUI, Person thisPerson, float x, float y, float speed) {
        super(x, y);
        target = new ArrayList<>();

        this.simulatorGUI = simulatorGUI;
        this.thisPerson = thisPerson;
        this.speed = speed;
        startingPosition = new Vector(position);
    }


    @Override
    protected void update() {
        //this Person isn't allowed to move by the GUI
        if (!doMove) return;

        Vector offset, movement;
        if (target.size() > 0) {
            //if my target is faster than me i'll just wait
            //if(target.get(0).SPEED >= SPEED && target.get(0).target.size()>0) return;

            //(target.position-position).normalized * deltaTime
            offset = target.get(0).position.sub(this.position);
            movement = offset.normalized().multiplyUpdate(speed * simulatorGUI.getSpeedMultiplier() * Engine.deltaTime);
            if (movement.magnitude() < offset.magnitude()) {
                //System.out.println("MOVING: "+this+" to "+target.get(0)+"MOVE:"+ movement);
                position.sumUpdate(movement);
            } else {
                //i'm really close to the other person, i'm gonna consider that i've met him.
                target.remove(0);
            }
        } else if ((offset = startingPosition.sub(position)).magnitude() > POSITION_PRECISION) {

            movement = startingPosition.sub(this.position).normalize()
                    .multiplyUpdate(speed * simulatorGUI.getSpeedMultiplier() * Engine.deltaTime);
            if (movement.magnitude() < offset.magnitude()) {
                //System.out.println("GOING BACK: "+position+" to "+startingPosition+"MOVE:"+ movement);
                position.sumUpdate(movement);
            } else {
                position.set(startingPosition);
                doMove = false;
                simulatorGUI.doneMoving(this);
            }
        } else {
            doMove = false;
            simulatorGUI.doneMoving(this);
        }
    }

    @Override
    protected void draw(Graphics graphics, int x, int y, float scale) {
        //calculating circle size
        int scaledSize = (int) (SIZE * scale);
        int halfScaledSize = scaledSize / 2;
        //drawing outer can't move circle
        if (!canMove) {
            graphics.setColor(Color.BLACK);
            graphics.fillOval(x - halfScaledSize - (int)(1*scale), y - halfScaledSize - (int)(1*scale), scaledSize + (int)(2*scale), scaledSize + (int)(2*scale));
        }

        //drawing person regular color
        graphics.setColor(color);
        graphics.fillOval(x - halfScaledSize, y - halfScaledSize, scaledSize, scaledSize);

        //drawing inner yellow circle if this person is green but infected
        if (innerYellow) {
            graphics.setColor(Color.YELLOW);
            graphics.fillOval(x - halfScaledSize / 2, y - halfScaledSize / 2, scaledSize / 2, scaledSize / 2);
        }

    }

    /**
     * Update the color of this DrawablePerson.<BR>
     * The color isn't always updated to the right color of the Person because otherwise all the colors of the DrawablePersons
     * would change at the beginning of the day since the {@link com.epidemic_simulator.Simulator} runs way faster than the graphical simulation.
     */
    protected void updateColor() {
        color = thisPerson.getColor();
        innerYellow = color == Color.GREEN && thisPerson.isInfected();
        canMove = thisPerson.canMove;
    }
}
