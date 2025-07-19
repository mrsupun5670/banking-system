package lk.banking.core.service;

import lk.banking.core.model.User;

public interface AuthService {

    User authenticate(String email, String password);
    User findUserByEmail(String email);
    boolean userExists(String email);

}