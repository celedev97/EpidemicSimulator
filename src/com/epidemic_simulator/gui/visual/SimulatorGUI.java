package com.epidemic_simulator.gui.visual;

import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.gui.SimulatorSettings;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.awt.*;

public class SimulatorGUI extends JDialog {
    JLabel dayLabel;

    public SimulatorGUI(Frame parent, Simulator simulator){
        super(parent);

        //creating the Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screenSize.width*.95),(int)(screenSize.height*.9));

        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        contentPane.add(Engine.renderer, BorderLayout.CENTER);

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("DAY: "));
        dayLabel = new JLabel("0");
        northPanel.add(dayLabel);

        contentPane.add(northPanel, BorderLayout.NORTH);

        //forcing component draw so i can get the canvas size
        setVisible(true);
        revalidate();
        repaint();

        //Starting the graphic engine
        Engine.start();

        //creating the manager that will decide which person can move and which have to wait
        new PersonManager(this, simulator);

    }

    public static void main(String[] args) {
        //fake main that just start a Setting window and call the start button
        SimulatorSettings settings = new SimulatorSettings();
        settings.startGUIButtonListener.actionPerformed(null);
    }

    public void updateUI(Simulator simulator) {
        dayLabel.setText(""+simulator.getDay());
    }
}