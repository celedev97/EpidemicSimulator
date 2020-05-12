package com.epidemic_simulator.gui.visual;

import com.epidemic_simulator.*;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.util.*;

public class PersonManager {
    private HashMap<Person, DrawablePerson> drawablePersonsDictionary;
    private ArrayList<DrawablePerson> drawablePersons;
    private int lastMovingPersonIndex, currentlyMoving;

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
            //if(count++<2)
            drawablePersonsDictionary.get(person1).target.add(drawablePersonsDictionary.get(person2));
        }

        @Override
        public void afterExecuteDay(Simulator.Outcome outcome) {
            lastDayOutCome = outcome;
            simulatorGUI.updateUI(simulator);
        }
    };

    public void doneMoving(DrawablePerson drawablePerson) {
        currentlyMoving--;//TODO: this is clearly not working, this is reaching 0 even if they did not all finish to move
        lastMovingPersonIndex++;
        if(lastMovingPersonIndex < drawablePersons.size()){
            drawablePersons.get(lastMovingPersonIndex).doMove = true;
            currentlyMoving++;
        }else if(currentlyMoving < 1){
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

        lastMovingPersonIndex = drawablePersons.size()/5;
        //if it's 0 then move them all because they are less than 3
        lastMovingPersonIndex = lastMovingPersonIndex == 0 ? drawablePersons.size()-1 : lastMovingPersonIndex;


        for (int i = 0; i < lastMovingPersonIndex; i++)
            drawablePersons.get(i).doMove = true;

        currentlyMoving = lastMovingPersonIndex;
        //#endregion

    }

}
