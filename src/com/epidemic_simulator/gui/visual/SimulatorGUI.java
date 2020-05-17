package com.epidemic_simulator.gui.visual;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.SimulatorCallBack;
import com.epidemic_simulator.Utils;
import com.epidemic_simulator.gui.SimulatorSettings;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class SimulatorGUI extends JFrame {
    JLabel dayLabel;

    //#region Visual simulation data
    private Simulator simulator;

    private HashMap<Person, DrawablePerson> drawablePersonsDictionary;
    private ArrayList<DrawablePerson> drawablePersons;
    private ArrayList<DrawablePerson> toMovePersons;
    private int movingPersons;

    private Simulator.Outcome lastDayOutCome = Simulator.Outcome.NOTHING;
    //#endregion

    //#region
    JProgressBar greenBar;
    JProgressBar orangeBar;
    JProgressBar blueBar;
    JProgressBar blackBar;
    JProgressBar resourcesBar;
    //#endregion

    JFrame settingsFrame;

    public SimulatorGUI(JFrame settingsFrame, Simulator simulator){
        super("Epidemic simulator - Visual Simulator");
        this.settingsFrame = settingsFrame;
        this.simulator = simulator;
        int populationSize = simulator.getPopulation().size();

        //#region Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width*.95),(int)(screenSize.height*.9));

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        contentPane.add(Engine.renderer, BorderLayout.CENTER);

        //#region North panel
        JPanel northPanel = new JPanel(new BorderLayout());

        JPanel leftNorthPanel = new JPanel();
        northPanel.add(leftNorthPanel, BorderLayout.WEST);

        //#region first row
        JPanel firstRow = new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.Y_AXIS));
        leftNorthPanel.add(firstRow);

        //day panel
        JPanel dayFlow = new JPanel();
        firstRow.add(dayFlow);

        dayFlow.add(new JLabel("DAY: "));
        dayLabel = new JLabel("0");
        dayFlow.add(dayLabel);

        //data chart TODO: ADD IT

        //#endregion

        //#region second row
        JPanel secondRow = new JPanel(new GridLayout(5,2));
        leftNorthPanel.add(secondRow);

        //#region progressbar
        //creating progressbar
        greenBar     = new ColoredBar(Color.GREEN, 0, populationSize);
        orangeBar    = new ColoredBar(Color.ORANGE,0, populationSize);
        blueBar      = new ColoredBar(Color.BLUE,  0, populationSize);
        blackBar     = new ColoredBar(Color.BLACK, 0, populationSize);
        resourcesBar = new ColoredBar(Color.CYAN,  0, (int)(simulator.getResources()/simulator.getTestPrice()));

        //setting values
        greenBar.setValue(populationSize-1);
        orangeBar.setValue(1);
        blueBar.setValue(0);
        blackBar.setValue(0);
        resourcesBar.setValue(resourcesBar.getMaximum());

        //creating panels for the bars
        secondRow.add(new JLabel("Healthy:"));
        secondRow.add(greenBar);
        secondRow.add(new JLabel("Infected:"));
        secondRow.add(orangeBar);
        secondRow.add(new JLabel("Immune:"));
        secondRow.add(blueBar);
        secondRow.add(new JLabel("Dead:"));
        secondRow.add(blackBar);
        secondRow.add(new JLabel("Resources:"));
        secondRow.add(resourcesBar);

        //#endregion progressbar

        //#endregion


        contentPane.add(northPanel, BorderLayout.NORTH);


        //#endregion
        //#endregion frame

        //Starting the graphic engine
        Engine.start();
        
        //#region Creating and placing drawablePersons
        //calculating the best rows and column configuration for the canvas rateo and the number of Persons that i have
        int width = Engine.renderer.getWidth();
        int height = Engine.renderer.getHeight();

        int nx = (int)Math.sqrt(((float)populationSize)*width/height);
        int ny = (int)Math.sqrt(((float)populationSize)*height/width)+1;

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
                if (++created == populationSize) break creationLoop;
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

        //linking simulator callbacks to GUI
        simulator.callBacks.add(simulatorEventListener);

        addWindowListener(windowListener);

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
            dayLabel.setText(""+simulator.getDay());
            //resourceBar.setValue(simulator.getResources());
        }
    };

    public void doneMoving(DrawablePerson drawablePerson) {
        movingPersons--;
        //if there are still people that need to move
        if(toMovePersons.size() != 0){
            moveRandomPerson();
            movingPersons++;
        }
        //else if all the people did already move
        if(movingPersons == 0){
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

        movingPersons = drawablePersons.size()/5;

        //if it's 0 then move them all because they are less than 5
        movingPersons = movingPersons == 0 ? drawablePersons.size()-1 : movingPersons;

        for (int i = 0; i < movingPersons; i++){
            moveRandomPerson();
        }

        //#endregion

    }

    private void moveRandomPerson() {
        int index = Utils.random(toMovePersons.size());
        toMovePersons.get(index).doMove = true;
        toMovePersons.remove(index);
    }


    private WindowAdapter windowListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
           Engine.stop();
           settingsFrame.setVisible(true);
           simulator.dispose();
        }
    };


    public static void main(String[] args) {
        //fake main that just start a Setting window and call the start button
        SimulatorSettings settings = new SimulatorSettings();
        settings.startGUIButtonListener.actionPerformed(null);
    }
}

class ColoredBar extends JProgressBar{

    public ColoredBar(Color color, int min, int max) {
        super(min, max);
        setForeground(color);
        setBackground(Color.WHITE);
        setStringPainted(true);
        setMinimumSize(new Dimension(150,50));
    }

    @Override
    public void setValue(int n) {
        super.setValue(n);
    }
}