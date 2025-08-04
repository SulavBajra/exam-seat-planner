import React, { useState } from "react";
import { Button } from "@/components/ui/button";

export default function RoomForm({ onSuccess }) {
  const [roomNumber, setRoomNumber] = useState("");
  const [numRow, setNumRow] = useState("");
  const [numColumn, setNumColumn] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8081/api/rooms/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          roomNo: roomNumber.trim(),
          numRow: parseInt(numRow),
          numColumn: parseInt(numColumn),
        }),
      });

      if (!response.ok) {
        const errData = await response.json();
        throw new Error(errData.message || "Failed to add room");
      }

      const newRoom = await response.json();
      console.log("New Room Added:", newRoom);

      onSuccess?.(newRoom);
      setRoomNumber("");
      setNumRow("");
      setNumColumn("");
    } catch (error) {
      setError(error.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-2 mt-2">
      <label htmlFor="roomNumber">Room Number</label>
      <input
        type="text"
        id="roomNumber"
        value={roomNumber}
        onChange={(e) => setRoomNumber(e.target.value)}
        className="border p-1"
        required
      />

      <label htmlFor="numRow">Number of Rows</label>
      <input
        type="number"
        id="numRow"
        value={numRow}
        onChange={(e) => setNumRow(e.target.value)}
        className="border p-1"
        required
        min={1}
      />

      <label htmlFor="numColumn">Number of Columns</label>
      <input
        type="number"
        id="numColumn"
        value={numColumn}
        onChange={(e) => setNumColumn(e.target.value)}
        className="border p-1"
        required
        min={1}
      />

      {error && <p className="text-red-500">{error}</p>}

      <Button
        type="submit"
        disabled={submitting}
        className="bg-indigo-600 text-white mt-2"
      >
        {submitting ? "Submitting..." : "Submit"}
      </Button>
    </form>
  );
}
