import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.exceptions.InvalidSimulationException;
import strategies.StopEpidemyOnFirstRed;

public class Main {

    public static void main(String[] args) throws InvalidSimulationException {
        boolean dio, cane;
        dio = cane = true;

        int simulatorCreated = 0;

        while(dio == cane){
            simulatorCreated++;
            //create simulator
            Simulator simulator = new Simulator(10000, 449999,5,.3, 50,50,50,45);
            //link strategy
            StopEpidemyOnFirstRed strategy = new StopEpidemyOnFirstRed(simulator);
            System.out.println("CREATED "+simulatorCreated+"SIMULATORS");

            while(simulator.executeDay() == Simulator.Outcome.NOTHING){}

        }
    }

}
