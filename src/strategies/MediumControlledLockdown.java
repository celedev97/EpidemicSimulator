package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import com.epidemic_simulator.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class MediumControlledLockdown extends Strategy {
    private int percentualOfStop;
    private int sintomatici=0;
    private int limite=0;
    private HashMap<Integer,ArrayList<Person>> check;
    private ArrayList<Person>AlreadyInfected=new ArrayList<>();
    private boolean flag=true;

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
        if(simulator.getRedCount()>=this.limite&&flag){
            int count_check=0;
            int count_yel=0;
            int estrazione=((simulator.getAlivePopulation().size()*15)/100)+simulator.getRedCount();
            //super.output("MAXIMUM LIMIT REACHED: "+simulator.getRedCount()+" INFECTED CONFIRMED ->PROCEED TO EXTRACTION OF: "+estrazione+" PEOPLE!");
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
                            if(!persone.contains(tizio)&&(!extracted.contains(tizio))&&(!AlreadyInfected.contains(tizio))){
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
            check.put(simulator.getDay(),persone);
            super.output(count_yel+" YELLOW STOPPED AND STILL "+count_check+" PERSON TO CHECK AT DAY: "+(simulator.getDay()+simulator.canInfectDay+1));
        }
        if(simulator.getResources()<=((originalResources*45)/100)){
            EmergencyCheck();//Metodo usato per fare l'ultimo dei check laddove le risorse stiano scendendo troppo...
            check.clear();
            flag=false;
        }
        System.out.println(sintomatici+" "+limite);
        TimeCheck();//Metodo richiamato giornalmente per verificare eventualmente le persone inserite in "check"...
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

    public void TimeCheck(){
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
                        else this.AlreadyInfected.add(t);
                }
                super.output(count+" PERSON SET FREE!");
            }
        }
        check.remove(dayToRemove);
    }

    public void EmergencyCheck(){
        for (int data:check.keySet()) {
            for (Person t:check.get(data)) {
                    if(!simulator.testVirus(t)){
                        t.canMove = true;
                    }
                    else this.AlreadyInfected.add(t);
            }
        }
    }
}
