package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    public final Map<Person, Boolean> precautionaryQuarantine;
    public final Map<Person, Integer> quarantineStartDay;

    public ContactTracingLightTest(Simulator simulator, int testPercentage) {
        super(simulator);
        this.testPercentage = testPercentage;

        precautionaryQuarantine = new HashMap<>();
        quarantineStartDay = new HashMap<>();

        for (Person person : simulator.getPopulation()) {
            precautionaryQuarantine.put(person, false);
            quarantineStartDay.put(person, 0);
        }
    }

    @Override
    public void personHasSymptoms (Person person) {
        super.personHasSymptoms(person);
        precautionaryQuarantine.put(person, false);
    }

    public void checkQuarantine(Person person, int currentDay) { //free a person if he got not symptoms and if 5/6 of diseaseDuration passed
        if ((!person.hasSymptoms()) && ((currentDay - quarantineStartDay.get(person)) > Math.ceil((5 * simulator.diseaseDuration) / 6.0))) {
            person.canMove = true;
            precautionaryQuarantine.put(person, false);
        }
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        for (Person person : simulator.getAlivePopulation()) {
            if (Boolean.TRUE.equals(precautionaryQuarantine.get(person)))
                checkQuarantine(person, simulator.getDay());
        }

        ArrayList<Person> reds = new ArrayList<>();
        simulator.getAlivePopulation().stream().filter(s -> (Color.RED).equals(s.getColor())).forEach(reds::add); //aggiungo all'array reds tutti i sintomatici
        for (Person tizio : reds) {
            List<Person> encounters;
            encounters = findEncounters(tizio, simulator.developSymptomsMaxDay);  //per ogni sintomatico controllo la lista dei suoi ultimi incontri
            if (!encounters.isEmpty()) {
                for (int i = 0; i < ((encounters.size() * testPercentage) / 100); i++) {     //una percentuale fa il tampone
                    if (isTestable(encounters, i) && (simulator.testVirus(encounters.get(i))))
                        quarantine(encounters, i);
                }
                for (int i = (testPercentage * 100 / encounters.size()); i < encounters.size(); i++) {  //il resto viene messo in quarantena per tot giorni
                    if (isTestable(encounters, i))
                        quarantine(encounters, i);
                }
            }
        }
    }

    public void quarantine(List<Person> encounters, int i) {
        precautionaryQuarantine.put(encounters.get(i), true);
        quarantineStartDay.put(encounters.get(i), simulator.getDay());
        encounters.get(i).canMove = false;
    }

    boolean isTestable(List<Person> encounters, int i) {
        return (encounters.get(i).getColor() != Color.RED) && (encounters.get(i).getColor() != Color.BLACK) && (encounters.get(i).getColor() != Color.BLUE) && (!precautionaryQuarantine.get(encounters.get(i)));
    }
}
