package lk.banking.ejb.bean;

import lk.banking.core.model.Account;
import lk.banking.core.service.ManagerTransferService;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ManagerTransferServiceBean implements ManagerTransferService {

    private static final Logger LOGGER = Logger.getLogger(ManagerTransferServiceBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public boolean transferMoneyToCustomer(String accountNumber, BigDecimal amount, String note) {
        try {
            // Validate input
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Account number is null or empty");
                return false;
            }

            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                LOGGER.log(Level.WARNING, "Transfer amount is null or non-positive: {0}", amount);
                return false;
            }

            // Find the account
            Account account = findAccountByAccountNumber(accountNumber.trim());
            if (account == null) {
                LOGGER.log(Level.WARNING, "Account not found: {0}", accountNumber);
                return false;
            }

            // Calculate new balance
            BigDecimal currentBalance = account.getBalance();
            BigDecimal newBalance = currentBalance.add(amount);

            // Update the account balance
            account.setBalance(newBalance);
            entityManager.merge(account);

            LOGGER.log(Level.INFO, "Transfer successful - Account: {0}, Amount: {1}, New Balance: {2}",
                    new Object[]{accountNumber, amount, newBalance});

            // Here you could also log the transfer to a transaction history table if needed
            // logTransferHistory(accountNumber, amount, note);

            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during money transfer", e);
            return false;
        }
    }

    @Override
    public Account findAccountByAccountNumber(String accountNumber) {
        try {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return null;
            }

            TypedQuery<Account> query = entityManager.createQuery(
                    "SELECT a FROM Account a WHERE a.accountNumber = :accountNumber", Account.class);
            query.setParameter("accountNumber", accountNumber.trim());

            return query.getSingleResult();

        } catch (Exception e) {
            LOGGER.log(Level.INFO, "Account not found or error occurred: {0}", accountNumber);
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateAccountBalance(String accountNumber, BigDecimal newBalance) {
        try {
            Account account = findAccountByAccountNumber(accountNumber);
            if (account == null) {
                return false;
            }

            account.setBalance(newBalance);
            entityManager.merge(account);

            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating account balance", e);
            return false;
        }
    }
}