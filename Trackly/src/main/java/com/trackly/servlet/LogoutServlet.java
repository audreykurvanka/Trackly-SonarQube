package com.trackly.servlet;

import jakarta.servlet.ServletException;  // ← GANTI INI
import jakarta.servlet.annotation.WebServlet;  // ← GANTI INI
import jakarta.servlet.http.*;  // ← GANTI INI
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        response.sendRedirect("login");
    }
}