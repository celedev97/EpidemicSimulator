package com.epidemic_simulator;

import com.epidemic_simulator.Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SimulatorGUI extends JFrame {

    Simulator simulator;
    Canvas canvas;

    public SimulatorGUI(Simulator simulator, int sleep) {
        this.simulator = simulator;

        //#region JFrame setup
        //title
        setTitle("Epidemic simulator - Simulation");

        //size
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        //closeButton
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //#endregion

        //#region UI Building
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        SimulationPanel canvasPanel = new SimulationPanel();
        contentPane.add(canvasPanel, BorderLayout.CENTER);

        //#endregion

        //forcing component draw so i can get the canvas size
        setVisible(true);
        revalidate();
        repaint();

        canvasPanel.init();
    }

    class SimulationPanel extends JPanel {
        BufferedImage image = null;

        SimulationPanel() {
            setBackground(Color.WHITE);
        }

        void init(){
            int width = getWidth();
            int height = getHeight();

            image = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_3BYTE_BGR);

            int nPoints = 1000;

            double nx = Math.sqrt(((float)nPoints)*width/height);
            double ny = Math.sqrt(((float)nPoints)*height/width)+1;

            double xDistance = width/nx;
            double yDistance = height/ny;

            Graphics graphic = image.getGraphics();
            graphic.setColor(Color.red);

            int size = (int)((xDistance<yDistance ? xDistance : yDistance)/2);

            int drawn = 0;
            for (double y = yDistance/2; y<height; y+=yDistance){
                for (double x = (int)(xDistance/2); (x+size)<width; x+=xDistance){
                    drawn++;
                    graphic.fillOval((int)x,(int)y,size,size);
                    if (drawn == nPoints) return;
                }
            }




        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image,0,0, null);
        }
    }

    public static void main(String[] args) {
        //fake main that just start a Setting window and call the start button
        SimulatorSettings settings = new SimulatorSettings();
        settings.startButtonListener.actionPerformed(null);
    }

}