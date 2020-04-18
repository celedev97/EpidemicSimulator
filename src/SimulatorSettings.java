import com.epidemic_simulator.*;
import jdk.jshell.spi.ExecutionControl;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class SimulatorSettings extends JFrame implements ActionListener {
    //constants
    private final int PARAMETERS_PER_ROW = 4;

    private JPanel contentPane;
    private JPanel menuPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JPanel stateData;
    private JPanel diseaseData;
    private JButton startButton;

    //menu stuff
    private JMenuBar menu;
    private JSpinner population;
    private JSpinner resources;
    private JSpinner testPrice;
    private JSpinner encountersPerDay;
    private JSpinner spinner5;
    private JSpinner spinner6;
    private JSpinner spinner7;
    private JSpinner spinner8;
    private JComboBox strategy;
    private JPanel strategyParametersColumns[];
    private JPanel strategyPanel;
    private JMenu file;
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem quitButton;

    //dialogue
    private JFileChooser fileChooser;

    //strategy parameters
    private ArrayList<JComponent> strategyParameters;


    public SimulatorSettings() throws IOException, URISyntaxException {

        //#region Menu
        //>file>
        file = new JMenu("File");
        menu.add(file);

        //>file>open
        openButton = new JMenuItem("Open");
        file.add(openButton);

        //>file>save
        saveButton = new JMenuItem("Save");
        file.add(saveButton);

        //>file>-------
        file.addSeparator();

        //>file>quit
        quitButton = new JMenuItem("Quit");
        file.add(quitButton);

        openButton.addActionListener(this);
        saveButton.addActionListener(this);
        quitButton.addActionListener(this);
        //#endregion

        //#region JFrame
        //title
        setTitle("Epidemic simulator");

        //visibility
        setVisible(true);
        setContentPane(contentPane);

        //size ()
        Dimension windowSize = new Dimension(600, 450);
        setSize(windowSize);

        //closeButton
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2));
        //#endregion

        //file load and save
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Simulator config", "simconf"));

        //#region strategies combobox
        strategyParameters = new ArrayList<>();

        String packageName = "strategies";
        strategy.addItem(new SelectableStrategy(null));
        for(Class clas : Utils.getClassesForPackage(packageName)){
            strategy.addItem(new SelectableStrategy(clas));
        }
        strategy.addActionListener(e -> {
            try {
                Class strategyClass = ((SelectableStrategy)((JComboBox)e.getSource()).getSelectedItem()).getValue();

                if(strategyClass == null) return;
                generateStrategyGUI(strategyClass.getDeclaredConstructors()[0].getParameters());

            } catch (ExecutionControl.NotImplementedException ex) {
                ex.printStackTrace();
            }
        });
        //#endregion

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

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new Simulator(population.getText(),resources.getText(),testPrice.getText(),encountersPerDay.getText(),)
            }
        });
    }

    private void generateStrategyGUI(Parameter[] parameters) throws ExecutionControl.NotImplementedException {
        //clearing old parameters
        for(JPanel panel : strategyParametersColumns){
            panel.removeAll();
        }

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
            strategyParametersColumns[x].add(new JLabel(parameterName));

            //generating the component
            JComponent generatedComponent = null;
            if(type == int.class){
                generatedComponent = new JSpinner();
                strategyParametersColumns[x].add(generatedComponent);
                System.out.println("setting ["+x+"] to: "+parameters[i].toString());
            }else{
                throw new ExecutionControl.NotImplementedException("PARAMETER TYPE: "+type.toString()+" NOT SUPPORTED!!!");
            }
            strategyParameters.add(generatedComponent);
        }
        strategyPanel.revalidate();
        strategyPanel.repaint();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        new SimulatorSettings();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == quitButton){
            this.dispose();
            return;
        }else if(source == openButton){
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                readFile(fileChooser.getSelectedFile());
            }
        }else if(source == saveButton){
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                writeFile(fileChooser.getSelectedFile());
            }
        }

    }

    private void readFile(File selectedFile) {

    }

    private void writeFile(File file) {

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