import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";

export default function ExamForm({ onSuccess }) {
  const [programs, setPrograms] = useState([]);
  const [subjects, setSubjects] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    subjectCode: "",
    date: "",
    roomNumbers: [],
  });

  const getTodayDate = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  const [minDate] = useState(getTodayDate());

  useEffect(() => {
    fetchPrograms();
    fetchRooms();
    fetchSubjects();
  }, []);

  async function fetchPrograms() {
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      const data = await response.json();
      setPrograms(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
    }
  }

  async function fetchRooms() {
    try {
      const response = await fetch("http://localhost:8081/api/rooms");
      const data = await response.json();
      setRooms(data);
    } catch (error) {
      console.error("Error fetching rooms:", error);
    }
  }

  async function fetchSubjects() {
    try {
      const response = await fetch("http://localhost:8081/api/subjects");
      const data = await response.json();
      setSubjects(data);
    } catch (error) {
      console.error("Error fetching subjects:", error);
    } finally {
      setLoading(false);
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const handleRoomCheckboxChange = (roomNo) => {
    setFormData((prev) => {
      const newRoomNumbers = prev.roomNumbers.includes(roomNo)
        ? prev.roomNumbers.filter((num) => num !== roomNo) // Remove if already selected
        : [...prev.roomNumbers, roomNo]; // Add if not selected

      return {
        ...prev,
        roomNumbers: newRoomNumbers,
      };
    });

    if (errors.roomNumbers) {
      setErrors((prev) => ({
        ...prev,
        roomNumbers: "",
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);

    try {
      const response = await fetch("http://localhost:8081/api/exams", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) throw new Error("Failed to schedule exam");

      const newExam = await response.json();
      onSuccess(newExam);
    } catch (error) {
      console.error("Error scheduling exam:", error);
      setErrors({ submit: error.message });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 mt-4">
      <div className="flex flex-col gap-1">
        <label htmlFor="subjectCode" className="font-medium">
          Subject
        </label>
        <select
          id="subjectCode"
          name="subjectCode"
          className={`bg-black border rounded-md p-2 ${
            errors.subjectCode ? "border-red-500" : "border-gray-300"
          }`}
          value={formData.subjectCode}
          onChange={handleInputChange}
          required
        >
          <option value="">Select a subject</option>
          {subjects.map((subject) => (
            <option key={subject.subjectCode} value={subject.subjectCode}>
              {subject.subjectCode} - {subject.subjectName}
            </option>
          ))}
        </select>
        {errors.subjectCode && (
          <span className="text-red-500 text-sm">{errors.subjectCode}</span>
        )}
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="date" className="font-medium">
          Exam Date
        </label>
        <input
          type="date"
          id="date"
          name="date"
          min={minDate}
          className={`bg-black border rounded-md p-2 ${
            errors.date ? "border-red-500" : "border-gray-300"
          }`}
          value={formData.date}
          onChange={handleInputChange}
          required
        />
        {errors.date && (
          <span className="text-red-500 text-sm">{errors.date}</span>
        )}
      </div>

      <div className="flex flex-col gap-1">
        <label className="font-medium">Rooms Available</label>
        <div
          className={`border rounded-md p-2 ${
            errors.roomNumbers ? "border-red-500" : "border-gray-300"
          }`}
        >
          {rooms.length > 0 ? (
            rooms.map((room) => (
              <div key={room.roomNo} className="flex items-center gap-2 p-1">
                <input
                  type="checkbox"
                  id={`room-${room.roomNo}`}
                  checked={formData.roomNumbers.includes(room.roomNo)}
                  onChange={() => handleRoomCheckboxChange(room.roomNo)}
                  className="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                />
                <label htmlFor={`room-${room.roomNo}`} className="text-sm">
                  {room.roomNo}
                </label>
              </div>
            ))
          ) : (
            <p className="text-sm text-gray-500">No rooms available</p>
          )}
        </div>
        {errors.roomNumbers && (
          <span className="text-red-500 text-sm">{errors.roomNumbers}</span>
        )}
      </div>

      {errors.submit && <p className="text-red-500">{errors.submit}</p>}

      <Button
        type="submit"
        disabled={submitting}
        className="bg-indigo-600 text-white mt-2 py-2 px-4 rounded disabled:opacity-50"
      >
        {submitting ? "Scheduling..." : "Schedule Exam"}
      </Button>
    </form>
  );
}
