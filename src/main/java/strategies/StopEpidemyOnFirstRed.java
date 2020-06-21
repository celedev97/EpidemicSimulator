package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

public class StopEpidemyOnFirstRed extends Strategy {
    private int controllo = 0;
    private int dataCheck = 0;

    public StopEpidemyOnFirstRed(Simulator simulator) {
        super(simulator);
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        if (this.controllo == 0) {
            dataCheck = simulator.getDay();
            super.output("BEGINING OF THE LOCKDOWN UNTIL DAY: " + (dataCheck + simulator.canInfectDay + 1) + "!");
            for (Person p : simulator.getAlivePopulation()) {
                p.canMove = false;
            }
            this.controllo++;
        }
        if (this.controllo > 0 && simulator.getDay() == (dataCheck + simulator.canInfectDay + 1)) {
            super.output("START OF THE CONTROL!");
            int count = 0;
            for (Person p : simulator.getAlivePopulation()) {
                if (!simulator.testVirus(p)) {
                    count++;
                    p.canMove = true;
                }
            }
            super.output("RESULT: " + count + " PEOPLE FREED!");
            dataCheck = 0;
        }
    }
}
