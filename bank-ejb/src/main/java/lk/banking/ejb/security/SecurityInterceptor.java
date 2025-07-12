package lk.banking.ejb.security;

import lk.banking.core.security.Secured;
import lk.banking.core.model.User;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Logger;

@Secured
@Interceptor
public class SecurityInterceptor {

    private static final Logger logger = Logger.getLogger(SecurityInterceptor.class.getName());

    @Inject
    private HttpServletRequest request;

    @AroundInvoke
    public Object checkSecurity(InvocationContext context) throws Exception {
        String methodName = context.getTarget().getClass().getSimpleName() +
                "." + context.getMethod().getName();

        // Get the @Secured annotation
        Secured secured = getSecuredAnnotation(context);
        String requiredRole = secured != null ? secured.role() : "";

        // Check authentication
        if (!isAuthenticated()) {
            logger.warning("Access denied to " + methodName + " - not authenticated");
            throw new SecurityException("Please login to access this feature");
        }

        // Check role if specified
        if (!requiredRole.isEmpty() && !hasRole(requiredRole)) {
            String currentRole = getCurrentUser().getRole().getName();
            logger.warning("Access denied to " + methodName + " - user has role: " +
                    currentRole + ", required: " + requiredRole);
            throw new SecurityException("Insufficient privileges. Required role: " + requiredRole);
        }

        logger.info("Access granted to " + methodName + " for user: " + getCurrentUsername());
        return context.proceed();
    }

    private boolean isAuthenticated() {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private User getCurrentUser() {
        HttpSession session = request.getSession(false);
        return session != null ? (User) session.getAttribute("user") : null;
    }

    private String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : "anonymous";
    }

    private boolean hasRole(String roleName) {
        User user = getCurrentUser();
        return user != null && user.getRole().getName().equals(roleName);
    }

    private Secured getSecuredAnnotation(InvocationContext context) {
        // Check method-level annotation first
        Secured methodAnnotation = context.getMethod().getAnnotation(Secured.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // Check class-level annotation
        return context.getTarget().getClass().getAnnotation(Secured.class);
    }
}
