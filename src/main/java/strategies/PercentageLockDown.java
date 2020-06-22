package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import com.epidemic_simulator.Utils;

import java.util.ArrayList;

public class PercentageLockDown extends Strategy {
    private final int percentage;

    public PercentageLockDown(Simulator simulator, @ParameterData(value = 30, max = 100, min = 0) int percentage) {
        super(simulator);
        this.percentage = percentage;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        ArrayList<Person> notQuarantinedPopulation = new ArrayList<>(simulator.getAlivePopulation());
        int toQuarantine = notQuarantinedPopulation.size() * percentage / 100;
        for (int i = toQuarantine -1; i >= 0 ; i--) {
            int extracted = Utils.random(notQuarantinedPopulation.size());
            notQuarantinedPopulation.get(extracted).canMove = false;
            notQuarantinedPopulation.remove(i);
        }
        simulator.setStrategy(null);
    }
}
