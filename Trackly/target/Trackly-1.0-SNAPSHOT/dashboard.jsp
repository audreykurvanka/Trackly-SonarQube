<!-- dashboard.jsp -->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trackly - Dashboard</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <button class="menu-btn">☰</button>
            <h1>Hi ${sessionScope.userName}! Let's make today productive!</h1>
            <div class="header-right">
                <span class="user-email">${sessionScope.userEmail}</span>
                <a href="logout" class="logout-btn">Logout</a>
            </div>
        </div>
        
        <!-- Content Grid -->
        <div class="content-grid">
            <!-- Calendar -->
            <div class="card calendar-card">
                <h2>Calendar</h2>
                <div id="calendar"></div>
            </div>
            
            <!-- Schedule -->
            <div class="card schedule-card">
                <div class="card-header">
                    <h2>Schedule</h2>
                    <button class="btn-add-small" onclick="showAddScheduleModal()">+ Add</button>
                </div>
                <div class="schedule-list">
                    <c:forEach var="schedule" items="${schedules}">
                        <div class="schedule-item">
                            <div class="schedule-day">${schedule.hari}</div>
                            <div class="schedule-course">${schedule.courseName}</div>
                            <div class="schedule-time">${schedule.jamMulai}-${schedule.jamSelesai}</div>
                            <div class="schedule-actions">
                                <button class="btn-edit" onclick="editSchedule(${schedule.id}, ${schedule.courseId}, '${schedule.hari}', '${schedule.jamMulai}', '${schedule.jamSelesai}')">✏️</button>
                                <button class="btn-delete" onclick="deleteSchedule(${schedule.id})">🗑️</button>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty schedules}">
                        <p class="empty-message">Belum ada jadwal. Klik "+ Add" untuk menambah.</p>
                    </c:if>
                </div>
            </div>
            
            <!-- To-Do -->
            <div class="card todo-card">
                <div class="card-header">
                    <h2>To-Do</h2>
                    <select id="todoFilter" onchange="filterTodos()">
                        <option value="ongoing">Ongoing</option>
                        <option value="completed">Completed</option>
                        <option value="all">All</option>
                    </select>
                </div>
                <div class="todo-list" id="todoList">
                    <c:forEach var="todo" items="${todos}">
                        <div class="todo-item" data-status="${todo.statusSelesai}">
                            <input type="checkbox" class="todo-checkbox" 
                                   data-id="${todo.id}" 
                                   ${todo.statusSelesai ? 'checked' : ''}
                                   onchange="updateTodoStatus(${todo.id}, this.checked)">
                            <div class="todo-text">
                                <div class="todo-name">${todo.namaAktivitas}</div>
                                <div class="todo-detail">
                                    ${todo.courseName} | 
                                    <fmt:formatDate value="${todo.deadline}" pattern="dd MMM yyyy HH:mm"/>
                                </div>
                            </div>
                            <div class="todo-actions">
                                <button class="btn-edit" onclick="editTodo(${todo.id}, ${todo.courseId}, '${todo.namaAktivitas}', '${todo.jenis}', '<fmt:formatDate value="${todo.deadline}" pattern="yyyy-MM-dd\'T\'HH:mm"/>')">✏️</button>
                                <button class="btn-delete" onclick="deleteTodo(${todo.id})">🗑️</button>
                            </div>
                        </div>
                    </c:forEach>
                    <c:if test="${empty todos}">
                        <p class="empty-message">Belum ada tugas. Klik tombol "+" untuk menambah.</p>
                    </c:if>
                </div>
                <button class="add-btn" onclick="showAddToDoModal()">+</button>
            </div>
            
            <!-- Courses -->
            <div class="card courses-card">
                <div class="card-header">
                    <h2>Courses</h2>
                    <button class="btn-add-small" onclick="showAddCourseModal()">+ Add</button>
                </div>
                <div class="courses-grid">
                    <c:forEach var="course" items="${courses}">
                        <div class="course-card" onclick="window.location.href='courseDetail?id=${course.id}'">
                            <div class="course-icon">📁</div>
                            <div class="course-name">${course.namaCourse}</div>
                            <div class="progress-bar">
                                <div class="progress-fill" style="width: ${course.progress}%"></div>
                            </div>
                            <div class="progress-text"><fmt:formatNumber value="${course.progress}" maxFractionDigits="1"/>%</div>
                            <button class="btn-delete-course" onclick="event.stopPropagation(); deleteCourse(${course.id})">🗑️</button>
                        </div>
                    </c:forEach>
                    <c:if test="${empty courses}">
                        <p class="empty-message">Belum ada course. Klik "+ Add" untuk menambah.</p>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Add Course Modal -->
    <div id="courseModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal('courseModal')">&times;</span>
            <h2>Tambah Course Baru</h2>
            <form id="courseForm" onsubmit="submitCourse(event)">
                <div class="form-group">
                    <label>Nama Course</label>
                    <input type="text" name="namaCourse" required>
                </div>
                <button type="submit">Simpan</button>
            </form>
        </div>
    </div>
    
    <!-- Add/Edit ToDo Modal -->
    <div id="todoModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal('todoModal')">&times;</span>
            <h2 id="todoModalTitle">Tambah ToDo Baru</h2>
            <form id="todoForm" onsubmit="submitTodo(event)">
                <input type="hidden" id="todoId" name="todoId">
                <div class="form-group">
                    <label>Nama Aktivitas</label>
                    <input type="text" id="todoNama" name="namaAktivitas" required>
                </div>
                <div class="form-group">
                    <label>Jenis</label>
                    <input type="text" id="todoJenis" name="jenis" required>
                </div>
                <div class="form-group">
                    <label>Deadline</label>
                    <input type="datetime-local" id="todoDeadline" name="deadline" required>
                </div>
                <div class="form-group">
                    <label>Course</label>
                    <select id="todoCourse" name="courseId">
                        <option value="0">-- Ketik Manual di Bawah --</option>
                        <c:forEach var="course" items="${courses}">
                            <option value="${course.id}">${course.namaCourse}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Atau Ketik Nama Course Manual</label>
                    <input type="text" id="todoManualCourse" name="manualCourseName" placeholder="Ketik nama course jika tidak ada di dropdown">
                    <small style="color: #666;">*Kosongkan jika sudah pilih dari dropdown</small>
                </div>
                <button type="submit">Simpan</button>
            </form>
        </div>
    </div>
    
    <!-- Add/Edit Schedule Modal -->
    <div id="scheduleModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal('scheduleModal')">&times;</span>
            <h2 id="scheduleModalTitle">Tambah Schedule Baru</h2>
            <form id="scheduleForm" onsubmit="submitSchedule(event)">
                <input type="hidden" id="scheduleId" name="scheduleId">
                <div class="form-group">
                    <label>Course</label>
                    <select id="scheduleCourse" name="courseId">
                        <option value="0">-- Ketik Manual di Bawah --</option>
                        <c:forEach var="course" items="${courses}">
                            <option value="${course.id}">${course.namaCourse}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label>Atau Ketik Nama Course Manual</label>
                    <input type="text" id="scheduleManualCourse" name="manualCourseName" placeholder="Ketik nama course jika tidak ada di dropdown">
                    <small style="color: #666;">*Kosongkan jika sudah pilih dari dropdown</small>
                </div>
                <div class="form-group">
                    <label>Hari</label>
                    <select id="scheduleHari" name="hari" required>
                        <option value="Monday">Monday</option>
                        <option value="Tuesday">Tuesday</option>
                        <option value="Wednesday">Wednesday</option>
                        <option value="Thursday">Thursday</option>
                        <option value="Friday">Friday</option>
                        <option value="Saturday">Saturday</option>
                        <option value="Sunday">Sunday</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Jam Mulai</label>
                    <input type="time" id="scheduleStart" name="jamMulai" required>
                </div>
                <div class="form-group">
                    <label>Jam Selesai</label>
                    <input type="time" id="scheduleEnd" name="jamSelesai" required>
                </div>
                <button type="submit">Simpan</button>
            </form>
        </div>
    </div>
    
    <script>
        let currentCourseId = null;
        let editMode = false;
        let editId = null;
        
        // Deadline dates from server
        const deadlineDates = [
<c:forEach var="d" items="${deadlineDatesJson}" varStatus="s">
    "${d}"<c:if test="${!s.last}">,</c:if>
</c:forEach>
];

    </script>
    <script src="js/script.js"></script>
</body>
</html>