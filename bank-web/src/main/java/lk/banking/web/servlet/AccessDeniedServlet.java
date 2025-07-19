package lk.banking.web.servlet;

import lk.banking.core.model.User;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/access-denied")
@PermitAll
public class AccessDeniedServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AccessDeniedServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                LOGGER.log(Level.WARNING, "Access denied for user: {0}, Role: {1}, Requested URL: {2}",
                        new Object[]{currentUser.getEmail(), currentUser.getRole(), request.getRequestURI()});
            }
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().println("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Access Denied - SecureBank</title>
                <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" rel="stylesheet">
            </head>
            <body class="bg-light">
                <div class="container">
                    <div class="row justify-content-center align-items-center min-vh-100">
                        <div class="col-md-6">
                            <div class="card border-danger">
                                <div class="card-body text-center">
                                    <i class="bi bi-shield-exclamation text-danger" style="font-size: 4rem;"></i>
                                    <h2 class="card-title text-danger mt-3">Access Denied</h2>
                                    <p class="card-text text-muted">
                                        You don't have permission to access this resource.
                                    </p>
                                    <div class="mt-4">
                                        <a href="javascript:history.back()" class="btn btn-secondary me-2">
                                            <i class="bi bi-arrow-left"></i> Go Back
                                        </a>
                                        <a href="  + request.getContextPath() + /pages/login.jsp" class="btn btn-primary">
                                            <i class="bi bi-house"></i> Home
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}