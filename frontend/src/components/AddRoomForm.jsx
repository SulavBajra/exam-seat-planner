import React, { useState } from "react";

export default function AddRoomForm({ onClose, onRoomAdded }) {
  const [roomNo, setRoomNo] = useState("");
  const [seatingCapacity, setSeatingCapacity] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const newRoom = {
      roomNo,
      seatingCapacity: parseInt(seatingCapacity),
      numRow: parseInt(seatingCapacity) / (3 * 2),
    };

    try {
      const response = await fetch(`http://localhost:8081/api/rooms`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newRoom),
      });

      if (!response.ok) {
        throw new Error("Failed to add room");
      }

      const savedRoom = await response.json();

      if (onRoomAdded) {
        onRoomAdded(savedRoom);
      }

      if (onClose) {
        onClose();
      }
    } catch (error) {
      console.error("Error adding room:", error);
    }
  };

  return (
    <div className="p-6 border rounded-xl shadow bg-white w-80">
      <h2 className="text-lg font-bold mb-4">Add New Room</h2>
      <form onSubmit={handleSubmit} className="flex flex-col gap-4">
        <input
          type="text"
          placeholder="Room Number"
          value={roomNo}
          onChange={(e) => setRoomNo(e.target.value)}
          className="border p-2 rounded"
          required
        />
        <input
          type="number"
          placeholder="Seating Capacity"
          value={seatingCapacity}
          onChange={(e) => setSeatingCapacity(e.target.value)}
          className="border p-2 rounded"
          required
        />
        <div className="flex gap-2">
          <button
            type="submit"
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition"
          >
            Add Room
          </button>
          <button
            type="button"
            onClick={onClose}
            className="bg-gray-300 px-4 py-2 rounded hover:bg-gray-400 transition"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
