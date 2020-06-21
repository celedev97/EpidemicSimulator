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
    private int dataCheck = 0;
    private boolean flag=true;
    private ArrayList<Person> YelFound=new ArrayList<>();

    public FullControlledLockdownAndStopSpread(Simulator simulator, int percentualOfStop) {
        super(simulator);
        this.limite = (simulator.getAlivePopulation().size() * percentualOfStop) / 100;
        check = new ArrayList<>();
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        if (sintomatici >= this.limite&&flag) {
            flag=false;
            super.output("MAXIMUM LIMIT REACHED: " + sintomatici + " CONFIRMED CASES -> PROCEED TO FULL LOCKDOWN FROM TODAY!");
            HashMap<Person, List<Person>> person;
            this.dataCheck = simulator.getDay();
            for (int i = 0; i < simulator.getDay(); i++) {
                person = findEncounters(i);
                for (Person key : person.keySet()) {
                    if (!YelFound.contains(key)) {
                        if (key.getColor()==Color.YELLOW) {
                            YelFound.add(key);
                            for (int y = 0; y < (findEncounters(key, i).size()); y++) {
                                Person p = findEncounters(key, i).get(y);
                                if (simulator.getResources() >= simulator.testPrice) {
                                    if (p.getColor() == Color.RED) {
                                        personClean(p);
                                    } else if(!check.contains(p)) {
                                        if(!simulator.testVirus(p)){
                                            check.add(p);
                                        }
                                        else {
                                            YelFound.add(p);
                                        }
                                        p.canMove = false;
                                    }
                                } else {
                                    return;
                                }
                            }
                            key.canMove = false;
                        }
                    }
                }
            }
            this.sintomatici = 0;
            super.output(check.size() + " PEOPLE STILL TO CHECK...");
        } else if (simulator.getDay() == (this.dataCheck + simulator.canInfectDay + 1) && !check.isEmpty()) {
            int count = 0;
            int count2 = 0;
            for (Person p : check) {
                if(!YelFound.contains(p)){
                    if (simulator.testVirus(p)) {
                        count++;
                        YelFound.add(p);
                        //check.remove(p);
                    }
                    else {
                        count2++;
                        p.canMove = true;
                        //check.remove(p);
                    }
                }
            }
            super.output(count + " PEOPLE INFECTED AND " + count2 + " FREE PEOPLE!");
            flag=true;
            check.clear();
        }
    }


    @Override
    public void personHasSymptoms(Person person) {
        this.sintomatici++;
        person.canMove = false;
    }

}
