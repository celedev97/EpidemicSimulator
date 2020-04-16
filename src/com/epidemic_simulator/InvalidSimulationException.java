package com.epidemic_simulator;

public class InvalidSimulationException extends Exception{
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    public InvalidSimulationException(String message) {
        this.message = message;
    }
}
