package lk.banking.core.service;

import lk.banking.core.model.User;
import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    List<User> getAllCustomers();
    long getTotalCustomerCount();
    BigDecimal getTotalDeposits();
}