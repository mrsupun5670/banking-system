package lk.banking.core.service;

import jakarta.ejb.Remote;
import lk.banking.core.model.User;

public interface UserService {
    User getUserById(Long id);
    User getUserByEmail(String email);
    void addUser(User user);
    void deleteUser(User user);
    boolean validate(String email, String password);
}
