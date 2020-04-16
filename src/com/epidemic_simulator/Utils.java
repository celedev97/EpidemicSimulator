package com.epidemic_simulator;

import java.time.LocalDateTime;
import java.util.Random;

public class Utils {
    private static Random rand;
    static{
        rand = new Random(LocalDateTime.now().getNano());
    }

    /** Generate a random int between 0 and maxValue(excluded)
     * @param maxValue the maximum value to generate (excluded)
     * @return the random number
     */
    public static int random(int maxValue) {
        return random(0,maxValue);
    }

    /** Generate a random int between minValue(included) and maxValue(excluded)
     * @param minValue the minimum value to generate
     * @param maxValue the maximum value to generate (excluded)
     * @return the random number
     */
    public static int random(int minValue, int maxValue) {
        return minValue + rand.nextInt(maxValue-minValue);
    }


    /** Generate a boolean, that has *truePercentage*% possibilities of being true.
     * @param truePercentage The percentage rate for the value true.
     * @return the boolean generated.
     */
    public static boolean randomBool(int truePercentage){
        return random(0,100) < truePercentage;
    }

}
