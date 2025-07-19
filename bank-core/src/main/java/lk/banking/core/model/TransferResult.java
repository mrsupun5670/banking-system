package lk.banking.core.model;

import java.math.BigDecimal;

public class TransferResult {
    private boolean success;
    private String message;
    private BigDecimal newSenderBalance;
    private BigDecimal newRecipientBalance;

    public TransferResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public TransferResult(boolean success, String message, BigDecimal newSenderBalance, BigDecimal newRecipientBalance) {
        this.success = success;
        this.message = message;
        this.newSenderBalance = newSenderBalance;
        this.newRecipientBalance = newRecipientBalance;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public BigDecimal getNewSenderBalance() {
        return newSenderBalance;
    }

    public BigDecimal getNewRecipientBalance() {
        return newRecipientBalance;
    }
}
