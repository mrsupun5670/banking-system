package lk.banking.core.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reference_account_id")
    private Account referenceAccount;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", nullable = false)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private TransactionType transactionType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction() {
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(Account account, Account referenceAccount, BigDecimal amount, BigDecimal balanceAfter, TransactionType transactionType, String description, LocalDateTime transactionDate) {
        this.account = account;
        this.referenceAccount = referenceAccount;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getReferenceAccount() {
        return referenceAccount;
    }

    public void setReferenceAccount(Account referenceAccount) {
        this.referenceAccount = referenceAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public boolean isDebit() {
        return transactionType.isDebit();
    }

    public boolean isCredit() {
        return transactionType.isCredit();
    }

    public String getFormattedAmount() {
        if (isDebit()) {
            return "-LKR " + String.format("%,.2f", amount);
        } else {
            return "+LKR " + String.format("%,.2f", amount);
        }
    }
}
