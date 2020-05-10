package com.epidemic_simulator.gui;

import com.epidemic_simulator.*;

import javax.swing.*;
import java.awt.*;

public class SimulatorText extends JDialog {
    Simulator simulator;

    JEditorPane output;
    JScrollPane outputScroll;

    public SimulatorText(Frame owner, Simulator simulator) {
        super(owner);
        this.simulator = simulator;

        //creating the Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int)(screenSize.width*.3),(int)(screenSize.height*.9));

        setSize(windowSize);
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2) - screenSize.height/30);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        output = new JEditorPane();
        outputScroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(outputScroll, BorderLayout.CENTER);

        setVisible(true);

        //start the simulation thread
        simulationThread.start();

    }

    private final Thread simulationThread = new Thread(){
        @Override
        public void run() {
            //starting the simulation
            long start = System.currentTimeMillis();
            Simulator.Outcome outcome;
            output.setText(output.getText()+ "DAY " + simulator.getDay() + '\n');
            while ((outcome = simulator.executeDay()) == Simulator.Outcome.NOTHING)
                dayReport();

            output.setText(output.getText()+ "\nFinal report:\n");
            output.setText(output.getText()+ simulator.getDay() + "d " + outcome.toString());

            dayReport();

            output.setText(output.getText()+ "\n" + (System.currentTimeMillis() - start) + " milliseconds passed");
        }

        private void dayReport() {
            output.setText(output.getText()+ "\nDAY " + simulator.getDay() + '\n');

            int black = (int) simulator.population.stream().filter(person -> person.getColor() == Color.BLACK).count();
            int green = (int) simulator.population.stream().filter(person -> person.getColor() == Color.GREEN).count();
            int yellow = (int) simulator.population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
            int red = (int) simulator.population.stream().filter(person -> person.getColor() == Color.RED).count();
            int blue = (int) simulator.population.stream().filter(person -> person.getColor() == Color.BLUE).count();


            writeOutput( "green : " + green + '\n', Color.GREEN);
            writeOutput( "yellow: " + yellow + '\n', Color.YELLOW);
            writeOutput( "red   : " + red + '\n', Color.RED);
            writeOutput( "blue  : " + blue + '\n', Color.BLUE);
            writeOutput( "black : " + black + '\n', Color.BLACK);
            writeOutput( "resources : " + simulator.getResources() + '\n');
            writeOutput( "R0 factor: " + simulator.r0 + '\n');

            //Scroll to bottom:
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = outputScroll.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });

        }
    };

    private void writeOutput(String line){
        writeOutput(line, Color.BLACK);
    }

    private void writeOutput(String line, Color color){
        //getting text data
        String previous = output.getText();
        int caret = previous.length()-1;

        //setting new text
        output.setText(previous + line);

        //coloring it
        output.setSelectionStart(caret);
        output.setSelectionEnd(caret + line.length());
        output.setSelectedTextColor(color);

        //setting caret
        output.setCaretPosition(output.getText().length()-1);

    }



    public static void main(String[] args) {
        //fake main that just start a Setting window and call the start button
        SimulatorSettings settings = new SimulatorSettings();
        settings.startTextButtonListener.actionPerformed(null);
    }

}
