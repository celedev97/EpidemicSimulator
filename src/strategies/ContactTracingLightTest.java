package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Da quando viene trovato il primo sintomatico, scorre tutta la popolazione in vita,
 * e per ogni sintomatico trovato analizza la sua lista degli incontri nei precedenti "developSyntomsMaxDay" giorni.
 * Di queste, effettua il tampone ad una percentuale data da "testPercentage",
 * se positive vengono messe in quarantena fin quando non si è sicuri abbiano debellato la malattia
 * (o fino a quando non sviluppino sintomi).
 * Il resto invece viene messo in quarantena senza controllo del tampone.
 */

public class ContactTracingLightTest extends Strategy {

    private int testPercentage;

    public ContactTracingLightTest(Simulator simulator, int testPercentage) {
        super(simulator);
        this.testPercentage = testPercentage;
    }

    @Override
    public void afterExecuteDay() {
        if (simulator.firstRed) {
            ArrayList<Person> reds = new ArrayList<>();
            simulator.alivePopulation.stream().filter(s -> (Color.RED).equals(s.getColor())).forEach(reds::add); //aggiungo all'array reds tutti i sintomatici
            for (Person tizio : reds) {
                List<Person> encounters;
                encounters = findEncounters(tizio, simulator.developSymptomsMaxDay);  //per ogni sintomatico controllo la lista dei suoi ultimi incontri
                if (encounters.size() > 0) {
                    for (int i = 0; i < ((encounters.size() * testPercentage) / 100); i++) {     //una percentuale fa il tampone
                        if ((encounters.get(i).getColor() != Color.RED) && (encounters.get(i).getColor() != Color.BLUE) && (!encounters.get(i).precautionaryQuarantine)) {
                            if (simulator.testVirus(encounters.get(i))) {
                                encounters.get(i).precautionaryQuarantine = true;
                                encounters.get(i).setCanMove(false);
                                encounters.get(i).quarantineStartDay = simulator.getDay();
                            }
                        }
                    }
                    for (int i = (testPercentage * 100 / encounters.size()); i < encounters.size(); i++) {  //il resto viene messo in quarantena per tot giorni
                        if ((encounters.get(i).getColor() != Color.RED) && (encounters.get(i).getColor() != Color.BLUE) && (!encounters.get(i).precautionaryQuarantine)) {
                            encounters.get(i).precautionaryQuarantine = true;
                            encounters.get(i).setCanMove(false);
                            encounters.get(i).quarantineStartDay = simulator.getDay();
                        }
                    }
                }
            }
        }
    }
}
