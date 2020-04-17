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

public class SimulatorSettings extends JFrame implements ActionListener {
    private JPanel contentPane;
    private JPanel menuPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JPanel stateData;
    private JPanel diseaseData;
    private JButton startButton;

    //menu stuff
    private JMenuBar menu;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JSpinner spinner5;
    private JSpinner spinner6;
    private JSpinner spinner7;
    private JSpinner spinner8;
    private JComboBox strategy;
    private JTextField population;
    private JTextField resources;
    private JTextField testPrice;
    private JTextField encountersPerDay;
    private JMenu file;
    private JMenuItem openButton;
    private JMenuItem saveButton;
    private JMenuItem quitButton;

    //dialogue
    private JFileChooser fileChooser;


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
        for (int i = 1; i < parameters.length; i++) {
            Class type = parameters[i].getType();
            String name = Utils.javaNameToUserString(parameters[i].getName());
            JOptionPane.showMessageDialog(this,name);
            if(type == int.class){

            }else{
                throw new ExecutionControl.NotImplementedException("PARAMETER TYPE: "+type.toString()+" NOT SUPPORTED!!!");
            }

        }
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
                writeFile(fileChooser.getSelectedFile());
            }
        }else if(source == saveButton){
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                writeFile(fileChooser.getSelectedFile());
            }
        }

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