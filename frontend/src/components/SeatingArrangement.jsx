import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import html2canvas from "html2canvas-oklch";
import jsPDF from "jspdf";

export default function SeatingArrangement() {
  const { examId } = useParams();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [examData, setExamData] = useState(null);
  const [programData, setProgramData] = useState([]);
  const fullRef = useRef(null); // 👈 Capture entire layout

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

  const handleDownload = async () => {
  const element = fullRef.current;
  if (!element) {
    console.error("PDF ref not found");
    return;
  }

  try {
    const canvas = await html2canvas(element, {
      scale: 2,
      useCORS: true,
      backgroundColor: "#ffffff",
      logging: false,
    });

    const imgData = canvas.toDataURL("image/png");
    const pdf = new jsPDF("p", "mm", "a4");

    const pdfWidth = pdf.internal.pageSize.getWidth();
    const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
    let heightLeft = pdfHeight;
    let position = 0;

    // Add first page
    pdf.addImage(imgData, "PNG", 0, position, pdfWidth, pdfHeight);
    heightLeft -= pdf.internal.pageSize.getHeight();

    // Add more pages if needed
    while (heightLeft > 0) {
      position = heightLeft - pdfHeight;
      pdf.addPage();
      pdf.addImage(imgData, "PNG", 0, position, pdfWidth, pdfHeight);
      heightLeft -= pdf.internal.pageSize.getHeight();
    }

    pdf.save(`seating-plan-${examId}.pdf`);
  } catch (error) {
    console.error("PDF generation failed:", error);
  }
};
1

  if (loading) return <div>Loading...</div>;
  if (error) return <div className="text-red-500">{error}</div>;
  if (!examData) return null;

  // Sort programs
  const sortedPrograms = [...examData.programs].sort(
    (a, b) => a.programCode - b.programCode
  );

  // Prepare seat assignments
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

  const programQueues = {};
  sortedPrograms.forEach((program) => {
    programQueues[program.programCode] = [
      ...studentsByProgram[program.programCode].list,
    ];
  });

  // Render each room grid
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
      <div
        key={room.roomNo}
        className="border p-4 mb-4 rounded-lg shadow-sm break-inside-avoid"
      >
        <h2 className="font-bold mb-2 text-lg">Room {room.roomNo}</h2>
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
    <div>
      {/* Header & Download */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">Seating Arrangement</h1>
        <button
          onClick={handleDownload}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Download PDF
        </button>
      </div>

      {/* Everything captured in this ref */}
      <div
        ref={fullRef}
        className="flex flex-col md:flex-row gap-6 bg-white p-4 rounded-md"
      >
        {/* Seating Section */}
        <div className="flex-1">
          {examData.rooms.map((room) => renderRoom(room))}
        </div>

        {/* Legend Section */}
        <div className="md:w-64 lg:w-100">
          <div className="bg-white p-4 rounded-lg shadow-md border border-gray-200">
         <div className="inline-flex items-center px-3 py-1.5 rounded-md"
              style={{ backgroundColor: "#f3f4f6", color: "#1e3a8a", fontSize: "14px" }}>
            Format: ProgramCode - Semester - Roll
          </div>
            <h3 className="text-lg font-semibold mb-3 pb-2 border-b border-gray-200">
              Program Legend
            </h3>
            <div className="space-y-2">
              {programData.map((program) => (
                <div
                  key={program.programCode}
                  className="flex items-start py-1.5"
                >
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
    </div>
  );
}
