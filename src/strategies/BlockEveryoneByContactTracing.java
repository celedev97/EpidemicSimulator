package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Simple strategy written just as a test.
 * -it put a percentage of the population in lockdown
 * -it heals every red one
 *
 */
public class BlockEveryoneByContactTracing extends Strategy {

    private HashSet<Person> immunes = new HashSet<>();
    private HashMap<Person, Integer> quarantine = new HashMap<>();

    private int quarantined = 0;

    private int postIncubation = 0;

    public BlockEveryoneByContactTracing(Simulator simulator) {
        super(simulator);
        postIncubation = (simulator.diseaseDuration/3) - (simulator.diseaseDuration/6);
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        System.out.println("QUARANTINED: " + quarantined);

        HashSet<Person> toRemove = new HashSet<>();
        //for each quarantined person
        for (Person key : quarantine.keySet()) {
            //lowering their quarantine days count
            int newQuarantineDays = quarantine.get(key) -1;

            if(newQuarantineDays == 0){
                //if they reached 0 days then i test them and put them to work.
                toRemove.add(key);
                key.setCanMove(!simulator.testVirus(key));
            }else{
                //if they didn't reach 0 i just save the new day.
                quarantine.put(key, newQuarantineDays);
            }
        }
        //remove the ones i had to remove inside the for
        for (Person person : toRemove){
            quarantine.remove(person);
        }
    }

    @Override
    public void personClean(Person person) {
        immunes.add(person);
        person.setCanMove(true);
    }

    @Override
    public void personHasSymptoms(Person person) {
        super.personHasSymptoms(person);

        for (Person contact : findEncounters(person, postIncubation)){
            if(!immunes.contains(contact)){
                quarantine.put(person, simulator.canInfectDay);
                quarantined++;
            }
        }
    }


}
