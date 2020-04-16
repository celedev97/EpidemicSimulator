package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

public class PreemptiveLockdown extends Strategy {
    public PreemptiveLockdown(Simulator simulator, int lockDownPercentage) {
        super(simulator);

        for (Person person : simulator.population){
            person.setCanMove(false);
        }
    }

}
