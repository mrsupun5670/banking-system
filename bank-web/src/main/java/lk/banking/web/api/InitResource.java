package lk.banking.web.api;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lk.banking.core.model.Role;
import lk.banking.core.model.User;
import lk.banking.core.service.InitService;
import lk.banking.core.util.PasswordUtil;

import java.util.List;

@Path("/init")
@RequestScoped
public class InitResource {

    @PersistenceContext(unitName = "BankPU")
    private EntityManager em;

    @Inject
    private InitService initBean;

    @GET
    public Response initialize() {
        initBean.initDatabase();
        return Response.ok("Database initialized").build();
    }

    private void initializeUsers() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        List<User> existingUsers = query.getResultList();

        if (existingUsers.isEmpty()) {
            // Get roles
            Role adminRole = em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", "ADMIN")
                    .getSingleResult();

            Role managerRole = em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", "MANAGER")
                    .getSingleResult();

            Role customerRole = em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", "CUSTOMER")
                    .getSingleResult();

            // Create users with hashed passwords
            User admin = new User("admin", PasswordUtil.hashPassword("Admin@123"), adminRole);
            User manager = new User("manager", PasswordUtil.hashPassword("Manager@123"), managerRole);
            User customer = new User("customer", PasswordUtil.hashPassword("Customer@123"), customerRole);

            em.persist(admin);
            em.persist(manager);
            em.persist(customer);

            System.out.println("Default users created:");
            System.out.println("- Admin: admin / Admin@123");
            System.out.println("- Manager: manager / Manager@123");
            System.out.println("- Customer: customer / Customer@123");
        }
    }

}
