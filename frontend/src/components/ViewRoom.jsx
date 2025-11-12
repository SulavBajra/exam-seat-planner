import React from "react";

export default function ViewRoom({ roomDetails }) {
  if (!roomDetails) return null;

  const { numRow, roomColumn, seatsPerBench, roomNo } = roomDetails;

  const totalCols = roomColumn * seatsPerBench;

  const roomRows = Array.from({ length: numRow }, () =>
    Array(totalCols).fill(null)
  );

  return (
    <div className="p-4">
      <h3 className="text-lg font-bold mb-4 text-center">Room {roomNo}</h3>
      <p className="text-sm text-gray-600 mb-4 text-left">Door →</p>
      <div className="flex flex-col gap-2">
        {roomRows.map((row, rowIndex) => (
          <div key={rowIndex} className="flex gap-2 justify-center">
            {row.map((seat, seatIndex) => (
              <React.Fragment key={seatIndex}>
                <div
                  className="w-10 h-10 border-2 border-gray-400 rounded flex items-center justify-center bg-gray-100 text-xs"
                  title={`Seat ${rowIndex + 1}-${seatIndex + 1}`}
                >
                  {/* Optional: Label seats like 1-1, 1-2 */}
                  {rowIndex + 1}-{seatIndex + 1}
                </div>

                {/* Gap between benches */}
                {(seatIndex + 1) % seatsPerBench === 0 &&
                  seatIndex !== row.length - 1 && (
                    <div className="w-4" />
                  )}
              </React.Fragment>
            ))}
          </div>
        ))}
      </div>
    </div>
  );
}
