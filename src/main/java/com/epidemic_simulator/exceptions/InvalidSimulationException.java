package com.epidemic_simulator.exceptions;

/**
 * This Exception can be thrown when trying to instantiate a {@link com.epidemic_simulator.Simulator}
 * with invalid parameters (according to the project specs).
 */
public class InvalidSimulationException extends Exception {
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Instantiates a new Invalid simulation exception.
     *
     * @param message the message associated with the exception
     * @see #getMessage
     */
    public InvalidSimulationException(String message) {
        this.message = message;
    }

}
