package com.epidemic_simulator.gui.visual;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.SimulatorCallBack;
import com.epidemic_simulator.Utils;
import com.epidemic_simulator.gui.SimulatorSettings;

import dev.federicocapece.jdaze.*;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.Marker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class SimulatorGUI extends JFrame {
    JFrame settingsFrame;

    JLabel dayLabel;

    //#region Visual simulation data
    private Simulator simulator;

    private HashMap<Person, DrawablePerson> drawablePersonsDictionary;
    private ArrayList<DrawablePerson> drawablePersons;
    private ArrayList<DrawablePerson> toMovePersons;
    private int movingPersons;

    private Simulator.Outcome lastDayOutCome = Simulator.Outcome.NOTHING;
    //#endregion

    //#region ProgressBar
    ColoredBar greenBar;
    ColoredBar orangeBar;
    ColoredBar blueBar;
    ColoredBar blackBar;
    ColoredBar resourcesBar;
    //#endregion

    //#region graph
    private XYChart peopleGraphData;
    private XChartPanel<XYChart> peopleGraphPanel;

    private ArrayList<Integer> days;
    private ArrayList<Integer> healthyByDay;
    private ArrayList<Integer> infectedByDay;
    private ArrayList<Integer> immuneByDay;
    private ArrayList<Integer> deadByDay;



    private XYChart resourcesGraphData;
    private XChartPanel<XYChart> resourcesGraphPanel;

    private ArrayList<Long> resourcesByDay;

    //#endregion

    JSlider speedSlider;

    //milliseconds of the last day start, used to fake a sleep in case there are no movements
    private long lastStart = 0;

    private final int MINIMUM_DAY_TIME = 10000;

    public SimulatorGUI(JFrame settingsFrame, Simulator simulator){
        super("Epidemic simulator - Visual Simulator");
        this.settingsFrame = settingsFrame;
        this.simulator = simulator;

        buildGUI();

        //Starting the graphic engine
        Engine.start();

        //#region Creating and placing drawablePersons
        //calculating the best rows and column configuration for the canvas rateo and the number of Persons that i have
        int width = Engine.renderer.getWidth();
        int height = Engine.renderer.getHeight();

        float nx = (float)Math.sqrt(((float)simulator.getPopulation().size())*width/height);
        float ny = (float)Math.sqrt(((float)simulator.getPopulation().size())*height/width)+1;


        //calculating the world size
        float worldX = nx * 20;
        float worldY = ny * 20;

        float worldDiagonal = (float)Math.sqrt((worldX*worldX) + (worldY*worldY));

        //creating the persons
        drawablePersonsDictionary = new HashMap<>();
        drawablePersons = new ArrayList<>();
        int created = 0;
        creationLoop:
        for (int y = 0; y < worldY; y+=20){
            for (int x = 0; x < worldX; x+=20){
                Person person = simulator.getPopulation().get(created);
                DrawablePerson drawablePerson = new DrawablePerson(this, person, x, y, worldDiagonal/4);
                drawablePersonsDictionary.put(person, drawablePerson);
                drawablePersons.add(drawablePerson);
                if (++created == simulator.getPopulation().size()) break creationLoop;
            }
        }
        //#endregion

        //#region Creating and setting the camera script
        //clamping the worldX and Y to 20
        worldX =  (int)(worldX / 20) * 20;
        worldY =  (int)(worldY / 20) * 20;

        //creating the camera movement script
        CameraMove cameraScript = new CameraMove(200);
        //centering the camera
        cameraScript.setPosition(worldX/2f, worldY/2f -10);
        //setting the camera bounds
        cameraScript.setBound(0,0,worldX,worldY);

        //calculating the maxCameraZoom
        cameraScript.setScales(.2f,30f);

        //setting the ideal zoom for the world size
        Engine.camera.setScale((float)height/(worldY+20));
        //#endregion

        //linking simulator callbacks to GUI
        simulator.callBacks.add(simulatorEventListener);

        addWindowListener(windowListener);

        startANewDay();
    }

    private void buildGUI(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width*.95),(int)(screenSize.height*.9));

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        contentPane.add(Engine.renderer, BorderLayout.CENTER);

        //#region North panel
        JPanel northPanel = new JPanel(new BorderLayout());
        contentPane.add(northPanel, BorderLayout.NORTH);

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

        //#endregion

        //#region progressbar
        JPanel progressBarPanel = new JPanel(new GridBagLayout());
        progressBarPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        leftNorthPanel.add(progressBarPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;

        gbc.gridy = 0;
        progressBarPanel.add(new JLabel("Healthy:"), gbc);
        gbc.gridy = 1;
        progressBarPanel.add(new JLabel("Infected:"), gbc);
        gbc.gridy = 2;
        progressBarPanel.add(new JLabel("Immune:"), gbc);
        gbc.gridy = 3;
        progressBarPanel.add(new JLabel("Dead:"), gbc);
        gbc.gridy = 4;
        progressBarPanel.add(new JLabel("Resources: "), gbc);

        //creating progressbar
        greenBar     = new ColoredBar(Color.GREEN, 0, simulator.getPopulation().size());
        orangeBar    = new ColoredBar(Color.ORANGE,0, simulator.getPopulation().size());
        blueBar      = new ColoredBar(Color.BLUE,  0, simulator.getPopulation().size());
        blackBar     = new ColoredBar(Color.BLACK, 0, simulator.getPopulation().size());
        resourcesBar = new ColoredBar(Color.CYAN,  0, (int)(simulator.getResources()/simulator.testPrice));

        //setting values
        greenBar.setValue(simulator.getPopulation().size()-1);
        orangeBar.setValue(1);
        blueBar.setValue(0);
        blackBar.setValue(0);
        resourcesBar.setValue(resourcesBar.getMaximum());

        //adding the bars to the row
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 1;
        gbc.gridy = 0;
        progressBarPanel.add(greenBar, gbc);
        gbc.gridy = 1;
        progressBarPanel.add(orangeBar, gbc);
        gbc.gridy = 2;
        progressBarPanel.add(blueBar, gbc);
        gbc.gridy = 3;
        progressBarPanel.add(blackBar, gbc);
        gbc.gridy = 4;
        progressBarPanel.add(resourcesBar, gbc);

        //#endregion

        //#region graphs

        //#region people graph
        //Create graph
        peopleGraphData = new XYChartBuilder().height(200).width(300).xAxisTitle("Day").theme(Styler.ChartTheme.Matlab).build();

        // style
        peopleGraphData.getStyler().setLegendVisible(false);
        peopleGraphData.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        // Series
        days            = new ArrayList<>(40);

        healthyByDay    = new ArrayList<>(40);
        infectedByDay   = new ArrayList<>(40);
        immuneByDay     = new ArrayList<>(40);
        deadByDay       = new ArrayList<>(40);
        resourcesByDay  = new ArrayList<>(40);

        int[] zero = new int[]{0};

        XYSeries greenSerie  = peopleGraphData.addSeries("Healthy",  zero, zero);
        XYSeries orangeSerie = peopleGraphData.addSeries("Infected", zero, zero);
        XYSeries blueSerie   = peopleGraphData.addSeries("Immunes",  zero, zero);
        XYSeries blackSerie  = peopleGraphData.addSeries("Deads",    zero, zero);

        //setting line colors
        greenSerie.setLineColor(Color.GREEN);
        orangeSerie.setLineColor(Color.ORANGE);
        blueSerie.setLineColor(Color.BLUE);
        blackSerie.setLineColor(Color.BLACK);

        Marker none = new org.knowm.xchart.style.markers.None();

        greenSerie.setMarker(none);
        orangeSerie.setMarker(none);
        blueSerie.setMarker(none);
        blackSerie.setMarker(none);

        greenSerie.setSmooth(true);
        orangeSerie.setSmooth(true);
        blueSerie.setSmooth(true);
        blackSerie.setSmooth(true);

        peopleGraphPanel = new XChartPanel<XYChart>(peopleGraphData);
        leftNorthPanel.add(peopleGraphPanel);
        //#endregion


        resourcesGraphData = new XYChartBuilder().height(200).width(300).xAxisTitle("Day").theme(Styler.ChartTheme.Matlab).build();

        resourcesGraphData.getStyler().setLegendVisible(false);
        resourcesGraphData.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        resourcesGraphData.getStyler().setYAxisMin(0.0);

        XYSeries resourcesSerie  = resourcesGraphData.addSeries("Resources", zero, zero);
        resourcesSerie.setSmooth(true);
        resourcesSerie.setLineColor(Color.CYAN);
        resourcesSerie.setMarker(none);

        resourcesGraphPanel = new XChartPanel<XYChart>(resourcesGraphData);
        leftNorthPanel.add(resourcesGraphPanel);

        //#endregion

        //min = 0.05, max = 3.50, value = 1.00
        speedSlider = new JSlider(JSlider.HORIZONTAL, 5, 3500,100);
        northPanel.add(speedSlider, BorderLayout.SOUTH);

        //#endregion


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
        long timePassed = System.currentTimeMillis() - lastStart;
        long needToSleepTime = (int)(MINIMUM_DAY_TIME /getSpeedMultiplier()) - timePassed;
        if(needToSleepTime>0) {
            try {
                Thread.sleep(needToSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastStart = System.currentTimeMillis();

        //UPDATING ALL THE COLORS SO THAT THEY STAY FIXED TILL THE END OF THE NEXT DAY
        drawablePersons.forEach(DrawablePerson::updateColor);

        //#region progressbar update
        greenBar.setValue(simulator.getHealthy());
        orangeBar.setValue(simulator.getInfected());
        blueBar.setValue(simulator.getImmunes());
        blackBar.setValue(simulator.getDeads());
        resourcesBar.setValue((int)(simulator.getResources()/simulator.testPrice));
        resourcesBar.setString(""+simulator.getResources());
        //#endregion progressbar

        //#region graph update
        days.add(simulator.getDay());
        healthyByDay.add(simulator.getHealthy());
        infectedByDay.add(simulator.getInfected());
        immuneByDay.add(simulator.getImmunes());
        deadByDay.add(simulator.getDeads());
        resourcesByDay.add(simulator.getResources());

        peopleGraphData.updateXYSeries("Healthy",   days, healthyByDay  , null);
        peopleGraphData.updateXYSeries("Infected",  days, infectedByDay , null);
        peopleGraphData.updateXYSeries("Immunes",   days, immuneByDay   , null);
        peopleGraphData.updateXYSeries("Deads",     days, deadByDay     , null);

        resourcesGraphData.updateXYSeries("Resources", days, resourcesByDay, null);

        peopleGraphPanel.revalidate();
        peopleGraphPanel.repaint();

        resourcesGraphPanel.revalidate();
        resourcesGraphPanel.repaint();

        //#endregion

        if (lastDayOutCome != Simulator.Outcome.NOTHING) {
            //TODO: start this in another thread? it would allow balls to change color before the OK is pressed
            JOptionPane.showMessageDialog(null, "OUTCOME: " + lastDayOutCome);
            return;
        }

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


    public float getSpeedMultiplier() {
        //TODO: calculate this on change, don't do this at every function call!
        return speedSlider.getValue() * 0.01f;
        //return 1;
    }

    //fake main that just start a Setting window and call the start button, useful for testing purposes
    public static void main(String[] args) {
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
        setValue(getMinimum());
    }

    @Override
    public void setValue(int n) {
        super.setValue(n);
        setString(""+n);
    }
}