package lk.banking.ejb.bean;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import lk.banking.core.model.User;
import lk.banking.core.service.UserService;

@Stateless
public class RegisterBean {
    @EJB
    private UserService userService;

    public void addUser(User user) {
        userService.addUser(user);
    }
}
