package org.command.atm.component;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.annotation.DirtiesContext;

@ShellTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ComponentScan(basePackages = "org.command.atm")
class AtmCommandTest {

    @Autowired
    private AtmCommand atmCommand;

    @Test
    void testLoginAlice()  {
        String loginName = "Alice";
        String result = atmCommand.login(loginName);
        String expected = "Hello, ".concat(loginName).concat("!\n")
                .concat("Your balance is $0");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testLogoutAlice()  {
        String loginName = "Alice";
        String result = atmCommand.login(loginName);
        String expected = "Hello, ".concat(loginName).concat("!\n")
                .concat("Your balance is $0");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.logout();
        expected = "Goodbye, Alice!";
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testDepositAlice()  {
        String loginName = "Alice";
        atmCommand.login(loginName);
        String result = atmCommand.deposit(100.0);
        String expected = "Your balance is $100";
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void testIntegration() {

        loginAsAlice();

        loginAsBob();

        //Login Alice
        String loginName = "Alice";
        String result = atmCommand.login(loginName);
        String expected = "Hello, ".concat(loginName).concat("!\n")
                .concat("Your balance is $210").concat("\n")
                .concat("Owed $40 from Bob");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.transfer("Bob", 30.0);
        expected = "Your balance is $210".concat("\n")
                .concat("Owed $10 from Bob");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.logout();
        expected = "Goodbye, Alice!";

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        loginName = "Bob";
        result = atmCommand.login(loginName);
        expected = "Hello, ".concat(loginName).concat("!\n")
                .concat("Your balance is $0").concat("\n")
                .concat("Owed $10 to Alice");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.deposit(100.0);
        expected = "Transferred $10 to Alice".concat("\n")
                .concat("Your balance is $90");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.logout();
        expected = "Goodbye, Bob!";

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    private void loginAsAlice() {
        String loginName = "Alice";
        String result = atmCommand.login(loginName);

        String expected = "Hello, "
                .concat(loginName)
                .concat("!\n")
                .concat("Your balance is $0");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.deposit(100.0);
        expected = "Your balance is $100";

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.logout();
        expected = "Goodbye, ".concat(loginName).concat("!");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    private void loginAsBob() {
        String loginName = "Bob";
        String result = atmCommand.login(loginName);
        String expected = "Hello, "
                .concat(loginName).concat("!\n")
                .concat("Your balance is $0");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.deposit(80.0);

        expected = "Your balance is $80";
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.transfer("Alice", 50.0);
        expected = "Transferred $50 to Alice".concat("\n")
                .concat("Your balance is $30");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.transfer("Alice", 100.0);
        expected = "Transferred $30 to Alice".concat("\n")
                .concat("Your balance is $0").concat("\n")
                .concat("Owed $70 to Alice");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.deposit(30.0);
        expected = "Transferred $30 to Alice".concat("\n")
                .concat("Your balance is $0").concat("\n")
                .concat("Owed $40 to Alice");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);

        result = atmCommand.logout();
        expected = "Goodbye, Bob!";

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }


}
