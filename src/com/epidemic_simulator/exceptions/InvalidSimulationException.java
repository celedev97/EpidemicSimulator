package com.epidemic_simulator.exceptions;

public class InvalidSimulationException extends Exception{
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    public InvalidSimulationException(String message) {
        this.message = message;
    }

}
