package com.trackly.servlet;

import com.google.gson.Gson;
import com.trackly.model.Note;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(401);
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String action = request.getParameter("action");

        Map<String, Object> res = new HashMap<>();

        try {
            if ("add".equals(action)) {
                int courseId = Integer.parseInt(request.getParameter("courseId"));
                String title = request.getParameter("title");
                String content = request.getParameter("content");

                res.put("success", Note.add(courseId, userId, title, content));

            } else if ("update".equals(action)) {
                int noteId = Integer.parseInt(request.getParameter("noteId"));
                String title = request.getParameter("title");
                String content = request.getParameter("content");

                res.put("success", Note.update(noteId, title, content));

            } else if ("delete".equals(action)) {
                int noteId = Integer.parseInt(request.getParameter("noteId"));
                res.put("success", Note.delete(noteId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.put("success", false);
        }

        response.getWriter().write(new Gson().toJson(res));
    }
}
