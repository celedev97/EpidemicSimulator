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
        this.limite=(simulator.getAlivePopulation().size()*percentualOfStop)/100;
        check= new HashMap<>();
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        ArrayList<Person> persone = new ArrayList<>();
        ArrayList<Person> extracted = new ArrayList<>();
        if(sintomatici>=this.limite){
            int count_check=0;
            int count_yel=0;
            int estrazione=(simulator.getAlivePopulation().size()*this.sintomatici)/100;
            //super.output("MAXIMUM LIMIT REACHED: "+sintomatici+" INFECTED CONFIRMED ->PROCEED TO EXTRACTION OF: "+estrazione+" PEOPLE!");
            //extracting 'estrazione' from alive population
            for (int i = 0; i < estrazione; i++) {
                Person randomPerson = simulator.getAlivePopulation().get(Utils.random(simulator.getAlivePopulation().size()));
                if(!extracted.contains(randomPerson)){
                    extracted.add(randomPerson);
                    //testing the extracted person
                    if(simulator.testVirus(randomPerson)){
                        count_yel++;
                        randomPerson.canMove = false;
                        //if it's positive i stop all his encounters
                        for (Person tizio:findEncounters(randomPerson, simulator.getDay())) {
                            if(!persone.contains(tizio)&&(!extracted.contains(tizio))){
                                count_check++;
                                tizio.canMove = false;
                                persone.add(tizio);
                            }
                        }
                    }
                } else {
                    i--;
                }
            }
            //this.sintomatici=0;
            check.put(simulator.getDay(),persone);
            super.output(count_yel+" YELLOW STOPPED AND STILL "+count_check+" PERSON TO CHECK AT DAY: "+(simulator.getDay()+simulator.canInfectDay+1));
        }
        System.out.println(sintomatici+" "+limite);
        /* cancellare dopo se funziona lo stesso
        if(check.size()==0){
            check.put(simulator.getDay(),persone);
        } else {
            if(check.containsKey(simulator.getDay())){
                check.get(simulator.getDay()).addAll(persone);
            }
        }//*/

        int dayToRemove = -1;
        for (int data:check.keySet()) {
            int count=0;
            if(simulator.getDay()==(data+simulator.canInfectDay+1)){
                dayToRemove = data;
                for (Person t:check.get(data)) {
                    if(!simulator.testVirus(t)){
                        count++;
                        t.canMove = true;
                    }
                }
                super.output(count+" PERSON SET FREE!");
            }
        }
        check.remove(dayToRemove);
        System.out.println(sintomatici+" "+limite);
    }

    @Override
    public void personHasSymptoms(Person person){
        this.sintomatici++;
        person.canMove = false;
    }

    @Override
    public void personClean(Person person) {
        this.sintomatici--;
        person.canMove = true;
    }

}
