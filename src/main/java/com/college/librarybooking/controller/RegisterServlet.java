package com.college.librarybooking.controller;

import com.college.librarybooking.dao.UserDAO;
import com.college.librarybooking.model.User;
import com.college.librarybooking.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String studentId = req.getParameter("studentId");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (studentId == null || !studentId.matches("^[A-Za-z0-9_-]{4,30}$")) {
            req.setAttribute("error", "Student ID must be 4-30 characters (letters/numbers/_/-).");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        if (name == null || name.trim().length() < 3) {
            req.setAttribute("error", "Name must be at least 3 characters.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        if (password == null || password.length() < 6) {
            req.setAttribute("error", "Password must be at least 6 characters.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        User user = new User();
        user.setStudentId(studentId.trim());
        user.setName(name.trim());
        user.setEmail(email == null ? "" : email.trim());
        user.setPasswordHash(PasswordUtil.hash(password));

        boolean created = userDAO.createUser(user);
        if (!created) {
            req.setAttribute("error", "Registration failed. Student ID or Email might already exist.");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/login?registered=true");
    }
}
