package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import com.epidemic_simulator.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MediumControlledLockdown extends Strategy {
    private int percentualOfStop;
    private int sintomatici=0;
    private int limite=0;
    private HashMap<Integer,ArrayList<Person>> check;

    public MediumControlledLockdown(Simulator simulator,int percentualOfStop) {
        super(simulator);
        this.percentualOfStop=percentualOfStop;
        this.limite=(simulator.alivePopulation.size()*percentualOfStop)/100;
        check= new HashMap<>();
    }

    @Override
    public void afterExecuteDay() {
        ArrayList<Person>persone = new ArrayList<>();
        ArrayList<Person>check1=new ArrayList<>();
        if(sintomatici>=this.limite){
            int estrazione=(simulator.alivePopulation.size()*this.sintomatici)/100;
            HashMap<Person, List<Person>> p=findEncounters(simulator.getDay());
            Set<Person>key=p.keySet();
            ArrayList<Person>chiavi=new ArrayList<>();
            for (Person a:key) {
                chiavi.add(a);
            }
            for (int i = 0; i < estrazione; i++) {
                Person randomPerson = chiavi.get(Utils.random(chiavi.size()));
                int controllo=0;
                for (Person r:check1) {
                    if(randomPerson.equals(r)){
                        i--;
                        controllo++;
                        break;
                    }
                }
                if(controllo!=0){
                    check1.add(randomPerson);
                    if(randomPerson.getColor()==Color.YELLOW||randomPerson.getColor()==Color.RED){
                        randomPerson.setCanMove(false);
                        for (Person tizio:p.get(randomPerson)) {
                            tizio.setCanMove(false);
                            persone.add(tizio);
                        }
                    }
                }
            }
            if(check.size()==0){
                check.put(simulator.getDay(),persone);
            }
            else {
                int controllo=0;
                for (Integer data:check.keySet()) {
                    if(data==simulator.getDay()){
                        ArrayList appoggio=check.get(data);
                        appoggio.addAll(persone);
                        check.put(data,appoggio);
                        controllo++;
                    }
                }
                if(controllo==0){
                    check.put(simulator.getDay(),persone);
                }
            }
        }
        if(check.size()!=0){
            for (int data:check.keySet()) {
                ArrayList<Person>p=check.get(data);
                for (Person t:p) {
                    if (t.getColor() == Color.RED) {
                        personClean(t);
                        p.remove(t);
                    }
                }
                if(simulator.getDay()==data+simulator.canInfectDay+simulator.developSymptomsMaxDay){
                    for (Person t:p) {
                        if(!simulator.testVirus(t)){
                            t.setCanMove(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void personHasSymptoms(Person person){
        this.sintomatici++;
        person.setCanMove(false);
    }

    @Override
    public void personClean(Person person) {
        this.sintomatici--;
        person.setCanMove(true);
    }

}
