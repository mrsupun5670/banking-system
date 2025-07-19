<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SecureBank - Login</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link href="css/style.css" rel="stylesheet"/>
</head>
<body class="d-flex justify-content-center align-items-center">
<div class="login-container">
    <div class="login-card">
        <!-- Brand Logo -->
        <div class="brand-logo">
            <i class="bi bi-bank brand-icon"></i>
            <div class="brand-name">SecureBank</div>
            <div class="brand-tagline">Your trusted financial partner</div>
        </div>

        <!-- Error Message Display -->
        <%
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <%= errorMessage %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <!-- Login Form -->
        <form action="${pageContext.request.contextPath}/login" method="post">
            <!-- Email Field -->
            <div class="form-floating">
                <input type="email" class="form-control form-control-futuristic"
                       id="email" name="email" placeholder="Email" required>
                <label for="email">Email</label>
            </div>

            <!-- Password Field -->
            <div class="form-floating position-relative">
                <input type="password" class="form-control form-control-futuristic"
                       id="password" name="password" placeholder="Password" required>
                <label for="password">Password</label>
                <button type="button" class="password-toggle" onclick="togglePassword()">
                    <i class="bi bi-eye" id="passwordIcon"></i>
                </button>
            </div>

            <!-- Login Button -->
            <button type="submit" class="btn btn-login">
                <i class="bi bi-box-arrow-in-right me-2"></i>
                Sign In
            </button>
        </form>

        <!-- Divider -->
        <div class="divider">
            <span>New to SecureBank?</span>
        </div>

        <!-- Register Link -->
        <div class="register-link">
            <span class="text-muted">Don't have an account? </span>
            <a href="customer-register.jsp">Create Account</a>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>

<script>
    // Password toggle functionality
    function togglePassword() {
        const passwordField = document.getElementById('password');
        const passwordIcon = document.getElementById('passwordIcon');

        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            passwordIcon.className = 'bi bi-eye-slash';
        } else {
            passwordField.type = 'password';
            passwordIcon.className = 'bi bi-eye';
        }
    }

    // Add floating label animation
    document.querySelectorAll('.form-control-futuristic').forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });

        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });
    });

    // Add subtle animation on form field focus
    document.querySelectorAll('.form-control-futuristic').forEach(input => {
        input.addEventListener('focus', function() {
            this.style.transform = 'translateY(-2px)';
        });

        input.addEventListener('blur', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Form validation
    document.querySelector('form').addEventListener('submit', function(e) {
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!email) {
            e.preventDefault();
            alert('Please enter your email address');
            return;
        }

        if (!password) {
            e.preventDefault();
            alert('Please enter your password');
            return;
        }

        // Simple email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            e.preventDefault();
            alert('Please enter a valid email address');
            return;
        }
    });
</script>
</body>
</html>