import com.epidemic_simulator.InvalidSimulationException;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import strategies.*;

import java.awt.*;

public class Main {

    public static void main(String[] args){

        long start = System.currentTimeMillis();

        Simulator sim = null;

        try {
            sim = new Simulator(1000,20000,100,3,50,50,50, 40);
            //Strategy strategy = new PreemptiveLockdownAndStopSpread(sim,25, 2);
            //Strategy strategy=new BusinessAsUsual(sim);
        } catch (InvalidSimulationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(0);
        }
        Simulator.Outcomes outcome;
        System.out.println("DAY "+sim.getDay());
        while ((outcome = sim.executeDay())== Simulator.Outcomes.NOTHING){
            System.out.println();
            System.out.println("DAY "+sim.getDay());
            int black   = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLACK).count();
            int green   = (int) sim.population.stream().filter(person -> person.getColor() == Color.GREEN).count();
            int yellow  = (int) sim.population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
            int red     = (int) sim.population.stream().filter(person -> person.getColor() == Color.RED).count();
            int blue    = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLUE).count();

            System.out.println("green : "+green );
            System.out.println("yellow: "+yellow);
            System.out.println("red   : "+red   );
            System.out.println("blue  : "+blue  );
            System.out.println("black : "+black );
            System.out.println("resources : "+sim.getResources());
        }
        System.out.println("\nFinal report:");
        System.out.println(sim.getDay() +"d "+ outcome.toString());

        int black   = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLACK).count();
        int green   = (int) sim.population.stream().filter(person -> person.getColor() == Color.GREEN).count();
        int yellow  = (int) sim.population.stream().filter(person -> person.getColor() == Color.YELLOW).count();
        int red     = (int) sim.population.stream().filter(person -> person.getColor() == Color.RED).count();
        int blue    = (int) sim.population.stream().filter(person -> person.getColor() == Color.BLUE).count();
//        int R0 = (sim.averageEncountersPerDay * sim.healDay * sim.infectionRate)/100;
        //TODO: dear Cencia, is this R0 useful??

        System.out.println("green : "+green );
        System.out.println("yellow: "+yellow);
        System.out.println("red   : "+red   );
        System.out.println("blue  : "+blue  );
        System.out.println("black : "+black );
        System.out.println("resources : "+sim.getResources());
//        System.out.println("Fattore R0: " + R0);

        long end = System.currentTimeMillis();
        long time=(end-start);
        System.out.println();
        System.out.println(time + " millisec");
    }
}
