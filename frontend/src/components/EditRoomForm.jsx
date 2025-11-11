import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { toast } from "sonner";

export default function EditRoomForm({ roomData, onClose, onRoomUpdated }) {
  const roomNo = roomData.roomNo;
  const [seatingCapacity, setSeatingCapacity] = useState(roomData.seatingCapacity);
  const [numRow, setNumRow] = useState(roomData.numRow);
  const [roomColumn, setRoomColumn] = useState(roomData.roomColumn);
  const [seatsPerBench, setSeatsPerBench] = useState(roomData.seatsPerBench);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`http://localhost:8081/api/rooms/${roomData.roomNo}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ roomNo, seatingCapacity, numRow, roomColumn, seatsPerBench }),
      });
      if (!res.ok) throw new Error(`Failed to update room: ${res.status}`);
      const updatedRoom = await res.json();
      toast.success(`Room ${roomNo} updated successfully`);
      onRoomUpdated(updatedRoom);
      onClose();
    } catch (err) {
      console.error(err);
      toast.error(err.message);
    }
  };

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Edit Room {roomNo}</DialogTitle>
        </DialogHeader>
        <form className="space-y-3 mt-4" onSubmit={handleSubmit}>
          <input type="number" value={seatingCapacity} onChange={(e) => setSeatingCapacity(e.target.value)} placeholder="Seating Capacity" className="w-full p-2 border rounded-md" />
          <input type="number" value={numRow} onChange={(e) => setNumRow(e.target.value)} placeholder="Number of Rows" className="w-full p-2 border rounded-md" />
          <input type="number" value={roomColumn} onChange={(e) => setRoomColumn(e.target.value)} placeholder="Room Column" className="w-full p-2 border rounded-md" />
          <input type="number" value={seatsPerBench} onChange={(e) => setSeatsPerBench(e.target.value)} placeholder="Seats per Bench" className="w-full p-2 border rounded-md" />
          <DialogFooter>
            <Button variant="outline" onClick={onClose}>Cancel</Button>
            <Button type="submit">Update</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
