import com.epidemic_simulator.InvalidSimulationException;
import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;

import java.awt.*;

public class Main {

    public static void main(String[] args) throws InvalidSimulationException {
	    //new GUI();
        Simulator sim = new Simulator(1000,10000,100,5,50,50,50, 40);
        for (int i = 0; i < 80; i++) {
            sim.executeDay();
        }

        int black = 0;
        int blue = 0;

        for (Person person: sim.population) {
            if(person.getColor() == Color.BLACK) black++;
            if(person.getColor() == Color.BLUE) blue++;
        }

        System.out.println("black: "+black);
        System.out.println("blue: "+blue);
    }
}
