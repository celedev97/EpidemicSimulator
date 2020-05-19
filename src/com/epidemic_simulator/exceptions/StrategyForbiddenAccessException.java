package com.epidemic_simulator.exceptions;

public class StrategyForbiddenAccessException extends RuntimeException {

    public StrategyForbiddenAccessException(){
        super("Strategy cannot access: " + "methodName");
    }
}
