import com.epidemic_simulator.*;
import jdk.jshell.spi.ExecutionControl;
import netscape.javascript.JSObject;


//GUI IMPORTS
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;

//TODO: I DON'T EVEN REMEMBER WHAT I USE THOSE FOR
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

//XML IMPORTS
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class SimulatorSettings extends JFrame {
    private final int PARAMETERS_PER_ROW = 4;

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

    private JPanel strategyParametersColumns[];
    private ArrayList<JComponent> strategyParameters;

    private JPanel strategyPanel;
    //#endregion

    private JButton startButton;

    //dialogue
    private JFileChooser fileChooser;

    //strategy parameters



    public SimulatorSettings() throws IOException, URISyntaxException {
        //#region JFrame setup
        //title
        setTitle("Epidemic simulator");

        //visibility
        setVisible(true);

        //size ()
        Dimension windowSize = new Dimension(600, 450);
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


        //#region Menu
        JMenuBar menu = new JMenuBar();

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

        //>file>quit
        JMenuItem quitButton = new JMenuItem("Quit");
        file.add(quitButton);

        openButton.addActionListener(openButtonListener);
        saveButton.addActionListener(saveButtonListener);
        quitButton.addActionListener(e -> this.dispose());
        //#endregion

        //Panel containing all the north panels
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel,BoxLayout.Y_AXIS));
        contentPane.add(northPanel, BorderLayout.NORTH);

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
        for(Class clas : Utils.getClassesForPackage(packageName)){
            strategyCombobox.addItem(new SelectableStrategy(clas));
        }
        //#endregion

        strategyCombobox.addActionListener(strategyComboboxListener);

        //#endregion

        //#region Strategy Parameters Panel
        GridLayout stategyDataGridLayout = new GridLayout(2,4);
        strategyPanel = new JPanel(stategyDataGridLayout);
        stategyDataGridLayout.setHgap(100);//TODO: FIND OUT WHY THIS IS NOT WORKING!!!
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

        startButton = new JButton("Start Simulation");
        contentPane.add(startButton, BorderLayout.SOUTH);

        //#endregion

        //file load and save
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Simulator config", "simconf"));

        startButton.addActionListener(startButtonListener);
    }

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
                    System.out.println("setting ["+x+"] to: "+parameterName);
                    JLabel label = new JLabel(parameterName);
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    strategyParametersColumns[x].add(label);

                    //generating the component
                    JComponent generatedComponent = null;
                    if(type == int.class){
                        generatedComponent = new JSpinner();
                        generatedComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
                        strategyParametersColumns[x].add(generatedComponent);
                        System.out.println("setting ["+x+"] to: "+parameters[i].toString());
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

    public static void main(String[] args) throws IOException, URISyntaxException {
        new SimulatorSettings();
    }

    private ActionListener startButtonListener = e -> {
        try {
            new Simulator((int)population.getValue(), (int)resources.getValue(), (int)testPrice.getValue(), (int)encountersPerDay.getValue(), (int)infectivity.getValue(), (int)symptomaticity.getValue(), (int)lethality.getValue(), (int)duration.getValue());
        } catch (InvalidSimulationException ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    };

    private ActionListener openButtonListener = e -> {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            readFile(fileChooser.getSelectedFile());
        }
    };

    private ActionListener saveButtonListener = e -> {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            writeFile(fileChooser.getSelectedFile());
        }
    };

    private void readFile(File selectedFile) {

    }

    private void writeFile(File file) {
        try {
            //i swear this isn't confused at all, good fucking job java, that's how to make thing simple.
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            // root element
            Element root = document.createElement("simulator");
            document.appendChild(root);

            // employee element
            Element employee = document.createElement("employee");

            root.appendChild(employee);

            // set an attribute to staff element
            Attr attr = document.createAttribute("id");
            attr.setValue("10");
            employee.setAttributeNode(attr);

            //you can also use staff.setAttribute("id", "1") for this

            // firstname element
            Element firstName = document.createElement("firstname");
            firstName.appendChild(document.createTextNode("James"));
            employee.appendChild(firstName);

            // lastname element
            Element lastname = document.createElement("lastname");
            lastname.appendChild(document.createTextNode("Harley"));
            employee.appendChild(lastname);

            // email element
            Element email = document.createElement("email");
            email.appendChild(document.createTextNode("james@example.org"));
            employee.appendChild(email);

            // department elements
            Element department = document.createElement("department");
            department.appendChild(document.createTextNode("Human Resources"));
            employee.appendChild(department);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            transformer = transformerFactory.newTransformer();

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");
        } catch (Exception e) {
            e.printStackTrace();
        }
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