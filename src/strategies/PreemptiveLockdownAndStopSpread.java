package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.util.Iterator;

/**
 * Simple strategy written just as a test.
 * -it put a percentage of the population in lockdown
 * -it heals every red one
 *
 */
public class PreemptiveLockdownAndStopSpread extends Strategy {
    int stopSpreadFrequency;
    public PreemptiveLockdownAndStopSpread(Simulator simulator, int lockDownPercentage, int stopSpreadFrequency) {
        super(simulator);
        this.stopSpreadFrequency = stopSpreadFrequency;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        if(simulator.getDay()%(simulator.diseaseDuration /stopSpreadFrequency) == 0){
            System.out.println("TESTING AND RESTRAINING!");
            //TODO: only test the people you can test with x% of the original budget
            Iterator<Person> sickIterator = simulator.getPopulation().stream().filter(person -> person.getCanMove()).filter(person -> simulator.testVirus(person)).iterator();
            while(sickIterator.hasNext()){
                sickIterator.next().setCanMove(false);
            }
        }
    }

    @Override
    public void personClean(Person person) {
        person.setCanMove(true);
        System.out.println("HEAL!");
    }

    @Override
    public void personHasSymptoms(Person person) {
        super.personHasSymptoms(person);

        for (Person contact : findEncounters(person,10)){
            System.out.println(contact);
        }
        System.exit(1);
    }


}
