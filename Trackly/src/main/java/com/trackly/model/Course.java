package com.trackly.model;

import com.trackly.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Course {

    private int id;
    private int userId;
    private String namaCourse;
    private float progress;

    public Course() {}

    public Course(int id, int userId, String namaCourse, float progress) {
        this.id = id;
        this.userId = userId;
        this.namaCourse = namaCourse;
        this.progress = progress;
    }

    // ==================== GET COURSES ====================

    public static List<Course> getCoursesByUserId(int userId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("nama_course"),
                    rs.getFloat("progress")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Course(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("nama_course"),
                    rs.getFloat("progress")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== CRUD ====================

    public static boolean addCourse(int userId, String namaCourse) {
        String sql = "INSERT INTO courses (user_id, nama_course, progress) VALUES (?, ?, 0)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, namaCourse);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================== PROGRESS ====================

    public static void updateProgress(int courseId) {
        String sql =
            "UPDATE courses c SET progress = (" +
            " SELECT COALESCE(" +
            "   (COUNT(CASE WHEN status_selesai = 1 THEN 1 END) * 100.0 / NULLIF(COUNT(*),0)), 0)" +
            " FROM todos WHERE course_id = c.id)" +
            " WHERE c.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getNamaCourse() { return namaCourse; }
    public float getProgress() { return progress; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setNamaCourse(String namaCourse) { this.namaCourse = namaCourse; }
    public void setProgress(float progress) { this.progress = progress; }
}
