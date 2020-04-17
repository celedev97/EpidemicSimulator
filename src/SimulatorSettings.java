import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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


    public SimulatorSettings(){

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
        Dimension windowSize = new Dimension((int)(screenSize.width * .75), (int)(screenSize.height * .90));
        setSize(windowSize);

        //closeButton
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //position
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2));
        //#endregion

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Simulator config", "simconf"));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        new SimulatorSettings();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
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
