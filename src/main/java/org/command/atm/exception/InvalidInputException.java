package org.command.atm.exception;

public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String s) {
        super(s);
    }
}
