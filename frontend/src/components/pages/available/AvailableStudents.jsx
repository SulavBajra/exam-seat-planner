import React from "react";
import { useEffect, useState } from "react";

export default function AvailableStudents() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStudents();
  }, []);

  const fetchStudents = async () => {
    try {
      const response = await fetch("http://localhost:8081/api/students");
      const data = await response.json();
      setStudents(data);
    } catch (error) {
      console.error("Error fetching students:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div>
        {students.map((student) => (
          <div key={student.studentId} className="p-2 border-b">
            {student.semester} - {student.programName}
          </div>
        ))}
      </div>
    </>
  );
}
