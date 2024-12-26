package org.command.atm.component;

import org.command.atm.repository.Customer;
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
        return "Hello, "
                .concat(customer.getName())
                .concat("!")
                .concat("\n")
                .concat(BALANCE)
                .concat(String.valueOf(customer.getBalance()));
    }

    @ShellMethod(key = "deposit")
    public String deposit(@ShellOption Double amount) {
        Customer customer = atmService.deposit(amount);
        return BALANCE.concat(String.valueOf(customer.getBalance()));
    }

    @ShellMethod(key = "withdraw")
    public String withdraw(@ShellOption Double amount) {
        return BALANCE.concat(String.valueOf(amount));
    }

    @ShellMethod(key = "transfer")
    public String transfer(@ShellOption String target, @ShellOption Double amount) {

        Customer customer = atmService.getActiveCustomer();
        Customer targetCustomer = atmService.getCustomer(target);

        atmService.transfer(customer, targetCustomer, amount);

        return "Transferred $" +
                amount +
                " to " +
                targetCustomer.getName() +
                "\n" +
                BALANCE.concat(String.valueOf(customer.getBalance()));
    }

    @ShellMethod(key = "logout")
    public String logout() {
        Customer customer = atmService.getActiveCustomer();
        atmService.logout(customer);
        return "Good bye ".concat(customer.getName());
    }
}
