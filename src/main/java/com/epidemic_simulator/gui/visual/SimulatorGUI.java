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
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * The type Simulator gui.
 */
public class SimulatorGUI extends JFrame {

    private JFrame settingsFrame;

    private JLabel dayLabel;

    //#region Visual simulation data
    private Simulator simulator;

    private HashMap<Person, DrawablePerson> drawablePersonsDictionary;
    private ArrayList<DrawablePerson> drawablePersons;
    private ArrayList<DrawablePerson> toMovePersons;
    private int movingPersons;

    private Simulator.Outcome lastDayOutCome = Simulator.Outcome.NOTHING;
    //#endregion

    //#region ProgressBar
    private ColoredBar greenBar;
    private ColoredBar orangeBar;
    private ColoredBar blueBar;
    private ColoredBar blackBar;
    private ColoredBar resourcesBar;
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

    private JSlider speedSlider;
    private float speedSliderValue;
    private long dayMinimumDuration;

    //milliseconds of the last day start, used to fake a sleep in case there are no movements
    private long lastStart = 0;
    private boolean doSleep = false;

    private final int MINIMUM_DAY_TIME = 10000;

    /**
     * Instantiates a new Simulator GUI.
     *
     * @param settingsFrame The settings frame that instantiated this SimulatorGUI
     * @param simulator     The Simulator that will be used for this simulation.
     */
    public SimulatorGUI(JFrame settingsFrame, Simulator simulator) {
        super("Epidemic simulator - Visual Simulator");
        this.settingsFrame = settingsFrame;
        this.simulator = simulator;

        buildGUI();

        speedSlider.addChangeListener(changeSpeedListener);
        changeSpeedListener.stateChanged(new ChangeEvent(speedSlider));

        //Starting the graphic engine
        Engine.start();

        //#region Creating and placing drawablePersons
        //calculating the best rows and column configuration for the canvas rateo and the number of Persons that i have
        int width = Engine.renderer.getWidth();
        int height = Engine.renderer.getHeight();

        float nx = (float) Math.sqrt(((float) simulator.getPopulation().size()) * width / height);
        float ny = (float) Math.sqrt(((float) simulator.getPopulation().size()) * height / width) + 1;


        //calculating the world size
        float worldX = nx * 20;
        float worldY = ny * 20;

        float worldDiagonal = (float) Math.sqrt((worldX * worldX) + (worldY * worldY));

        //creating the persons
        drawablePersonsDictionary = new HashMap<>();
        drawablePersons = new ArrayList<>();
        int created = 0;
        creationLoop:
        for (int y = 0; y < worldY; y += 20) {
            for (int x = 0; x < worldX; x += 20) {
                Person person = simulator.getPopulation().get(created);
                DrawablePerson drawablePerson = new DrawablePerson(this, person, x, y, worldDiagonal / 4);
                drawablePersonsDictionary.put(person, drawablePerson);
                drawablePersons.add(drawablePerson);
                if (++created == simulator.getPopulation().size()) break creationLoop;
            }
        }
        //#endregion

        //#region Creating and setting the camera script
        //clamping the worldX and Y to 20
        worldX = (int) (worldX / 20) * 20;
        worldY = (int) (worldY / 20) * 20;

        //creating the camera movement script
        CameraMove cameraScript = new CameraMove(350);
        //centering the camera
        cameraScript.setPosition(worldX / 2f, worldY / 2f - 10);
        //setting the camera bounds
        cameraScript.setBound(0, 0, worldX, worldY);

        //calculating the maxCameraZoom
        cameraScript.setScales(.2f, 30f);

        //setting the ideal zoom for the world size
        Engine.camera.setScale((float) height / (worldY + 20));
        //#endregion

        //linking simulator callbacks to GUI
        simulator.addCallBack(simulatorEventListener);

        addWindowListener(windowListener);

        startANewDay();
    }

    private void buildGUI() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) (screenSize.width * .95), (int) (screenSize.height * .9));

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        contentPane.add(Engine.renderer, BorderLayout.CENTER);

        //#region Data panel
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.Y_AXIS));
        dataPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new MatteBorder(0,0,0,1, Color.BLACK),
                        new EmptyBorder(5, 5, 5, 5)
                )
        );
        contentPane.add(dataPanel, BorderLayout.WEST);

        //#region first row
        JPanel dayAndBarBox = new JPanel();
        dayAndBarBox.setLayout(new BoxLayout(dayAndBarBox, BoxLayout.Y_AXIS));
        dataPanel.add(dayAndBarBox);

        //day panel
        JPanel dayFlow = new JPanel();
        dayAndBarBox.add(dayFlow);

        JLabel dayType = new JLabel("DAY ");
        dayFlow.add(dayType);
        dayType.setFont(dayType.getFont().deriveFont(30.0f));
        dayLabel = new JLabel("0");
        dayFlow.add(dayLabel);
        dayLabel.setFont(dayLabel.getFont().deriveFont(30.0f));

        //#region progressbar
        JPanel progressBarPanel = new JPanel(new GridBagLayout());
        progressBarPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        dayAndBarBox.add(progressBarPanel);

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
        greenBar = new ColoredBar(Color.GREEN, 0, simulator.getPopulation().size());
        orangeBar = new ColoredBar(Color.ORANGE, 0, simulator.getPopulation().size());
        blueBar = new ColoredBar(Color.BLUE, 0, simulator.getPopulation().size());
        blackBar = new ColoredBar(Color.BLACK, 0, simulator.getPopulation().size());
        resourcesBar = new ColoredBar(Color.CYAN, 0, (int) (simulator.getResources() / simulator.testPrice));

        //setting values
        greenBar.setValue(simulator.getPopulation().size() - 1);
        orangeBar.setValue(1);
        blueBar.setValue(0);
        blackBar.setValue(0);
        resourcesBar.setValue(resourcesBar.getMaximum());

        //adding the bars to the row
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 150;
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

        //#endregion

        //#region graphs

        //#region people graph
        //Create graph
        peopleGraphData = new XYChartBuilder().height(200).width(300).xAxisTitle("Day").theme(Styler.ChartTheme.Matlab).build();

        // style
        peopleGraphData.getStyler().setLegendVisible(false);
        peopleGraphData.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        // Series
        days = new ArrayList<>(40);

        healthyByDay = new ArrayList<>(40);
        infectedByDay = new ArrayList<>(40);
        immuneByDay = new ArrayList<>(40);
        deadByDay = new ArrayList<>(40);
        resourcesByDay = new ArrayList<>(40);

        int[] zero = new int[]{0};

        XYSeries greenSerie = peopleGraphData.addSeries("Healthy", zero, zero);
        XYSeries orangeSerie = peopleGraphData.addSeries("Infected", zero, zero);
        XYSeries blueSerie = peopleGraphData.addSeries("Immunes", zero, zero);
        XYSeries blackSerie = peopleGraphData.addSeries("Deads", zero, zero);

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
        dataPanel.add(peopleGraphPanel);
        //#endregion


        resourcesGraphData = new XYChartBuilder().height(200).width(300).xAxisTitle("Day").theme(Styler.ChartTheme.Matlab).build();

        resourcesGraphData.getStyler().setLegendVisible(false);
        resourcesGraphData.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        resourcesGraphData.getStyler().setYAxisMin(0.0);

        XYSeries resourcesSerie = resourcesGraphData.addSeries("Resources", zero, zero);
        resourcesSerie.setSmooth(true);
        resourcesSerie.setLineColor(Color.CYAN);
        resourcesSerie.setMarker(none);

        resourcesGraphPanel = new XChartPanel<XYChart>(resourcesGraphData);
        dataPanel.add(resourcesGraphPanel);

        //#endregion

        //#regionparameters

        JPanel parametersPanel = new JPanel(new GridBagLayout());
        parametersPanel.setBorder(BorderFactory.createTitledBorder("Starting parameters"));
        dataPanel.add(parametersPanel);

        GridBagConstraints gbcPar = new GridBagConstraints();
        gbcPar.anchor = GridBagConstraints.WEST;
        gbcPar.ipadx = 10;
        gbcPar.ipady = 10;

        gbcPar.gridx = 0;

        gbcPar.gridy = 0;
        parametersPanel.add(new JLabel("Population:"), gbcPar);
        gbcPar.gridy = 1;
        parametersPanel.add(new JLabel("Resources:"), gbcPar);
        gbcPar.gridy = 2;
        parametersPanel.add(new JLabel("Test price:"), gbcPar);
        gbcPar.gridy = 3;
        parametersPanel.add(new JLabel("Encounters per day:  "), gbcPar);

        gbcPar.anchor = GridBagConstraints.EAST;
        gbcPar.ipadx = 40;

        gbcPar.gridx = 1;
        gbcPar.gridy = 0;
        parametersPanel.add(new JLabel(Integer.toString(simulator.getPopulation().size())), gbcPar);
        gbcPar.gridy = 1;
        parametersPanel.add(new JLabel(Long.toString(simulator.initialResources)), gbcPar);
        gbcPar.gridy = 2;
        parametersPanel.add(new JLabel(Integer.toString(simulator.testPrice)), gbcPar);
        gbcPar.gridy = 3;
        parametersPanel.add(new JLabel(Double.toString(simulator.averageEncountersPerDay)), gbcPar);

        gbcPar.anchor = GridBagConstraints.WEST;
        gbcPar.ipadx = 10;

        gbcPar.gridx = 2;
        gbcPar.gridy = 0;
        parametersPanel.add(new JLabel("Infectivity:"), gbcPar);
        gbcPar.gridy = 1;
        parametersPanel.add(new JLabel("Symptomaticity:"), gbcPar);
        gbcPar.gridy = 2;
        parametersPanel.add(new JLabel("Lethality:"), gbcPar);
        gbcPar.gridy = 3;
        parametersPanel.add(new JLabel("Disease duration:  "), gbcPar);

        gbcPar.anchor = GridBagConstraints.EAST;
        gbcPar.gridx = 3;

        gbcPar.gridy = 0;
        parametersPanel.add(new JLabel(Integer.toString(simulator.infectionRate)), gbcPar);
        gbcPar.gridy = 1;
        parametersPanel.add(new JLabel(Long.toString(simulator.symptomsRate)), gbcPar);
        gbcPar.gridy = 2;
        parametersPanel.add(new JLabel(Integer.toString(simulator.deathRate)), gbcPar);
        gbcPar.gridy = 3;
        parametersPanel.add(new JLabel(Double.toString(simulator.diseaseDuration)), gbcPar);

        gbcPar.anchor = GridBagConstraints.WEST;
        gbcPar.gridwidth = 4;

        gbcPar.gridx = 0;
        gbcPar.gridy = 4;
        parametersPanel.add((new JLabel("Strategy used: " + Utils.getStrategyName(simulator.getStrategy()))), gbcPar);

        //#endregion

        JPanel sliderBox = new JPanel();
        sliderBox.setLayout(new BoxLayout(sliderBox, BoxLayout.Y_AXIS));

        JLabel sliderLabel = new JLabel("Simulator speed", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sliderBox.add(sliderLabel);

        //min = 0.05, max = 3.50, value = 1.00
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 2500, 100);
        sliderBox.add(speedSlider);
        speedSlider.setMajorTickSpacing(698);
        speedSlider.setMinorTickSpacing(349);
        speedSlider.setPaintTicks(true);
        sliderBox.setBorder(BorderFactory.createEmptyBorder(80, 0, 0, 0));

        Hashtable labelTable = new Hashtable();
        labelTable.put(0, new JLabel("0,00x"));
        labelTable.put(2500, new JLabel("2.50x"));
        speedSlider.setLabelTable(labelTable);
        speedSlider.setPaintLabels(true);

        dataPanel.add(sliderBox);

        //#endregion


    }

    private final ChangeListener changeSpeedListener = l -> {
        speedSliderValue = speedSlider.getValue() * 0.01f;
        dayMinimumDuration = (int) (MINIMUM_DAY_TIME / getSpeedMultiplier());
    };

    private final SimulatorCallBack simulatorEventListener = new SimulatorCallBack() {
        @Override
        public void personHasSymptoms(Person person) {
        }

        @Override
        public void personClean(Person person) {
        }

        @Override
        public void registerEncounter(Person person1, Person person2) {
            drawablePersonsDictionary.get(person1).target.add(drawablePersonsDictionary.get(person2));
            doSleep = false;
        }

        @Override
        public void afterExecuteDay(Simulator.Outcome outcome) {
            lastDayOutCome = outcome;

            dayLabel.setText("" + simulator.getDay());
            //resourceBar.setValue(simulator.getResources());
        }
    };

    /**
     * This method it's called by {@link DrawablePerson}s
     * to signal to this class that they already met all their targets and that they're back to their starting position
     *
     * @param drawablePerson the drawable person
     */
    protected void doneMoving(DrawablePerson drawablePerson) {
        movingPersons--;
        //if there are still people that need to move
        if (toMovePersons.size() != 0) {
            moveRandomPerson();
            movingPersons++;
        }
        //else if all the people did already move
        if (movingPersons == 0) {
            startANewDay();
        }
    }

    private void startANewDay() {
        if (doSleep) {
            long timePassed = System.currentTimeMillis() - lastStart;
            while (dayMinimumDuration > timePassed) {
                try {
                    Thread.sleep(10);
                    timePassed += 10;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        lastStart = System.currentTimeMillis();

        //UPDATING ALL THE COLORS SO THAT THEY STAY FIXED TILL THE END OF THE NEXT DAY
        drawablePersons.forEach(DrawablePerson::updateColor);

        //#region progressbar update
        greenBar.setValue(simulator.getHealthy());
        orangeBar.setValue(simulator.getInfected());
        blueBar.setValue(simulator.getBlueCount());
        blackBar.setValue(simulator.getBlackCount());
        resourcesBar.setValue((int) (simulator.getResources() / simulator.testPrice));
        resourcesBar.setString("" + simulator.getResources());
        //#endregion progressbar

        //#region graph update
        days.add(simulator.getDay());
        healthyByDay.add(simulator.getHealthy());
        infectedByDay.add(simulator.getInfected());
        immuneByDay.add(simulator.getBlueCount());
        deadByDay.add(simulator.getBlackCount());
        resourcesByDay.add(simulator.getResources());

        peopleGraphData.updateXYSeries("Healthy", days, healthyByDay, null);
        peopleGraphData.updateXYSeries("Infected", days, infectedByDay, null);
        peopleGraphData.updateXYSeries("Immunes", days, immuneByDay, null);
        peopleGraphData.updateXYSeries("Deads", days, deadByDay, null);

        resourcesGraphData.updateXYSeries("Resources", days, resourcesByDay, null);

        peopleGraphPanel.revalidate();
        peopleGraphPanel.repaint();

        resourcesGraphPanel.revalidate();
        resourcesGraphPanel.repaint();

        //#endregion

        if (lastDayOutCome != Simulator.Outcome.NOTHING) {
            SwingUtilities.invokeLater(() ->{
                JOptionPane.showMessageDialog(null, "OUTCOME: " + lastDayOutCome);
            });
            return;
        }

        doSleep = true;

        //executing all this day simulation
        simulator.executeDay();

        //#region STARTING THE BALL SIMULATION
        toMovePersons = new ArrayList<>(drawablePersons);

        movingPersons = drawablePersons.size() / 5;

        //if it's 0 then move them all because they are less than 5
        movingPersons = movingPersons == 0 ? drawablePersons.size() - 1 : movingPersons;

        for (int i = 0; i < movingPersons; i++) {
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


    /**
     * Get the speed multiplier float from the JSlider using for regulating the speed.
     *
     * @return the speed multiplier
     */
    protected float getSpeedMultiplier() {
        return speedSliderValue;
    }

    /**
     * A main used just for testing purposes.<BR>
     * It just start a Setting window and call the start button for the visual simulator.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SimulatorSettings settings = new SimulatorSettings();
        settings.startGUIButtonListener.actionPerformed(null);
    }

}

/**
 * A colored progress bar.<BR>
 * It is not really different from a {@link JProgressBar} and it's used just to simplify the code.
 */
class ColoredBar extends JProgressBar {
    /**
     * Instantiates a new Colored bar.
     *
     * @param color the color
     * @param min   the min
     * @param max   the max
     */
    public ColoredBar(Color color, int min, int max) {
        super(min, max);
        setForeground(color);
        setBackground(Color.WHITE);
        setStringPainted(true);
        setMinimumSize(new Dimension(150, 50));
        setValue(getMinimum());
    }

    @Override
    public void setValue(int n) {
        super.setValue(n);
        setString("" + n);
    }
}