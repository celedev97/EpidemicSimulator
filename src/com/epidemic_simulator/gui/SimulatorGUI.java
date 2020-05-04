package com.epidemic_simulator.gui;

import com.epidemic_simulator.Simulator;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.awt.*;

public class SimulatorGUI extends JFrame {
    
        Simulator simulator;
    
        public SimulatorGUI(Simulator simulator) {
            this.simulator = simulator;
            //size
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize((int)(screenSize.width*.95),(int)(screenSize.height*.9));
    
            setResizable(false);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
    
            JPanel contentPane = new JPanel(new BorderLayout());
            setContentPane(contentPane);
    
            contentPane.add(Engine.renderer, BorderLayout.CENTER);
    
            //forcing component draw so i can get the canvas size
            setVisible(true);
            revalidate();
            repaint();

            Engine.start(100);

            //adding Persons to the GameEngine
            /*for(com.epidemic_simulator.Person person : simulator.population){
                new Person(person);
            }*/
            new DrawablePerson(new com.epidemic_simulator.Person()).position.sumUpdate(10,10);
            new DrawablePerson(new com.epidemic_simulator.Person()).position.sumUpdate(-10,-10);

            new CameraMove(200);
        }


        public static void main(String[] args) {
            //fake main that just start a Setting window and call the start button
            SimulatorSettings settings = new SimulatorSettings();
            settings.startButtonListener.actionPerformed(null);
        }
    
    }