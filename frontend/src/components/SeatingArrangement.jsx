import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Label } from "@/components/ui/label";

export default function SeatingArrangement() {
  const { examId } = useParams();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [examData, setExamData] = useState(null);
  const [programData, setProgramData] = useState([]);

  useEffect(() => {
    if (!examId) return;

    const fetchExamData = async () => {
      try {
        setLoading(true);
        const response = await fetch(
          `http://localhost:8081/api/exam-data/${examId}/data`
        );
        if (!response.ok)
          throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        setExamData(data);
      } catch (err) {
        console.error(err);
        setError(err.message || "Failed to fetch exam data");
      } finally {
        setLoading(false);
      }
    };

    fetchExamData();
  }, [examId]);

  useEffect(() => {
    if (!examId) return;

    const fetchProgram = async () => {
      try {
        setLoading(true);
        const response = await fetch(
          `http://localhost:8081/api/exams/programNames/${examId}`
        );
        if (!response.ok)
          throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        setProgramData(data);
      } catch (err) {
        console.error(err);
        setError(err.message || "Failed to fetch exam data");
      } finally {
        setLoading(false);
      }
    };

    fetchProgram();
  }, [examId]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!examData) return null;

  // Sort programs by ascending programCode
  const sortedPrograms = [...examData.programs].sort(
    (a, b) => a.programCode - b.programCode
  );

  // Group students by program with pointers
  const studentsByProgram = {};
  sortedPrograms.forEach((program) => {
    studentsByProgram[program.programCode] = {
      list: [
        ...examData.students.filter(
          (s) => s.programCode === program.programCode
        ),
      ],
      pointer: 0,
    };
  });

  // Create a global queue of students per program BEFORE rendering any room
  const programQueues = {};
  sortedPrograms.forEach((program) => {
    programQueues[program.programCode] = [
      ...studentsByProgram[program.programCode].list,
    ];
  });

  const renderRoom = (room) => {
    const { numRow, roomColumn, seatsPerBench } = room;
    const numCols = roomColumn * seatsPerBench;

    const roomRows = Array.from({ length: numRow }, () =>
      Array(numCols).fill(null)
    );

    for (let col = 0; col < numCols; col++) {
      for (let row = 0; row < numRow; row++) {
        let chosenStudent = null;

        for (let program of sortedPrograms) {
          const queue = programQueues[program.programCode];
          if (queue.length === 0) continue;

          const leftNeighbor = col > 0 ? roomRows[row][col - 1] : null;
          if (
            !leftNeighbor ||
            leftNeighbor.programCode !== program.programCode
          ) {
            chosenStudent = queue.shift();
            break;
          }
        }

        roomRows[row][col] = chosenStudent || null;
      }
    }

    return (
      <div key={room.roomNo} className="border p-4 mb-4">
        <h2 className="font-bold mb-2">Room {room.roomNo}</h2>
        {roomRows.map((row, rowIndex) => (
          <div key={rowIndex} className="flex gap-2 mb-1">
            {row.reduce((acc, student, idx) => {
              acc.push(
                <div
                  key={idx}
                  className={`border-2 border-blue-600 p-2.5 w-28 h-10 text-center text-sm ${
                    student ? "bg-white" : "bg-gray-100"
                  }`}
                >
                  {student
                    ? `${student.programCode}-${student.semester}-${student.roll}`
                    : ""}
                </div>
              );

              // Add a visible gap after each bench
              if ((idx + 1) % seatsPerBench === 0 && idx !== row.length - 1) {
                acc.push(
                  <div
                    key={`gap-${idx}`}
                    className="w-4 border-r-2 border-transparent"
                  ></div>
                );
              }

              return acc;
            }, [])}
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className="flex flex-col md:flex-row gap-6">
      <div className="flex-1">
        <h1 className="text-2xl font-bold mb-4">Seating Arrangement</h1>
        <div className="inline-flex items-center px-3 py-1.5 rounded-md bg-gray-100 text-gray-700 text-sm font-medium mb-4">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-4 w-4 mr-2"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
            />
          </svg>
          <span>
            Format:{" "}
            <span className="font-semibold text-blue-600">Program Code</span> -{" "}
            <span className="font-semibold text-blue-600">Semester</span> -{" "}
            <span className="font-semibold text-blue-600">Roll Number</span>
          </span>
        </div>
        {examData.rooms.map((room) => renderRoom(room))}
      </div>

      {/* Legend Section */}
      <div className="md:w-64 lg:w-80">
        <div className="sticky top-4 bg-white p-4 rounded-lg shadow-md border border-gray-200">
          <h3 className="text-lg font-semibold mb-3 pb-2 border-b border-gray-200">
            Program Legend
          </h3>
          <div className="space-y-2">
            {programData.map((program) => (
              <div
                key={program.programCode}
                className="flex items-start py-1.5"
              >
                <span className="inline-block w-4 h-4 mt-1 mr-2 rounded-sm flex-shrink-0"></span>
                <div className="text-sm">
                  <span className="font-medium">{program.programCode}</span>
                  <p className="text-gray-600">{program.programName}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
