package org.command.atm.exception.handler;


import org.command.atm.exception.UserFoundException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.shell.command.CommandExceptionResolver;
import org.springframework.shell.command.CommandHandlingResult;
import org.springframework.stereotype.Component;

@Component
public class GlobalErrorHandler implements CommandExceptionResolver {

    @Override
    public CommandHandlingResult resolve(Exception ex) {
        if (ex instanceof UserFoundException || ex instanceof InvalidInputException) {
            return CommandHandlingResult.of(ex.getMessage().concat("\n"), 42);
        }
        if (ex instanceof ConversionFailedException) {
            return CommandHandlingResult.of(ex.getMessage().concat("\n"), 42);
        }
        return null;
    }
}
