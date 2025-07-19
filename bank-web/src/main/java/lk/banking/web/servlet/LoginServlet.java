package lk.banking.web.servlet;

import lk.banking.core.model.User;
import lk.banking.core.service.AuthService;

import jakarta.annotation.security.PermitAll;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/login")
@PermitAll
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    @EJB
    private AuthService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                redirectToDashboard(currentUser, request, response);
                return;
            }
        }

        request.getRequestDispatcher(request.getContextPath()+"/pages/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (email == null || email.trim().isEmpty()) {
                setErrorMessage(request, "Email is required");
                forwardToLoginPage(request, response);
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                setErrorMessage(request, "Password is required");
                forwardToLoginPage(request, response);
                return;
            }

            User user = authService.authenticate(email.trim(), password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", user);
                session.setMaxInactiveInterval(30 * 60);

                LOGGER.log(Level.INFO, "User logged in successfully - Email: {0}, Role: {1}",
                        new Object[]{user.getEmail(), user.getRole()});

                redirectToDashboard(user, request, response);

            } else {
                // Authentication failed
                LOGGER.log(Level.WARNING, "Login failed for email: {0}", email);
                setErrorMessage(request, "Invalid email or password");
                forwardToLoginPage(request, response);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process", e);
            setErrorMessage(request, "An error occurred during login. Please try again.");
            forwardToLoginPage(request, response);
        }
    }

    private void redirectToDashboard(User user, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String contextPath = request.getContextPath();

        if ("MANAGER".equals(user.getRole())) {
            response.sendRedirect(contextPath + "/pages/manager-dashboard.jsp");
        } else if ("CUSTOMER".equals(user.getRole())) {
            response.sendRedirect(contextPath + "/pages/customer-dashboard.jsp");
        } else {
            LOGGER.log(Level.SEVERE, "SECURITY WARNING: Unknown user role: {0} for user: {1}",
                    new Object[]{user.getRole(), user.getEmail()});

            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            setErrorMessage(request, "Invalid user role. Please contact administrator.");
            response.sendRedirect(contextPath + "/login");
        }
    }

    private void setErrorMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("errorMessage", message);
        request.getSession().setAttribute("messageType", "error");
    }

    private void forwardToLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}