package com.trackly.servlet;

import com.trackly.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        List<Course> courses = Course.getCoursesByUserId(userId);
        List<ToDo> todos = ToDo.getToDosByUserId(userId);  // GANTI INI - ambil SEMUA todos
        List<Schedule> schedules = Schedule.getSchedulesByUserId(userId);

        // Get deadline dates for calendar
        List<String> deadlineDates = ToDo.getDeadlineDates(userId);

        request.setAttribute("courses", courses);
        request.setAttribute("todos", todos);
        request.setAttribute("schedules", schedules);
        request.setAttribute("deadlineDatesJson", new Gson().toJson(deadlineDates));

        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}