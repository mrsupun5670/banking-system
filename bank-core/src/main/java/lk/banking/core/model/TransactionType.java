package lk.banking.core.model;

public enum TransactionType {
    TRANSFER_SENT("Transfer Sent"),
    TRANSFER_RECEIVED("Transfer Received"),
    INTEREST_CREDIT("Interest Credit"),
    BANK_TRANSFER("Bank Transfer");

    private final String description;

    TransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDebit() {
        return this == TRANSFER_SENT;
    }

    public boolean isCredit() {
        return this == TRANSFER_RECEIVED || this == INTEREST_CREDIT || this == BANK_TRANSFER;
    }
}