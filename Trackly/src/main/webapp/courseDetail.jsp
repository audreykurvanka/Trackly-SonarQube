<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
    <title>Trackly - ${course.namaCourse}</title>
    <link rel="stylesheet" href="css/style.css">

    <style>
        body {
            background: #8ba9e8;
            font-family: Arial, sans-serif;
        }

        .course-detail-container {
            max-width: 1100px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .btn-back {
            display: inline-block;
            margin-bottom: 20px;
            padding: 10px 16px;
            background: #667eea;
            color: white;
            border-radius: 8px;
            font-weight: bold;
            text-decoration: none;
        }

        .btn-back:hover {
            background: #5568d3;
        }

        .page-title {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 30px;
            color: #1f2d5c;
        }

        .section-card {
            background: white;
            border-radius: 14px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 4px 14px rgba(0,0,0,0.08);
        }

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        /* NOTES */
        .notes-list {
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .note-item {
            padding: 14px 18px;
            border-radius: 10px;
            background: #f6f8ff;
            cursor: pointer;
            transition: all .2s;
            border-left: 5px solid #667eea;
        }

        .note-item:hover {
            background: #eef1ff;
            transform: translateX(4px);
        }

        .note-title {
            font-weight: bold;
        }

        /* TODO */
        .todo-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 15px;
            border-radius: 10px;
            background: #fafafa;
            margin-bottom: 10px;
        }

        .todo-item.done {
            opacity: 0.6;
            text-decoration: line-through;
        }

        /* MODAL */
        .modal {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.45);
            z-index: 1000;
        }

        .modal-content {
            background: white;
            max-width: 480px;
            margin: 8% auto;
            padding: 25px;
            border-radius: 14px;
        }

        .modal-content input,
        .modal-content textarea {
            width: 100%;
            padding: 10px;
            margin-bottom: 12px;
            border-radius: 8px;
            border: 1px solid #ddd;
        }

        .note-actions {
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .btn-primary {
            background: #667eea;
            color: white;
            border: none;
            padding: 10px 16px;
            border-radius: 8px;
            cursor: pointer;
        }

        .btn-danger {
            background: #ff5c5c;
            color: white;
            border: none;
            padding: 10px 16px;
            border-radius: 8px;
            cursor: pointer;
        }

        .btn-secondary {
            background: #ddd;
            border: none;
            padding: 10px 16px;
            border-radius: 8px;
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="course-detail-container">

    <a href="dashboard" class="btn-back">← Kembali ke Dashboard</a>

    <div class="page-title">${course.namaCourse}</div>

    <!-- NOTES -->
    <div class="section-card">
        <div class="section-header">
            <h3>📝 Notes</h3>
            <button class="btn-primary" onclick="openNoteModal()">+ Tambah Note</button>
        </div>

        <div class="notes-list">
            <c:forEach var="note" items="${notes}">
                <div class="note-item"
                     onclick="openNoteModal(
                         ${note.id},
                         '${note.title}',
                         '${note.content}'
                     )">
                    <div class="note-title">${note.title}</div>
                </div>
            </c:forEach>

            <c:if test="${empty notes}">
                <p style="color:#777;">Belum ada catatan.</p>
            </c:if>
        </div>
    </div>

    <!-- TODO -->
    <div class="section-card">
        <h3>📋 Daftar Tugas</h3><br>

        <c:forEach var="todo" items="${todos}">
            <div class="todo-item ${todo.statusSelesai ? 'done' : ''}">
                <input type="checkbox"
                       ${todo.statusSelesai ? "checked" : ""}
                       onchange="updateTodoStatus(${todo.id}, this)">
                <span>${todo.namaAktivitas}</span>
            </div>
        </c:forEach>

        <c:if test="${empty todos}">
            <p style="color:#777;">Tidak ada tugas.</p>
        </c:if>
    </div>

</div>

<!-- NOTE MODAL -->
<div id="noteModal" class="modal">
    <div class="modal-content">
        <h3>Note</h3>

        <input id="noteTitle" placeholder="Judul note">
        <textarea id="noteContent" rows="5" placeholder="Isi note..."></textarea>

        <div class="note-actions">
            <button class="btn-secondary" onclick="closeNoteModal()">Tutup</button>
            <button class="btn-danger" onclick="deleteNote()">Hapus</button>
            <button class="btn-primary" onclick="saveNote()">Simpan</button>
        </div>
    </div>
</div>

<script>
    const courseId = ${course.id};

    let activeNoteId = null;
    const modal = document.getElementById("noteModal");
    const noteTitle = document.getElementById("noteTitle");
    const noteContent = document.getElementById("noteContent");

    function openNoteModal(id = null, title = "", content = "") {
        activeNoteId = id;
        noteTitle.value = title;
        noteContent.value = content;
        modal.style.display = "block";
    }

    function closeNoteModal() {
        modal.style.display = "none";
        activeNoteId = null;
        noteTitle.value = "";
        noteContent.value = "";
    }

    function saveNote() {
        const data = new URLSearchParams();
        data.append("action", activeNoteId ? "update" : "add");
        if (activeNoteId) data.append("noteId", activeNoteId);
        data.append("courseId", courseId);
        data.append("title", noteTitle.value);
        data.append("content", noteContent.value);

        fetch("note", { method: "POST", body: data })
            .then(() => location.reload());
    }

    function deleteNote() {
        if (!activeNoteId) return;

        const data = new URLSearchParams();
        data.append("action", "delete");
        data.append("noteId", activeNoteId);

        fetch("note", { method: "POST", body: data })
            .then(() => location.reload());
    }

    function updateTodoStatus(todoId, checkbox) {
        fetch("todo", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                action: "updateStatus",
                todoId: todoId,
                status: checkbox.checked
            })
        }).then(res => res.json())
          .then(data => {
              if (!data.success) {
                  checkbox.checked = !checkbox.checked;
              }
          });
    }
</script>

</body>
</html>
