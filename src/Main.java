import com.epidemic_simulator.exceptions.InvalidSimulationException;
import com.epidemic_simulator.Simulator;

import java.awt.*;

public class Main {

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        Simulator sim = null;

        try {
            sim = new Simulator(10000, 449999, 5, 0.3f, 50, 50, 50, 45);
            //new TestEveryOneOnFirstRed(sim);
            //ContactTracingLightTest strategy = new ContactTracingLightTest(sim, 20);
            //MediumControlledLockdown strategy=new MediumControlledLockdown(sim,10);
            //FullControlledLockdownAndStopSpread strategy=new FullControlledLockdownAndStopSpread(sim,20);
        } catch (InvalidSimulationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(0);
        }
        Simulator.Outcome outcome;
        System.out.println("DAY " + sim.getDay());
        while ((outcome = sim.executeDay()) == Simulator.Outcome.NOTHING) {
            System.out.println();
            System.out.println("DAY " + sim.getDay());
            int black = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.BLACK).count();
            int green = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.GREEN).count();
            int yellow = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.YELLOW).count();
            int red = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.RED).count();
            int blue = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.BLUE).count();

            System.out.println("green : " + green);
            System.out.println("yellow: " + yellow);
            System.out.println("red   : " + red);
            System.out.println("blue  : " + blue);
            System.out.println("black : " + black);
            System.out.println("resources : " + sim.getResources());
            System.out.println("R0 factor: " + sim.getR0());
        }
        System.out.println("\nFinal report:");
        System.out.println(sim.getDay() + "d " + outcome.toString());

        int black = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.BLACK).count();
        int green = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.GREEN).count();
        int yellow = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.YELLOW).count();
        int red = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.RED).count();
        int blue = (int) sim.getPopulation().stream().filter(person -> person.getColor() == Color.BLUE).count();

        System.out.println("green : " + green);
        System.out.println("yellow: " + yellow);
        System.out.println("red   : " + red);
        System.out.println("blue  : " + blue);
        System.out.println("black : " + black);
        System.out.println("resources : " + sim.getResources());
        System.out.println("R0 factor: " + sim.getR0());

        long end = System.currentTimeMillis();
        long time = (end - start);
        System.out.println();
        System.out.println(time + " millisec");
    }
}
