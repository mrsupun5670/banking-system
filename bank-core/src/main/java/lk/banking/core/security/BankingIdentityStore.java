package lk.banking.core.security;

import lk.banking.core.model.User;
import lk.banking.core.service.AuthService;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class BankingIdentityStore implements IdentityStore {

    private static final Logger LOGGER = Logger.getLogger(BankingIdentityStore.class.getName());

    @EJB
    private AuthService authService;

    @Override
    public CredentialValidationResult validate(jakarta.security.enterprise.credential.Credential credential) {

        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;

            String email = usernamePassword.getCaller();
            String password = usernamePassword.getPasswordAsString();

            try {
                // Use existing AuthService to validate credentials
                User user = authService.authenticate(email, password);

                if (user != null) {
                    // Create roles set
                    Set<String> roles = new HashSet<>();
                    roles.add(user.getRole()); // Add user's role (CUSTOMER or MANAGER)

                    LOGGER.log(Level.INFO, "Identity validation successful for user: {0}, Role: {1}",
                            new Object[]{user.getEmail(), user.getRole()});

                    return new CredentialValidationResult(user.getEmail(), roles);
                } else {
                    LOGGER.log(Level.WARNING, "Identity validation failed for user: {0}", email);
                    return CredentialValidationResult.INVALID_RESULT;
                }

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during credential validation", e);
                return CredentialValidationResult.INVALID_RESULT;
            }
        }

        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return Set.of(ValidationType.VALIDATE);
    }
}