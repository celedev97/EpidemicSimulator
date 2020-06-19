package strategies;
import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FullControlledLockdownAndStopSpread extends Strategy {
    private int percentualOfStop;
    private int sintomatici=0;
    private int limite=0;
    private ArrayList<Person> check;

    public FullControlledLockdownAndStopSpread(Simulator simulator,int percentualOfStop) {
        super(simulator);
        this.percentualOfStop=percentualOfStop;
        this.limite=(simulator.getAlivePopulation().size()*percentualOfStop)/100;
        check=new ArrayList<>();
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        int data_check = 0;
        if(sintomatici>=this.limite){
            super.output("MAXIMUM LIMIT REACHED: "+sintomatici+" CASE CONFIRMED ->PROCEED TO THE FULL LOCKDOWN FROM TODAY!");
            HashMap<Person,List<Person>>person;
            data_check=simulator.getDay();
            for (int i = 0; i < simulator.getDay(); i++) {
                person=findEncounters(i);
                for (Person key:person.keySet()) {
                    if(key.getColor()==Color.YELLOW){
                        for (int y=0;y<(findEncounters(key,i).size());y++) {
                            Person p=findEncounters(key,i).get(y);
                            if(simulator.getResources()>=simulator.testPrice){
                                if(p.getColor()==Color.RED){
                                    personClean(p);
                                }
                                else if(!simulator.testVirus(p)){
                                    check.add(p);
                                    p.canMove = false;
                                }
                                else{
                                    p.canMove = false;
                                }
                            }
                            else {
                                return;
                            }
                        }
                    }
                    key.canMove = false;
                }
            }
            this.sintomatici=0;
            super.output(check.size()+" PERSON STILL TO CHECK...");
        }
        else if(simulator.getDay()==(data_check+simulator.canInfectDay+1) && check.size()>0) {
            int count=0;
            int count2=0;
            for (Person p : check) {
                if (simulator.testVirus(p)) {
                    count++;
                    check.remove(p);
                }
                count2++;
                p.canMove = true;
                check.remove(p);
            }
            super.output(count+" PERSON INFECTED AND "+count2+" PERSON FREE!");
        }
        return;
    }


    @Override
    public void personHasSymptoms(Person person){
        this.sintomatici++;
        person.canMove = false;
    }

}
