/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.trackly.servlet;

import com.trackly.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/courseDetail")
public class CourseDetailServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String courseIdParam = request.getParameter("id");
        
        if (courseIdParam == null) {
            response.sendRedirect("dashboard");
            return;
        }
        
        int courseId = Integer.parseInt(courseIdParam);
        
        // Get course details
        Course course = Course.getCourseById(courseId);
        
        // Get todos for this course
        List<ToDo> todos = ToDo.getToDosByCourseId(courseId);
        
        List<Note> notes = Note.getNotesByCourse(courseId, userId);

        
        // Calculate progress
        int totalTodos = todos.size();
        int completedTodos = 0;
        for (ToDo todo : todos) {
            if (todo.isStatusSelesai()) {
                completedTodos++;
            }
        }
        
        float progress = totalTodos > 0 ? (completedTodos * 100.0f / totalTodos) : 0;
        
        request.setAttribute("course", course);
        request.setAttribute("todos", todos);
        request.setAttribute("totalTodos", totalTodos);
        request.setAttribute("completedTodos", completedTodos);
        request.setAttribute("notes", notes);
        request.setAttribute("progress", progress);
       
        request.getRequestDispatcher("courseDetail.jsp").forward(request, response);
    }
}
