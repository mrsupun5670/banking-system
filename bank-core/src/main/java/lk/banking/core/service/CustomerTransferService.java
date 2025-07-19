package lk.banking.core.service;

import lk.banking.core.model.Account;
import lk.banking.core.model.TransferResult;
import lk.banking.core.model.User;
import java.math.BigDecimal;

public interface CustomerTransferService {

    TransferResult transferMoney(String senderAccountNumber, String recipientAccountNumber,
                                 BigDecimal amount, String note);
    Account getAccountByAccountNumber(String accountNumber);
    User getUserByAccountNumber(String accountNumber);
    boolean hasSufficientBalance(String accountNumber, BigDecimal amount);


}