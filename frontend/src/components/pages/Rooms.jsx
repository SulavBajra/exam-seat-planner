import React from "react";
import { Button } from "@/components/ui/button";
import { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import RoomForm from "@/components/ui/RoomForm";

export default function Rooms() {
  const [rooms, setRooms] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    fetchRooms();
  }, []);

  async function fetchRooms() {
    try {
      const response = await fetch("http://localhost:8081/api/rooms");
      const data = await response.json();
      setRooms(data);
    } catch (error) {
      console.error("Error fetching rooms:", error);
    }
  }

  const handleSuccess = (newRoom) => {
    setRooms((prev) => [...prev, newRoom]);
    setDialogOpen(false);
  };

  return (
    <>
      <div>Total Rooms: {rooms.length}</div>
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogTrigger
          className="shadow-xl border hover:bg-indigo-100 pr-2 pl-2"
          asChild
        >
          <Button variant="outline">Add Room</Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Adding New Room</DialogTitle>
            <DialogDescription>
              Fill in the details of the new room.
            </DialogDescription>
          </DialogHeader>
          <RoomForm onSuccess={handleSuccess} />
        </DialogContent>
      </Dialog>
      <div>
        <h2 className="text-lg font-semibold">Room List</h2>
        <ul className="list-disc pl-5">
          {rooms.map((room) => (
            <li key={room.id}>
              Room No: {room.roomNo}, Total Capacity: {room.seatingCapacity}
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
