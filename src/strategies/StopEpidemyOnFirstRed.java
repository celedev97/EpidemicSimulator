package strategies;

import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;

public class StopEpidemyOnFirstRed extends Strategy {
    private int sintomatici=0;
    private int controllo=0;
    private int data_check = 0;

    public StopEpidemyOnFirstRed(Simulator simulator){
        super(simulator);
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        if(this.controllo==0){
            data_check=simulator.getDay();
            System.out.println("DATA: "+data_check+" CANINFECTDAY: "+simulator.canInfectDay+" SYMPTOMSMAXDAY: "+simulator.developSymptomsMaxDay);
            System.out.println("BEGINING OF THE LOCKDOWN UNTIL: "+((data_check+simulator.canInfectDay+1))+" DAY!");
            for (Person p:simulator.getAlivePopulation()) {
                p.setCanMove(false);
            }
            this.controllo++;
        }
        //System.out.println(this.controllo>0&&simulator.getDay()==(data_check+simulator.canInfectDay+simulator.developSymptomsMaxDay));
        if(this.controllo>0&&simulator.getDay()==((data_check+simulator.canInfectDay+1))){
            System.out.println("START OF THE CONTROLL!");
            int count=0;
            for (Person p:simulator.getAlivePopulation()){
                if(!simulator.testVirus(p)){
                    count++;
                    p.setCanMove(true);
                }
            }
            System.out.println(count+" persone rimesse in libertà!");
            data_check=0;
        }
    }
}
