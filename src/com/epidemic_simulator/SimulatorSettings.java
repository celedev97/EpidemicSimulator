package com.epidemic_simulator;

import jdk.jshell.spi.ExecutionControl;


//GUI IMPORTS
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

//TODO: I DON'T EVEN REMEMBER WHAT I USE THOSE FOR
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//XML IMPORTS
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class SimulatorSettings extends JFrame {
    private final int PARAMETERS_PER_ROW = 4;

    private static final String CONF_EXTENSION = "simconf";
    private static final String DEFAULT_CONF_FILE = "./configuration."+CONF_EXTENSION;

    //constants for XML
    private static final String XML_ROOT                = "simulator";

    private static final String XML_STATE               = "state";
    private static final String XML_POPULATION          = "population";
    private static final String XML_RESOURCES           = "resources";
    private static final String XML_TEST_PRICE          = "test_price";
    private static final String XML_ENCOUNTERS_PER_DAY  = "encounters_per_day";

    private static final String XML_DISEASE             = "disease";
    private static final String XML_INFECTIVITY         = "infectivity";
    private static final String XML_SYMPTOMATICITY      = "symptomaticity";
    private static final String XML_LETHALITY           = "lethality";
    private static final String XML_DURATION            = "duration";


    //#region class fields

    //#region state
    private JSpinner population;
    private JSpinner resources;
    private JSpinner testPrice;
    private JSpinner encountersPerDay;
    //#endregion

    //#region disease
    private JSpinner infectivity;
    private JSpinner symptomaticity;
    private JSpinner lethality;
    private JSpinner duration;
    //#endregion

    //#region strategy
    private JComboBox strategyCombobox;

    private JPanel[] strategyParametersColumns;
    private ArrayList<JComponent> strategyParameters;

    private JPanel strategyPanel;
    //#endregion
    //dialogue
    private JFileChooser fileChooser;

    //#endregion


    public SimulatorSettings(){
        //#region JFrame setup
        //title
        setTitle("Epidemic simulator - Settings");

        //size ()
        Dimension windowSize = new Dimension(600, 450);
        //TODO: change window size at the end of the project, accordingly to the most complex strategy
        setSize(windowSize);
        setMinimumSize(windowSize);

        //closeButton
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2));
        //#endregion

        //#region UI Building
        //creating main panel
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        //Panel containing all the north panels
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel,BoxLayout.Y_AXIS));
        contentPane.add(northPanel, BorderLayout.NORTH);

        //#region Menu
        FlowLayout menuFlowLayout = new FlowLayout(FlowLayout.LEFT);
        menuFlowLayout.setVgap(0);
        menuFlowLayout.setHgap(0);
        JPanel menuContainerPanel = new JPanel(menuFlowLayout);

        JMenuBar menu = new JMenuBar();
        menuContainerPanel.add(menu);
        northPanel.add(menuContainerPanel);

        //>file>
        JMenu file = new JMenu("File");
        menu.add(file);

        //>file>open
        JMenuItem openButton = new JMenuItem("Open");
        file.add(openButton);

        //>file>save
        JMenuItem saveButton = new JMenuItem("Save");
        file.add(saveButton);

        //>file>-------
        file.addSeparator();

        //>file>default
        JMenuItem defaultButton = new JMenuItem("Load default");
        file.add(defaultButton);

        //>file>-------
        file.addSeparator();

        //>file>quit
        JMenuItem quitButton = new JMenuItem("Quit");
        file.add(quitButton);
        //#endregion

        //#region State Data Panel
        GridLayout stateDataGridLayout = new GridLayout(2,4);
        stateDataGridLayout.setHgap(10);
        JPanel stateDataPanel = new JPanel(stateDataGridLayout);
        stateDataPanel.setBorder(BorderFactory.createTitledBorder("State data"));
        northPanel.add(stateDataPanel);

        //adding labels
        stateDataPanel.add(new JLabel("Population (P):"));
        stateDataPanel.add(new JLabel("Resources (R):"));
        stateDataPanel.add(new JLabel("Test price (C):"));
        stateDataPanel.add(new JLabel("Encounters per day (V):"));

        //adding spinners
        population          = new JSpinner();
        resources           = new JSpinner();
        testPrice           = new JSpinner();
        encountersPerDay    = new JSpinner();

        stateDataPanel.add(population);
        stateDataPanel.add(resources);
        stateDataPanel.add(testPrice);
        stateDataPanel.add(encountersPerDay);
        //#endregion

        //#region Disease Data Panel
        GridLayout diseaseDataGridLayout = new GridLayout(2,4);
        diseaseDataGridLayout.setHgap(10);
        JPanel diseaseDataPanel = new JPanel(diseaseDataGridLayout);
        diseaseDataPanel.setBorder(BorderFactory.createTitledBorder("Disease data"));
        northPanel.add(diseaseDataPanel);

        //adding labels
        JLabel infectivityLabel = new JLabel("Infectivity (I):");
        infectivityLabel.setToolTipText("The percentage of possibility that an infected person could infect another one when they meet");
        diseaseDataPanel.add(infectivityLabel);

        JLabel symptomaticityLabel = new JLabel("Symptomaticity (S):");
        symptomaticityLabel.setToolTipText("The percentage of possibility that an infected person could develop symptoms");
        diseaseDataPanel.add(symptomaticityLabel);

        JLabel lethalityLabel = new JLabel("Lethality (L):");
        lethalityLabel.setToolTipText("The percentage of possibility that a person with symptoms could die");
        diseaseDataPanel.add(lethalityLabel);

        JLabel durationLabel = new JLabel("Duration (D):");
        lethalityLabel.setToolTipText("The numbers of days that the disease will last in a person body");
        diseaseDataPanel.add(durationLabel);

        //adding spinners
        infectivity     = new JSpinner();
        symptomaticity  = new JSpinner();
        lethality       = new JSpinner();
        duration        = new JSpinner();

        diseaseDataPanel.add(infectivity);
        diseaseDataPanel.add(symptomaticity);
        diseaseDataPanel.add(lethality);
        diseaseDataPanel.add(duration);
        //#endregion

        //#region Strategy Selector Panel
        JPanel strategySelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(strategySelectorPanel);

        strategySelectorPanel.add(new JLabel("Strategy:  "));
        strategyCombobox = new JComboBox();
        strategySelectorPanel.add(strategyCombobox);

        //#region strategies combobox population
        strategyParameters = new ArrayList<>();

        String packageName = "strategies";
        strategyCombobox.addItem(new SelectableStrategy(null));
        try{
            for(Class clas : Utils.getClassesForPackage(packageName)){
                strategyCombobox.addItem(new SelectableStrategy(clas));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"There was an error while trying to get the list of the strategies.", "Error",JOptionPane.ERROR_MESSAGE);
        }

        //#endregion


        //#endregion

        //#region Strategy Parameters Panel
        GridLayout strategyDataGridLayout = new GridLayout(2,4);
        strategyDataGridLayout.setHgap(10);//TODO: FIND OUT WHY THIS IS NOT WORKING!!!
        strategyPanel = new JPanel(strategyDataGridLayout);
        northPanel.add(strategyPanel);

        //#region strategy dynamic GUI preparation
        strategyPanel.setLayout(new GridLayout(1,PARAMETERS_PER_ROW));
        strategyParametersColumns = new JPanel[PARAMETERS_PER_ROW];
        for (int x = 0; x < PARAMETERS_PER_ROW; x++) {
            strategyParametersColumns[x] = new JPanel();
            strategyParametersColumns[x].setLayout(new BoxLayout(strategyParametersColumns[x], BoxLayout.Y_AXIS));

            //i put the BoxLayout inside the north area of a BorderLayout, so it doesn't expand towards bottom filling the whole grid cell
            JPanel borderLayoutContainer = new JPanel(new BorderLayout());
            borderLayoutContainer.add(strategyParametersColumns[x], BorderLayout.NORTH);
            strategyPanel.add(borderLayoutContainer);
        }
        //#endregion

        //#endregion

        JButton startButton = new JButton("Start Simulation");
        contentPane.add(startButton, BorderLayout.SOUTH);

        //#endregion

        //#region UI event binding

        //Window event binding
        addWindowListener(windowAdapter);

        //Menu binding
        openButton.addActionListener(openButtonListener);
        saveButton.addActionListener(saveButtonListener);
        defaultButton.addActionListener(e -> setDefaultParameters());
        quitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        //Strategy selector binding
        strategyCombobox.addActionListener(strategyComboboxListener);

        //Start Button binding
        startButton.addActionListener(startButtonListener);

        //#endregion

        //Show JFrame
        setVisible(true);

        //file load and save
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Simulator config", CONF_EXTENSION));

        //setting default parameters
        setDefaultParameters();

        //try to read configuration file if present
        readConfig(new File(DEFAULT_CONF_FILE));

    }


    //#region event listeners

    public final ActionListener startButtonListener = e -> {
        try {
            //creating simulator
            Simulator simulator = new Simulator((int)population.getValue(), (int)resources.getValue(), (int)testPrice.getValue(), (int)encountersPerDay.getValue(), (int)infectivity.getValue(), (int)symptomaticity.getValue(), (int)lethality.getValue(), (int)duration.getValue());

            //fetching parameters from JSpinners
            List<Object> parametersList  = strategyParameters.stream().map(par -> {return ((JSpinner)par).getValue();}).collect(Collectors.toList());
            //adding the simulator as the first parameter
            parametersList.add(0,simulator);

            //creating strategy
            Class choosenStrategy = ((com.epidemic_simulator.SelectableStrategy)strategyCombobox.getSelectedItem()).getValue();
            if(choosenStrategy != null)
                choosenStrategy.getConstructors()[0].newInstance(parametersList.toArray());

            //creating simulator GUI and closing configurator
            new SimulatorGUI(simulator,1000);
            this.windowAdapter.windowClosing(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    };

    private ActionListener strategyComboboxListener = e -> {
        try {
            //clearing old parameters
            strategyParameters.clear();
            for(JPanel panel : strategyParametersColumns){
                panel.removeAll();
            }

            strategyPanel.setBorder(null);

            Class strategyClass = ((SelectableStrategy)((JComboBox)e.getSource()).getSelectedItem()).getValue();
            if(strategyClass != null){
                strategyPanel.setBorder(BorderFactory.createTitledBorder("Strategy parameters"));
                Parameter[] parameters = strategyClass.getDeclaredConstructors()[0].getParameters();
                //populating the parameters columns with the data from parameters
                //(SKIPPING THE FIRST PARAMETER, THAT'S THE SIMULATOR)
                for (int i = 1; i < parameters.length; i++) {
                    //getting parameter data
                    Class type = parameters[i].getType();
                    String parameterName = Utils.javaNameToUserString(parameters[i].getName());

                    //calculating y and x for this parameter
                    int x = (i-1) % PARAMETERS_PER_ROW;

                    //creating the label
                    JLabel label = new JLabel(parameterName);
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    strategyParametersColumns[x].add(label);

                    //generating the component
                    JComponent generatedComponent = null;
                    if(type == int.class){
                        generatedComponent = new JSpinner();
                        generatedComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
                        strategyParametersColumns[x].add(generatedComponent);
                    }else{
                        throw new ExecutionControl.NotImplementedException("PARAMETER TYPE: "+type.toString()+" NOT SUPPORTED!!!");
                    }
                    strategyParameters.add(generatedComponent);
                }
            }

        } catch (ExecutionControl.NotImplementedException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

        //force redraw of the strategy panel
        strategyPanel.revalidate();
        strategyPanel.repaint();
    };

    private ActionListener openButtonListener = e -> {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            readConfig(fileChooser.getSelectedFile());
        }
    };

    private ActionListener saveButtonListener = e -> {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            writeConfig(fileChooser.getSelectedFile());
        }
    };

    private WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            writeConfig(new File(DEFAULT_CONF_FILE));
            ((JFrame)e.getSource()).dispose();
        }
    };
    //#endregion

    public void forceJSpinnerCommit() {
        forceJSpinnerCommit(this);
    }

    public void forceJSpinnerCommit(Container container) {
        for (Component component : container.getComponents()) {
            if(component instanceof JSpinner) {
                try {
                    ((JSpinner) component).commitEdit();
                } catch (ParseException e) {
                    //TODO: investigate on this!!!
                    e.printStackTrace();
                }
            }else if (component instanceof Container){
                forceJSpinnerCommit((Container) component);
            }
        }
    }


    private void setDefaultParameters() {
        population.setValue(1000);
        resources.setValue(20000);
        testPrice.setValue(100);
        encountersPerDay.setValue(3);

        infectivity.setValue(50);
        symptomaticity.setValue(50);
        lethality.setValue(50);
        duration.setValue(40);
    }

    private void readConfig(File file)  {
        if(!file.exists()) return;

        //forcing all the JSpinner to validate any possible input not yet validated (otherwise they can't be written into)
        forceJSpinnerCommit();

        //saving current parameter to some vars so i can restore them later if something goes wrong
        int populationVal       = (int) population.getValue();
        int resourcesVal        = (int) resources.getValue();
        int testPriceVal        = (int) testPrice.getValue();
        int encountersPerDayVal = (int) encountersPerDay.getValue();

        int infectivityVal      = (int) infectivity.getValue();
        int symptomaticityVal   = (int) symptomaticity.getValue();
        int lethalityVal        = (int) lethality.getValue();
        int durationVal         = (int) duration.getValue();

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);

            //state data
            population.setValue(Integer.parseInt(doc.getElementsByTagName(XML_POPULATION).item(0).getTextContent()));
            resources.setValue(Integer.parseInt(doc.getElementsByTagName(XML_RESOURCES).item(0).getTextContent()));
            testPrice.setValue(Integer.parseInt(doc.getElementsByTagName(XML_TEST_PRICE).item(0).getTextContent()));
            encountersPerDay.setValue(Integer.parseInt(doc.getElementsByTagName(XML_ENCOUNTERS_PER_DAY).item(0).getTextContent()));

            //disease data
            infectivity.setValue(Integer.parseInt(doc.getElementsByTagName(XML_INFECTIVITY).item(0).getTextContent()));
            symptomaticity.setValue(Integer.parseInt(doc.getElementsByTagName(XML_SYMPTOMATICITY).item(0).getTextContent()));
            lethality.setValue(Integer.parseInt(doc.getElementsByTagName(XML_LETHALITY).item(0).getTextContent()));
            duration.setValue(Integer.parseInt(doc.getElementsByTagName(XML_DURATION).item(0).getTextContent()));

            /*((JSpinner.DefaultEditor) duration.getEditor()).getTextField().setForeground(Color.red);
            duration.revalidate();
            duration.repaint();*/

        } catch (Exception e) {
            e.printStackTrace();

            population.setValue(populationVal);
            resources.setValue(resourcesVal);
            testPrice.setValue(testPriceVal);
            encountersPerDay.setValue(encountersPerDayVal);

            infectivity.setValue(infectivityVal);
            symptomaticity.setValue(symptomaticityVal);
            lethality.setValue(lethalityVal);
            duration.setValue(durationVal);

        }
    }

    private void writeConfig(File file) {
        try {
            //i swear this isn't confused at all, good fucking job java, that's how to make thing simple. TODO: remove this comment, not now, i love it, but... you'll have to sooner or later
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            // root element
            Element simulator = document.createElement(XML_ROOT);
            document.appendChild(simulator);

            //#region State data
            Element state = document.createElement(XML_STATE);
            simulator.appendChild(state);

            //population
            Element population = document.createElement(XML_POPULATION);
            population.appendChild(document.createTextNode(this.population.getValue().toString()));
            state.appendChild(population);

            //resources
            Element resources = document.createElement(XML_RESOURCES);
            resources.appendChild(document.createTextNode(this.resources.getValue().toString()));
            state.appendChild(resources);

            //testPrice
            Element testPrice = document.createElement(XML_TEST_PRICE);
            testPrice.appendChild(document.createTextNode(this.testPrice.getValue().toString()));
            state.appendChild(testPrice);

            //encountersPerDay
            Element encountersPerDay = document.createElement(XML_ENCOUNTERS_PER_DAY);
            encountersPerDay.appendChild(document.createTextNode(this.encountersPerDay.getValue().toString()));
            state.appendChild(encountersPerDay);

            //#endregion

            //#region Disease data
            Element disease = document.createElement(XML_DISEASE);
            simulator.appendChild(disease);

            //infectivity
            Element infectivity = document.createElement(XML_INFECTIVITY);
            infectivity.appendChild(document.createTextNode(this.infectivity.getValue().toString()));
            disease.appendChild(infectivity);

            //infectivity
            Element symptomaticity = document.createElement(XML_SYMPTOMATICITY);
            symptomaticity.appendChild(document.createTextNode(this.symptomaticity.getValue().toString()));
            disease.appendChild(symptomaticity);

            //infectivity
            Element lethality = document.createElement(XML_LETHALITY);
            lethality.appendChild(document.createTextNode(this.lethality.getValue().toString()));
            disease.appendChild(lethality);

            //duration
            Element duration = document.createElement(XML_DURATION);
            duration.appendChild(document.createTextNode(this.duration.getValue().toString()));
            disease.appendChild(duration);
            //#endregion

            // create the xml file
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(file));

            System.out.println("Done creating XML File ("+file.getName()+")");
        } catch (Exception e) {
            //TODO: message the user about the error
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        new SimulatorSettings();
    }

}

class SelectableStrategy{
    private Class value;
    private String humanReadableClassName;
    public Class getValue() {
        return value;
    }

    public SelectableStrategy(Class value) {
        this.value = value;
        if(value == null){
            humanReadableClassName = "No strategy";
            return;
        }
        this.humanReadableClassName = Utils.javaNameToUserString(value.toString());
    }

    @Override
    public String toString() {
        return humanReadableClassName;
    }
}