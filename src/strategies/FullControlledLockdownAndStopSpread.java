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
                                    p.setCanMove(false);
                                }
                                else{
                                    p.setCanMove(false);
                                }
                            }
                            else {
                                return;
                            }
                        }
                    }
                    key.setCanMove(false);
                }
            }
            this.sintomatici=0;
        }
        else if(simulator.getDay()<=(data_check+simulator.canInfectDay+simulator.developSymptomsMaxDay) && check.size()>0) {
            for (Person p : check) {
                if (p.getColor() == Color.RED) this.personClean(p);
                check.remove(p);
            }
        }
        else if(simulator.getDay()==(data_check+simulator.canInfectDay+simulator.developSymptomsMaxDay) && check.size()>0) {
            for (Person p : check) {
                if (simulator.testVirus(p)) {
                    check.remove(p);
                }
                p.setCanMove(true);
                check.remove(p);
            }
        }
        return;
    }


    @Override
    public void personHasSymptoms(Person person){
        this.sintomatici++;
        person.setCanMove(false);
    }

}
