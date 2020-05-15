package com.epidemic_simulator.gui.textual;

import com.epidemic_simulator.*;
import com.epidemic_simulator.gui.SimulatorSettings;
import dev.federicocapece.jdaze.Engine;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulatorText extends JFrame {
    Simulator simulator;

    JTextPane output;
    JScrollPane outputScroll;

    JFrame settingsFrame;

    public SimulatorText(JFrame settingsFrame, Simulator simulator) {
        super("Epidemic simulator - Textual Simulator");
        this.settingsFrame = settingsFrame;
        this.simulator = simulator;

        //#region creating the Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int)(screenSize.width*.3),(int)(screenSize.height*.9));

        setSize(windowSize);
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2) - screenSize.height/30);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        output = new JTextPane();
        output.setBackground(Color.GRAY);
        output.setFont(new Font("monospaced", Font.PLAIN, 14));

        outputScroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(outputScroll, BorderLayout.CENTER);

        JButton exitButton = new JButton("Exit");
        contentPane.add(exitButton, BorderLayout.SOUTH);

        setVisible(true);
        //#endregion

        //hooking exit button
        exitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        getRootPane().setDefaultButton(exitButton);
        exitButton.grabFocus();

        //hooking the windows adapter
        addWindowListener(windowListener);


        //start the simulation thread
        simulationThread.start();

    }

    private final Thread simulationThread = new Thread(){
        @Override
        public void run() {
            //starting the simulation
            long start = System.currentTimeMillis();
            Simulator.Outcome outcome;
            while ((outcome = simulator.executeDay()) == Simulator.Outcome.NOTHING)
                dayReport();

            dayReport();

            writeOutput( "\nFINAL REPORT:\n", 20, "b", Color.BLACK);
            writeOutput( "DAY "+simulator.getDay()+ ": " + outcome.toString(), "b", 18);
            writeOutput("\n");
            writeOutput( "\nSTARTING PARAMETERS:\n", 20, "b", Color.BLACK);
            writeOutput("Hey, give me the time to implement this...", 10);

            writeOutput( "\n\n" + (System.currentTimeMillis() - start) + " milliseconds passed");
        }

        private void dayReport() {
            int black = (int) simulator.getPopulation().stream().filter(person -> person.getColor() == Color.BLACK).count();
            int green = (int) simulator.getPopulation().stream().filter(person -> person.getColor() == Color.GREEN).count();
            int yellow = (int) simulator.getPopulation().stream().filter(person -> person.getColor() == Color.YELLOW).count();
            int red = (int) simulator.getPopulation().stream().filter(person -> person.getColor() == Color.RED).count();
            int blue = (int) simulator.getPopulation().stream().filter(person -> person.getColor() == Color.BLUE).count();


            writeOutput( "\nDAY " + simulator.getDay() + '\n',"b",18);
            writeOutput("----------\n");
            writeOutput( "GREEN : " + green + '\n', Color.GREEN, "b");
            writeOutput( "YELLOW: " + yellow + '\n', Color.YELLOW, "b");
            writeOutput( "RED   : " + red + '\n', Color.RED, "b");
            writeOutput( "BLUE  : " + blue + '\n', Color.BLUE, "b");
            writeOutput( "BLACK : " + black + '\n', Color.BLACK , "b");
            writeOutput("----------\n");
            writeOutput( "resources : " + simulator.getResources() + '\n');
            writeOutput( "R0 factor: " + simulator.r0 + '\n');

            //Scroll to bottom:
            SwingUtilities.invokeLater(() -> {
                JScrollBar bar = outputScroll.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            });

        }
    };

    private void writeOutput(String line, Object... styleParams){
        SimpleAttributeSet set = new SimpleAttributeSet();

        //unpacking params to set style
        for (Object parameter : styleParams){
            if(parameter.getClass() == Color.class) {
                StyleConstants.setForeground(set, (Color) parameter);
            }else if(parameter.getClass() == String.class){
                String style = ((String) parameter).toLowerCase();

                if(style.contains("b"))
                    StyleConstants.setBold(set,true);

                if(style.contains("i"))
                    StyleConstants.setItalic(set,true);

            }else if(parameter.getClass() == Integer.class){
                StyleConstants.setFontSize(set, (Integer)parameter);
            }
        }

        //positioning caret to the end
        output.setCaretPosition(output.getDocument().getLength());

        // Set the attributes before adding text
        output.setCharacterAttributes(set, true);

        //setting new text
        output.replaceSelection(line);

        //positioning caret to the end
        output.setCaretPosition(output.getDocument().getLength());

    }

    public static void main(String[] args) {
        //fake main that just start a Setting window and call the start button
        SimulatorSettings settings = new SimulatorSettings();
        settings.startTextButtonListener.actionPerformed(null);
    }

    private final WindowAdapter windowListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            settingsFrame.setVisible(true);
            simulator.dispose();
        }
    };

}
