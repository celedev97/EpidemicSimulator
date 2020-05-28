package com.epidemic_simulator.gui.textual;

import com.epidemic_simulator.*;
import com.epidemic_simulator.gui.SimulatorSettings;
import jdk.jshell.execution.Util;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SimulatorText extends JFrame {
    Simulator simulator;
    JFrame settingsFrame;

    JTextPane output;
    JScrollPane outputScroll;
    JButton exitButton;

    public SimulatorText(JFrame settingsFrame, Simulator simulator) {
        super("Epidemic simulator - Textual Simulator");
        this.settingsFrame = settingsFrame;
        this.simulator = simulator;

        buildGUI();

        //#region listeners hooking
        //hooking exit button
        exitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        getRootPane().setDefaultButton(exitButton);
        exitButton.grabFocus();

        //hooking the windows adapter
        addWindowListener(windowListener);
        //#endregion listener

        //start the simulation thread
        simulationThread.start();

    }

    private void buildGUI() {
        //#region creating the Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int) (screenSize.width * .3), (int) (screenSize.height * .9));

        setSize(windowSize);
        setLocation((screenSize.width / 2 - windowSize.width / 2), (screenSize.height / 2 - windowSize.height / 2) - screenSize.height / 30);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        output = new JTextPane();
        output.setBackground(Color.GRAY);
        output.setFont(new Font("monospaced", Font.PLAIN, 14));

        outputScroll = new JScrollPane(output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(outputScroll, BorderLayout.CENTER);

        exitButton = new JButton("Exit");
        contentPane.add(exitButton, BorderLayout.SOUTH);

        setVisible(true);
        //#endregion
    }

    private final Thread simulationThread = new Thread() {
        @Override
        public void run() {
            //starting the simulation
            long startTime = System.currentTimeMillis();
            Simulator.Outcome outcome;

            //simulation loop
            while ((outcome = simulator.executeDay()) == Simulator.Outcome.NOTHING)
                dayReport();
            //#region final report
            finalReport(outcome, startTime);
            //#endregion
        }
    };

    private void dayReport() {
        writeOutput("\nDAY " + simulator.getDay() + '\n', "b", 18);
        writeOutput("----------\n");
        writeOutput("GREEN : " + simulator.getGreenCount() + '\n', Color.GREEN, "b");
        writeOutput("YELLOW: " + simulator.getYellowCount() + '\n', Color.YELLOW, "b");
        writeOutput("RED   : " + simulator.getRedCount() + '\n', Color.RED, "b");
        writeOutput("BLUE  : " + simulator.getBlueCount() + '\n', Color.BLUE, "b");
        writeOutput("BLACK : " + simulator.getBlackCount() + '\n', Color.BLACK, "b");

        if (simulator.getStrategy() != null) {
            String messages = simulator.getStrategy().clearOutput();
            if (messages.length() > 0) {
                writeOutput("SIMULATOR REPORT : " + messages + '\n', Color.CYAN, "b");
            }
        }
        writeOutput("----------\n");
        writeOutput("resources : " + simulator.getResources() + '\n');
        writeOutput("R0 factor: " + simulator.getR0() + '\n');

        //Scroll to bottom:
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = outputScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

    }

    private void finalReport(Simulator.Outcome outcome, long startTime) {
        writeOutput("\nFINAL REPORT:\n", 20, "b", Color.BLACK);

        dayReport();

        //finding the appropriate color for the outcome
        Color outcomeColor = Color.BLACK;
        switch (outcome) {
            case ALL_HEALED:
                outcomeColor = Color.GREEN;
                break;
            case ALL_DEAD:
                outcomeColor = Color.ORANGE;
                break;
            case ECONOMIC_COLLAPSE:
                outcomeColor = Color.RED;
                break;
        }

        writeOutput(outcome.toString(), "b", 18, outcomeColor);
        writeOutput("\n");
        writeOutput("\nSTARTING PARAMETERS:\n", 20, "b", Color.BLACK);
        writeOutput("\nPopulation: " + simulator.getPopulation().size() + '\n', 16);
        writeOutput("Resources: " + simulator.initialResources + '\n', 16);
        writeOutput("Test Price: " + simulator.testPrice + '\n', 16);
        writeOutput("Encounters per day: " + simulator.averageEncountersPerDay + "\n\n", 16);
        writeOutput("Infectivity: " + simulator.infectionRate + '\n', 16);
        writeOutput("Symptomaticity: " + simulator.symptomsRate + '\n', 16);
        writeOutput("Lethality: " + simulator.deathRate + '\n', 16);
        writeOutput("Duration: " + simulator.diseaseDuration + "\n\n", 16);
        writeOutput("Strategy used: " + Utils.getStrategyName(simulator.getStrategy()) + '\n', 16);

        writeOutput("\n\n" + (System.currentTimeMillis() - startTime) + " milliseconds passed");
    }

    private void writeOutput(String line, Object... styleParams) {
        SimpleAttributeSet set = new SimpleAttributeSet();

        //unpacking params to set style
        for (Object parameter : styleParams) {
            if (parameter.getClass() == Color.class) {
                StyleConstants.setForeground(set, (Color) parameter);
            } else if (parameter.getClass() == String.class) {
                String style = ((String) parameter).toLowerCase();

                if (style.contains("b"))
                    StyleConstants.setBold(set, true);

                if (style.contains("i"))
                    StyleConstants.setItalic(set, true);

            } else if (parameter.getClass() == Integer.class) {
                StyleConstants.setFontSize(set, (Integer) parameter);
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
