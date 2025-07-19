<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SecureBank - Create Account</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link href="css/style.css" rel="stylesheet">
</head>
<body class="d-flex align-items-center justify-content-center">
<div class="register-container ">
    <div class="register-card">
        <!-- Brand Logo -->
        <div class="brand-logo">
            <i class="bi bi-person-plus-fill brand-icon"></i>
            <div class="brand-name">Join SecureBank</div>
            <div class="brand-tagline">Create your secure banking account</div>
        </div>

        <!-- Registration Form -->
        <form method="POST" action="${pageContext.request.contextPath}/register">
            <!-- Full Name -->
            <div class="form-row">
                <div class="form-floating half-width">
                    <input type="text" class="form-control form-control-futuristic" id="firstName"
                           placeholder="First Name" name="firstname" required>
                    <label for="firstName">First Name</label>
                    <div class="validation-feedback" id="firstNameFeedback"></div>
                </div>
                <div class="form-floating half-width">
                    <input type="text" class="form-control form-control-futuristic" id="lastName" name="lastname"
                           placeholder="Last Name" required>
                    <label for="lastName">Last Name</label>
                    <div class="validation-feedback" id="lastNameFeedback"></div>
                </div>
            </div>
            <!-- Email and Phone -->
                <div class="form-floating half-width">
                    <input type="email" class="form-control form-control-futuristic" id="email" name="email"
                           placeholder="Email Address" required>
                    <label for="email">Email Address</label>
                    <div class="validation-feedback" id="emailFeedback"></div>
                </div>

            <!-- Password -->
            <div class="form-floating position-relative">
                <input type="password" class="form-control form-control-futuristic" id="password" name="password"
                       placeholder="Password" required>
                <label for="password">Password</label>
                <button type="button" class="password-toggle" onclick="togglePassword('password', 'passwordIcon')">
                    <i class="bi bi-eye" id="passwordIcon"></i>
                </button>
                <div class="password-strength">
                    <div class="strength-bar">
                        <div class="strength-fill" id="strengthFill"></div>
                    </div>
                    <div id="strengthText">Password strength: Weak</div>
                </div>
            </div>

            <!-- Confirm Password -->
            <div class="form-floating position-relative">
                <input type="password" class="form-control form-control-futuristic" id="confirmPassword" name="confirmPassword"
                       placeholder="Confirm Password" required>
                <label for="confirmPassword">Confirm Password</label>
                <button type="button" class="password-toggle" onclick="togglePassword('confirmPassword', 'confirmPasswordIcon')">
                    <i class="bi bi-eye" id="confirmPasswordIcon"></i>
                </button>
                <div class="validation-feedback" id="confirmPasswordFeedback"></div>
            </div>

            <!-- Register Button -->
            <button type="submit" class="btn btn-register">
                <i class="bi bi-person-plus me-2"></i>
                Create Account
            </button>
        </form>

        <div class="divider">
            <span>Already have an account?</span>
        </div>

        <!-- Login Link -->
        <div class="login-link">
            <span class="text-muted">Ready to sign in? </span>
            <a href="login.jsp">Login Here</a>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>

<script>
    function togglePassword(fieldId, iconId) {
        const passwordField = document.getElementById(fieldId);
        const passwordIcon = document.getElementById(iconId);

        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            passwordIcon.className = 'bi bi-eye-slash';
        } else {
            passwordField.type = 'password';
            passwordIcon.className = 'bi bi-eye';
        }
    }

    function checkPasswordStrength(password) {
        let strength = 0;
        const checks = [
            password.length >= 8,
            /[a-z]/.test(password),
            /[A-Z]/.test(password),
            /[0-9]/.test(password),
            /[^A-Za-z0-9]/.test(password)
        ];

        strength = checks.filter(Boolean).length;

        const strengthFill = document.getElementById('strengthFill');
        const strengthText = document.getElementById('strengthText');

        strengthFill.className = 'strength-fill';

        if (strength <= 2) {
            strengthFill.classList.add('strength-weak');
            strengthText.textContent = 'Password strength: Weak';
        } else if (strength === 3) {
            strengthFill.classList.add('strength-fair');
            strengthText.textContent = 'Password strength: Fair';
        } else if (strength === 4) {
            strengthFill.classList.add('strength-good');
            strengthText.textContent = 'Password strength: Good';
        } else {
            strengthFill.classList.add('strength-strong');
            strengthText.textContent = 'Password strength: Strong';
        }

        return strength;
    }

    // Real-time validation
    function validateField(field, validationFn, feedbackId) {
        const feedback = document.getElementById(feedbackId);
        const isValid = validationFn(field.value);

        if (field.value.length > 0) {
            if (isValid.valid) {
                field.classList.remove('is-invalid');
                field.classList.add('is-valid');
                feedback.textContent = isValid.message || '';
                feedback.classList.add('valid');
            } else {
                field.classList.remove('is-valid');
                field.classList.add('is-invalid');
                feedback.textContent = isValid.message;
                feedback.classList.remove('valid');
            }
        } else {
            field.classList.remove('is-valid', 'is-invalid');
            feedback.textContent = '';
        }
    }

    // Validation functions
    const validators = {
        firstname: (value) => ({
            valid: value.length >= 2 && /^[a-zA-Z\s]+$/.test(value),
            message: value.length < 2 ? 'Name must be at least 2 characters' :
                !/^[a-zA-Z\s]+$/.test(value) ? 'Name can only contain letters and spaces' : ''
        }),
        lastname: (value) => ({
            valid: value.length >= 2 && /^[a-zA-Z\s]+$/.test(value),
            message: value.length < 2 ? 'Name must be at least 2 characters' :
                !/^[a-zA-Z\s]+$/.test(value) ? 'Name can only contain letters and spaces' : ''
        }),
        email: (value) => ({
            valid: /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value),
            message: /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) ? '' : 'Please enter a valid email address'
        }),
        phone: (value) => ({
            valid: /^[\+]?[1-9][\d]{0,15}$/.test(value.replace(/[-\s]/g, '')),
            message: /^[\+]?[1-9][\d]{0,15}$/.test(value.replace(/[-\s]/g, '')) ? '' : 'Please enter a valid phone number'
        }),

        password: (value) => {
            const strength = checkPasswordStrength(value);
            return {
                valid: strength >= 3,
                message: strength < 3 ? 'Password should be stronger' : ''
            };
        },
        confirmPassword: (value) => {
            const password = document.getElementById('password').value;
            return {
                valid: value === password && value.length > 0,
                message: value !== password ? 'Passwords do not match' : ''
            };
        }
    };

    // Add event listeners for real-time validation
    Object.keys(validators).forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) {
            field.addEventListener('input', () => {
                validateField(field, validators[fieldId], fieldId + 'Feedback');

                // Also validate confirm password when password changes
                if (fieldId === 'password') {
                    const confirmField = document.getElementById('confirmPassword');
                    if (confirmField.value) {
                        validateField(confirmField, validators.confirmPassword, 'confirmPasswordFeedback');
                    }
                }
            });
        }
    });

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
</script>
</body>
</html>