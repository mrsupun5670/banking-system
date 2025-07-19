package lk.banking.core.model;


public class AuthResult {
    private boolean success;
    private String message;
    private User user;

    public AuthResult(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public AuthResult(boolean success, String message) {
        this(success, message, null);
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}