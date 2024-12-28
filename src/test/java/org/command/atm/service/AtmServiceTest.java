package org.command.atm.service;

import org.command.atm.repository.model.Customer;
import org.command.atm.repository.model.Owed;
import org.command.atm.repository.model.OwedType;
import org.command.atm.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AtmServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    AtmService atmService;

    @Test
    void testLoginUnregisteredAccount() {
        String loginName = "Ahmad";
        Mockito.when(customerRepository.getCustomerByName(any(String.class))).thenReturn(null);
        Customer customer = atmService.login(loginName);
        Assertions.assertTrue(customer.isActive(), "Status active customer should be true");
    }

    @Test
    void testLoginRegisteredAccount() {
        String loginName = "Alice";
        Customer customer = getCustomer(loginName, 0.0);

        Mockito.when(customerRepository.getCustomerByName(any(String.class))).thenReturn(customer);
        Customer loggedInCustomer = atmService.login(loginName);
        Assertions.assertEquals(loggedInCustomer.getName(), loginName);
    }

    @Test
    void testDeposit100() {

        double amount = 100.0;
        String loginName = "Alice";

        Customer customer = getCustomer(loginName, 0.0);
        customer.setActive(true);

        Mockito.when(customerRepository.getActive()).thenReturn(customer);
        Customer updatedCustomer = atmService.deposit(amount);
        Assertions.assertEquals(updatedCustomer.getBalance(), amount,
                "Amount balance should be ".concat(String.valueOf(100.0)));
    }

    @Test
    void testLogout() {

        String loginName = "Alice";

        Customer customer = getCustomer(loginName, 0.0);
        customer.setActive(true);
        atmService.logout(customer);

        Assertions.assertFalse(customer.isActive(), "Customer "
                .concat(loginName)
                .concat(" status active should be false"));
    }

    @Test
    void testDeposit80() {
        double amount = 80.0;
        String loginName = "Ahmad";
        Customer customer = getCustomer(loginName, 0.0);
        customer.setActive(true);

        Mockito.when(customerRepository.getActive()).thenReturn(customer);
        Customer updatedCustomer = atmService.deposit(amount);
        Assertions.assertEquals(updatedCustomer.getBalance(), amount);
    }

    @Test
    void testTransfer50() {
        String loginName = "Ahmad";
        Customer sourceCustomer = getCustomer(loginName, 80.0);
        sourceCustomer.setActive(true);

        String targetName = "Husain";
        Customer targetCustomer = getCustomer(targetName, 100.0);

        atmService.transfer(sourceCustomer, targetCustomer, 50.0, null);

        Assertions.assertEquals(Double.valueOf(30.0),
                sourceCustomer.getBalance(), "Current customer "
                        .concat(loginName)
                        .concat(" balance ")
                        .concat(String.valueOf(30.0)));

        Assertions.assertEquals(Double.valueOf(150.0),
                targetCustomer.getBalance());
    }

    @Test
    void testTransfer100() {
        String loginName = "Ahmad";
        Customer sourceCustomer = getCustomer(loginName, 30.0);
        sourceCustomer.setActive(true);

        String targetName = "Husain";
        Customer targetCustomer = getCustomer(targetName, 150.0);

        atmService.transfer(sourceCustomer, targetCustomer, 100.0, null);

        Assertions.assertEquals(Double.valueOf(0.0),
                sourceCustomer.getBalance(), "Current logged customer "
                        .concat(sourceCustomer.getName())
                        .concat(" should be 0.0"));

        Assertions.assertEquals(Double.valueOf(180.0),
                targetCustomer.getBalance(), "Target customer "
                        .concat(targetCustomer.getName())
                        .concat(" should be 180.0"));

        Owed owed = sourceCustomer.getOweds().values().stream()
                .findFirst().orElse(Owed.createOwed());

        Assertions.assertEquals(Double.valueOf(70.0),
                owed.getAmount(), "Owed 70.0 to ".concat(targetCustomer.getName()));
    }

    @Test
    void testDeposit30() {
        String loginName = "Ahmad";
        String targetName = "Husain";

        Customer sourceCustomer = getCustomer(loginName, 0.0);
        sourceCustomer.setActive(true);
        sourceCustomer.getOweds().put("1", getOwed("1", targetName, OwedType.TO, 70));

        Customer targetCustomer = getCustomer(targetName, 180.0);
        targetCustomer.getOweds().put("1", getOwed("1", targetName, OwedType.FROM, 70));

        Mockito.when(customerRepository.getActive()).thenReturn(sourceCustomer);
        Mockito.when(customerRepository.getCustomerByName(sourceCustomer.getName())).thenReturn(sourceCustomer);
        Mockito.when(customerRepository.getCustomerByName(targetCustomer.getName())).thenReturn(targetCustomer);

        atmService.deposit(30.0);

        Assertions.assertEquals(Double.valueOf(0.0),
                sourceCustomer.getBalance(), "Current logged customer "
                        .concat(sourceCustomer.getName())
                        .concat(" should be 0.0"));

        Assertions.assertEquals(Double.valueOf(210.0),
                targetCustomer.getBalance(), "Target customer "
                        .concat(targetCustomer.getName())
                        .concat(" should be 210.0"));

        Owed owed = sourceCustomer.getOweds().values().stream()
                .filter(owed1 -> !owed1.isRemedy()).findFirst().orElseThrow();

        Assertions.assertFalse(owed.isRemedy(), "New Owed status should be false");
        Assertions.assertEquals(owed.getAmount(), Double.valueOf(40.0), "New owed should be 40.0");
    }

    @Test
    void testTransfer30() {
        String loginName = "Husain";
        String targetName = "Ahmad";

        Customer sourceCustomer = getCustomer(loginName, 210.0);
        sourceCustomer.setActive(true);
        sourceCustomer.getOweds().put("1", getOwed("1", targetName, OwedType.FROM, 40));

        Customer targetCustomer = getCustomer(targetName, 0.0);
        targetCustomer.getOweds().put("1", getOwed("1", targetName, OwedType.TO, 40));

        atmService.transfer(sourceCustomer, targetCustomer, 30.0, null);

        Assertions.assertEquals(Double.valueOf(210.0),
                sourceCustomer.getBalance(), "Current logged customer "
                        .concat(sourceCustomer.getName())
                        .concat(" should be 210.0"));

        Assertions.assertEquals(Double.valueOf(0.0),
                targetCustomer.getBalance(), "Target customer "
                        .concat(targetCustomer.getName())
                        .concat(" should be 0.0"));

        Owed owed = sourceCustomer.getOweds().values().stream()
                .filter(owed1 -> !owed1.isRemedy()).findFirst().orElseThrow();

        Assertions.assertFalse(owed.isRemedy(), "Owed status should be false");
        Assertions.assertEquals(owed.getAmount(), Double.valueOf(10.0));
    }

    @Test
    void testDeposit100WithOwed10() {
        String loginName = "Ahmad";
        String targetName = "Husain";

        Customer sourceCustomer = getCustomer(loginName, 0.0);
        sourceCustomer.setActive(true);
        sourceCustomer.getOweds().put("1", getOwed("1", targetName, OwedType.TO, 10));

        Customer targetCustomer = getCustomer(targetName, 210.0);
        targetCustomer.getOweds().put("1", getOwed("1", targetName, OwedType.FROM, 10));

        Mockito.when(customerRepository.getActive()).thenReturn(sourceCustomer);
        Mockito.when(customerRepository.getCustomerByName(sourceCustomer.getName())).thenReturn(sourceCustomer);
        Mockito.when(customerRepository.getCustomerByName(targetCustomer.getName())).thenReturn(targetCustomer);

        atmService.deposit(100.0);

        Assertions.assertEquals(Double.valueOf(90.0),
                sourceCustomer.getBalance(), "Current logged customer "
                        .concat(sourceCustomer.getName())
                        .concat(" should be 90.0"));

        Assertions.assertEquals(Double.valueOf(220.0),
                targetCustomer.getBalance(), "Target customer "
                        .concat(targetCustomer.getName())
                        .concat(" should be 220.0"));

        Owed owed = sourceCustomer.getOweds().values().stream()
                .filter(owed1 -> owed1.getId().equals("1")).findFirst().orElseThrow();

        Assertions.assertTrue(owed.isRemedy(), "New Owed status should be true");
    }

    private static Customer getCustomer(String targetName, Double balance) {
        Customer targetCustomer = Customer.createCustomer();
        targetCustomer.setName(targetName);
        targetCustomer.setBalance(balance);
        return targetCustomer;
    }

    private static Owed getOwed(String key, String name, OwedType owedType, double amount) {
        Owed owed = Owed.createOwed();
        owed.setId(key);
        owed.setName(name);
        owed.setRemedy(false);
        owed.setOwedType(owedType);
        owed.setAmount(amount);
        return owed;
    }

}
