package com.trackly.servlet;

import com.google.gson.Gson;
import com.trackly.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/course")
public class CourseServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        Map<String, Object> result = new HashMap<>();
        Gson gson = new Gson();

        try {
            if ("add".equals(action)) {

                String namaCourse = request.getParameter("namaCourse");
                boolean success = Course.addCourse(userId, namaCourse);

                result.put("success", success);
                result.put("message",
                        success ? "Course berhasil ditambahkan!" : "Gagal menambahkan course!");

            } else if ("delete".equals(action)) {

                int courseId = Integer.parseInt(request.getParameter("courseId"));
                boolean success = Course.deleteCourse(courseId);

                result.put("success", success);
                result.put("message",
                        success ? "Course berhasil dihapus!" : "Gagal menghapus course!");

            } else {
                result.put("success", false);
                result.put("message", "Action tidak dikenali!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Terjadi kesalahan server!");
        }

        response.getWriter().write(gson.toJson(result));
    }
}
