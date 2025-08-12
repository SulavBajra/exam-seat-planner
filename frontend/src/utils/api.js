const API_BASE = "http://localhost:8081/api";

export const apiService = {
  // Generic API call function
  async request(endpoint, options = {}) {
    try {
      const response = await fetch(`${API_BASE}${endpoint}`, {
        headers: {
          "Content-Type": "application/json",
          ...options.headers,
        },
        ...options,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return options.method === "DELETE" ? null : await response.json();
    } catch (error) {
      console.error("API call failed:", error);
      throw error;
    }
  },

  // Students API
  students: {
    getAll: () => apiService.request("/students"),
    getById: (id) => apiService.request(`/students/${id}`),
    create: (student) =>
      apiService.request("/students", {
        method: "POST",
        body: JSON.stringify(student),
      }),
    update: (id, student) =>
      apiService.request(`/students/${id}`, {
        method: "PUT",
        body: JSON.stringify(student),
      }),
    delete: (id) =>
      apiService.request(`/students/${id}`, {
        method: "DELETE",
      }),
    getByProgramAndSemester: (programCode, semester) =>
      apiService.request(
        `/students/program/${programCode}/semester/${semester}`
      ),
  },

  // Programs API
  programs: {
    getAll: () => apiService.request("/programs"),
    getById: (id) => apiService.request(`/programs/${id}`),
    create: (program) =>
      apiService.request("/programs", {
        method: "POST",
        body: JSON.stringify(program),
      }),
    update: (id, program) =>
      apiService.request(`/programs/${id}`, {
        method: "PUT",
        body: JSON.stringify(program),
      }),
    delete: (id) =>
      apiService.request(`/programs/${id}`, {
        method: "DELETE",
      }),
  },

  // Rooms API
  rooms: {
    getAll: () => apiService.request("/rooms"),
    getById: (id) => apiService.request(`/rooms/${id}`),
    create: (room) =>
      apiService.request("/rooms", {
        method: "POST",
        body: JSON.stringify(room),
      }),
    update: (id, room) =>
      apiService.request(`/rooms/${id}`, {
        method: "PUT",
        body: JSON.stringify(room),
      }),
    delete: (id) =>
      apiService.request(`/rooms/${id}`, {
        method: "DELETE",
      }),
  },

  // Exams API
  exams: {
    getAll: () => apiService.request("/exams"),
    getById: (id) => apiService.request(`/exams/${id}`),
    create: (exam) =>
      apiService.request("/exams", {
        method: "POST",
        body: JSON.stringify(exam),
      }),
    update: (id, exam) =>
      apiService.request(`/exams/${id}`, {
        method: "PUT",
        body: JSON.stringify(exam),
      }),
    delete: (id) =>
      apiService.request(`/exams/${id}`, {
        method: "DELETE",
      }),
  },

  // Seat Assignments API
  seatAssignments: {
    getAll: () => apiService.request("/seat-assignments"),
    getByExam: (examId) =>
      apiService.request(`/seat-assignments/exam/${examId}`),
    getByRoom: (roomNo) =>
      apiService.request(`/seat-assignments/room/${roomNo}`),
    generate: (examId) =>
      apiService.request(`/seat-assignments/generate/${examId}`, {
        method: "POST",
      }),
    deleteByExam: (examId) =>
      apiService.request(`/seat-assignments/exam/${examId}`, {
        method: "DELETE",
      }),
  },
};
