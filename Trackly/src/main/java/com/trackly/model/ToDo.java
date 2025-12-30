package com.trackly.model;

import com.trackly.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ToDo {
    private int id;
    private int courseId;
    private String courseName;
    private String namaAktivitas;
    private String jenis;
    private Timestamp deadline;
    private boolean statusSelesai;

    // ================= CONSTRUCTOR =================
    public ToDo(int id, String namaAktivitas, String jenis, Timestamp deadline,
                boolean statusSelesai, String courseName, int courseId) {
        this.id = id;
        this.namaAktivitas = namaAktivitas;
        this.jenis = jenis;
        this.deadline = deadline;
        this.statusSelesai = statusSelesai;
        this.courseName = courseName;
        this.courseId = courseId;
    }

    // ================= GETTERS =================
    public int getId() { return id; }
    public int getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getNamaAktivitas() { return namaAktivitas; }
    public String getJenis() { return jenis; }
    public Timestamp getDeadline() { return deadline; }

    public boolean isStatusSelesai() {
        return statusSelesai;
    }

    // ================= ADD TODO =================
    public static boolean addToDo(Integer userId, Integer courseId, String manualCourseName,
                                  String namaAktivitas, String jenis, String deadlineStr) {

        String sql = "INSERT INTO todos (user_id, course_id, manual_course_name, nama_aktivitas, jenis, deadline) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            if (courseId == null) {
                ps.setNull(2, Types.INTEGER);
                ps.setString(3, manualCourseName);
            } else {
                ps.setInt(2, courseId);
                ps.setNull(3, Types.VARCHAR);
            }

            ps.setString(4, namaAktivitas);
            ps.setString(5, jenis);
            ps.setTimestamp(6, Timestamp.valueOf(deadlineStr.replace("T", " ") + ":00"));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= UPDATE TODO =================
    public static boolean updateToDo(Integer todoId, Integer courseId, String manualCourseName,
                                     String namaAktivitas, String jenis, String deadlineStr) {

        String sql = "UPDATE todos SET course_id=?, manual_course_name=?, nama_aktivitas=?, jenis=?, deadline=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (courseId == null) {
                ps.setNull(1, Types.INTEGER);
                ps.setString(2, manualCourseName);
            } else {
                ps.setInt(1, courseId);
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setString(3, namaAktivitas);
            ps.setString(4, jenis);
            ps.setTimestamp(5, Timestamp.valueOf(deadlineStr.replace("T", " ") + ":00"));
            ps.setInt(6, todoId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= UPDATE STATUS =================
    public static boolean updateStatus(int todoId, boolean status) {
        String sql = "UPDATE todos SET status_selesai=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, status);
            ps.setInt(2, todoId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= DELETE =================
    public static boolean deleteToDo(int todoId) {
        String sql = "DELETE FROM todos WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, todoId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getCourseIdByTodoId(int todoId) {
        String sql = "SELECT course_id FROM todos WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, todoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("course_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ================= GET TODOS BY USER =================
    public static List<ToDo> getToDosByUserId(int userId) {
        List<ToDo> list = new ArrayList<>();

        String sql =
            "SELECT t.*, COALESCE(c.nama_course, t.manual_course_name) AS nama_course " +
            "FROM todos t LEFT JOIN courses c ON t.course_id = c.id " +
            "WHERE t.user_id=? ORDER BY t.deadline";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ToDo(
                        rs.getInt("id"),
                        rs.getString("nama_aktivitas"),
                        rs.getString("jenis"),
                        rs.getTimestamp("deadline"),
                        rs.getBoolean("status_selesai"),
                        rs.getString("nama_course"),
                        rs.getInt("course_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= GET TODOS BY COURSE =================
    public static List<ToDo> getToDosByCourseId(int courseId) {
        List<ToDo> list = new ArrayList<>();

        String sql =
            "SELECT t.*, COALESCE(c.nama_course, t.manual_course_name) AS nama_course " +
            "FROM todos t LEFT JOIN courses c ON t.course_id = c.id " +
            "WHERE t.course_id=? ORDER BY t.deadline";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ToDo(
                        rs.getInt("id"),
                        rs.getString("nama_aktivitas"),
                        rs.getString("jenis"),
                        rs.getTimestamp("deadline"),
                        rs.getBoolean("status_selesai"),
                        rs.getString("nama_course"),
                        rs.getInt("course_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= CALENDAR =================
    public static List<String> getDeadlineDates(int userId) {
        List<String> dates = new ArrayList<>();

        String sql =
            "SELECT DISTINCT DATE(deadline) AS deadline_date FROM todos " +
            "WHERE user_id=? AND status_selesai=FALSE";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dates.add(rs.getString("deadline_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
}
