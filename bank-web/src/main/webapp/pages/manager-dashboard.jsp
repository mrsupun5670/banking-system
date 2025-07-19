<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="lk.banking.core.model.User" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    List<User> customers = (List<User>) request.getSession().getAttribute("customers");
    Long totalCustomers = (Long) request.getSession().getAttribute("totalCustomers");
    String totalDeposits = (String) request.getSession().getAttribute("totalDeposits");

    // Get success/error messages
    String successMessage = (String) request.getSession().getAttribute("successMessage");
    String errorMessage = (String) request.getSession().getAttribute("errorMessage");
    String messageType = (String) request.getSession().getAttribute("messageType");

    // Clear messages after displaying
    if (successMessage != null) {
        request.getSession().removeAttribute("successMessage");
    }
    if (errorMessage != null) {
        request.getSession().removeAttribute("errorMessage");
    }
    if (messageType != null) {
        request.getSession().removeAttribute("messageType");
    }

    System.out.println(totalCustomers);

    DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SecureBank - Manager Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="css/style.css">
</head>
<body class="dashboard-layout">
<nav class="navbar navbar-expand-lg navbar-admin">
    <div class="container">
        <a class="navbar-brand" href="#">
            <i class="bi bi-shield-lock"></i>
            SecureBank
            <span class="admin-badge">Manager</span>
        </a>
        <div class="d-flex align-items-center">
                <span class="welcome-text me-3">
                    Welcome, <span class="welcome-name" style="color: var(--accent-purple); font-weight: 700;">Manager Admin</span>
                </span>
            <button class="btn btn-admin-danger btn-sm" onclick="logout()">
                <i class="bi bi-power me-2"></i>Logout
            </button>
        </div>
    </div>
</nav>

<div class="container py-4">
    <div class="welcome-section fade-in-up">
        <div class="welcome-title">Manager Dashboard</div>
        <div class="welcome-subtitle">Manage customers, transfers, and banking operations</div>
    </div>

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

    <!-- Stats Overview -->
    <div class="stats-overview">
        <div class="row g-3 fade-in-left">
            <div class="col-lg-3 col-md-6">
                <div class="stat-card-admin">
                    <div class="stat-number-admin primary"><%= totalCustomers != null ? totalCustomers : 0 %></div>
                    <div class="stat-label-admin">Total Customers</div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card-admin">
                    <div class="stat-number-admin success"><%= totalDeposits != null ? totalDeposits : "LKR 0.00" %></div>
                    <div class="stat-label-admin">Total Deposits</div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card-admin">
                    <div class="stat-number-admin warning">24</div>
                    <div class="stat-label-admin">Pending Approvals</div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card-admin">
                    <div class="stat-number-admin info">3.5%</div>
                    <div class="stat-label-admin">Current Interest</div>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-4 mt-4">
        <div class="col-lg-6 mb-3">
            <div class="card-admin fade-in-left">
                <div class="card-header-admin">
                    <i class="bi bi-send"></i>Transfer Money to Customer
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/transferMoney" method="post" >
                        <div class="mb-3">
                            <label class="form-label">Customer Account Number</label>
                            <input type="text" name="accountNumber" class="form-control form-control-admin"
                                   placeholder="Enter customer account number" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Amount (LKR)</label>
                            <div class="input-group">
                                <span class="input-group-text" style="background: rgba(248, 250, 252, 0.8); border: 1px solid var(--border-color); color: var(--accent-purple); font-weight: 600;">LKR</span>
                                <input type="number" name="transferAmount" class="form-control form-control-admin"
                                       placeholder="0.00" min="0" step="0.01" required>
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="form-label">Transfer Note</label>
                            <input type="text" name="transferNote" class="form-control form-control-admin"
                                   placeholder="Purpose of transfer (optional)">
                        </div>
                        <button type="submit" class="btn btn-admin-primary w-100">
                            <i class="bi bi-send me-2"></i>Process Transfer
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-lg-6 mb-3">
            <div class="card-admin fade-in-right">
                <div class="card-header-admin">
                    <i class="bi bi-percent"></i>Update Interest Rate
                </div>
                <div class="card-body">
                    <form action="updateInterestServlet" method="post" id="interestForm">
                        <div class="mb-3">
                            <label class="form-label">Current Interest Rate</label>
                            <input type="text" class="form-control form-control-admin" value="3.5%" readonly
                                   style="background: rgba(248, 250, 252, 0.4);">
                        </div>
                        <div class="mb-4">
                            <label class="form-label">New Interest Rate (%)</label>
                            <div class="input-group">
                                <input type="number" step="0.01" name="interestRate" class="form-control form-control-admin"
                                       placeholder="3.75" min="0" max="15" required>
                                <span class="input-group-text" style="background: rgba(248, 250, 252, 0.8); border: 1px solid var(--border-color); color: var(--accent-purple); font-weight: 600;">%</span>
                            </div>
                        </div>
                        <div class="mb-4">
                            <small class="text-muted">
                                <i class="bi bi-info-circle me-1"></i>
                                This will affect all savings accounts effective immediately
                            </small>
                        </div>
                        <button type="submit" class="btn btn-admin-success w-100">
                            <i class="bi bi-arrow-up-circle me-2"></i>Update Rate
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div class="card-admin fade-in-up" style="animation-delay: 0.2s;">
        <div class="card-header-admin">
            <i class="bi bi-people"></i>All Customers
            <span class="ms-auto badge" style="background: var(--accent-purple); color: white; padding: 4px 8px; border-radius: 6px; font-size: 0.75rem;">
                <%= totalCustomers != null ? totalCustomers : 0 %> Total
            </span>
        </div>
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-admin mb-0">
                    <thead>
                    <tr>
                        <th>Account Number</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Balance</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                        if (customers != null) {
                            for (User customer : customers) {
                                String accountNumber = "No Account";
                                String balance = "LKR 0.00";

                                if (customer.getAccount() != null) {
                                    accountNumber = customer.getAccount().getAccountNumber();
                                    balance = "LKR " + currencyFormat.format(customer.getAccount().getBalance());
                                }
                    %>
                    <tr>
                        <td>
                            <% if (!"No Account".equals(accountNumber)) { %>
                            <%= accountNumber %>
                            <% } else { %>
                            <span class="text-muted">No Account</span>
                            <% } %>
                        </td>
                        <td><%= customer.getFirst_name() %> <%= customer.getLast_name() %></td>
                        <td><%= customer.getEmail() %></td>
                        <td>
                            <% if (!"LKR 0.00".equals(balance)) { %>
                            <span><%= balance %></span>
                            <% } else { %>
                            <span class="text-muted">LKR 0.00</span>
                            <% } %>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="4" class="text-center text-muted py-4">
                            <i class="bi bi-person-x me-2"></i>No customers found
                        </td>
                    </tr>
                    <%
                        }
                    %>
                    </tbody>
                </table>
            </div>
            <% if (customers != null && !customers.isEmpty()) { %>
            <div class="p-3 text-center" style="border-top: 1px solid var(--border-color);">
                <button class="btn btn-outline-primary">
                    <i class="bi bi-arrow-down-circle me-2"></i>Load More Customers
                </button>
            </div>
            <% } %>
        </div>
    </div>
</div>

<!-- Toast Container -->
<div class="toast-container position-fixed top-0 end-0 p-3">
    <div class="toast" role="alert">
        <div class="toast-header">
            <i class="bi bi-check-circle-fill text-success me-2"></i>
            <strong class="me-auto">SecureBank Manager</strong>
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

    // Interest rate form handling
    document.getElementById('interestForm').addEventListener('submit', function(e) {
        e.preventDefault();

        const button = this.querySelector('button[type="submit"]');
        const originalText = button.innerHTML;
        const newRate = this.querySelector('input[name="interestRate"]').value;

        // Show loading state
        button.innerHTML = '<i class="bi bi-arrow-clockwise spinner me-2"></i>Updating...';
        button.classList.add('btn-loading');

        // Simulate interest rate update
        setTimeout(() => {
            showNotification(`Interest rate updated to ${newRate}% successfully`);
            button.innerHTML = originalText;
            button.classList.remove('btn-loading');

            // Update the current rate display
            document.querySelector('input[readonly]').value = `${newRate}%`;
            this.reset();
        }, 1500);
    });

    // Logout function
    function logout() {
        if (confirm('Are you sure you want to logout?')) {
            showNotification('Logging out...');
            setTimeout(() => {
                window.location.href = 'manager-login.jsp';
            }, 2000);
        }
    }

    // Add loading animation CSS
    const style = document.createElement('style');
    style.textContent = `
            .spin { animation: spin 1s linear infinite; }
            @keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }
        `;
    document.head.appendChild(style);

    // Enhanced hover effects for cards
    document.querySelectorAll('.card-admin').forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    // Table row click handling
    document.querySelectorAll('.table-admin tbody tr').forEach(row => {
        row.addEventListener('click', function(e) {
            if (!e.target.closest('button')) {
                const accountNumber = this.querySelector('td:first-child').textContent.trim();
                if (accountNumber && accountNumber !== 'No customers found' && accountNumber !== 'No Account') {
                    showNotification(`Viewing details for account ${accountNumber}`, 'info');
                }
            }
        });
    });

    // Form validation enhancements
    document.querySelectorAll('.form-control-admin').forEach(input => {
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