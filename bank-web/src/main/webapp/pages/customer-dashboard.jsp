<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="lk.banking.core.model.User" %>
<%@ page import="lk.banking.core.model.Transaction" %>
<%@ page import="lk.banking.core.model.TransactionType" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%
    User currentUser = (User) request.getSession().getAttribute("currentUser");
    String formattedBalance =  currentUser.getAccount().getBalance().toString();
    String successMessage = (String) request.getSession().getAttribute("successMessage");
    String errorMessage = (String) request.getSession().getAttribute("errorMessage");
    String messageType = (String) request.getSession().getAttribute("messageType");
    List<Transaction> recentTransactions = (List<Transaction>) request.getSession().getAttribute("recentTransactions");

    // DEBUG OUTPUT - Remove this after debugging
    System.out.println("=== JSP DEBUG INFO ===");
    System.out.println("Current User: " + (currentUser != null ? currentUser.getEmail() : "NULL"));
    System.out.println("User Account: " + (currentUser != null && currentUser.getAccount() != null ?
            "ID=" + currentUser.getAccount().getId() + ", Number=" + currentUser.getAccount().getAccountNumber() : "NULL"));
    System.out.println("Recent Transactions: " + (recentTransactions != null ?
            recentTransactions.size() + " transactions" : "NULL"));
    if (recentTransactions != null && recentTransactions.size() > 0) {
        System.out.println("First Transaction: " + recentTransactions.get(0).getDescription() +
                " - " + recentTransactions.get(0).getAmount());
    }
    System.out.println("======================");

    String userFullName = "Customer";
    String userAccountNumber = "No Account";
    String userBalance = "0.00";

    if (currentUser != null) {
        userFullName = currentUser.getFullName();
        if (currentUser.getAccount() != null) {
            userAccountNumber = currentUser.getAccount().getAccountNumber();
            userBalance = formattedBalance != null ? formattedBalance : "0.00";
        }
    }

    // Date formatter for transactions
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, h:mm a");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SecureBank - Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css"
          rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link href="css/style.css" rel="stylesheet"/>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-light">
    <div class="container">
        <a class="navbar-brand" href="#">
            <i class="bi bi-bank"></i>
            SecureBank
        </a>
        <div class="d-flex align-items-center">
                <span class="welcome-text me-3">
                    Welcome, <span class="welcome-name"><%= userFullName %></span>
                </span>
            <button class="btn btn-danger-futuristic btn-sm" onclick="logout()">
                <i class="bi bi-power me-2"></i>Logout
            </button>
        </div>
    </div>
</nav>

<div class="container py-4">

    <!-- Success/Error Messages -->
    <% if (successMessage != null) { %>
    <div class="alert alert-success alert-dismissible fade show fade-in-up" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i>
        <%= successMessage %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <% if (errorMessage != null) { %>
    <div class="alert alert-danger alert-dismissible fade show fade-in-up" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        <%= errorMessage %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <!-- Balance and Stats -->
    <div class="row mb-4">
        <div class="col-lg-8 mb-3">
            <div class="card card-futuristic balance-card fade-in-left">
                <div class="card-body text-center py-5">
                    <i class="bi bi-wallet2 fs-1 balance-icon mb-3"></i>
                    <h6 class="text-uppercase text-muted mb-2 fw-semibold tracking-wider">Available Balance</h6>
                    <div class="balance-amount" id="balance"><%= userBalance %></div>
                    <small class="text-muted">LKR</small>
                    <div class="mt-2">
                        <small class="text-muted">Account: <%= userAccountNumber %></small>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="row g-3 fade-in-right">
                <div class="col-6">
                    <div class="stat-card">
                        <div class="stat-number"><%= recentTransactions != null ? recentTransactions.size() : 0 %></div>
                        <div class="stat-label">Recent Transactions</div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="stat-card">
                        <div class="stat-number">
                            <%
                                BigDecimal totalSent = BigDecimal.ZERO;
                                if (recentTransactions != null) {
                                    for (Transaction t : recentTransactions) {
                                        if (t.isDebit()) {
                                            totalSent = totalSent.add(t.getAmount());
                                        }
                                    }
                                }
                            %>
                            LKR <%= String.format("%,.0f", totalSent) %>
                        </div>
                        <div class="stat-label">Total Sent</div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="stat-card">
                        <div class="stat-number">
                            <%
                                BigDecimal totalReceived = BigDecimal.ZERO;
                                if (recentTransactions != null) {
                                    for (Transaction t : recentTransactions) {
                                        if (t.isCredit()) {
                                            totalReceived = totalReceived.add(t.getAmount());
                                        }
                                    }
                                }
                            %>
                            LKR <%= String.format("%,.0f", totalReceived) %>
                        </div>
                        <div class="stat-label">Total Received</div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="stat-card">
                        <div class="stat-number">
                            <%
                                long transferCount = 0;
                                if (recentTransactions != null) {
                                    for (Transaction t : recentTransactions) {
                                        if (t.getTransactionType() == TransactionType.TRANSFER_SENT ||
                                                t.getTransactionType() == TransactionType.TRANSFER_RECEIVED) {
                                            transferCount++;
                                        }
                                    }
                                }
                            %>
                            <%= transferCount %>
                        </div>
                        <div class="stat-label">Transfers</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Transaction Forms -->
    <div class="row mb-4">
        <div class="col-lg-6 mb-3">
            <div class="card card-futuristic fade-in-up">
                <div class="card-body">
                    <h5 class="card-title">
                        <i class="bi bi-send"></i>Transfer Money
                    </h5>
                    <form action="${pageContext.request.contextPath}/customerTransfer" method="post" id="transferForm">
                        <div class="mb-3">
                            <label class="form-label">Recipient Account Number</label>
                            <input type="text" name="recipientAccount" class="form-control form-control-futuristic"
                                   placeholder="Enter recipient account number" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Amount (LKR)</label>
                            <div class="input-group">
                                <span class="input-group-text">LKR</span>
                                <input type="number" name="amount" class="form-control form-control-futuristic"
                                       placeholder="0.00" min="0" step="0.01" max="50000" required>
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Note</label>
                            <input type="text" name="note" class="form-control form-control-futuristic"
                                   placeholder="Payment description (optional)">
                        </div>
                        <button type="submit" class="btn btn-futuristic w-100">
                            <i class="bi bi-lightning me-2"></i>Send Transfer
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-lg-6">
            <div class="card card-futuristic fade-in-up" style="animation-delay: 0.1s;">
                <div class="card-body">
                    <h5 class="card-title">
                        <i class="bi bi-clock-history"></i>Schedule Payment
                    </h5>
                    <form id="scheduleForm">
                        <div class="mb-3">
                            <label class="form-label">Recipient Account</label>
                            <input type="text" class="form-control form-control-futuristic"
                                   placeholder="Enter account number" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Amount (LKR)</label>
                            <div class="input-group">
                                <span class="input-group-text">LKR</span>
                                <input type="number" class="form-control form-control-futuristic"
                                       placeholder="0.00" min="0" step="0.01" required>
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Date</label>
                            <input type="date" class="form-control form-control-futuristic" required>
                        </div>
                        <button type="submit" class="btn btn-secondary-futuristic w-100">
                            <i class="bi bi-calendar-plus me-2"></i>Schedule
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Transactions -->
    <div class="row">
        <div class="col-12">
            <div class="card card-futuristic fade-in-up" style="animation-delay: 0.2s;">
                <div class="card-body">
                    <h5 class="card-title">
                        <i class="bi bi-activity"></i>Recent Activity
                    </h5>

                    <% if (recentTransactions != null && !recentTransactions.isEmpty()) { %>
                    <% for (Transaction transaction : recentTransactions) { %>
                    <div class="transaction-item">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <div class="transaction-desc">
                                    <%
                                        String description = transaction.getDescription();
                                        if (description == null || description.trim().isEmpty()) {
                                            if (transaction.getTransactionType() == TransactionType.TRANSFER_SENT) {
                                                description = "Transfer Sent";
                                            } else if (transaction.getTransactionType() == TransactionType.TRANSFER_RECEIVED) {
                                                description = "Transfer Received";
                                            } else {
                                                description = transaction.getTransactionType().toString();
                                            }
                                        }
                                    %>
                                    <%= description %>
                                    <% if (transaction.getReferenceAccount() != null) { %>
                                    <small class="text-muted">
                                        <% if (transaction.isDebit()) { %>
                                        to <%= transaction.getReferenceAccount().getAccountNumber() %>
                                        <% } else { %>
                                        from <%= transaction.getReferenceAccount().getAccountNumber() %>
                                        <% } %>
                                    </small>
                                    <% } %>
                                </div>
                                <div class="transaction-date">
                                    <%= transaction.getTransactionDate().format(dateFormatter) %>
                                </div>
                            </div>
                            <div class="<%= transaction.isDebit() ? "amount-debit" : "amount-credit" %>">
                                <%= transaction.getFormattedAmount() %>
                            </div>
                        </div>
                    </div>
                    <% } %>
                        <% } else { %>
                    <div class="text-center py-4">
                        <i class="bi bi-inbox fs-1 text-muted mb-3"></i>
                        <p class="text-muted">No recent transactions found.</p>
                        <p class="text-muted small">
                            <% if (currentUser != null && currentUser.getAccount() == null) { %>
                            You don't have an account associated with your profile.
                            <% } else { %>
                            Start by making your first transfer above!
                            <% } %>
                        </p>
                        <!-- DEBUG INFO - Remove after debugging -->
                        <small class="text-muted">
                            DEBUG: Transactions = <%= recentTransactions != null ? "Empty List" : "NULL" %>
                            <% if (currentUser != null && currentUser.getAccount() != null) { %>
                            | Account ID: <%= currentUser.getAccount().getId() %>
                            <% } %>
                        </small>
                    </div>
                        <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Toast Container -->
<div class="toast-container position-fixed top-0 end-0 p-3">
    <div class="toast" role="alert">
        <div class="toast-header">
            <i class="bi bi-check-circle-fill text-success me-2"></i>
            <strong class="me-auto">SecureBank</strong>
            <button type="button" class="btn-close"></button>
        </div>
        <div class="toast-body" id="toastMessage"></div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>

<script>
    // Show toast notification if there are messages
    <% if (successMessage != null) { %>
    showNotification('<%= successMessage %>', 'success');
    <% } %>

    <% if (errorMessage != null) { %>
    showNotification('<%= errorMessage %>', 'error');
    <% } %>

    // Toast notification system
    function showNotification(message, type = 'success') {
        const toastElement = document.querySelector('.toast');
        const toastMessage = document.getElementById('toastMessage');
        const toastIcon = document.querySelector('.toast-header i');

        toastMessage.textContent = message;

        if (type === 'success') {
            toastIcon.className = 'bi bi-check-circle-fill text-success me-2';
        } else {
            toastIcon.className = 'bi bi-exclamation-triangle-fill text-danger me-2';
        }

        const toast = new bootstrap.Toast(toastElement, { delay: 4000 });
        toast.show();
    }

    // Transfer form validation
    document.getElementById('transferForm').addEventListener('submit', function(e) {
        const amount = parseFloat(this.querySelector('input[name="amount"]').value);
        const currentBalance = parseFloat('<%= userBalance %>'.replace(/,/g, ''));

        if (amount > currentBalance) {
            e.preventDefault();
            showNotification('Transfer amount exceeds available balance', 'error');
        }
    });

    // Schedule form handling (placeholder)
    document.getElementById('scheduleForm').addEventListener('submit', function(e) {
        e.preventDefault();
        showNotification('Scheduled payment feature is coming soon!', 'info');
    });

    // Logout function
    function logout() {
        if (confirm('Are you sure you want to logout?')) {
            showNotification('Logging out...');
            setTimeout(() => {
                window.location.href = '${pageContext.request.contextPath}/logout';
            }, 2000);
        }
    }

    // Form validation enhancements
    document.querySelectorAll('.form-control-futuristic').forEach(input => {
        input.addEventListener('focus', function() {
            this.style.transform = 'translateY(-2px)';
        });

        input.addEventListener('blur', function() {
            this.style.transform = 'translateY(0)';
        });
    });
</script>

</body>
</html>