package lk.banking.core.service;

import lk.banking.core.model.Account;
import java.math.BigDecimal;

public interface ManagerTransferService {

    boolean transferMoneyToCustomer(String accountNumber, BigDecimal amount, String note);
    Account findAccountByAccountNumber(String accountNumber);
    boolean updateAccountBalance(String accountNumber, BigDecimal newBalance);
}