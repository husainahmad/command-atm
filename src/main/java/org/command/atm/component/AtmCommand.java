package org.command.atm.component;

import org.command.atm.model.Customer;
import org.command.atm.model.Owed;
import org.command.atm.service.AtmService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class AtmCommand {

    private final AtmService atmService;
    private static final String BALANCE = "Your balance is $";

    public AtmCommand(AtmService atmService) {
        this.atmService = atmService;
    }

    @ShellMethod(key = "login")
    public String login(@ShellOption String user) {
        Customer customer = atmService.login(user);
        return "Hello ".concat(customer.getName())
                .concat("!").concat("\n")
                .concat(getStringBuilder(customer).toString());
    }

    @ShellMethod(key = "deposit")
    public String deposit(@ShellOption Double amount) {
        Customer customer = atmService.deposit(amount);
        StringBuilder value = getStringBuilder(customer);
        return value.toString();
    }

    @ShellMethod(key = "withdraw")
    public String withdraw(@ShellOption Double amount) {
        return BALANCE.concat(String.valueOf(amount));
    }

    @ShellMethod(key = "transfer")
    public String transfer(@ShellOption String target, @ShellOption Double amount) {

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
            value.append(debit.getAmount());
            value.append(" to ");
            value.append(debit.getName());
            value.append("\n");
        });

        value.append(BALANCE);
        value.append(customer.getBalance());

        for (Owed owed : customer.getOweds().values()) {
            if (!owed.isRemedy()) {
                value.append("\n");
                value.append("Owed $")
                        .append(owed.getAmount())
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
        return "Goodbye ".concat(customer.getName());
    }
}
