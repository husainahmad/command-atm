package org.command.atm.exception;

public class UserFoundException extends RuntimeException {
    public UserFoundException(String s) {
        super(s);
    }
}
