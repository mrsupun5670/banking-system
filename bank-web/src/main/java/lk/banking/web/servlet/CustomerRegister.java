package lk.banking.web.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.banking.core.model.User;
import lk.banking.ejb.bean.RegisterBean;

import java.io.IOException;

@WebServlet("/register")
public class CustomerRegister extends HttpServlet {

    @EJB
    private RegisterBean registerBean;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = new User(email, password, "CUSTOMER", firstname, lastname);

        registerBean.addUser(user);

        System.out.println("OKAY");

        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(req.getContextPath() + "/pages/register.jsp").forward(req, resp);
    }
}
