package com.epidemic_simulator.gui;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.SimulatorCallBack;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class SimulatorGUI extends JFrame {

    Simulator simulator;
    private HashMap<Person, DrawablePerson> drawablePersons;
    private HashMap<DrawablePerson, Boolean> movingPersons;

    public SimulatorGUI(Simulator simulator) throws NoSuchMethodException {
        this.simulator = simulator;

        //creating the Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width*.95),(int)(screenSize.height*.9));

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        contentPane.add(Engine.renderer, BorderLayout.CENTER);

        //forcing component draw so i can get the canvas size
        setVisible(true);
        revalidate();
        repaint();

        //Starting the graphic engine
        Engine.start();

        //calculating the best rows and column configuration for the canvas rateo and the number of Persons that i have
        int nPersons = simulator.population.size();

        int width = Engine.renderer.getWidth();
        int height = Engine.renderer.getHeight();

        int nx = (int)Math.sqrt(((float)nPersons)*width/height);
        int ny = (int)Math.sqrt(((float)nPersons)*height/width)+1;

        //calculating the world size
        int worldX = nx * 20;
        int worldY = ny * 20;

        //creating the persons
        drawablePersons = new HashMap<>();
        int created = 0;
        creationLoop:
        for (int y = 0; y < worldY; y+=20){
            for (int x = 0; x < worldX; x+=20){
                Person person = simulator.population.get(created);
                drawablePersons.put(person, new DrawablePerson(person, x, y));
                if (++created == nPersons) break creationLoop;
            }
        }

        //creating the camera movement script
        CameraMove cameraScript = new CameraMove(200);
        //centering the camera
        cameraScript.setPosition(worldX/2f, worldY/2f);
        //setting the camera bounds
        cameraScript.setBound(0,0,worldX,worldY);

        //calculating the maxCameraZoom
        cameraScript.setScales(.2f,30f);

        //setting manual ideal zoom
        Engine.camera.setScale((float)width/(worldX+40));

        //linking simulator to GUI
        simulator.callBacks.add(simulatorEventListener);

        //starting simulator
        simulator.executeDay();

    }

    public static void main(String[] args) {
        //fake main that just start a Setting window and call the start button
        SimulatorSettings settings = new SimulatorSettings();
        settings.startButtonListener.actionPerformed(null);
    }

    private final SimulatorCallBack simulatorEventListener = new SimulatorCallBack() {
        int count = 0;
        @Override
        public void personHasSymptoms(Person person) {}

        @Override
        public void personClean(Person person) {}

        @Override
        public void registerEncounter(Person person1, Person person2) {
            //if(count++<2)
                drawablePersons.get(person1).target.add(drawablePersons.get(person2));
        }

        @Override
        public void afterExecuteDay(Simulator.Outcome outcome) {
            if(outcome != Simulator.Outcome.NOTHING){
                System.out.println("OUTCOME: " + outcome);
                return;
            }
            //TODO: WAIT TILL EVERY DRAWINGPERSON IS NOT MOVING, THEN CALL EXECUTEDAY
            boolean moving = true;
            whileLabel:
            do{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                moving = false;
                for(DrawablePerson person : drawablePersons.values()){
                    if(person.moving){
                        moving = true;
                        break;
                    }
                }
            }while (moving);
            simulator.executeDay();
        }

    };

}