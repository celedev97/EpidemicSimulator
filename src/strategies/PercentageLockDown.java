package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import com.epidemic_simulator.Utils;
import jdk.jshell.execution.Util;

import java.util.ArrayList;

public class PercentageLockDown extends Strategy {

    private boolean activated = false;

    private int percentage;

    public PercentageLockDown(Simulator simulator, @ParameterData(value = 30,max = 100,min = 0) int percentage) {
        super(simulator);
        this.percentage = percentage;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        activated = true;
        ArrayList<Person> notQuarantinedPopulation = new ArrayList<>(simulator.getAlivePopulation());
        int toQuarantine = notQuarantinedPopulation.size() * percentage / 100;
        for (int i = 0; i<toQuarantine; i++){
            int extracted = Utils.random(notQuarantinedPopulation.size());
            notQuarantinedPopulation.get(extracted).canMove = false;
            notQuarantinedPopulation.remove(i);
        }
        simulator.setStrategy(null);
    }
}
