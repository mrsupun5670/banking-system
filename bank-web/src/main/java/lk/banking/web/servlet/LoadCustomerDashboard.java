package lk.banking.web.servlet;

import lk.banking.core.model.User;
import lk.banking.core.model.Transaction;
import lk.banking.core.service.TransactionService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/customer-dashboard")
@RolesAllowed({"CUSTOMER", "MANAGER"})
public class LoadCustomerDashboard extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoadCustomerDashboard.class.getName());

    @EJB
    private TransactionService transactionService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                LOGGER.log(Level.WARNING, "SECURITY: No user found in session for customer dashboard access");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            if (!"CUSTOMER".equals(currentUser.getRole()) && !"MANAGER".equals(currentUser.getRole())) {
                LOGGER.log(Level.SEVERE, "SECURITY WARNING: Unauthorized role {0} attempting to access customer dashboard: {1}",
                        new Object[]{currentUser.getRole(), currentUser.getEmail()});

                response.sendRedirect(request.getContextPath() + "/access-denied");
                return;
            }

            if ("MANAGER".equals(currentUser.getRole())) {
                LOGGER.log(Level.INFO, "Manager {0} accessing customer dashboard", currentUser.getEmail());
            }

            // Refresh user data if customer has an account
            if ("CUSTOMER".equals(currentUser.getRole()) && currentUser.getAccount() != null) {
                User refreshedUser = transactionService.getUserByAccountNumber(
                        currentUser.getAccount().getAccountNumber());
                if (refreshedUser != null) {
                    currentUser = refreshedUser;
                    session.setAttribute("currentUser", currentUser);
                }
            }

            // Load recent transactions for the user
            List<Transaction> recentTransactions = null;
            if (currentUser.getAccount() != null) {
                recentTransactions = transactionService.findTransactionsByAccount(currentUser.getAccount().getId());

                if (recentTransactions != null && recentTransactions.size() > 10) {
                    recentTransactions = recentTransactions.subList(0, 10);
                }

                LOGGER.log(Level.INFO, "Loaded {0} recent transactions for user: {1}",
                        new Object[]{recentTransactions != null ? recentTransactions.size() : 0, currentUser.getEmail()});
            }

            // Get messages from session
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            String messageType = (String) session.getAttribute("messageType");

            // Set attributes for JSP
            request.getSession().setAttribute("currentUser", currentUser);
            request.getSession().setAttribute("successMessage", successMessage);
            request.getSession().setAttribute("errorMessage", errorMessage);
            request.getSession().setAttribute("messageType", messageType);
            request.getSession().setAttribute("recentTransactions", recentTransactions);

            // Format and set balance
            if (currentUser.getAccount() != null) {
                BigDecimal balance = currentUser.getAccount().getBalance();
                String formattedBalance = formatCurrency(balance);
                request.getSession().setAttribute("formattedBalance", formattedBalance);
            } else {
                request.getSession().setAttribute("formattedBalance", "0.00");

                if ("CUSTOMER".equals(currentUser.getRole())) {
                    LOGGER.log(Level.WARNING, "DATA INTEGRITY: Customer {0} has no account", currentUser.getEmail());
                }
            }

            // Clean up session messages
            session.removeAttribute("successMessage");
            session.removeAttribute("errorMessage");
            session.removeAttribute("messageType");

            LOGGER.log(Level.INFO, "Loading customer dashboard for user: {0} (Role: {1})",
                    new Object[]{currentUser.getEmail(), currentUser.getRole()});

            request.getRequestDispatcher(request.getContextPath() + "/pages/customer-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading customer dashboard", e);

            // Set error attributes for JSP to handle gracefully
            request.getSession().setAttribute("errorMessage", "Unable to load dashboard data. Please try again.");
            request.getSession().setAttribute("formattedBalance", "0.00");
            request.getSession().setAttribute("recentTransactions", null);

            // Forward to JSP even in error case
            request.getRequestDispatcher(request.getContextPath() + "/pages/customer-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount);
    }
}