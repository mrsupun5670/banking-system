package lk.banking.web.servlet;

import lk.banking.core.model.Transaction;
import lk.banking.core.model.Account;
import lk.banking.core.model.User;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/customerTransfer")
@RolesAllowed("CUSTOMER")
public class CustomerTransferServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CustomerTransferServlet.class.getName());

    @EJB
    private TransactionService transactionService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                LOGGER.log(Level.WARNING, "No user found in session for transfer request");
                setErrorMessage(session, "Please log in to perform transfers");
                response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
                return;
            }

            if (currentUser.getAccount() == null) {
                LOGGER.log(Level.WARNING, "No account found for user: {0}", currentUser.getEmail());
                setErrorMessage(session, "No account found for your profile");
                redirectToCustomerDashboard(request, response);
                return;
            }

            String recipientAccountNumber = request.getParameter("recipientAccount");
            String transferAmountStr = request.getParameter("amount");
            String transferNote = request.getParameter("note");

            LOGGER.log(Level.INFO, "Transfer request - Recipient: {0}, Amount: {1}",
                    new Object[]{recipientAccountNumber, transferAmountStr});

            // Validate recipient account number
            if (recipientAccountNumber == null || recipientAccountNumber.trim().isEmpty()) {
                setErrorMessage(session, "Recipient account number is required");
                redirectToCustomerDashboard(request, response);
                return;
            }

            // Validate transfer amount
            if (transferAmountStr == null || transferAmountStr.trim().isEmpty()) {
                setErrorMessage(session, "Transfer amount is required");
                redirectToCustomerDashboard(request, response);
                return;
            }

            BigDecimal transferAmount;
            try {
                transferAmount = new BigDecimal(transferAmountStr.trim());
            } catch (NumberFormatException e) {
                setErrorMessage(session, "Invalid amount format");
                redirectToCustomerDashboard(request, response);
                return;
            }

            if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                setErrorMessage(session, "Transfer amount must be positive");
                redirectToCustomerDashboard(request, response);
                return;
            }

            BigDecimal maxTransferAmount = new BigDecimal("50000.00");
            if (transferAmount.compareTo(maxTransferAmount) > 0) {
                setErrorMessage(session, "Transfer amount exceeds maximum limit of LKR 50,000.00");
                redirectToCustomerDashboard(request, response);
                return;
            }

            // Check if sender and recipient are the same
            if (currentUser.getAccount().getAccountNumber().equals(recipientAccountNumber.trim())) {
                setErrorMessage(session, "Cannot transfer to the same account");
                redirectToCustomerDashboard(request, response);
                return;
            }

            // Get recipient account using existing service
            Account recipientAccount = transactionService.getAccountByAccountNumber(recipientAccountNumber.trim());
            if (recipientAccount == null) {
                LOGGER.log(Level.WARNING, "Recipient account not found: {0}", recipientAccountNumber);
                setErrorMessage(session, "Recipient account not found");
                redirectToCustomerDashboard(request, response);
                return;
            }

            // Check if sender has sufficient balance
            if (!transactionService.hasSufficientBalance(currentUser.getAccount().getAccountNumber(), transferAmount)) {
                LOGGER.log(Level.WARNING, "Insufficient balance for transfer - Account: {0}, Amount: {1}, Balance: {2}",
                        new Object[]{currentUser.getAccount().getAccountNumber(), transferAmount, currentUser.getAccount().getBalance()});
                setErrorMessage(session, "Insufficient balance for this transfer");
                redirectToCustomerDashboard(request, response);
                return;
            }

            // Process transfer using TransactionService
            Transaction transferTransaction = transactionService.processTransfer(
                    currentUser.getAccount().getId(),
                    recipientAccount.getId(),
                    transferAmount,
                    transferNote != null ? transferNote : "Customer Transfer"
            );

            if (transferTransaction != null) {
                // Transfer successful - update session balance
                BigDecimal newBalance = transactionService.getAccountBalance(currentUser.getAccount().getId());
                if (newBalance != null) {
                    currentUser.getAccount().setBalance(newBalance);
                    session.setAttribute("currentUser", currentUser);
                }

                setSuccessMessage(session,
                        String.format("Successfully transferred LKR %,.2f to account %s", transferAmount, recipientAccountNumber));

                LOGGER.log(Level.INFO, "Customer transfer completed - From: {0} to {1}, Amount: {2}, User: {3}",
                        new Object[]{currentUser.getAccount().getAccountNumber(), recipientAccountNumber, transferAmount, currentUser.getEmail()});
            } else {
                // Transfer failed
                setErrorMessage(session, "Transfer failed. Please try again.");

                LOGGER.log(Level.WARNING, "Customer transfer failed - From: {0} to {1}, Amount: {2}",
                        new Object[]{currentUser.getAccount().getAccountNumber(), recipientAccountNumber, transferAmount});
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing customer transfer request", e);
            setErrorMessage(session, "An error occurred while processing your transfer. Please try again.");
        }

        redirectToCustomerDashboard(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        redirectToCustomerDashboard(request, response);
    }

    private void setSuccessMessage(HttpSession session, String message) {
        session.setAttribute("successMessage", message);
        session.setAttribute("messageType", "success");
    }

    private void setErrorMessage(HttpSession session, String message) {
        session.setAttribute("errorMessage", message);
        session.setAttribute("messageType", "error");
    }

    private void redirectToCustomerDashboard(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/pages/customer-dashboard.jsp");
    }
}