// ==================== MODAL MANAGEMENT ====================
let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();

function showModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
    resetForms();
}

function resetForms() {
    document.getElementById('courseForm').reset();
    document.getElementById('todoForm').reset();
    document.getElementById('scheduleForm').reset();
    editMode = false;
    editId = null;
}

// Close modal when clicking outside
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
        resetForms();
    }
}

// ==================== COURSE MANAGEMENT ====================

function showAddCourseModal() {
    document.getElementById('courseModal').querySelector('h2').textContent = 'Tambah Course Baru';
    showModal('courseModal');
}

function submitCourse(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    formData.append('action', 'add');
    
    fetch('course', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan!');
    });
}

function deleteCourse(courseId) {
    if (!confirm('Yakin ingin menghapus course ini? Semua data terkait akan ikut terhapus!')) {
        return;
    }
    
    const formData = new URLSearchParams();
    formData.append('action', 'delete');
    formData.append('courseId', courseId);
    
    fetch('course', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan!');
    });
}

// ==================== TODO MANAGEMENT ====================

function showAddToDoModal() {
    editMode = false;
    editId = null;
    document.getElementById('todoModalTitle').textContent = 'Tambah ToDo Baru';
    document.getElementById('todoForm').reset();
    document.getElementById('todoId').value = '';
    showModal('todoModal');
}

function editTodo(todoId, courseId, namaAktivitas, jenis, deadline) {
    editMode = true;
    editId = todoId;
    
    document.getElementById('todoModalTitle').textContent = 'Edit ToDo';
    document.getElementById('todoId').value = todoId;
    document.getElementById('todoNama').value = namaAktivitas;
    document.getElementById('todoJenis').value = jenis;
    document.getElementById('todoDeadline').value = deadline;
    document.getElementById('todoCourse').value = courseId;
    
    showModal('todoModal');
}

function submitTodo(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    
    if (editMode && editId) {
        formData.append('action', 'update');
        formData.append('todoId', editId);
    } else {
        formData.append('action', 'add');
    }
    
    fetch('todo', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan!');
    });
}

function updateTodoStatus(todoId, status) {
    const formData = new URLSearchParams();
    formData.append('action', 'updateStatus');
    formData.append('todoId', todoId);
    formData.append('status', status);
    
    fetch('todo', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Update todo item status in DOM without reload
            const todoItem = document.querySelector(`input[data-id="${todoId}"]`).closest('.todo-item');
            todoItem.dataset.status = status;
            
            // Reapply filter
            filterTodos();
        } else {
            alert(data.message);
            const checkbox = document.querySelector(`input[data-id="${todoId}"]`);
            if (checkbox) checkbox.checked = !status;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        const checkbox = document.querySelector(`input[data-id="${todoId}"]`);
        if (checkbox) checkbox.checked = !status;
    });
}

function deleteTodo(todoId) {
    if (!confirm('Yakin ingin menghapus ToDo ini?')) {
        return;
    }
    
    const formData = new URLSearchParams();
    formData.append('action', 'delete');
    formData.append('todoId', todoId);
    
    fetch('todo', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan!');
    });
}

function filterTodos() {
    const filter = document.getElementById('todoFilter').value;
    const todos = document.querySelectorAll('.todo-item');
    
    todos.forEach(todo => {
        const isCompleted = todo.dataset.status === 'true';
        
        if (filter === 'all') {
            todo.style.display = 'flex';
        } else if (filter === 'ongoing') {
            todo.style.display = isCompleted ? 'none' : 'flex';
        } else if (filter === 'completed') {
            todo.style.display = isCompleted ? 'flex' : 'none';
        }
    });
}

// ==================== SCHEDULE MANAGEMENT ====================

function showAddScheduleModal() {
    editMode = false;
    editId = null;
    document.getElementById('scheduleModalTitle').textContent = 'Tambah Schedule Baru';
    document.getElementById('scheduleForm').reset();
    document.getElementById('scheduleId').value = '';
    showModal('scheduleModal');
}

function editSchedule(scheduleId, courseId, hari, jamMulai, jamSelesai) {
    editMode = true;
    editId = scheduleId;
    
    document.getElementById('scheduleModalTitle').textContent = 'Edit Schedule';
    document.getElementById('scheduleId').value = scheduleId;
    document.getElementById('scheduleCourse').value = courseId;
    document.getElementById('scheduleHari').value = hari;
    document.getElementById('scheduleStart').value = jamMulai;
    document.getElementById('scheduleEnd').value = jamSelesai;
    
    showModal('scheduleModal');
}

function submitSchedule(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    
    if (editMode && editId) {
        formData.append('action', 'update');
        formData.append('scheduleId', editId);
    } else {
        formData.append('action', 'add');
    }
    
    fetch('schedule', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan!');
    });
}

function deleteSchedule(scheduleId) {
    if (!confirm('Yakin ingin menghapus Schedule ini?')) {
        return;
    }
    
    const formData = new URLSearchParams();
    formData.append('action', 'delete');
    formData.append('scheduleId', scheduleId);
    
    fetch('schedule', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(data.message);
            location.reload();
        } else {
            alert(data.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Terjadi kesalahan!');
    });
}

// ==================== CALENDAR GENERATION ====================
// ==================== DEADLINE DATES ====================
let deadlineDates = [];

function collectDeadlineDates() {
    deadlineDates = [];

    document.querySelectorAll(".todo-item").forEach(item => {
        const detail = item.querySelector(".todo-detail");
        if (!detail) return;

        // ambil "18 Dec 2025"
        const match = detail.textContent.match(/(\d{2}) (\w{3}) (\d{4})/);
        if (!match) return;

        const day = match[1];
        const monthStr = match[2];
        const year = match[3];

        const monthMap = {
            Jan: "01", Feb: "02", Mar: "03", Apr: "04",
            May: "05", Jun: "06", Jul: "07", Aug: "08",
            Sep: "09", Oct: "10", Nov: "11", Dec: "12"
        };

        const month = monthMap[monthStr];
        if (!month) return;

        const dateStr = `${year}-${month}-${day}`;

        if (!deadlineDates.includes(dateStr)) {
            deadlineDates.push(dateStr);
        }
    });

    console.log("Deadline dates (fixed):", deadlineDates);
}

function generateCalendar() {
    collectDeadlineDates();
    const calendar = document.getElementById("calendar");
    calendar.innerHTML = "";

    const monthNames = [
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    ];

    // HEADER
    const header = document.createElement("div");
    header.className = "calendar-header";
    header.innerHTML = `
        <button onclick="prevMonth()">◀</button>
        ${monthNames[currentMonth]} ${currentYear}
        <button onclick="nextMonth()">▶</button>
    `;
    calendar.appendChild(header);

    // DAYS HEADER
    const daysGrid = document.createElement("div");
    daysGrid.className = "calendar-grid";

    ["Mon","Tue","Wed","Thu","Fri","Sat","Sun"].forEach(d => {
        const el = document.createElement("div");
        el.className = "calendar-day-header";
        el.textContent = d;
        daysGrid.appendChild(el);
    });

    const firstDay = new Date(currentYear, currentMonth, 1);
    const startDay = (firstDay.getDay() + 6) % 7; // monday start
    const lastDate = new Date(currentYear, currentMonth + 1, 0).getDate();

    // EMPTY CELLS
    for (let i = 0; i < startDay; i++) {
        daysGrid.appendChild(document.createElement("div"));
    }

    for (let d = 1; d <= lastDate; d++) {
        const dateStr = `${currentYear}-${String(currentMonth+1).padStart(2,'0')}-${String(d).padStart(2,'0')}`;

        const dayEl = document.createElement("div");
        dayEl.className = "calendar-day";
        dayEl.textContent = d;

        // today
        const today = new Date();
        if (
            d === today.getDate() &&
            currentMonth === today.getMonth() &&
            currentYear === today.getFullYear()
        ) {
            dayEl.classList.add("today");
        }

       // deadline marker
if (Array.isArray(deadlineDates) && deadlineDates.includes(dateStr)) {
    dayEl.classList.add("has-deadline");
    dayEl.innerHTML += `<span class="deadline-dot">•</span>`;
    dayEl.onclick = () => showDeadlineTodos(dateStr);
}


        daysGrid.appendChild(dayEl);
    }

    calendar.appendChild(daysGrid);
}
function prevMonth() {
    currentMonth--;
    if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
    }
    generateCalendar();
}

function nextMonth() {
    currentMonth++;
    if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
    }
    generateCalendar();
}
// === DEADLINE POPUP MODAL ===
const deadlineModal = document.createElement("div");
deadlineModal.id = "deadlineModal";
deadlineModal.style.display = "none";
deadlineModal.innerHTML = `
    <div class="modal">
        <div class="modal-content">
            <h3 id="deadlineTitle">Deadline</h3>
            <div id="deadlineList"></div>
            <button onclick="closeDeadlineModal()" style="margin-top:15px;">Tutup</button>
        </div>
    </div>
`;
document.body.appendChild(deadlineModal);

function showDeadlineTodos(date) {
    const list = document.getElementById("deadlineList");
    const title = document.getElementById("deadlineTitle");

    // convert YYYY-MM-DD -> Date object
    const dateObj = new Date(date);

    // format jadi "dd MMM yyyy" (SAMA kayak To-Do)
    const formattedDate = dateObj.toLocaleDateString("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    }).replace(",", "");

    title.textContent = `Deadline ${formattedDate}`;
    list.innerHTML = "";

    let found = false;

    document.querySelectorAll(".todo-item").forEach(item => {
        const detail = item.querySelector(".todo-detail");
        const name = item.querySelector(".todo-name");

        if (detail && detail.textContent.includes(formattedDate)) {
            found = true;
            list.innerHTML += `
                <div style="
                    padding:10px;
                    border-radius:8px;
                    background:#f6f8ff;
                    margin-bottom:8px;
                ">
                    <strong>${name.textContent}</strong><br>
                    <small>${detail.textContent}</small>
                </div>
            `;
        }
    });

    if (!found) {
        list.innerHTML = `<p style="color:#777;">Tidak ada deadline.</p>`;
    }

    deadlineModal.style.display = "block";
}


function closeDeadlineModal() {
    deadlineModal.style.display = "none";
}


// ==================== INITIALIZATION ====================

// Generate calendar on page load
window.addEventListener('load', function() {
    generateCalendar();
    
    // Set default filter to ongoing
    const todoFilter = document.getElementById('todoFilter');
    if (todoFilter) {
        todoFilter.value = 'ongoing';
        filterTodos();
    }
});

// Prevent form submission on Enter key (except in textarea)
document.addEventListener('keydown', function(event) {
    if (event.key === 'Enter' && event.target.tagName !== 'TEXTAREA') {
        const form = event.target.closest('form');
        if (form && event.target.type !== 'submit') {
            event.preventDefault();
        }
    }
});

// ==================== NOTES MANAGEMENT ====================

let activeNoteId = null;



function openNoteModal(id = null, title = '', content = '') {
    activeNoteId = id;

    document.getElementById('noteTitle').value = title;
    document.getElementById('noteContent').value = content;

    showModal('noteModal');
}

function saveNote() {
    const data = new URLSearchParams();
    data.append('action', activeNoteId ? 'update' : 'add');
    if (activeNoteId) data.append('noteId', activeNoteId);
    data.append('courseId', courseId);
    data.append('title', document.getElementById('noteTitle').value);
    data.append('content', document.getElementById('noteContent').value);

    fetch('note', {
        method: 'POST',
        body: data
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            alert(data.message);
        }
    });
}

function deleteNote() {
    if (!activeNoteId) return;
    if (!confirm('Hapus note ini?')) return;

    const data = new URLSearchParams();
    data.append('action', 'delete');
    data.append('noteId', activeNoteId);

    fetch('note', {
        method: 'POST',
        body: data
    })
    .then(() => location.reload());
}
