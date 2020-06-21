package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FullControlledLockdownAndStopSpread extends Strategy {
    private int sintomatici = 0;
    private int limite = 0;
    private ArrayList<Person> check;

    public FullControlledLockdownAndStopSpread(Simulator simulator, int percentualOfStop) {
        super(simulator);
        this.limite = (simulator.getAlivePopulation().size() * percentualOfStop) / 100;
        check = new ArrayList<>();
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        int dataCheck = 0; //@PAOLETTO: questo dataCheck a cosa ti serviva? Perché nel caso dell'if gli viene assegnato simulator.getDay ma non viene più usato,
        //invece nel caso dell'else viene usato solo nella somma (ma essendo per forza inizializzato a 0 è inutile)
        if (sintomatici >= this.limite) {
            super.output("MAXIMUM LIMIT REACHED: " + sintomatici + " CONFIRMED CASES -> PROCEED TO FULL LOCKDOWN FROM TODAY!");
            HashMap<Person, List<Person>> person;
            dataCheck = simulator.getDay();
            for (int i = 0; i < simulator.getDay(); i++) {
                person = findEncounters(i);
                for (Person key : person.keySet()) {
                    if (key.getColor() == Color.YELLOW) {
                        for (int y = 0; y < (findEncounters(key, i).size()); y++) {
                            Person p = findEncounters(key, i).get(y);
                            if (simulator.getResources() >= simulator.testPrice) {
                                if (p.getColor() == Color.RED) {
                                    personClean(p);
                                } else if (!simulator.testVirus(p)) {
                                    check.add(p);
                                    p.canMove = false;
                                } else {
                                    p.canMove = false;
                                }
                            } else {
                                return;
                            }
                        }
                    }
                    key.canMove = false;
                }
            }
            this.sintomatici = 0;
            super.output(check.size() + " PEOPLE STILL TO CHECK...");
        } else if (simulator.getDay() == (dataCheck + simulator.canInfectDay + 1) && !check.isEmpty()) {
            int count = 0;
            int count2 = 0;
            for (Person p : check) {
                if (simulator.testVirus(p)) {
                    count++;
                    check.remove(p);
                }
                count2++;
                p.canMove = true;
                check.remove(p);
            }
            super.output(count + " PEOPLE INFECTED AND " + count2 + " FREE PEOPLE!");
        }
    }


    @Override
    public void personHasSymptoms(Person person) {
        this.sintomatici++;
        person.canMove = false;
    }

}
