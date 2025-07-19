package lk.banking.ejb.bean;

import lk.banking.core.model.User;
import lk.banking.core.service.CustomerService;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class CustomerServiceBean implements CustomerService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> getAllCustomers() {
        try {
            String jpql = "SELECT DISTINCT u FROM User u " +
                    "LEFT JOIN u.account " +
                    "WHERE u.role = 'CUSTOMER' " +
                    "ORDER BY u.first_name, u.last_name";

            TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public long getTotalCustomerCount() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.role = 'CUSTOMER'", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public BigDecimal getTotalDeposits() {
        try {
            TypedQuery<BigDecimal> query = entityManager.createQuery(
                    "SELECT COALESCE(SUM(a.balance), 0) FROM Account a", BigDecimal.class);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}