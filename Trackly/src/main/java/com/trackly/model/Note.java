package com.trackly.model;

import com.trackly.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Note {

    private int id;
    private int courseId;
    private int userId;
    private String title;
    private String content;

    public Note() {}

    public Note(int id, int courseId, int userId, String title, String content) {
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    // ================= GETTERS =================
    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    // ================= CRUD =================

    public static List<Note> getNotesByCourse(int courseId, int userId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE course_id=? AND user_id=? ORDER BY updated_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("content")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notes;
    }

    public static boolean add(int courseId, int userId, String title, String content) {
        String sql = "INSERT INTO notes (course_id, user_id, title, content) VALUES (?,?,?,?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, userId);
            ps.setString(3, title);
            ps.setString(4, content);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean update(int id, String title, String content) {
        String sql = "UPDATE notes SET title=?, content=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, id);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean delete(int id) {
        String sql = "DELETE FROM notes WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
