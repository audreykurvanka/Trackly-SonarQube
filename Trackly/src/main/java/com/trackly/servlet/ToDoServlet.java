package com.trackly.servlet;

import com.google.gson.Gson;
import com.trackly.model.ToDo;
import com.trackly.model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/todo")
public class ToDoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Map<String, Object> result = new HashMap<>();
        Gson gson = new Gson();

        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            result.put("success", false);
            result.put("message", "Session habis");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        try {

            /* ================= ADD / UPDATE TODO ================= */
            if ("add".equals(action) || "update".equals(action)) {

                Integer todoId = null;
                if ("update".equals(action)) {
                    todoId = Integer.parseInt(request.getParameter("todoId"));
                }

                int courseId = Integer.parseInt(request.getParameter("courseId"));
                String manualCourseName = request.getParameter("manualCourseName");
                String namaAktivitas = request.getParameter("namaAktivitas");
                String jenis = request.getParameter("jenis");
                String deadlineStr = request.getParameter("deadline");

                // VALIDASI COURSE
                if (courseId == 0) {
                    if (manualCourseName == null || manualCourseName.trim().isEmpty()) {
                        result.put("success", false);
                        result.put("message", "Nama course wajib diisi");
                        response.getWriter().write(gson.toJson(result));
                        return;
                    }
                } else {
                    manualCourseName = null;
                }

                boolean success;
                if ("add".equals(action)) {
                    success = ToDo.addToDo(
                            userId,
                            courseId == 0 ? null : courseId,
                            manualCourseName,
                            namaAktivitas,
                            jenis,
                            deadlineStr
                    );
                } else {
                    success = ToDo.updateToDo(
                            todoId,
                            courseId == 0 ? null : courseId,
                            manualCourseName,
                            namaAktivitas,
                            jenis,
                            deadlineStr
                    );
                }

                // 🔥 HITUNG ULANG PROGRESS COURSE
                if (courseId != 0) {
                    Course.updateProgress(courseId);
                }

                result.put("success", success);
                result.put("message", success ? "ToDo berhasil disimpan!" : "Gagal menyimpan ToDo");
            }

            /* ================= UPDATE STATUS (CHECK / UNCHECK) ================= */
            else if ("updateStatus".equals(action)) {

                int todoId = Integer.parseInt(request.getParameter("todoId"));
                boolean status = Boolean.parseBoolean(request.getParameter("status"));

                boolean success = ToDo.updateStatus(todoId, status);

                // 🔥 AMBIL COURSE ID + UPDATE PROGRESS (WAJIB)
                int courseId = ToDo.getCourseIdByTodoId(todoId);
                if (courseId != 0) {
                    Course.updateProgress(courseId);
                }

                result.put("success", success);
            }

            /* ================= DELETE TODO ================= */
            else if ("delete".equals(action)) {

                int todoId = Integer.parseInt(request.getParameter("todoId"));

                // 🔥 ambil courseId SEBELUM delete
                int courseId = ToDo.getCourseIdByTodoId(todoId);

                boolean success = ToDo.deleteToDo(todoId);

                // 🔥 update progress setelah delete
                if (courseId != 0) {
                    Course.updateProgress(courseId);
                }

                result.put("success", success);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Terjadi kesalahan server");
        }

        response.getWriter().write(gson.toJson(result));
    }
}
