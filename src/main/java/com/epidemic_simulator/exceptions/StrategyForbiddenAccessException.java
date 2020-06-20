package com.epidemic_simulator.exceptions;

/**
 * This Exception can be thrown when trying to access data that the Strategy shouldn't access at all.<BR>
 * It is a {@link RuntimeException} so there's no need to surround all the function calls
 * to {@link com.epidemic_simulator.Simulator} getters with try/catch.<BR>
 * <BR>
 * This should <b>never</b> be thrown unless a Strategy try to do an illegal operation.<BR>
 * (For example accessing the number of yellows without testing them)
 */
public class StrategyForbiddenAccessException extends RuntimeException {

    /**
     * Instantiates a new Strategy forbidden access exception.
     *
     * @param methodName The name of the method that was called and caused this exception.
     */
    public StrategyForbiddenAccessException(String methodName){
        super("Strategy cannot access: " + methodName);
    }
}
