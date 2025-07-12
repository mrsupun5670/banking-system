package lk.banking.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findActiveUsers",
                query = "SELECT u FROM User u WHERE u.active = true"),
        @NamedQuery(name = "User.findCustomers",
                query = "SELECT u FROM User u WHERE u.role.name = 'CUSTOMER' AND u.active = true"),
        @NamedQuery(name = "User.findManagers",
                query = "SELECT u FROM User u WHERE u.role.name = 'MANAGER' AND u.active = true")
})
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    @NotBlank(message = "Password is required")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ✅ Enhanced: Add customer profile fields
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    // ✅ Enhanced: Initialize with ArrayList to prevent NullPointerException
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String email, String password, Role role) {
        this();
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ✅ Enhanced: Constructor with profile information
    public User(String email, String password, Role role, String firstName, String lastName) {
        this(email, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }

    // ✅ Enhanced: Utility methods
    public boolean hasRole(String roleName) {
        return role != null && role.getName().equals(roleName);
    }

    public boolean isCustomer() {
        return hasRole("CUSTOMER");
    }

    public boolean isManager() {
        return hasRole("MANAGER");
    }

    // ✅ Enhanced: Better display name logic
    public String getFullName() {
        if (firstName != null && lastName != null &&
                !firstName.trim().isEmpty() && !lastName.trim().isEmpty()) {
            return firstName.trim() + " " + lastName.trim();
        }
        return email; // Fallback to email
    }

    public String getDisplayName() {
        String fullName = getFullName();
        return fullName.equals(email) ? email.split("@")[0] : fullName;
    }

    // ✅ Enhanced: Account management helper methods
    public void addAccount(Account account) {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        accounts.add(account);
        account.setUser(this);
    }

    public void removeAccount(Account account) {
        if (accounts != null) {
            accounts.remove(account);
            account.setUser(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return email != null ? email.equals(user.email) : user.email == null;
    }

    @Override
    public int hashCode() {
        return email != null ? email.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + (role != null ? role.getName() : "null") +
                ", active=" + active +
                '}';
    }
}
