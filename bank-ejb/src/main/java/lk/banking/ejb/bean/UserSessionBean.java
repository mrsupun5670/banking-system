package lk.banking.ejb.bean;

import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TimerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lk.banking.core.model.User;
import lk.banking.core.service.UserService;

@Stateless
public class UserSessionBean implements UserService {

    @PersistenceContext(unitName = "BankingApp")
    private EntityManager em;

    @Override
    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        return em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
    }

    @Override
    public void addUser(User user) {
        em.persist(user);
    }

    @RolesAllowed({"USER","MANAGER","ADMIN"})
    @Override
    public void deleteUser(User user) {
        em.remove(user);
    }

    @Override
    public boolean validate(String email, String password) {
        User user = em.createNamedQuery("User.findByEmail", User.class)
                .setParameter("email", email).getSingleResult();

        return user != null && user.getPassword().equals(password);
    }
}
