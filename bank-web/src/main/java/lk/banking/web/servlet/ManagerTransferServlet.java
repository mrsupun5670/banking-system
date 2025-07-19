package lk.banking.web.servlet;


import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.banking.core.service.ManagerTransferService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/transferMoney")
@RolesAllowed("MANAGER") // Only managers can perform manager transfers
public class ManagerTransferServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ManagerTransferServlet.class.getName());

    @EJB
    private ManagerTransferService transferService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String accountNumber = request.getParameter("accountNumber");
            String transferAmountStr = request.getParameter("transferAmount");
            String transferNote = request.getParameter("transferNote");

            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                setErrorMessage(request, "Account number is required");
                redirectToDashboard(request, response);
                return;
            }

            if (transferAmountStr == null || transferAmountStr.trim().isEmpty()) {
                setErrorMessage(request, "Transfer amount is required");
                redirectToDashboard(request, response);
                return;
            }

            BigDecimal transferAmount;
            try {
                transferAmount = new BigDecimal(transferAmountStr.trim());
            } catch (NumberFormatException e) {
                setErrorMessage(request, "Invalid amount format");
                redirectToDashboard(request, response);
                return;
            }

            if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                setErrorMessage(request, "Transfer amount must be positive");
                redirectToDashboard(request, response);
                return;
            }

            boolean transferSuccessful = transferService.transferMoneyToCustomer(
                    accountNumber.trim(), transferAmount, transferNote);

            if (transferSuccessful) {
                setSuccessMessage(request, String.format(
                        "Successfully transferred LKR %,.2f to account %s",
                        transferAmount, accountNumber.trim()));

                LOGGER.log(Level.INFO, "Transfer completed - Account: {0}, Amount: {1}",
                        new Object[]{accountNumber, transferAmount});
            } else {
                setErrorMessage(request, "Transfer failed. Please check the account number and try again.");

                LOGGER.log(Level.WARNING, "Transfer failed - Account: {0}, Amount: {1}",
                        new Object[]{accountNumber, transferAmount});
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing transfer request", e);
            setErrorMessage(request, "An error occurred while processing the transfer. Please try again.");
        }

        redirectToDashboard(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/manager-dashboard");
    }

    private void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("successMessage", message);
        request.getSession().setAttribute("messageType", "success");
    }

    private void setErrorMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("errorMessage", message);
        request.getSession().setAttribute("messageType", "error");
    }

    private void redirectToDashboard(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/manager-dashboard");
    }
}