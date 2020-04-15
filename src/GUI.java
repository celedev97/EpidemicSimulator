import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private JPanel mainPane;

    public GUI(){
        //title
        setTitle("Epidemic simulator");
        //visibility
        setVisible(true);
        setContentPane(mainPane);
        //size ()
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Toolkit.getDefaultToolkit().beep();
        Dimension windowSize = new Dimension((int)(screenSize.width * .75), (int)(screenSize.height * .90));
        setSize(windowSize);
        //closeButton
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //position
        setLocation((screenSize.width/2 - windowSize.width/2), (screenSize.height/2 - windowSize.height/2));

    }
}
