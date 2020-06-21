package strategies;
import com.epidemic_simulator.Person;
import com.epidemic_simulator.Simulator;
import com.epidemic_simulator.Strategy;
import com.epidemic_simulator.Utils;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class PercentageLockdownAndPartialStopSpread extends Strategy{
    private int percentageOfLock;
    private boolean flag=false;
    private ArrayList<Person>move=new ArrayList<>();
    private int limite;

    public PercentageLockdownAndPartialStopSpread(Simulator simulator, int percentageOfLock){
        super(simulator);
        this.percentageOfLock=percentageOfLock;
    }

    @Override
    public void afterExecuteDay(Simulator.Outcome outcome) {
        if (simulator.getRedCount() == 1 && (!flag)) {
            int estrazione = (simulator.getAlivePopulation().size() * this.percentageOfLock) / 100;
            for (int i = 0; i < estrazione; i++) {
                Person randomPerson = simulator.getAlivePopulation().get(Utils.random(simulator.getAlivePopulation().size()));
                randomPerson.canMove = false;
            }
            flag = true;
            simulator.getAlivePopulation().stream().filter(person -> person.canMove).collect(Collectors.toCollection(() -> this.move));
            limite=2;
        }
        if (flag&&simulator.getRedCount()>=limite&&simulator.getResources()>=(originalResources*35)/100){
            for (Person p:move) {
                if(simulator.testVirus(p)){
                    p.canMove=false;
                }
            }
            limite+=2;
            move=simulator.getAlivePopulation().stream().filter(person -> person.canMove).collect(Collectors.toCollection(() -> this.move));
        }
    }
}
