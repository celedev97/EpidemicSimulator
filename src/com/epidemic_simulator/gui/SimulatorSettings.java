package com.epidemic_simulator.gui;

//INTERNAL IMPORTS
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Utils;
import com.epidemic_simulator.gui.textual.SimulatorText;
import com.epidemic_simulator.gui.visual.SimulatorGUI;

//AWT/SWING
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

//JSON/FILE
import org.json.*;
import java.io.*;

//REFLECTION (FOR STRATEGIES)
import java.lang.reflect.*;

//LIST UTILS
import java.text.ParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class SimulatorSettings extends JFrame {
    private final int PARAMETERS_PER_ROW = 4;

    private static final String CONF_EXTENSION = ".simconf.json";
    private static final String DEFAULT_CONF_FILE = "./configuration"+CONF_EXTENSION;

    //#region class fields

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

        resources = new JSpinner();
        resources.setName("resources");
        stateDataPanel.add(resources);

        testPrice = new JSpinner();
        testPrice.setName("testPrice");
        stateDataPanel.add(testPrice);

        encountersPerDay = new JSpinner();
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
        lethalityLabel.setToolTipText("The numbers of days that the disease will last in a person body");
        diseaseDataPanel.add(durationLabel);

        //adding spinners
        infectivity = new JSpinner();
        infectivity.setName("infectivity");
        diseaseDataPanel.add(infectivity);

        symptomaticity = new JSpinner();
        symptomaticity.setName("symptomaticity");
        diseaseDataPanel.add(symptomaticity);

        lethality = new JSpinner();
        lethality.setName("lethality");
        diseaseDataPanel.add(lethality);

        duration = new JSpinner();
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
        strategyPanel.setName("strategy");
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
        JButton startTextButton = new JButton("Start Textual Simulation (Quicker)");
        JButton startGUIButton = new JButton("Start Visual Simulation (Slower)");
        southPanel.add(startTextButton);
        southPanel.add(startGUIButton);

        contentPane.add(southPanel, BorderLayout.SOUTH);

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
        strategyComboBox.addActionListener(strategyComboboxListener);

        //Start Button binding
        startGUIButton.addActionListener(startGUIButtonListener);
        startTextButton.addActionListener(startTextButtonListener);

        //#endregion

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

    //#region Start
    public final ActionListener startTextButtonListener = e -> {
        Simulator simulator;
        if((simulator = createSimulator()) == null) return;

        //creating simulator GUI and closing configurator
        new SimulatorText(this, simulator);
        //this.windowAdapter.windowClosing(new WindowEvent(this,WindowEvent.WINDOW_CLOSING));
    };

    public final ActionListener startGUIButtonListener = e -> {
        Simulator simulator;
        if((simulator = createSimulator()) == null) return;

        //creating simulator GUI and closing configurator
        new SimulatorGUI(this, simulator);
    };

    private Simulator createSimulator() {
        try {
            //creating simulator
            Simulator simulator = new Simulator((int)population.getValue(), (int)resources.getValue(), (int)testPrice.getValue(), (int)encountersPerDay.getValue(), (int)infectivity.getValue(), (int)symptomaticity.getValue(), (int)lethality.getValue(), (int)duration.getValue());

            //fetching parameters from JSpinners
            List<Object> parametersList  = strategyParameters.stream().map(parameter -> parameter.getValue()).collect(Collectors.toList());

            //adding the simulator as the first parameter
            parametersList.add(0,simulator);

            //creating strategy
            Class choosenStrategy = ((SelectableStrategy) strategyComboBox.getSelectedItem()).getValue();
            if(choosenStrategy != null)
                choosenStrategy.getConstructors()[0].newInstance(parametersList.toArray());

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
        population.setValue(1000);
        resources.setValue(35000);
        testPrice.setValue(100);
        encountersPerDay.setValue(5);

        infectivity.setValue(50);
        symptomaticity.setValue(50);
        lethality.setValue(50);
        duration.setValue(40);

        strategyComboBox.setSelectedIndex(0);
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
        forceJSpinnerCommit(this);

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

            //deserialize stategy data
            if(root.has(strategyPanel.getName())){
                JSONObject strategyData = root.getJSONObject(strategyPanel.getName());
                String selectedClass = strategyData.getString("selected");

                //looking for the strategy with that name
                int i;
                for(i = 0; i<strategyComboBox.getItemCount(); i++){
                    Class classValue = strategyComboBox.getItemAt(i).getValue();
                    if((classValue == null && selectedClass.equals("null")) || (classValue != null && selectedClass.equals(classValue.toString())) ){
                        strategyComboBox.setSelectedIndex(i);
                        strategyComboboxListener.actionPerformed(null);
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

        //forcing all the JSpinner to validate any possible input not yet validated (otherwise they can't be written into)
        forceJSpinnerCommit(this);

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
        //get all the spinners in this
        JSONObject panel = new JSONObject();
        JSONObject values = new JSONObject();
        panel.put(spinnerPanel.getName(), values);

        getJSpinners(spinnerPanel).forEach(jSpinner -> values.put(jSpinner.getName(), jSpinner.getValue()));

        return panel;
    }

    private void deserializeSpinners(JPanel dataPanel, JSONObject jsonObject) {
        getJSpinners(dataPanel).forEach(jSpinner -> {
            System.out.println("SETTING: "+jSpinner.getName()+" TO "+jsonObject.getInt(jSpinner.getName()));
            jSpinner.setValue(jsonObject.getInt(jSpinner.getName()));
        });
    }

    //#endregion

    private ActionListener strategyComboboxListener = e -> {
        //#region clearing old strategy
        strategyParameters.clear();
        for(JPanel panel : strategyParametersColumns){
            panel.removeAll();
        }

        //TODO: use visibility, not border
        strategyPanel.setBorder(null);
        //#endregion

        //#region generating new strategy
        Class strategyClass = ((SelectableStrategy)strategyComboBox.getSelectedItem()).getValue();
        if(strategyClass != null){
            strategyPanel.setBorder(BorderFactory.createTitledBorder("Strategy parameters"));
            Parameter[] parameters = strategyClass.getDeclaredConstructors()[0].getParameters();
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

    //#region Utils

    private static List<JSpinner> getJSpinners(final Container c) {
        Component[] comps = c.getComponents();
        List<JSpinner> compList = new ArrayList<>();
        for (Component comp : comps) {
            if(comp instanceof JSpinner){
                compList.add((JSpinner)comp);
            }else if (comp instanceof Container){
                compList.addAll(getJSpinners((Container) comp));
            }
        }
        return compList;
    }

    public void forceJSpinnerCommit(Container container) {
        getJSpinners(container).forEach(jSpinner -> {
            try {
                jSpinner.commitEdit();
            } catch (ParseException e) {
                //TODO: investigate on this!!!
                e.printStackTrace();
            }
        });
    }

    //#endregion

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