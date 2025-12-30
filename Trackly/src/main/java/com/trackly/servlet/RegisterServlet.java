package com.trackly.servlet;

import com.trackly.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nama = request.getParameter("nama");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (nama == null || nama.trim().isEmpty()) {
            request.setAttribute("error", "Nama tidak boleh kosong!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email tidak boleh kosong!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (password == null || password.length() < 6) {
            request.setAttribute("error", "Password minimal 6 karakter!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Password tidak cocok!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (User.emailExists(email)) {
            request.setAttribute("error", "Email sudah terdaftar!");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }
        
        if (User.register(nama, email, password)) {
            request.setAttribute("success", "Registrasi berhasil! Silakan login.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registrasi gagal! Coba lagi.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}