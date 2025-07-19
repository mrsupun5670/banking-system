/**
 * SecureBank - Common JavaScript Utilities
 * File: pages/js/common.js
 */

// Common utility functions
const SecureBank = {
    // API endpoints (configure based on your backend)
    API: {
        LOGIN: '/api/auth/login',
        REGISTER: '/api/auth/register',
        ACCOUNTS: '/api/accounts',
        TRANSACTIONS: '/api/transactions',
        TRANSFER: '/api/transactions/transfer',
        LOGOUT: '/api/auth/logout'
    },

    // Common UI utilities
    UI: {
        // Show loading state on button
        showButtonLoading: function(button, loadingText = 'Processing...') {
            const originalText = button.textContent;
            button.textContent = loadingText;
            button.classList.add('loading');
            button.disabled = true;
            return originalText;
        },

        // Hide loading state on button
        hideButtonLoading: function(button, originalText) {
            button.textContent = originalText;
            button.classList.remove('loading');
            button.disabled = false;
        },

        // Show modal
        showModal: function(modalId) {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.classList.add('show');
                document.body.style.overflow = 'hidden';
            }
        },

        // Hide modal
        hideModal: function(modalId) {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.classList.remove('show');
                document.body.style.overflow = '';
            }
        },

        // Show alert
        showAlert: function(message, type = 'info', duration = 5000) {
            const alertContainer = document.getElementById('alertContainer') || this.createAlertContainer();

            const alert = document.createElement('div');
            alert.className = `alert alert-${type}`;
            alert.innerHTML = `
                <span>${message}</span>
                <button onclick="this.parentElement.remove()" class="close-btn" style="margin-left: auto; background: none; border: none; font-size: 1.2rem; cursor: pointer;">&times;</button>
            `;

            alertContainer.appendChild(alert);

            // Auto remove after duration
            setTimeout(() => {
                if (alert.parentElement) {
                    alert.remove();
                }
            }, duration);
        },

        // Create alert container if it doesn't exist
        createAlertContainer: function() {
            const container = document.createElement('div');
            container.id = 'alertContainer';
            container.style.cssText = `
                position: fixed;
                top: 1rem;
                right: 1rem;
                z-index: 9999;
                display: flex;
                flex-direction: column;
                gap: 0.5rem;
                max-width: 400px;
            `;
            document.body.appendChild(container);
            return container;
        },

        // Add input animations
        addInputAnimations: function() {
            document.querySelectorAll('.form-input, .form-select').forEach(input => {
                input.addEventListener('focus', () => {
                    input.style.transform = 'translateY(-1px)';
                });

                input.addEventListener('blur', () => {
                    input.style.transform = 'translateY(0)';
                });
            });
        }
    },

    // Validation utilities
    Validation: {
        // Email validation
        isValidEmail: function(email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            return emailRegex.test(email);
        },

        // Password strength validation
        getPasswordStrength: function(password) {
            let strength = 0;
            const checks = [
                /.{8,}/, // At least 8 characters
                /[A-Z]/, // Uppercase letter
                /[a-z]/, // Lowercase letter
                /[0-9]/, // Number
                /[^A-Za-z0-9]/ // Special character
            ];

            checks.forEach(check => {
                if (check.test(password)) strength++;
            });

            if (strength === 0) return { level: 'none', text: 'Enter a password' };
            if (strength <= 2) return { level: 'weak', text: 'Weak password' };
            if (strength === 3) return { level: 'fair', text: 'Fair password' };
            if (strength === 4) return { level: 'good', text: 'Good password' };
            return { level: 'strong', text: 'Strong password' };
        },

        // Form validation
        validateForm: function(formElement) {
            const errors = [];
            const inputs = formElement.querySelectorAll('[required]');

            inputs.forEach(input => {
                if (!input.value.trim()) {
                    errors.push(`${this.getFieldLabel(input)} is required`);
                    input.classList.add('error');
                } else {
                    input.classList.remove('error');
                }

                // Email validation
                if (input.type === 'email' && input.value && !this.isValidEmail(input.value)) {
                    errors.push('Please enter a valid email address');
                    input.classList.add('error');
                }
            });

            return errors;
        },

        // Get field label for error messages
        getFieldLabel: function(input) {
            const label = input.previousElementSibling;
            if (label && label.tagName === 'LABEL') {
                return label.textContent.replace('*', '').trim();
            }
            return input.name || input.id || 'Field';
        }
    },

    // Formatting utilities
    Format: {
        // Format currency
        currency: function(amount, currency = 'USD') {
            return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: currency
            }).format(amount);
        },

        // Format date
        date: function(date, options = {}) {
            const defaultOptions = {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            };
            return new Intl.DateTimeFormat('en-US', { ...defaultOptions, ...options }).format(new Date(date));
        },

        // Format account number (mask middle digits)
        accountNumber: function(accountNumber) {
            if (!accountNumber || accountNumber.length < 4) return accountNumber;
            const first = accountNumber.substring(0, 4);
            const last = accountNumber.substring(accountNumber.length - 4);
            const middle = '*'.repeat(accountNumber.length - 8);
            return `${first}${middle}${last}`;
        },

        // Format time ago
        timeAgo: function(date) {
            const now = new Date();
            const diffInSeconds = Math.floor((now - new Date(date)) / 1000);

            if (diffInSeconds < 60) return 'Just now';
            if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
            if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
            if (diffInSeconds < 2592000) return `${Math.floor(diffInSeconds / 86400)}d ago`;

            return this.date(date);
        }
    },

    // HTTP utilities
    HTTP: {
        // Make API request
        request: async function(url, options = {}) {
            const defaultOptions = {
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            };

            // Add CSRF token if available
            const csrfToken = document.querySelector('meta[name="_csrf"]');
            if (csrfToken) {
                defaultOptions.headers['X-CSRF-TOKEN'] = csrfToken.getAttribute('content');
            }

            const config = {
                ...defaultOptions,
                ...options,
                headers: { ...defaultOptions.headers, ...options.headers }
            };

            try {
                const response = await fetch(url, config);

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({ message: 'An error occurred' }));
                    throw new Error(errorData.message || `HTTP ${response.status}`);
                }

                return await response.json();
            } catch (error) {
                console.error('API Request failed:', error);
                throw error;
            }
        },

        // GET request
        get: function(url) {
            return this.request(url, { method: 'GET' });
        },

        // POST request
        post: function(url, data) {
            return this.request(url, {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        // PUT request
        put: function(url, data) {
            return this.request(url, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        },

        // DELETE request
        delete: function(url) {
            return this.request(url, { method: 'DELETE' });
        }
    },

    // Local storage utilities
    Storage: {
        // Set item
        set: function(key, value) {
            try {
                localStorage.setItem(key, JSON.stringify(value));
            } catch (error) {
                console.error('LocalStorage set failed:', error);
            }
        },

        // Get item
        get: function(key, defaultValue = null) {
            try {
                const item = localStorage.getItem(key);
                return item ? JSON.parse(item) : defaultValue;
            } catch (error) {
                console.error('LocalStorage get failed:', error);
                return defaultValue;
            }
        },

        // Remove item
        remove: function(key) {
            try {
                localStorage.removeItem(key);
            } catch (error) {
                console.error('LocalStorage remove failed:', error);
            }
        },

        // Clear all
        clear: function() {
            try {
                localStorage.clear();
            } catch (error) {
                console.error('LocalStorage clear failed:', error);
            }
        }
    },

    // Initialize common functionality
    init: function() {
        // Add input animations
        this.UI.addInputAnimations();

        // Handle modal close on backdrop click
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.classList.remove('show');
                document.body.style.overflow = '';
            }
        });

        // Handle ESC key for modals
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                const openModal = document.querySelector('.modal.show');
                if (openModal) {
                    openModal.classList.remove('show');
                    document.body.style.overflow = '';
                }
            }
        });

        // Add form validation error styles
        const style = document.createElement('style');
        style.textContent = `
            .form-input.error, .form-select.error {
                border-color: var(--error) !important;
                box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1) !important;
            }
        `;
        document.head.appendChild(style);
    }
};

// Initialize when DOM is loaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => SecureBank.init());
} else {
    SecureBank.init();
}

// Export for use in other scripts
window.SecureBank = SecureBank;