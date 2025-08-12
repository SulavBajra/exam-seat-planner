import React, { useEffect, useState } from "react";
import AddRoomForm from "@/components/AddRoomForm";

export default function Room() {
  const [rooms, setRoom] = useState([]);
  const [showAddRoom, setShowAddRoom] = useState(false);

  useEffect(() => {
    const fetchRooms = async () => {
      try {
        const response = await fetch(`http://localhost:8081/api/rooms`);
        const data = await response.json();
        setRoom(data);
      } catch (error) {
        console.error("Error fetching rooms:", error);
      }
    };

    fetchRooms();
  }, []);

  const handleAddRoom = () => {
    setShowAddRoom(true);
  };

  return (
    <div className="flex flex-col gap-6">
      {/* Room Cards */}
      <div className="flex flex-row gap-4 flex-wrap">
        {rooms.map((room) => (
          <div
            key={room.roomNo}
            className="p-5 border rounded-2xl shadow hover:shadow-lg transition"
          >
            <div className="flex flex-col gap-4">
              <div className="font-semibold">Room Number: {room.roomNo}</div>
              <div>Seating Capacity: {room.seatingCapacity}</div>
            </div>
          </div>
        ))}

        <button
          onClick={handleAddRoom}
          className="p-5 border rounded-2xl flex flex-col items-center justify-center text-blue-600 border-blue-400 hover:bg-blue-50 transition cursor-pointer"
        >
          <span className="text-2xl font-bold">+</span>
          <span>Add Room</span>
        </button>
      </div>

      {showAddRoom && (
        <div className="mt-4">
          <AddRoomForm
            onClose={() => setShowAddRoom(false)}
            onRoomAdded={(newRoom) => setRoom((prev) => [...prev, newRoom])}
          />
        </div>
      )}
    </div>
  );
}
