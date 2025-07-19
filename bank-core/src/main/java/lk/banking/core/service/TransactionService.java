package lk.banking.core.service;

import jakarta.ejb.Local;
import lk.banking.core.model.Transaction;
import lk.banking.core.model.Account;
import lk.banking.core.model.TransactionType;
import lk.banking.core.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Local
public interface TransactionService {

    Transaction createTransaction(Long accountId, BigDecimal amount, TransactionType transactionType, String description);

    Transaction createTransaction(Long accountId, Long referenceAccountId, BigDecimal amount, TransactionType transactionType, String description);

    Transaction processDebit(Long accountId, BigDecimal amount, String description);

    Transaction processCredit(Long accountId, BigDecimal amount, String description);

    Transaction processTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description);

    Transaction findTransactionById(Long transactionId);

    List<Transaction> findTransactionsByAccount(Long accountId);

    List<Transaction> findTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getAccountBalance(Long accountId);

    Account getAccountByAccountNumber(String accountNumber);

    boolean hasSufficientBalance(String accountNumber, BigDecimal amount);

    User getUserByAccountNumber(String accountNumber);
}