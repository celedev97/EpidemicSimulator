package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

/**
 * Simple strategy written just as a test.
 * -it put a percentage of the population in lockdown
 * -it heals every red one
 *
 */
public class PreemptiveLockdownAndHeal extends Strategy {
    public PreemptiveLockdownAndHeal(Simulator simulator, int lockDownPercentage) {
        super(simulator);
        initialLockDown(lockDownPercentage);
    }

    @Override
    public void afterExecuteDay() {

    }

    @Override
    public void personClean(Person person) {
        //if the person is now clean from the virus i allow him to move again
        person.setCanMove(true);
        System.out.println("HEAL!");
    }

    @Override
    public void personHasSymptoms(Person person) {
        if(simulator.getResources()>(originalResources*.75)){
            simulator.heal(person);
            System.out.println("HEAL "+person+"!");
        }else{
            System.out.println("Can't heal "+person+", risk of economic disaster!");
        }
    }


}
