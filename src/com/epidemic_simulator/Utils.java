package com.epidemic_simulator;

import com.epidemic_simulator.exceptions.StrategyForbiddenAccessException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.*;

public class Utils {
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
        return minValue + ThreadLocalRandom.current().nextInt(maxValue-minValue);
    }


    /** Generate a boolean, that has *truePercentage*% possibilities of being true.
     * @param truePercentage The percentage rate for the value true.
     * @return the boolean generated.
     */
    public static boolean randomBool(int truePercentage){
        return random(0,100) < truePercentage;
    }

    /**
     * This functions convert a method or a class name into a human readable string.
     * It only works with strings that do not contains special characters.
     * Example: animalCageBuilder => Animal cage builder
     * @param javaName the method or class name
     * @return the human readable string
     */
    public static String javaNameToUserString(String javaName) {
        String output = "";

        //removing package name
        int lastPoint;
        if((lastPoint = javaName.lastIndexOf('.'))>0)
            javaName = javaName.substring(lastPoint+1);

        //putting spaces between words
        for (int i = 0; i < javaName.length(); i++) {
            char charAtI = javaName.charAt(i);
            if(Character.isUpperCase(charAtI))
                output += " ";
            output += charAtI;
        }

        //removing first space
        if(output.indexOf(' ') == 0)
            output = output.substring(1);

        //making string lowercase except first character
        output = output.charAt(0) + output.substring(1).toLowerCase();

        return output;
    }

    public static List<JSpinner> getJSpinners(final Container c) {
        Component[] comps = c.getComponents();
        List<JSpinner> compList = new ArrayList<>();
        for (Component comp : comps) {
            if(comp instanceof JSpinner){
                compList.add((JSpinner)comp);
            }else if (comp instanceof Container){
                compList.addAll(getJSpinners((Container) comp));
            }
        }
        return compList;
    }

    public static void forceJSpinnerCommit(Container container) {
        getJSpinners(container).forEach(jSpinner -> {
            try {
                jSpinner.commitEdit();
            } catch (ParseException ignored) {
                //even if this exception get thrown the spinner will still have a valid value, so there's no need to do anything.
                ignored.printStackTrace();
            }
        });
    }

    public static String getStrategyName(Class<?> strategy) {
        if (strategy == null)
            return "No strategy used";
        return Utils.javaNameToUserString(strategy.toString());
    }

    public static String getStrategyName(Strategy strategy) {
        if(strategy == null)
            return getStrategyName((Class)null);
        return getStrategyName(strategy.getClass());
    }

    public static void negateStrategyAccess() {
        //TODO: TEST!!!
        try {
            Class callerClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (Strategy.class.isAssignableFrom(callerClass)) {
                throw new StrategyForbiddenAccessException("methodName");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
