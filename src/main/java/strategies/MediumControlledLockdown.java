package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import com.epidemic_simulator.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MediumControlledLockdown extends Strategy {
    private int sintomatici=0;
    private int limite=0;
    private HashMap<Integer,ArrayList<Person>> check;
    private ArrayList<Person>alreadyInfected=new ArrayList<>();
    private boolean flag=true;
    private int percentualOfBlock=0;

    public MediumControlledLockdown(Simulator simulator,int percentualOfStop,int percentualOfBlock) {
        super(simulator);
        this.limite=(simulator.getAlivePopulation().size()*percentualOfStop)/100;
        check= new HashMap<>();
        this.percentualOfBlock=percentualOfBlock;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        ArrayList<Person> persone = new ArrayList<>();
        ArrayList<Person> extracted = new ArrayList<>();
        if(simulator.getRedCount()>=this.limite&&flag){
            int countCheck=0;
            int countYel=0;
            int estrazione=((simulator.getAlivePopulation().size()*this.percentualOfBlock)/100)+simulator.getRedCount();
            //extracting 'estrazione' from alive population
            for (int i = 0; i < estrazione; i++) {
                Person randomPerson = simulator.getAlivePopulation().get(Utils.random(simulator.getAlivePopulation().size()));
                if(!extracted.contains(randomPerson)){
                    extracted.add(randomPerson);
                    //testing the extracted person
                    if(simulator.testVirus(randomPerson)){
                        countYel++;
                        randomPerson.canMove = false;
                        //if it's positive i stop all his encounters
                        for (Person tizio:findEncounters(randomPerson, simulator.getDay())) {
                            if(!persone.contains(tizio)&&(!extracted.contains(tizio))&&(!alreadyInfected.contains(tizio))){
                                countCheck++;
                                tizio.canMove = false;
                                persone.add(tizio);
                            }
                        }
                    }
                } else {
                    i--;
                }
            }
            check.put(simulator.getDay(),persone);
            super.output(countYel+" YELLOW STOPPED AND "+countCheck+" MORE PEOPLE TO CHECK ON DAY: "+(simulator.getDay()+simulator.canInfectDay+1));
        }
        if(simulator.getResources()<=((originalResources*45)/100)){
            emergencyCheck();//Metodo usato per fare l'ultimo dei check laddove le risorse stiano scendendo troppo...
            check.clear();
            flag=false;
        }
        System.out.println(sintomatici+" "+limite);
        timeCheck();//Metodo richiamato giornalmente per verificare eventualmente le persone inserite in "check"...
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

    public void timeCheck(){
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
                        else this.alreadyInfected.add(t);
                }
                super.output(count+" PEOPLE SET FREE!");
            }
        }
        check.remove(dayToRemove);
    }

    public void emergencyCheck(){
        for (int data:check.keySet()) {
            for (Person t:check.get(data)) {
                    if(!simulator.testVirus(t)){
                        t.canMove = true;
                    }
                    else this.alreadyInfected.add(t);
            }
        }
    }
}
