package lk.banking.web.servlet;

import lk.banking.core.model.User;
import lk.banking.core.service.CustomerService;

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

@WebServlet("/manager-dashboard")
@RolesAllowed("MANAGER")
public class LoadManagerDashboard extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoadManagerDashboard.class.getName());

    @EJB
    private CustomerService customerService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                LOGGER.log(Level.SEVERE, "SECURITY: No user found in session for manager dashboard access");
                response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
                return;
            }

            if (!"MANAGER".equals(currentUser.getRole())) {
                LOGGER.log(Level.SEVERE, "SECURITY VIOLATION: Non-manager user {0} with role {1} attempting to access manager dashboard",
                        new Object[]{currentUser.getEmail(), currentUser.getRole()});

                response.sendRedirect(request.getContextPath() + "/pages/access-denied.jsp");
                return;
            }

            List<User> customers = customerService.getAllCustomers();
            long totalCustomers = customerService.getTotalCustomerCount();
            BigDecimal totalDeposits = customerService.getTotalDeposits();

            String formattedDeposits = formatCurrency(totalDeposits);

            LOGGER.log(Level.INFO, "Manager {0} accessing dashboard - Customers: {1}, Total Deposits: {2}",
                    new Object[]{currentUser.getEmail(), totalCustomers, formattedDeposits});

            request.getSession().setAttribute("customers", customers);
            request.getSession().setAttribute("totalCustomers", totalCustomers);
            request.getSession().setAttribute("totalDeposits", formattedDeposits);
            request.getSession().setAttribute("currentUser", currentUser);

            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");

            request.setAttribute("successMessage", successMessage);
            request.setAttribute("errorMessage", errorMessage);

            session.removeAttribute("successMessage");
            session.removeAttribute("errorMessage");
            session.removeAttribute("messageType");

            request.getRequestDispatcher(request.getContextPath()+"/pages/manager-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading manager dashboard", e);

            request.setAttribute("errorMessage", "Unable to load dashboard data. Please try again.");
            request.setAttribute("customers", List.of());
            request.setAttribute("totalCustomers", 0L);
            request.setAttribute("totalDeposits", "LKR 0.00");

            request.getRequestDispatcher(request.getContextPath()+"/pages/manager-dashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "LKR 0.00";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return "LKR " + formatter.format(amount);
    }
}