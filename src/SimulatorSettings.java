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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension(600, (int)(screenSize.height * .90));
        setSize(windowSize);

        //closeButton
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //position
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2));
        //#endregion

        //file load and save
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Simulator config", "simconf"));


        //populate strategies combobox
        strategyParameters = new ArrayList<>();

        String packageName = "strategies";
        strategy.addItem(new SelectableStrategy(null));
        for(Class clas : Utils.getClassesForPackage(packageName)){
            strategy.addItem(new SelectableStrategy(clas));
        }
        strategy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    generateStrategyGUI(((SelectableStrategy)((JComboBox)e.getSource()).getSelectedItem()).getValue().getDeclaredConstructors()[0].getParameters());
                } catch (ExecutionControl.NotImplementedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new Simulator(population.getText(),resources.getText(),testPrice.getText(),encountersPerDay.getText(),)
            }
        });
    }

    private void generateStrategyGUI(Parameter[] parameters) throws ExecutionControl.NotImplementedException {
        int paramsLengthExcludingSimulator = parameters.length-1;
        //clearing GUI
        strategyPanel.removeAll();

        //recreating gui
        //calculating necessary rows (4 parameters per column)
        //TODO: comment this in a decent way, you are gonna forget this in like 2 days
        int rows = (paramsLengthExcludingSimulator / PARAMETERS_PER_ROW + (paramsLengthExcludingSimulator%PARAMETERS_PER_ROW == 0 ? 0 : 1))*2;

        //populating the layout with panels from a matrix (for ease of use later)
        strategyPanel.setLayout(new GridLayout(rows,PARAMETERS_PER_ROW));
        JPanel holderPanels[][] = new JPanel[rows][PARAMETERS_PER_ROW];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < PARAMETERS_PER_ROW; x++) {
                holderPanels[y][x] = new JPanel();
                holderPanels[y][x].setLayout(new BorderLayout());
                strategyPanel.add(holderPanels[y][x]);
            }
        }

        //populating the component matrix with labels and parameters according to the parameters data
        for (int i = 0; i < paramsLengthExcludingSimulator; i++) {
            //getting parameter data
            Class type = parameters[i+1].getType();
            String parameterName = Utils.javaNameToUserString(parameters[i+1].getName());

            int parameterIndexExcludingSimulator = i-1;

            //calculating y and x for this parameter label
            int x = (i % PARAMETERS_PER_ROW);
            int y = i/PARAMETERS_PER_ROW;

            //creating the label
            System.out.println("setting ["+y+"]["+x+"] to: "+parameterName);
            holderPanels[y][x].add(new JLabel(parameterName));

            //generating the component
            JComponent generatedComponent = null;
            if(type == int.class){
                generatedComponent = new JSpinner();
                holderPanels[y+1][x].add(generatedComponent);
                System.out.println("setting ["+(y+1)+"]["+x+"] to: "+parameters[i+1].toString());
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