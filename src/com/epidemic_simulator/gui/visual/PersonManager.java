package com.epidemic_simulator.gui.visual;

import com.epidemic_simulator.*;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.util.*;

public class PersonManager {
    private HashMap<Person, DrawablePerson> drawablePersonsDictionary;
    private ArrayList<DrawablePerson> drawablePersons;
    private ArrayList<DrawablePerson> toMovePersons;
    private int movedPersons;

    private Simulator simulator;
    private Simulator.Outcome lastDayOutCome = Simulator.Outcome.NOTHING;
    private SimulatorGUI simulatorGUI;

    public PersonManager(SimulatorGUI simulatorGUI, Simulator simulator) {
        this.simulatorGUI = simulatorGUI;
        this.simulator = simulator;

        //#region Creating and placing drawablePersons
        //calculating the best rows and column configuration for the canvas rateo and the number of Persons that i have
        int nPersons = simulator.getPopulation().size();

        int width = Engine.renderer.getWidth();
        int height = Engine.renderer.getHeight();

        int nx = (int)Math.sqrt(((float)nPersons)*width/height);
        int ny = (int)Math.sqrt(((float)nPersons)*height/width)+1;

        //calculating the world size
        int worldX = nx * 20;
        int worldY = ny * 20;

        //creating the persons
        drawablePersonsDictionary = new HashMap<>();
        drawablePersons = new ArrayList<>();
        int created = 0;
        creationLoop:
        for (int y = 0; y < worldY; y+=20){
            for (int x = 0; x < worldX; x+=20){
                Person person = simulator.getPopulation().get(created);
                DrawablePerson drawablePerson = new DrawablePerson(this, person, x, y);
                drawablePersonsDictionary.put(person, drawablePerson);
                drawablePersons.add(drawablePerson);
                if (++created == nPersons) break creationLoop;
            }
        }
        //#endregion

        //#region Creating and setting the camera script
        //creating the camera movement script
        CameraMove cameraScript = new CameraMove(200);
        //centering the camera
        cameraScript.setPosition(worldX/2f, worldY/2f);
        //setting the camera bounds
        cameraScript.setBound(0,0,worldX,worldY);

        //calculating the maxCameraZoom
        cameraScript.setScales(.2f,30f);

        //setting the ideal zoom for the world size
        //TODO: this sucks, discover why
        Engine.camera.setScale((float)width/(worldX+40));
        //#endregion

        //linking simulator to GUI
        simulator.callBacks.add(simulatorEventListener);

        startANewDay();

    }


    private final SimulatorCallBack simulatorEventListener = new SimulatorCallBack() {
        @Override
        public void personHasSymptoms(Person person) {}

        @Override
        public void personClean(Person person) {}

        @Override
        public void registerEncounter(Person person1, Person person2) {
            drawablePersonsDictionary.get(person1).target.add(drawablePersonsDictionary.get(person2));
        }

        @Override
        public void afterExecuteDay(Simulator.Outcome outcome) {
            lastDayOutCome = outcome;
            simulatorGUI.updateUI(simulator);
        }
    };

    public void doneMoving(DrawablePerson drawablePerson) {
        if(toMovePersons.size()>0){
            moveRandomPerson();
        }else{
            startANewDay();
        }
    }

    private void startANewDay() {
        if (lastDayOutCome != Simulator.Outcome.NOTHING) {
            JOptionPane.showMessageDialog(null, "OUTCOME: " + lastDayOutCome);
            return;
        }
        //UPDATING ALL THE COLORS SO THAT THEY STAY FIXED TILL THE END OF THE NEXT DAY
        drawablePersons.forEach(drawablePerson -> drawablePerson.updateColor());

        //executing all this day simulation
        simulator.executeDay();

        //#region STARTING THE BALL SIMULATION
        toMovePersons = new ArrayList<>(drawablePersons);

        movedPersons = drawablePersons.size()/5;

        //if it's 0 then move them all because they are less than 5
        movedPersons = movedPersons == 0 ? drawablePersons.size()-1 : movedPersons;

        for (int i = 0; i < movedPersons; i++){
            moveRandomPerson();
        }

        //#endregion

    }

    private void moveRandomPerson() {
        int index = Utils.random(toMovePersons.size());
        toMovePersons.get(index).doMove = true;
        toMovePersons.remove(index);
    }

}
