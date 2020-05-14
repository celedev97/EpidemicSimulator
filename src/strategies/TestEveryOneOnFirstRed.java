package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.util.HashMap;
import java.util.HashSet;

public class TestEveryOneOnFirstRed extends BlockEveryoneByContactTracing {
    boolean firstRed = false;

    public TestEveryOneOnFirstRed(Simulator simulator) {
        super(simulator);
    }

    @Override
    public void personHasSymptoms(Person person) {
        if(firstRed = false){
            for(Person alive : simulator.getAlivePopulation()){
                if(simulator.testVirus(alive)){
                    if(!quarantine.containsKey(person)){
                        quarantine.put(person, simulator.canInfectDay);
                        quarantined++;
                    }
                }
            }
        }

        super.personHasSymptoms(person);

        for (Person contact : findEncounters(person, postIncubation)){
            if(!immunes.contains(contact)){
                if(!quarantine.containsKey(person)){
                    quarantine.put(person, simulator.canInfectDay);
                    quarantined++;
                }
            }
        }
    }


}
