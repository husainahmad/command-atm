package org.command.atm.component;

import org.command.atm.exception.UserNotFoundException;
import org.command.atm.exception.IllegalStateException;
import org.command.atm.exception.InvalidInputException;
import org.command.atm.repository.model.Customer;
import org.command.atm.repository.model.Owed;
import org.command.atm.service.AtmService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.validation.annotation.Validated;

@ShellComponent
public class AtmCommand {

    private final AtmService atmService;
    private static final String BALANCE = "Your balance is $";

    public AtmCommand(AtmService atmService) {
        this.atmService = atmService;
    }

    @ShellMethod(key = "login")
    public String login(@ShellOption String user) {
        Customer customer;
        if (atmService.isAnyActiveCustomer()) throw new IllegalStateException("You are in logged in session, " +
                "please logout and login again");
        try {
            customer = atmService.login(user);
        } catch (UserNotFoundException e) {
            atmService.createCustomer(user);
            customer = atmService.login(user);
        }
        return "Hello, ".concat(customer.getName())
                .concat("!").concat("\n")
                .concat(getStringBuilder(customer).toString());
    }

    @ShellMethod(key = "deposit")
    public String deposit(@ShellOption @Validated Double amount) {
        if (amount == null || amount <1) throw new InvalidInputException("Deposit amount must be not empty and > 0");
        Customer customer = atmService.deposit(amount);
        StringBuilder value = getStringBuilder(customer);
        return value.toString();
    }

    @ShellMethod(key = "withdraw")
    public String withdraw(@ShellOption Double amount) {
        return BALANCE.concat(String.valueOf(amount.intValue()));
    }

    @ShellMethod(key = "transfer")
    public String transfer(@ShellOption String target, @ShellOption Double amount) {
        if (amount == null || amount <1) throw new InvalidInputException("Transfer amount must be not empty and > 0");
        Customer customer = atmService.getActiveCustomer();
        Customer targetCustomer = atmService.getCustomer(target);

        atmService.transfer(customer, targetCustomer, amount, null);
        StringBuilder value = getStringBuilder(customer);

        return value.toString();
    }

    private static StringBuilder getStringBuilder(Customer customer) {
        StringBuilder value = new StringBuilder();

        customer.getDebits().forEach((s, debit) -> {
            value.append("Transferred $");
            value.append(debit.getAmount().intValue());
            value.append(" to ");
            value.append(debit.getName());
            value.append("\n");
        });

        value.append(BALANCE);
        value.append(customer.getBalance().intValue());

        for (Owed owed : customer.getOweds().values()) {
            if (!owed.isRemedy()) {
                value.append("\n");
                value.append("Owed $")
                        .append(owed.getAmount().intValue())
                        .append(" ")
                        .append(owed.getOwedType().toString().toLowerCase())
                        .append(" ")
                        .append(owed.getName());
            }
        }
        return value;
    }

    @ShellMethod(key = "logout")
    public String logout() {
        Customer customer = atmService.getActiveCustomer();
        atmService.logout(customer);
        return "Goodbye, ".concat(customer.getName().concat("!"));
    }
}
