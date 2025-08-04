import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";

export default function Student() {
  const [students, setStudents] = useState([]);
  const [enrollYear, setEnrollYear] = useState("");
  const [programId, setProgramId] = useState("");
  const [semester, setSemester] = useState("");
  const [rollNumber, setRollNumber] = useState("");
  const [showForm, setShowForm] = useState(false);

  useEffect(() => {
    fetchStudents();
  }, []);

  async function fetchStudents() {
    try {
      const response = await fetch("http://localhost:8081/api/students");
      const data = await response.json();
      setStudents(data);
    } catch (error) {
      console.error("Error fetching students:", error);
    }
  }

  async function addStudent(e) {
    e.preventDefault(); // prevent default form submit
    try {
      const response = await fetch(
        "http://localhost:8081/api/students/auth/register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            enrolledYear: enrollYear.trim(),
            programCode: programId.trim(),
            semester: semester.trim(),
            roll: rollNumber.trim(),
          }),
        }
      );

      if (!response.ok) {
        throw new Error("Failed to add student");
      }

      const newStudent = await response.json();
      setStudents((prev) => [...prev, newStudent]);

      // Reset form
      setEnrollYear("");
      setProgramId("");
      setSemester("");
      setRollNumber("");
      setShowForm(false); // hide form after adding
    } catch (error) {
      console.error("Error adding student:", error);
    }
  }

  return (
    <>
      <div className="mb-4">
        <h1 className="text-xl font-bold">Total Students: {students.length}</h1>
      </div>

      <div className="mb-4">
        <Button onClick={() => setShowForm((prev) => !prev)}>
          {showForm ? "Cancel" : "Add Student"}
        </Button>
      </div>

      {showForm && (
        <form onSubmit={addStudent} className="flex flex-col gap-2 max-w-sm">
          <label htmlFor="enrollYear">Enroll Year</label>
          <input
            type="text"
            name="enrollYear"
            required
            value={enrollYear}
            className="border p-1"
            onChange={(e) => setEnrollYear(e.target.value)}
          />

          <label htmlFor="programId">Program Code</label>
          <input
            type="number"
            name="programId"
            required
            value={programId}
            className="border p-1"
            onChange={(e) => setProgramId(e.target.value)}
          />

          <label htmlFor="semester">Semester</label>
          <input
            type="number"
            name="semester"
            required
            value={semester}
            className="border p-1"
            onChange={(e) => setSemester(e.target.value)}
          />

          <label htmlFor="rollNumber">Roll Number</label>
          <input
            type="number"
            name="rollNumber"
            required
            value={rollNumber}
            className="border p-1"
            onChange={(e) => setRollNumber(e.target.value)}
          />

          <Button type="submit">Submit Student</Button>
        </form>
      )}
    </>
  );
}
