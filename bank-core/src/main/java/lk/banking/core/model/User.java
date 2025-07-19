package lk.banking.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findCustomers",
                query = "SELECT u FROM User u WHERE u.role = 'CUSTOMER'"),
        @NamedQuery(name = "User.findCustomersWithAccounts",
                query = "SELECT u FROM User u LEFT JOIN FETCH u.account WHERE u.role = 'CUSTOMER'"),
        @NamedQuery(name = "User.findManagers",
                query = "SELECT u FROM User u WHERE u.role= 'MANAGER'"),
        @NamedQuery(name = "User.authenticate",
                query = "SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    private String password;
    private String role;
    private String first_name;
    private String last_name;

    // One user has one account (One-to-One relationship)
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Account account;

    public User() {
    }

    public User(String email, String password, String role, String first_name, String last_name) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    // Helper method to get full name
    public String getFullName() {
        return first_name + " " + last_name;
    }
}