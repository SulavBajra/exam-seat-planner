import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Skeleton } from "@/components/ui/skeleton";

export default function SeatGrid() {
  const { examId } = useParams();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [seatingChart, setSeatingChart] = useState([]);
  const [programs, setPrograms] = useState([]);
  const [examInfo, setExamInfo] = useState(null);

  useEffect(() => {
    if (!examId) return;

    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch exam seating data
        const seatRes = await fetch(
          `http://localhost:8081/api/seat-allocation/exam/${examId}/seating-chart`
        );
        if (!seatRes.ok)
          throw new Error(`Failed to fetch seating chart: ${seatRes.status}`);
        const seatData = await seatRes.json();

        // Fetch exam programs
        const progRes = await fetch(
          `http://localhost:8081/api/exams/programNames/${examId}`
        );
        if (!progRes.ok)
          throw new Error(`Failed to fetch programs: ${progRes.status}`);
        const progData = await progRes.json();

        setPrograms(
          progData.map((p, idx) => ({
            ...p,
            short: `P${idx + 1}`,
            color: `hsl(${(idx * 60) % 360}, 70%, 80%)`,
          }))
        );

        // Process seating data
        const processed = Object.keys(seatData).map((roomKey) => {
          const roomInfo = parseRoomInfo(roomKey);
          const seating = seatData[roomKey];
          return { roomInfo, seating };
        });

        setSeatingChart(processed);

        // Set exam info from first room (or use API if available)
        setExamInfo({
          id: examId,
          date: processed[0]?.roomInfo?.examDate || "",
        });
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [examId]);

  const parseRoomInfo = (key) => {
    const inside = key.replace(/^Room\{/, "").replace(/\}$/, "");
    const parts = inside.split(", ");
    const info = {};
    parts.forEach((part) => {
      const [k, v] = part.split("=");
      const numVal = Number(v);
      info[k] = isNaN(numVal) ? v : numVal;
    });
    return info;
  };

  const getProgramShort = (code) => {
    code = code?.toString().trim();
    return (
      programs.find((p) => p.programCode.toString() === code)?.short || "—"
    );
  };

  if (loading) return <LoadingSkeleton />;
  if (error) return <ErrorDisplay message={error} />;

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-2xl font-bold mb-4">
        Seating Arrangement {examInfo?.date ? `- ${examInfo.date}` : ""}
      </h1>

      {/* Program Legend */}
      <div className="flex flex-wrap gap-4 mb-6">
        {programs.map((p) => (
          <div
            key={p.programCode}
            className="border rounded px-3 py-1 text-sm font-medium"
            style={{ backgroundColor: p.color }}
          >
            {p.short}: {p.programName}
          </div>
        ))}
      </div>

      {/* Seating Charts */}
      <div className="space-y-8">
        {seatingChart.map(({ roomInfo, seating }) => (
          <div
            key={roomInfo.roomNo}
            className="border-2 border-gray-300 rounded-lg p-4 bg-gray-50 shadow-md"
          >
            <h2 className="text-lg font-bold mb-4 text-center">
              Room {roomInfo.roomNo} (Capacity: {roomInfo.seatingCapacity})
            </h2>
            <div className="flex flex-col gap-4">
              {seating.map((sideRows, sideIdx) => (
                <div key={sideIdx} className="space-y-2">
                  {sideRows.map((row, rowIdx) => (
                    <SeatRow
                      key={rowIdx}
                      row={row}
                      seatsPerBench={roomInfo.seatsPerBench}
                      programs={programs}
                      getProgramShort={getProgramShort}
                    />
                  ))}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

const SeatRow = ({ row, seatsPerBench, programs, getProgramShort }) => {
  const benches = [];
  for (let i = 0; i < row.length; i += seatsPerBench) {
    benches.push(row.slice(i, i + seatsPerBench));
  }

  return (
    <div className="flex gap-4 justify-center flex-wrap">
      {benches.map((bench, benchIdx) => (
        <div
          key={benchIdx}
          className="flex border border-green-400 rounded-sm overflow-hidden"
        >
          {bench.map((seat, seatIdx) => {
            const code = seat?.assignedStudent?.program?.programCode
              ?.toString()
              ?.trim();
            const shortCode = code ? getProgramShort(code) : "—";
            const program = programs.find(
              (p) => p.programCode.toString() === code
            );

            return (
              <div
                key={seatIdx}
                className="w-12 h-12 flex items-center justify-center text-sm font-medium"
                style={{
                  backgroundColor: program ? program.color : "#f3f4f6",
                  borderRight:
                    seatIdx < bench.length - 1 ? "1px solid #ccc" : "",
                }}
                title={
                  seat
                    ? `${seat.assignedStudent?.program?.programName} - Roll: ${seat.assignedStudent?.roll}`
                    : "Empty Seat"
                }
              >
                {shortCode}
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );
};

const LoadingSkeleton = () => (
  <div className="p-4 space-y-4">
    <Skeleton className="h-8 w-[300px]" />
    <div className="grid grid-cols-3 gap-4">
      {[...Array(6)].map((_, i) => (
        <Skeleton key={i} className="h-12 rounded-lg" />
      ))}
    </div>
  </div>
);

const ErrorDisplay = ({ message }) => (
  <div className="p-4 bg-red-50 border border-red-200 rounded text-red-600">
    Error: {message}
  </div>
);
