import React, { useEffect, useState } from "react";

export default function SeatPlanForm() {
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [exams, setExams] = useState([]);
  const [selectedExam, setSelectedExam] = useState("");
  const [rooms, setRooms] = useState([]);
  const [roomId, setRoomId] = useState(null);

  useEffect(() => {
    fetchExams();
  }, []);

  useEffect(() => {
    if (selectedExam) {
      fetchExamId(selectedExam);
    }
  }, [selectedExam]);

  async function fetchExams() {
    try {
      const response = await fetch("http://localhost:8081/api/exams");
      const data = await response.json();
      setExams(data);
    } catch (error) {
      console.error("Error fetching exams:", error);
    }
  }

  async function fetchExamId(examId) {
    try {
      const response = await fetch(`http://localhost:8081/api/exams/${examId}`);
      const data = await response.json();
      setRooms(data.roomNumbers);
    } catch (error) {
      console.error("Error fetching exam with Id: ", error);
    }
  }

  function fetchRooms() {
    rooms.map((room) => {
      setRoomId(room.roomId);
    });
  }

  async function fetchRowsAndColumn() {}

  return (
    <div className="p-4">
      <form>
        <div className="flex flex-col gap-3">
          <label className="font-medium">Choose Exam</label>
          {exams.map((exam) => (
            <label key={exam.examId} className="flex items-center gap-2">
              <input
                type="radio"
                name="exam"
                value={exam.examId}
                checked={selectedExam === exam.examId}
                onChange={() => setSelectedExam(exam.examId)}
              />
              {exam.subjectName}
            </label>
          ))}

          {selectedExam && rooms.length > 0 && (
            <div className="mt-4 text-sm text-gray-700">
              {rooms.map((room) => (
                <div key={room.roomId}>
                  <p>Seat Rows: {room.numRow}</p>
                  <p>Seat Columns: {room.numColumn}</p>
                </div>
              ))}
            </div>
          )}
        </div>
      </form>
    </div>
  );
}
