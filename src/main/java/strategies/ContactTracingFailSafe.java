package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;

public class ContactTracingFailSafe extends AggressiveContactTracing {
    double resourcesMinimum;
    boolean lockDown = false;

    public ContactTracingFailSafe(Simulator simulator, @ParameterData(value = 97.5, min = 0, max = 100, step = 0.25) double resourcesPercentageForLockDown) {
        super(simulator);
        resourcesMinimum = originalResources / 100f * resourcesPercentageForLockDown;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        super.afterExecuteDay(outcome);

        if(!lockDown && simulator.getResources() <= resourcesMinimum){
            super.output("TRIGGERED EMERGENCY LOCKDOWN!!!");
            lockDown = true;
            simulator.getAlivePopulation().forEach(person -> quarantine(person, simulator.canInfectDay));
        }
    }
}
