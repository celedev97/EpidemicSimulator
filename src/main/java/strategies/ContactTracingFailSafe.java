package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;

public class ContactTracingFailSafe extends AggressiveContactTracing {
    float resourcesMinimum;


    public ContactTracingFailSafe(Simulator simulator, @ParameterData(value=95,min=0,max=100) int resourcesPercentageForLockDown) {
        super(simulator);
        resourcesMinimum = originalResources / 100f * resourcesPercentageForLockDown;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        super.afterExecuteDay(outcome);

        if(simulator.getResources() <= resourcesMinimum){
            super.output("EMERGENCY LOCKDOWN TRIGGERED!!!");
            resourcesMinimum = 0;
            simulator.getAlivePopulation().forEach(person -> quarantine(person, simulator.canInfectDay));
        }
    }
}
