package com.epidemic_simulator.exceptions;

public class StrategyForbiddenAccessException extends RuntimeException {

    public StrategyForbiddenAccessException(String methodName){
        super("Strategy cannot access: " + methodName);
    }
}
