import com.epidemic_simulator.InvalidSimulationException;
import com.epidemic_simulator.Simulator;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        Simulator sim = null;

        try {
            sim = new Simulator(1000, 49999, 5, 3, 20, 20, 20, 50);
            //Strategy strategy = new PreemptiveLockdownAndStopSpread(sim,25, 2);
            //Strategy strategy=new BusinessAsUsual(sim);
            //ContactTracingLightTest strategy = new ContactTracingLightTest(sim, 20);
            //MediumControlledLockdown strategy=new MediumControlledLockdown(sim,10);
            //FullControlledLockdownAndStopSpread strategy=new FullControlledLockdownAndStopSpread(sim,20);
        } catch (InvalidSimulationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(0);
        }
        Simulator.Outcomes outcome;
        System.out.println("DAY " + sim.getDay());
        while ((outcome = sim.executeDay()) == Simulator.Outcomes.NOTHING) {
            System.out.println();
            System.out.println("DAY " + sim.getDay());
            int black = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLACK).count();
            int green = (int) sim.population.stream().filter(person -> person.getColor() == Color.GREEN).count();
            int yellow = (int) sim.population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
            int red = (int) sim.population.stream().filter(person -> person.getColor() == Color.RED).count();
            int blue = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLUE).count();

            System.out.println("green : " + green);
            System.out.println("yellow: " + yellow);
            System.out.println("red   : " + red);
            System.out.println("blue  : " + blue);
            System.out.println("black : " + black);
            System.out.println("resources : " + sim.getResources());
            System.out.println("R0 factor: " + sim.r0);
        }
        System.out.println("\nFinal report:");
        System.out.println(sim.getDay() + "d " + outcome.toString());

        int black = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLACK).count();
        int green = (int) sim.population.stream().filter(person -> person.getColor() == Color.GREEN).count();
        int yellow = (int) sim.population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
        int red = (int) sim.population.stream().filter(person -> person.getColor() == Color.RED).count();
        int blue = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLUE).count();

        System.out.println("green : " + green);
        System.out.println("yellow: " + yellow);
        System.out.println("red   : " + red);
        System.out.println("blue  : " + blue);
        System.out.println("black : " + black);
        System.out.println("resources : " + sim.getResources());
        System.out.println("R0 factor: " + sim.r0);

        long end = System.currentTimeMillis();
        long time = (end - start);
        System.out.println();
        System.out.println(time + " millisec");
    }
}
