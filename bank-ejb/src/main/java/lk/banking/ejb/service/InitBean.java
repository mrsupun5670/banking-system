package lk.banking.ejb.service;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.banking.core.service.InitService;
import lk.banking.core.model.Role;
import lk.banking.core.model.User;

@Stateless
public class InitBean implements InitService {

    @PersistenceContext(unitName = "BankingApp")
    private EntityManager em;

    @Override
    public void initDatabase() {
//        Role admin = new Role("ADMIN");
//        em.persist(admin);

        User user = new User("admin", "admin123", admin);
        em.persist(user);
    }
}
