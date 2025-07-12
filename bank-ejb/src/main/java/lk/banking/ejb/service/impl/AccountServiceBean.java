package lk.banking.ejb.service.impl;

import jakarta.ejb.Stateless;

@Stateless
public class AccountServiceBean {
    public void openAccount() {
        System.out.println("Account Opened");
    }
}
