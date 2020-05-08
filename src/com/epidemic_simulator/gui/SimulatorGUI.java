package com.epidemic_simulator.gui;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import dev.federicocapece.jdaze.*;

import javax.swing.*;
import java.awt.*;

public class SimulatorGUI extends JFrame {
    
        Simulator simulator;
    
        public SimulatorGUI(Simulator simulator) {
            this.simulator = simulator;

            //creating the Frame
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

            //Starting the graphic engine
            Engine.start(100);

            //calculating the best rows and column configuration for the number of Persons that i have
            int nPersons = 10000;

            int width = Engine.renderer.getWidth();
            int height = Engine.renderer.getHeight();

            int nx = (int)Math.sqrt(((float)nPersons)*width/height);
            int ny = (int)Math.sqrt(((float)nPersons)*height/width)+1;

            //creating the persons
            int created = 0;
            creationLoop:
            for (int y = 0; y < ny; y++){
                for (int x = 0; x < nx; x++){
                    created++;
                    new DrawablePerson(new Person(), x*20, y*20);
                    if (created == nPersons) break creationLoop;
                }
            }

            //creating the camera movement script
            new CameraMove(200);
            //centering the camera
            CameraMove.setPosition(nx*10, ny*10);

        }


        public static void main(String[] args) {
            //fake main that just start a Setting window and call the start button
            SimulatorSettings settings = new SimulatorSettings();
            settings.startButtonListener.actionPerformed(null);
        }
    
    }