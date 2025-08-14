import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Skeleton } from "@/components/ui/skeleton";

export default function SeatGrid() {
  const { examId } = useParams();
  const [seatingChart, setSeatingChart] = useState([]);
  const [programs, setPrograms] = useState([]);
  const [roomInfo, setRoomInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [allowedRooms, setAllowedRooms] = useState([]);

  useEffect(() => {
    if (!examId) {
      setError("Exam ID is required");
      setLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch seating chart data
        const response = await fetch(
          `http://localhost:8081/api/seat-allocation/exam/${examId}/seating-chart`
        );
        if (!response.ok)
          throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();

        // Fetch allowed rooms for the exam
        const examResponse = await fetch(
          `http://localhost:8081/api/exams/rooms/${examId}`
        );
        if (!examResponse.ok)
          throw new Error(`HTTP error! status: ${examResponse.status}`);
        const roomData = await examResponse.json();

        // Convert allowed rooms to numbers if needed (depends on your backend response)
        const allowedRoomNumbers = roomData.map((r) =>
          typeof r === "string" ? Number(r) : r
        );
        setAllowedRooms(allowedRoomNumbers);

        // Process seating data with allowed rooms list
        processSeatingData(data, allowedRoomNumbers);

        // Set roomInfo to first filtered room's info if exists
        const firstRoomKey = Object.keys(data).find((key) => {
          const match = key.match(/roomNo=(\d+)/);
          if (!match) return false;
          return allowedRoomNumbers.includes(Number(match[1]));
        });

        if (firstRoomKey) {
          const firstRoomInfo = parseRoomInfo(firstRoomKey);
          setRoomInfo(firstRoomInfo);
        } else {
          setRoomInfo(null);
        }
      } catch (err) {
        console.error("Fetch error:", err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [examId]);

  useEffect(() => {
    console.log("Seating chart:", seatingChart);
  }, [seatingChart]);

  function parseRoomInfo(key) {
    // Remove "Room{" and "}" around it
    const inside = key.replace(/^Room\{/, "").replace(/\}$/, "");

    // Split by comma and parse each key=value
    const parts = inside.split(", ");
    const info = {};

    parts.forEach((part) => {
      const [k, v] = part.split("=");
      // parse numbers if possible
      const numVal = Number(v);
      info[k] = isNaN(numVal) ? v : numVal;
    });

    return info;
  }

  const processSeatingData = (data, allowedRooms) => {
    const filteredRooms = Object.keys(data)
      .filter((roomKey) => {
        const match = roomKey.match(/roomNo=(\d+)/);
        if (!match) return false;
        const roomNo = Number(match[1]);
        return allowedRooms.includes(roomNo);
      })
      .map((roomKey) => ({
        roomKey,
        roomNo: Number(roomKey.match(/roomNo=(\d+)/)[1]),
        seating: data[roomKey],
        roomInfo: parseRoomInfo(roomKey), // <-- Use parseRoomInfo here
      }));

    // Build program map from seating data
    const programMap = new Map();
    filteredRooms.forEach(({ seating }) => {
      seating.forEach((side) => {
        side?.forEach((row) => {
          row?.forEach((seat) => {
            if (seat?.assignedStudent?.program) {
              const { programCode, programName } = seat.assignedStudent.program;
              programMap.set(programCode.trim(), programName); // trim here
            }
          });
        });
      });
    });

    setPrograms(
      Array.from(programMap.entries()).map(([code, name], idx) => ({
        code,
        name,
        short: `P${idx + 1}`,
        color: `hsl(${(idx * 60) % 360}, 70%, 80%)`,
      }))
    );

    setSeatingChart(filteredRooms);
  };

  const getProgramShort = (programCode) => {
    const code = programCode?.trim();
    return programs.find((p) => p.code === code)?.short || "—";
  };

  if (loading) return <LoadingSkeleton />;
  if (error) return <ErrorDisplay message={error} />;
  if (!roomInfo)
    return <div className="p-4">No room information available</div>;

  return (
    <div className="border p-4 max-w-6xl mx-auto">
      <Header roomInfo={roomInfo} />
      <ProgramLegend programs={programs} />
      <SeatingChartDisplay
        seatingChart={seatingChart}
        getProgramShort={getProgramShort}
        programs={programs}
      />
    </div>
  );
}

const LoadingSkeleton = () => (
  <div className="p-4 space-y-4">
    <Skeleton className="h-8 w-[300px]" />
    <div className="flex space-x-4">
      {[...Array(3)].map((_, i) => (
        <Skeleton key={i} className="h-4 w-[100px]" />
      ))}
    </div>
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

const Header = ({ roomInfo }) => (
  <h2 className="text-lg font-semibold mb-4">
    {/* Seating Chart for Room {roomInfo.roomNo} */}
    <span className="text-sm font-normal ml-2">
      {/* (Capacity: {roomInfo.seatingCapacity}) */}
    </span>
  </h2>
);
const ProgramLegend = ({ programs }) =>
  programs.length > 0 && (
    <div className="flex justify-center gap-4 mb-6 flex-wrap">
      {programs.map((p) => (
        <div
          key={p.code}
          className="border border-black px-3 py-1 rounded"
          style={{ backgroundColor: p.color }}
        >
          {p.code}: {p.name}
        </div>
      ))}
    </div>
  );

const SeatingChartDisplay = ({ seatingChart, getProgramShort, programs }) => (
  <div className="space-y-8">
    {seatingChart.map(({ roomInfo, seating }, idx) => (
      <div
        key={roomInfo.roomNo}
        className="border-2 border-gray-400 rounded-lg p-4 bg-gray-50 shadow-md"
      >
        <h2 className="text-lg font-bold mb-4 text-center">
          Room {roomInfo.roomNo}
        </h2>
        <div className="flex flex-col gap-4">
          {seating
            .flat()
            .filter((row) => row.some((seat) => seat !== null))
            .map((row, rowIdx) => (
              <SeatRow
                key={`room-${roomInfo.roomNo}-row-${rowIdx}`}
                row={row}
                getProgramShort={getProgramShort}
                programs={programs} // ✅ Will now have correct value
              />
            ))}
        </div>
      </div>
    ))}
  </div>
);
const SeatRow = ({ row, getProgramShort, programs }) => {
  const benches = [];
  for (let i = 0; i < row.length; i += 2) benches.push(row.slice(i, i + 2));

  return (
    <div className="flex flex-wrap gap-8">
      {benches.map((bench, benchIdx) => (
        <div
          key={benchIdx}
          className="border border-green-500 rounded-sm flex w-28 sm:w-32 h-12 items-center justify-around bg-white"
        >
          {bench.map((seat, seatIdx) => {
            const code = seat?.assignedStudent?.program?.programCode?.trim();
            const shortCode = code ? getProgramShort(code) : "—";
            const program = programs.find((p) => p.code === code);
            // Debug
            if (seat) {
              console.log(
                "Seat program code:",
                `"${seat.assignedStudent?.program?.programCode}"`,
                "Trimmed code:",
                `"${code}"`,
                "Programs array codes:",
                programs.map((p) => `"${p.code}"`)
              );
            }

            return (
              <div
                key={seatIdx}
                className="flex-1 flex items-center justify-center text-xs sm:text-sm font-medium rounded-sm"
                style={{ backgroundColor: program ? program.color : "#f3f4f6" }}
                title={
                  seat
                    ? `${
                        seat.assignedStudent?.program?.programName ||
                        "No program"
                      } (ID: ${seat.assignedStudent?.studentId || "N/A"})`
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

const Desk = ({ seats, getProgramShort }) => (
  <div className="border border-green-500 rounded-md px-4 py-2 flex gap-6 justify-center min-w-[120px]">
    {seats?.map((seat, idx) => (
      <div
        key={idx}
        className="text-sm flex items-center justify-center w-10"
        title={
          seat
            ? `${seat.assignedStudent?.program?.programName || "No program"} 
               (ID: ${seat.assignedStudent?.studentId || "N/A"})`
            : "Empty Seat"
        }
      >
        {seat
          ? getProgramShort(seat.assignedStudent?.program?.programCode)
          : "—"}
      </div>
    ))}
  </div>
);

const Seat = ({ seat, getProgramShort }) => (
  <div
    className={`border rounded p-2 w-12 h-12 flex items-center justify-center text-sm 
      ${seat ? "bg-white" : "bg-gray-100"}`}
    title={
      seat
        ? `${seat.assignedStudent?.program?.programName || "No program"} 
           (ID: ${seat.assignedStudent?.studentId || "N/A"})`
        : "Empty Seat"
    }
  >
    {seat ? getProgramShort(seat.assignedStudent?.program?.programCode) : "—"}
  </div>
);
