package com.epidemic_simulator;

public class StrategyForbiddenAccessException extends RuntimeException {

    public StrategyForbiddenAccessException(){
        super("Strategy cannot access: " + "methodName");
    }
}
