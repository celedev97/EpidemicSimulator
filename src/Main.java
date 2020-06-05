import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.exceptions.InvalidSimulationException;
import org.reflections.vfs.SystemDir;
import strategies.StopEpidemyOnFirstRed;

public class Main {

    public static void main(String[] args) throws InvalidSimulationException {
        int simulators = 1000;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < simulators; i++) {
            //create simulator
            Simulator simulator = new Simulator(10000, 449999,5,.3, 50,50,50,45);
            //link strategy
            StopEpidemyOnFirstRed strategy = new StopEpidemyOnFirstRed(simulator);

            while(simulator.executeDay() == Simulator.Outcome.NOTHING){}
            System.out.println(i);
        }

        System.out.println("time per simulator: " + ((System.currentTimeMillis() - startTime)/simulators) + "ms");
    }

}
