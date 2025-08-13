import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const SEATS_PER_BENCH = 2;
const BENCH_SIDES = ["Left", "Middle", "Right"];

export default function SeatingArrangement() {
  const { examId } = useParams();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [examData, setExamData] = useState(null);

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
    const { numRow } = room;
    const numCols = BENCH_SIDES.length * SEATS_PER_BENCH;
    const roomRows = Array.from({ length: numRow }, () =>
      Array(numCols).fill(null)
    );

    for (let col = 0; col < numCols; col++) {
      // iterate column first
      for (let row = 0; row < numRow; row++) {
        // then row
        // Pick a student whose program is NOT the same as left neighbor
        let chosenStudent = null;
        for (let program of sortedPrograms) {
          const queue = programQueues[program.programCode];
          if (queue.length === 0) continue;

          const leftNeighbor = col > 0 ? roomRows[row][col - 1] : null;
          if (
            !leftNeighbor ||
            leftNeighbor.programCode !== program.programCode
          ) {
            chosenStudent = queue.shift(); // remove from global queue
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
            {row.map((student, seatIndex) =>
              student ? (
                <div
                  key={seatIndex}
                  className="border p-2.5 w-25 h-10 text-center text-sm"
                >
                  {student.programCode}- {student.semester}- {student.roll}
                </div>
              ) : (
                <div
                  key={seatIndex}
                  className="border p-2.5 w-20 h-10 bg-gray-100"
                ></div>
              )
            )}
          </div>
        ))}
      </div>
    );
  };

  return (
    <div>
      <h1 className="text-2xl font-bold mb-4">Seating Arrangement</h1>
      {examData.rooms.map((room) => renderRoom(room))}
    </div>
  );
}
