package dev.federicocapece.jdaze;

import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.gui.SimulatorSettings;

import javax.swing.*;
import java.awt.*;

public class Test extends JFrame {

    Simulator simulator;

    public Test() {
        this.simulator = simulator;
        //size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width*.95),(int)(screenSize.height*.9));

        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        contentPane.add(MiniGameEngine.getEngine(), BorderLayout.CENTER);

        //forcing component draw so i can get the canvas size
        setVisible(true);
        revalidate();
        repaint();

        MiniGameEngine.getEngine().start();
    }

    public static void main(String[] args) {
        new Test();
    }

}