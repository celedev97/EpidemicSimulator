package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;

public class ContactTracingFailSafe extends AggressiveContactTracing {
    float resourcesMinimum;
    boolean lockDown = false;

    public ContactTracingFailSafe(Simulator simulator, @ParameterData(value=95,min=0,max=100) int resourcesPercentageForLockDown) {
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
            //TODO: controlla il modo in cui metti le persone in quarantena, perché la quarantena sembra durare più del normale!
        }
    }

    @Override
    public void personHasSymptoms(Person person) {
        super.personHasSymptoms(person);
    }


}
