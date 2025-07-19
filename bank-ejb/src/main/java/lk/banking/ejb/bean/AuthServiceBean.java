package lk.banking.ejb.bean;

import lk.banking.core.model.User;
import lk.banking.core.service.AuthService;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class AuthServiceBean implements AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthServiceBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User authenticate(String email, String password) {
        try {
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Authentication failed: Email is null or empty");
                return null;
            }

            if (password == null || password.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Authentication failed: Password is null or empty");
                return null;
            }

            // Use the existing named query for authentication
            TypedQuery<User> query = entityManager.createNamedQuery("User.authenticate", User.class);
            query.setParameter("email", email.trim().toLowerCase());
            query.setParameter("password", password);

            User user = query.getSingleResult();

            if (user != null) {
                LOGGER.log(Level.INFO, "Authentication successful for user: {0}, Role: {1}",
                        new Object[]{user.getEmail(), user.getRole()});
                return user;
            }

            return null;

        } catch (NoResultException e) {
            LOGGER.log(Level.INFO, "Authentication failed: Invalid email or password for email: {0}", email);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during authentication for email: " + email, e);
            return null;
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }

            TypedQuery<User> query = entityManager.createNamedQuery("User.findByEmail", User.class);
            query.setParameter("email", email.trim().toLowerCase());

            return query.getSingleResult();

        } catch (NoResultException e) {
            LOGGER.log(Level.FINE, "User not found with email: {0}", email);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error finding user by email: " + email, e);
            return null;
        }
    }

    @Override
    public boolean userExists(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }

            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email.trim().toLowerCase());

            Long count = query.getSingleResult();
            return count > 0;

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error checking if user exists: " + email, e);
            return false;
        }
    }
}