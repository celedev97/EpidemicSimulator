package com.epidemic_simulator.gui;

//INTERNAL IMPORTS
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Utils;
import com.epidemic_simulator.gui.textual.SimulatorText;
import com.epidemic_simulator.gui.visual.SimulatorGUI;

//AWT/SWING
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;

//JSON/FILE
import org.json.*;
import java.io.*;

//REFLECTION (FOR STRATEGIES)
import java.lang.reflect.*;

//LIST UTILS
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SimulatorSettings extends JFrame {
    //constants
    private final int PARAMETERS_PER_ROW = 4;
    private static final String CONF_EXTENSION = ".simconf.json";
    private static final String DEFAULT_CONF_FILE = "./configuration"+CONF_EXTENSION;

    //#region class fields

    //#region Menu
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem defaultButton;
    private JMenuItem quitButton;

    private JMenuItem calculateParameters;
    //#endregion

    //#region state
    private JPanel stateDataPanel;

    private JSpinner population;
    private JSpinner resources;
    private JSpinner testPrice;
    private JSpinner encountersPerDay;
    //#endregion

    //#region disease
    private JPanel diseaseDataPanel;

    private JSpinner infectivity;
    private JSpinner symptomaticity;
    private JSpinner lethality;
    private JSpinner duration;
    //#endregion

    //#region strategy
    private JComboBox<SelectableStrategy> strategyComboBox;

    private JPanel[] strategyParametersColumns;
    private ArrayList<JSpinner> strategyParameters;

    private JPanel strategyPanel;
    //#endregion

    //#region start buttons
    private JButton startTextButton;
    private JButton startGUIButton;
    //#endregion

    //dialogue
    private JFileChooser fileChooser;

    //#endregion

    public SimulatorSettings(){
        //#region JFrame setup
        //title
        setTitle("Epidemic simulator - Settings");

        //size
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

        buildGUI();

        bindGUI();

        //Show JFrame
        setVisible(true);

        //file load and save
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getPath().endsWith(CONF_EXTENSION);
            }

            @Override
            public String getDescription() {
                return "Simulator config file (*"+CONF_EXTENSION+")";
            }
        });

        //setting default parameters
        setDefaultParameters();

        //try to read configuration file if present
        readConfig(new File(DEFAULT_CONF_FILE));

    }

    private void buildGUI(){
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
        openButton = new JMenuItem("Open");
        file.add(openButton);

        //>file>save
        saveButton = new JMenuItem("Save");
        file.add(saveButton);

        //>file>-------
        file.addSeparator();

        //>file>default
        defaultButton = new JMenuItem("Load default");
        file.add(defaultButton);

        //>file>-------
        file.addSeparator();

        //>file>quit
        quitButton = new JMenuItem("Quit");
        file.add(quitButton);

        //>edit
        JMenu edit = new JMenu("Edit");
        menu.add(edit);

        //>edit>calculateParams
        calculateParameters = new JMenuItem("Calculate best parameters");
        edit.add(calculateParameters);
        //#endregion

        //#region State Data Panel
        GridLayout stateDataGridLayout = new GridLayout(2,4);
        stateDataGridLayout.setHgap(10);
        stateDataPanel = new JPanel(stateDataGridLayout);
        stateDataPanel.setName("state");
        stateDataPanel.setBorder(BorderFactory.createTitledBorder("State data"));
        northPanel.add(stateDataPanel);

        //adding labels
        stateDataPanel.add(new JLabel("Population (P):"));
        stateDataPanel.add(new JLabel("Resources (R):"));
        stateDataPanel.add(new JLabel("Test price (C):"));
        stateDataPanel.add(new JLabel("Encounters per day (V):"));

        //adding spinners
        population = new JSpinner();
        population.setName("population");
        stateDataPanel.add(population);

        resources = new JSpinner(new SpinnerNumberModel((Long)0L, (Long)0L, (Long)Long.MAX_VALUE, (Long)1L));
        resources.setName("resources");
        stateDataPanel.add(resources);

        testPrice = new JSpinner();
        testPrice.setName("testPrice");
        stateDataPanel.add(testPrice);

        encountersPerDay = new JSpinner(new SpinnerNumberModel(0.3, 0.0, 50.0, 0.1));
        encountersPerDay.setName("encountersPerDay");
        stateDataPanel.add(encountersPerDay);

        //#endregion

        //#region Disease Data Panel
        GridLayout diseaseDataGridLayout = new GridLayout(2,4);
        diseaseDataGridLayout.setHgap(10);
        diseaseDataPanel = new JPanel(diseaseDataGridLayout);
        diseaseDataPanel.setName("disease");
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
        durationLabel.setToolTipText("The numbers of days that the disease will last in a person body");
        diseaseDataPanel.add(durationLabel);

        //adding spinners
        infectivity = new JSpinner(new SpinnerNumberModel(0 , 0, 100, 1));
        infectivity.setName("infectivity");
        diseaseDataPanel.add(infectivity);

        symptomaticity = new JSpinner(new SpinnerNumberModel(0 , 0, 100, 1));
        symptomaticity.setName("symptomaticity");
        diseaseDataPanel.add(symptomaticity);

        lethality = new JSpinner(new SpinnerNumberModel(0 , 0, 100, 1));
        lethality.setName("lethality");
        diseaseDataPanel.add(lethality);

        duration = new JSpinner(new SpinnerNumberModel(6 , 6, 300, 1));
        duration.setName("duration");
        diseaseDataPanel.add(duration);
        //#endregion

        //#region Strategy Selector Panel
        JPanel strategySelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        northPanel.add(strategySelectorPanel);

        strategySelectorPanel.add(new JLabel("Strategy:  "));
        strategyComboBox = new JComboBox<>();
        strategySelectorPanel.add(strategyComboBox);

        //#region strategies combobox population
        strategyParameters = new ArrayList<>();

        strategyComboBox.addItem(new SelectableStrategy(null));
        try{
            for(Class clas : Utils.getClassesForPackage("strategies")){
                strategyComboBox.addItem(new SelectableStrategy(clas));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,"There was an error while trying to get the list of the strategies.", "Error",JOptionPane.ERROR_MESSAGE);
        }

        //#endregion


        //#endregion

        //#region Strategy Parameters Panel
        GridLayout strategyDataGridLayout = new GridLayout(1,PARAMETERS_PER_ROW);
        strategyDataGridLayout.setHgap(10);
        strategyPanel = new JPanel(strategyDataGridLayout);
        strategyPanel.setBorder(BorderFactory.createTitledBorder("Strategy parameters"));
        strategyPanel.setName("strategy");
        strategyPanel.setVisible(false);
        northPanel.add(strategyPanel);

        //#region strategy dynamic GUI preparation
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

        JPanel southPanel = new JPanel(new GridLayout(1,2));
        startTextButton = new JButton("Start Textual Simulation (Quicker)");
        startGUIButton = new JButton("Start Visual Simulation (Slower)");
        southPanel.add(startTextButton);
        southPanel.add(startGUIButton);

        contentPane.add(southPanel, BorderLayout.SOUTH);

        //#endregion
    }

    private void bindGUI(){
        //Window event binding
        addWindowListener(windowAdapter);

        //Menu binding
        openButton.addActionListener(openButtonListener);
        saveButton.addActionListener(saveButtonListener);
        defaultButton.addActionListener(e -> setDefaultParameters());
        quitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        calculateParameters.addActionListener(e -> setBestParameters());

        //Strategy selector binding
        strategyComboBox.addActionListener(strategyComboBoxListener);

        //Start Button binding
        startGUIButton.addActionListener(startGUIButtonListener);
        startTextButton.addActionListener(startTextButtonListener);
    }

    //#region Start Buttons listeners/methods
    public final ActionListener startTextButtonListener = e -> {
        Simulator simulator;
        if((simulator = createSimulator()) == null) return;

        //creating simulator GUI and closing configurator
        this.setVisible(false);
        new SimulatorText(this, simulator);
    };

    public final ActionListener startGUIButtonListener = e -> {
        Simulator simulator;
        if((simulator = createSimulator()) == null) return;

        //creating simulator GUI and closing configurator
        this.setVisible(false);
        new SimulatorGUI(this, simulator);
    };

    private Simulator createSimulator() {
        try {
            //creating simulator
            Simulator simulator = new Simulator((int)population.getValue(), ((Number)resources.getValue()).longValue(), (int)testPrice.getValue(), ((Number)encountersPerDay.getValue()).doubleValue(), (int)infectivity.getValue(), (int)symptomaticity.getValue(), (int)lethality.getValue(), (int)duration.getValue());

            //fetching parameters from JSpinners
            List<Object> parametersList  = strategyParameters.stream().map(parameter -> parameter.getValue()).collect(Collectors.toList());

            //adding the simulator as the first parameter
            parametersList.add(0,simulator);

            //creating strategy
            Class chosenStrategy = ((SelectableStrategy) strategyComboBox.getSelectedItem()).getValue();
            if(chosenStrategy != null)
                chosenStrategy.getConstructors()[0].newInstance(parametersList.toArray());

            return simulator;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return null;
    }

    //#endregion

    //#region Configuration LOAD/SAVE/RELOAD
    private void setDefaultParameters() {
        Utils.forceJSpinnerCommit(this);

        population.setValue(1000);
        resources.setValue(44999);
        testPrice.setValue(10);
        encountersPerDay.setValue(1);

        infectivity.setValue(50);
        symptomaticity.setValue(50);
        lethality.setValue(50);
        duration.setValue(45);

        strategyComboBox.setSelectedIndex(0);
    }

    private void setBestParameters(){
        long resourcesValue = (int)duration.getValue()*(int)population.getValue() -1;
        int testPriceValue = (int)(resourcesValue / (10*(int)population.getValue())+1);

        Utils.forceJSpinnerCommit(this);

        resources.setValue(resourcesValue);
        testPrice.setValue(testPriceValue);
    }

    private ActionListener openButtonListener = e -> {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            readConfig(fileChooser.getSelectedFile());
        }
    };

    private ActionListener saveButtonListener = e -> {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            writeFullConfig(fileChooser.getSelectedFile());
        }
    };

    private void readConfig(File file)  {
        if(!file.exists()) return;
        System.out.println("READING: "+ file.getPath());

        //forcing all the JSpinner to validate any possible input not yet validated (otherwise they can't be written into)
        Utils.forceJSpinnerCommit(this);

        try (FileInputStream fileReader = new FileInputStream(file)) {
            //reading the file
            byte[] data = new byte[(int) file.length()];
            fileReader.read(data);
            fileReader.close();

            String jsonString = new String(data,"UTF-8");

            //getting the root object
            JSONObject root = new JSONObject(jsonString);

            //deserialize state data
            if(root.has(stateDataPanel.getName())){
                deserializeSpinners(stateDataPanel, root.getJSONObject(stateDataPanel.getName()));
            }

            //deserialize disease data
            if(root.has(diseaseDataPanel.getName())){
                deserializeSpinners(diseaseDataPanel, root.getJSONObject(diseaseDataPanel.getName()));
            }

            //deserialize strategy data
            if(root.has(strategyPanel.getName())){
                JSONObject strategyData = root.getJSONObject(strategyPanel.getName());
                String selectedClass = strategyData.getString("selected");

                //looking for the strategy with that name
                int i;
                for(i = 0; i<strategyComboBox.getItemCount(); i++){
                    Class classValue = strategyComboBox.getItemAt(i).getValue();
                    if((classValue == null && selectedClass.equals("null")) || (classValue != null && selectedClass.equals(classValue.toString())) ){
                        strategyComboBox.setSelectedIndex(i);
                        strategyComboBoxListener.actionPerformed(null);
                        break;
                    }
                }

                //if i found a strategy then i can load the spinners
                if(i != strategyComboBox.getItemCount())
                    deserializeSpinners(strategyPanel, strategyData);

            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error!", JOptionPane.ERROR);
            setDefaultParameters();
        }

    }

    private void writeFullConfig(File file){
        //reading strategy combobox
        Class strategyClass = ((SelectableStrategy)strategyComboBox.getSelectedItem()).getValue();
        JSONObject strategyJSON = serializeSpinners(strategyPanel);
        strategyJSON.getJSONObject(strategyPanel.getName()).put("selected", strategyClass != null ? strategyClass.toString() : "null");

        writeConfig(file, serializeSpinners(stateDataPanel), serializeSpinners(diseaseDataPanel), strategyJSON);
    }

    private void writeConfig(File file, JSONObject... serializableInfo) {
        System.out.println("WRITING: "+ file.getPath());
        if (!file.getName().endsWith(CONF_EXTENSION))
            file = new File(file.getAbsolutePath()+CONF_EXTENSION);

        //creating root object
        JSONObject root = new JSONObject();

        for (JSONObject setting : serializableInfo){
            setting.keySet().forEach(s -> root.put(s,setting.get(s)));
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(root.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject serializeSpinners(JPanel spinnerPanel){
        Utils.forceJSpinnerCommit(spinnerPanel);

        //get all the spinners in this
        JSONObject panel = new JSONObject();
        JSONObject values = new JSONObject();
        panel.put(spinnerPanel.getName(), values);

        Utils.getJSpinners(spinnerPanel).forEach(jSpinner -> values.put(jSpinner.getName(), jSpinner.getValue()));

        return panel;
    }

    private void deserializeSpinners(JPanel dataPanel, JSONObject jsonObject) {
        Utils.getJSpinners(dataPanel).forEach(jSpinner -> jSpinner.setValue(jsonObject.getNumber(jSpinner.getName())));
    }

    //#endregion

    private ActionListener strategyComboBoxListener = e -> {
        //#region clearing old strategy
        strategyParameters.clear();
        for(JPanel panel : strategyParametersColumns){
            panel.removeAll();
        }
        //#endregion

        strategyPanel.setVisible(false);

        //#region generating new strategy GUI
        Class<?> strategyClass = ((SelectableStrategy)strategyComboBox.getSelectedItem()).getValue();
        if(strategyClass != null){
            Parameter[] parameters = strategyClass.getDeclaredConstructors()[0].getParameters();
            strategyPanel.setVisible(parameters.length>1);

            //populating the parameters columns with the data from parameters
            //(SKIPPING THE FIRST PARAMETER, THAT'S THE SIMULATOR)
            for (int i = 1; i < parameters.length; i++) {
                //getting parameter data
                //Class type = parameters[i].getType();
                String parameterName = Utils.javaNameToUserString(parameters[i].getName());

                //calculating y and x for this parameter
                int x = (i-1) % PARAMETERS_PER_ROW;

                //creating the label
                JLabel label = new JLabel(parameterName);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                strategyParametersColumns[x].add(label);

                //generating the component
                JSpinner generatedComponent = null;
                generatedComponent = new JSpinner();
                generatedComponent.setName(parameters[i].getName());
                generatedComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
                strategyParametersColumns[x].add(generatedComponent);

                //adding the component to the list of parameters and to the list of json to serialize
                strategyParameters.add(generatedComponent);
            }
        }
        //#endregion

        //force redraw of the strategy panel
        strategyPanel.revalidate();
        strategyPanel.repaint();
    };

    private WindowAdapter windowAdapter = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            writeFullConfig(new File(DEFAULT_CONF_FILE));
            ((JFrame)e.getSource()).dispose();
        }
    };

    public static void main(String[] args) {
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