package lk.banking.ejb.bean;

import lk.banking.core.model.Transaction;
import lk.banking.core.model.Account;
import lk.banking.core.model.User;
import lk.banking.core.model.TransactionType;
import lk.banking.core.service.TransactionService;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class TransactionServiceBean implements TransactionService {

    private static final Logger LOGGER = Logger.getLogger(TransactionServiceBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Transaction createTransaction(Long accountId, BigDecimal amount, TransactionType transactionType, String description) {
        try {
            if (accountId == null) {
                LOGGER.log(Level.WARNING, "Account ID is required");
                return null;
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.log(Level.WARNING, "Transaction amount must be positive: {0}", amount);
                return null;
            }

            if (transactionType == null) {
                LOGGER.log(Level.WARNING, "Transaction type is required");
                return null;
            }

            Account account = getAccountById(accountId);
            if (account == null) {
                LOGGER.log(Level.WARNING, "Account not found: {0}", accountId);
                return null;
            }

            if (transactionType.isDebit() && account.getBalance().compareTo(amount) < 0) {
                LOGGER.log(Level.WARNING, "Insufficient balance for transaction - Account: {0}, Amount: {1}, Balance: {2}",
                        new Object[]{accountId, amount, account.getBalance()});
                return null;
            }

            BigDecimal newBalance;
            if (transactionType.isDebit()) {
                newBalance = account.getBalance().subtract(amount);
            } else {
                newBalance = account.getBalance().add(amount);
            }

            Transaction transaction = new Transaction(account, null, amount, newBalance, transactionType, description, LocalDateTime.now());

            account.setBalance(newBalance);
            entityManager.merge(account);
            entityManager.persist(transaction);

            LOGGER.log(Level.INFO, "Transaction created successfully - Account: {0}, Type: {1}, Amount: {2}",
                    new Object[]{accountId, transactionType, amount});

            return transaction;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating transaction", e);
            return null;
        }
    }

    @Override
    public Transaction createTransaction(Long accountId, Long referenceAccountId, BigDecimal amount, TransactionType transactionType, String description) {
        try {
            if (accountId == null) {
                LOGGER.log(Level.WARNING, "Account ID is required");
                return null;
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.log(Level.WARNING, "Transaction amount must be positive: {0}", amount);
                return null;
            }

            if (transactionType == null) {
                LOGGER.log(Level.WARNING, "Transaction type is required");
                return null;
            }

            Account account = getAccountById(accountId);
            if (account == null) {
                LOGGER.log(Level.WARNING, "Account not found: {0}", accountId);
                return null;
            }

            Account referenceAccount = null;
            if (referenceAccountId != null) {
                referenceAccount = getAccountById(referenceAccountId);
                if (referenceAccount == null) {
                    LOGGER.log(Level.WARNING, "Reference account not found: {0}", referenceAccountId);
                    return null;
                }
            }

            if (transactionType.isDebit() && account.getBalance().compareTo(amount) < 0) {
                LOGGER.log(Level.WARNING, "Insufficient balance for transaction - Account: {0}, Amount: {1}, Balance: {2}",
                        new Object[]{accountId, amount, account.getBalance()});
                return null;
            }

            BigDecimal newBalance;
            if (transactionType.isDebit()) {
                newBalance = account.getBalance().subtract(amount);
            } else {
                newBalance = account.getBalance().add(amount);
            }

            Transaction transaction = new Transaction(account, referenceAccount, amount, newBalance, transactionType, description, LocalDateTime.now());

            account.setBalance(newBalance);
            entityManager.merge(account);
            entityManager.persist(transaction);

            LOGGER.log(Level.INFO, "Transaction created successfully - Account: {0}, Reference: {1}, Type: {2}, Amount: {3}",
                    new Object[]{accountId, referenceAccountId, transactionType, amount});

            return transaction;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating transaction with reference account", e);
            return null;
        }
    }

    @Override
    public Transaction processDebit(Long accountId, BigDecimal amount, String description) {
        return createTransaction(accountId, amount, TransactionType.TRANSFER_SENT, description);
    }

    @Override
    public Transaction processCredit(Long accountId, BigDecimal amount, String description) {
        return createTransaction(accountId, amount, TransactionType.TRANSFER_RECEIVED, description);
    }

    @Override
    public Transaction processTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        try {
            if (fromAccountId == null || toAccountId == null) {
                LOGGER.log(Level.WARNING, "Both account IDs are required for transfer");
                return null;
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.log(Level.WARNING, "Transfer amount must be positive: {0}", amount);
                return null;
            }

            if (fromAccountId.equals(toAccountId)) {
                LOGGER.log(Level.WARNING, "Cannot transfer to the same account");
                return null;
            }

            Account fromAccount = getAccountById(fromAccountId);
            if (fromAccount == null) {
                LOGGER.log(Level.WARNING, "Source account not found: {0}", fromAccountId);
                return null;
            }

            Account toAccount = getAccountById(toAccountId);
            if (toAccount == null) {
                LOGGER.log(Level.WARNING, "Destination account not found: {0}", toAccountId);
                return null;
            }

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                LOGGER.log(Level.WARNING, "Insufficient balance for transfer - Account: {0}, Amount: {1}, Balance: {2}",
                        new Object[]{fromAccountId, amount, fromAccount.getBalance()});
                return null;
            }

            BigDecimal fromNewBalance = fromAccount.getBalance().subtract(amount);
            Transaction debitTransaction = new Transaction(fromAccount, toAccount, amount, fromNewBalance,
                    TransactionType.TRANSFER_SENT, description, LocalDateTime.now());

            BigDecimal toNewBalance = toAccount.getBalance().add(amount);
            Transaction creditTransaction = new Transaction(toAccount, fromAccount, amount, toNewBalance,
                    TransactionType.TRANSFER_RECEIVED, description, LocalDateTime.now());

            fromAccount.setBalance(fromNewBalance);
            toAccount.setBalance(toNewBalance);

            entityManager.merge(fromAccount);
            entityManager.merge(toAccount);
            entityManager.persist(debitTransaction);
            entityManager.persist(creditTransaction);

            LOGGER.log(Level.INFO, "Transfer completed successfully - From: {0} to {1}, Amount: {2}",
                    new Object[]{fromAccountId, toAccountId, amount});

            return debitTransaction;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing transfer", e);
            return null;
        }
    }

    @Override
    public Transaction findTransactionById(Long transactionId) {
        try {
            if (transactionId == null) {
                return null;
            }

            return entityManager.find(Transaction.class, transactionId);

        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Transaction not found: {0}", transactionId);
            return null;
        }
    }

    @Override
    public List<Transaction> findTransactionsByAccount(Long accountId) {
        try {
            LOGGER.log(Level.INFO, "DEBUG: findTransactionsByAccount called with accountId: {0}", accountId);

            if (accountId == null) {
                LOGGER.log(Level.WARNING, "DEBUG: accountId is null, returning empty list");
                return new ArrayList<>();
            }

            // Debug: Check if account exists
            Account account = getAccountById(accountId);
            if (account == null) {
                LOGGER.log(Level.WARNING, "DEBUG: No account found with ID: {0}", accountId);
                return new ArrayList<>();
            }

            LOGGER.log(Level.INFO, "DEBUG: Found account {0} for ID: {1}",
                    new Object[]{account.getAccountNumber(), accountId});

            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.transactionDate DESC",
                    Transaction.class);
            query.setParameter("accountId", accountId);

            List<Transaction> result = query.getResultList();

            // getResultList() never returns null, but let's be extra safe
            if (result == null) {
                LOGGER.log(Level.WARNING, "DEBUG: Query returned null result (should not happen)");
                result = new ArrayList<>();
            }

            LOGGER.log(Level.INFO, "DEBUG: Query executed successfully, found {0} transactions for account ID: {1}",
                    new Object[]{result.size(), accountId});

            // Debug: Log first few transactions if any exist
            if (result.size() > 0) {
                LOGGER.log(Level.INFO, "DEBUG: First transaction: ID={0}, Type={1}, Amount={2}",
                        new Object[]{result.get(0).getId(), result.get(0).getTransactionType(), result.get(0).getAmount()});
            }

            return result;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERROR: Exception in findTransactionsByAccount for accountId: " + accountId, e);
            return new ArrayList<>(); // Return empty list instead of null
        }
    }

    @Override
    public List<Transaction> findTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            if (accountId == null || startDate == null || endDate == null) {
                return null;
            }

            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC",
                    Transaction.class);
            query.setParameter("accountId", accountId);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);

            return query.getResultList();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error getting transactions for account by date range: {0}", accountId);
            return null;
        }
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        try {
            Account account = getAccountById(accountId);
            if (account == null) {
                return null;
            }
            return account.getBalance();

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error getting balance for account: {0}", accountId);
            return null;
        }
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return null;
            }

            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber.trim());

            return query.getSingleResult();

        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Account not found: {0}", accountNumber);
            return null;
        }
    }

    @Override
    public User getUserByAccountNumber(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return null;
            }

            TypedQuery<User> query = entityManager.createQuery(
                    "SELECT u FROM User u WHERE u.account.accountNumber = :accountNumber", User.class);
            query.setParameter("accountNumber", accountNumber.trim());

            return query.getSingleResult();

        } catch (Exception e) {
            LOGGER.log(Level.FINE, "User not found for account: {0}", accountNumber);
            return null;
        }
    }

    @Override
    public boolean hasSufficientBalance(String accountNumber, BigDecimal amount) {
        try {
            Account account = getAccountByAccountNumber(accountNumber);
            if (account == null) {
                return false;
            }

            return account.getBalance().compareTo(amount) >= 0;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error checking balance for account: {0}", accountNumber);
            return false;
        }
    }

    private Account getAccountById(Long accountId) {
        try {
            if (accountId == null) {
                LOGGER.log(Level.WARNING, "DEBUG: getAccountById called with null ID");
                return null;
            }

            Account account = entityManager.find(Account.class, accountId);

            if (account == null) {
                LOGGER.log(Level.WARNING, "DEBUG: No account found in database with ID: {0}", accountId);
            } else {
                LOGGER.log(Level.INFO, "DEBUG: Found account: {0} with balance: {1}",
                        new Object[]{account.getAccountNumber(), account.getBalance()});
            }

            return account;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ERROR: Exception in getAccountById for ID: " + accountId, e);
            return null;
        }
    }
}