import com.epidemic_simulator.Simulator;

import javax.swing.*;
import java.awt.*;

public class SimulatorGUI extends JFrame {
    Canvas canvas;

    public SimulatorGUI(Simulator simulator, int sleep) {
        JPanel canvasPanel = new JPanel();
        canvas = new Canvas();
        canvasPanel.add(canvas);

        setContentPane(canvasPanel);

        setVisible(true);


    }

}