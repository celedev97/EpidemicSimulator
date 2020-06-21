package com.epidemic_simulator;

import com.epidemic_simulator.exceptions.StrategyForbiddenAccessException;

import javax.rmi.CORBA.Util;
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

/**
 * Utility class for the Epidemic Simulator project.<BR>
 * It contains some method for extracting random numbers easily, some other for reflections and some utilities for UI.
 */
public class Utils {

    private Utils(){}

    /**
     * Generate a random int between 0 and maxValue(excluded)
     *
     * @param maxValue the maximum value to generate (excluded)
     * @return the random number
     */
    public static int random(int maxValue) {
        return random(0,maxValue);
    }

    /**
     * Generate a random int between minValue(included) and maxValue(excluded)
     *
     * @param minValue the minimum value to generate
     * @param maxValue the maximum value to generate (excluded)
     * @return the random number
     */
    public static int random(int minValue, int maxValue) {
        return minValue + ThreadLocalRandom.current().nextInt(maxValue-minValue);
    }


    /**
     * Generate a boolean, that has {@code truePercentage}% possibilities of being true.
     *
     * @param truePercentage The percentage rate for the value true.
     * @return the boolean generated.
     */
    public static boolean randomBool(int truePercentage){
        return random(0,100) < truePercentage;
    }

    /**
     * This functions convert a method or a class name into a human readable string.<BR>
     * It only works with strings that do not contains special characters.<BR>
     * Example: animalCageBuilder - Animal cage builder
     *
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

    /**
     * Extract all the {@link JSpinner}s from a {@link Container} recursively.
     *
     * @param container the container to use for the search
     * @return The {@link List} of JSpinners found
     */
    public static List<JSpinner> getJSpinners(final Container container) {
        Component[] comps = container.getComponents();
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

    /**
     * Force the commit of pending edits on all the {@link JSpinner}s inside a {@link Container} and its children.
     *
     * @param container the container to use for the search
     */
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

    /**
     * Convert a strategy class into a user readable string.
     *
     * @param strategy The strategy class
     * @return the strategy name as a user readable string.
     */
    public static String getStrategyName(Class<?> strategy) {
        if (strategy == null)
            return "No strategy used";
        return Utils.javaNameToUserString(strategy.toString());
    }

    /**
     * Convert a strategy into a user readable string.
     *
     * @param strategy The strategy
     * @return the strategy name as a user readable string.
     */
    public static String getStrategyName(Strategy strategy) {
        if(strategy == null)
            return getStrategyName((Class)null);
        return getStrategyName(strategy.getClass());
    }

    /**
     * Throw an exception if there's a strategy in the calls stack.
     *
     */
    public static void negateStrategyAccess() {
        //TODO: TEST!!! TODO: REPLACE WITH AN ASSERTION!!!
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
