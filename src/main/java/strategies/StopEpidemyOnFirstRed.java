package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

public class StopEpidemyOnFirstRed extends Strategy {
    private boolean lockDown = false;
    private int startOfQuarantine = 0;

    public StopEpidemyOnFirstRed(Simulator simulator) {
        super(simulator);
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        if (!lockDown) {
            startOfQuarantine = simulator.getDay();
            super.output("BEGINING OF THE LOCKDOWN UNTIL DAY: " + (startOfQuarantine + simulator.canInfectDay + 1) + "!");
            for (Person p : simulator.getAlivePopulation()) {
                p.canMove = false;
            }
            lockDown = true;
        }else if (simulator.getDay() == (startOfQuarantine + simulator.canInfectDay + 1)) {
            super.output("START OF THE TESTS!");
            int count = 0;
            for (Person p : simulator.getAlivePopulation()) {
                if (!simulator.testVirus(p)) {
                    count++;
                    p.canMove = true;
                }
            }
            super.output("RESULT: " + count + " PEOPLE FREED!");
            //deactivating strategy after the end of the lockdown
            simulator.removeCallBack(this);
        }
    }

}
