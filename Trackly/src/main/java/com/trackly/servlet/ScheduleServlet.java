package com.trackly.servlet;

import com.google.gson.Gson;
import com.trackly.model.Schedule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Map<String, Object> res = new HashMap<>();

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            res.put("success", false);
            response.getWriter().write(new Gson().toJson(res));
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        try {
            if ("add".equals(action) || "update".equals(action)) {

                Integer scheduleId = null;
                if ("update".equals(action)) {
                    scheduleId = Integer.parseInt(request.getParameter("scheduleId"));
                }

                Integer courseId = Integer.parseInt(request.getParameter("courseId"));
                String manualCourseName = request.getParameter("manualCourseName");
                String hari = request.getParameter("hari");
                String jamMulai = request.getParameter("jamMulai");
                String jamSelesai = request.getParameter("jamSelesai");

                if (courseId == 0) {
                    courseId = null;
                    if (manualCourseName == null || manualCourseName.trim().isEmpty()) {
                        res.put("success", false);
                        res.put("message", "Nama course wajib diisi");
                        response.getWriter().write(new Gson().toJson(res));
                        return;
                    }
                } else {
                    manualCourseName = null;
                }

                boolean success = ("add".equals(action))
                        ? Schedule.addSchedule(userId, courseId, manualCourseName, hari, jamMulai, jamSelesai)
                        : Schedule.updateSchedule(scheduleId, courseId, manualCourseName, hari, jamMulai, jamSelesai);

                res.put("success", success);

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("scheduleId"));
                res.put("success", Schedule.deleteSchedule(id));
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
        }

        response.getWriter().write(new Gson().toJson(res));
    }
}
