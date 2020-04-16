import com.epidemic_simulator.InvalidSimulationException;
import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;

import java.awt.*;

public class Main {

    public static void main(String[] args){
	    //new GUI();
        Simulator sim = null;
        try {
            sim = new Simulator(null,1000,30000,10000,3,50,50,50, 40);
        } catch (InvalidSimulationException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(0);
        }
        Simulator.Outcomes outcome;
        while ((outcome = sim.executeDay())== Simulator.Outcomes.NOTHING){}
        System.out.println(sim.getDay() +"d "+ outcome.toString());

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
    }
}
