package com.trackly.model;

import com.trackly.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private int id;
    private Integer courseId;
    private String courseName;
    private String hari;
    private String jamMulai;
    private String jamSelesai;

    public Schedule(int id, Integer courseId, String courseName,
                    String hari, String jamMulai, String jamSelesai) {
        this.id = id;
        this.courseId = courseId;
        this.courseName = courseName;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
    }

    // ================= GET BY USER =================
    public static List<Schedule> getSchedulesByUserId(int userId) {
        List<Schedule> list = new ArrayList<>();

        String sql =
            "SELECT s.*, COALESCE(c.nama_course, s.manual_course_name) AS nama_course " +
            "FROM schedules s " +
            "LEFT JOIN courses c ON s.course_id = c.id " +
            "WHERE s.user_id = ? " +
            "ORDER BY FIELD(s.hari,'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Schedule(
                        rs.getInt("id"),
                        (Integer) rs.getObject("course_id"),
                        rs.getString("nama_course"),
                        rs.getString("hari"),
                        rs.getString("jam_mulai"),
                        rs.getString("jam_selesai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ================= ADD =================
    public static boolean addSchedule(int userId, Integer courseId, String manualCourseName,
                                      String hari, String jamMulai, String jamSelesai) {

        String sql =
            "INSERT INTO schedules (user_id, course_id, manual_course_name, hari, jam_mulai, jam_selesai) " +
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

            ps.setString(4, hari);
            ps.setString(5, jamMulai);
            ps.setString(6, jamSelesai);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= UPDATE =================
    public static boolean updateSchedule(int id, Integer courseId, String manualCourseName,
                                         String hari, String jamMulai, String jamSelesai) {

        String sql =
            "UPDATE schedules SET course_id=?, manual_course_name=?, hari=?, jam_mulai=?, jam_selesai=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (courseId == null) {
                ps.setNull(1, Types.INTEGER);
                ps.setString(2, manualCourseName);
            } else {
                ps.setInt(1, courseId);
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setString(3, hari);
            ps.setString(4, jamMulai);
            ps.setString(5, jamSelesai);
            ps.setInt(6, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= DELETE =================
    public static boolean deleteSchedule(int id) {
        String sql = "DELETE FROM schedules WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= GETTERS =================
    public int getId() { return id; }
    public Integer getCourseId() { return courseId; }
    public String getCourseName() { return courseName; }
    public String getHari() { return hari; }
    public String getJamMulai() { return jamMulai; }
    public String getJamSelesai() { return jamSelesai; }
}
