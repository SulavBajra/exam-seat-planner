import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DoorOpen } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";

export default function AddRoomForm({ onClose, onRoomAdded }) {
  const [roomNo, setRoomNo] = useState("");
  const [seatingCapacity, setSeatingCapacity] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    const newRoom = {
      roomNo,
      seatingCapacity: parseInt(seatingCapacity),
      numRow: Math.ceil(parseInt(seatingCapacity) / (3 * 2)), // More accurate row calculation
    };

    try {
      const response = await fetch(`http://localhost:8081/api/rooms`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newRoom),
      });

      if (!response.ok) {
        throw new Error(
          response.status === 409
            ? "Room number already exists"
            : "Failed to add room"
        );
      }

      const savedRoom = await response.json();
      onRoomAdded(savedRoom);
      onClose();
    } catch (error) {
      console.error("Error adding room:", error);
      setError(error.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <DoorOpen className="h-5 w-5" />
            Add New Room
          </DialogTitle>
          <DialogDescription>
            Enter the room details below. Click save when you're done.`` The
            capacity should be divisible by three
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="grid gap-4 py-4">
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="roomNo" className="">
              Room Number
            </Label>
            <Input
              id="roomNo"
              value={roomNo}
              onChange={(e) => setRoomNo(e.target.value)}
              className="col-span-3"
              placeholder="e.g., 101"
              required
            />
          </div>
          <div className="grid grid-cols-4 items-center gap-4">
            <Label htmlFor="capacity" className="text-right">
              Capacity
            </Label>
            <Input
              id="capacity"
              type="number"
              min="1"
              value={seatingCapacity}
              onChange={(e) => setSeatingCapacity(e.target.value)}
              className="col-span-3"
              placeholder="e.g., 30"
              required
            />
          </div>

          {error && (
            <div className="col-span-4 text-sm text-red-600">{error}</div>
          )}
        </form>

        <DialogFooter>
          <Button variant="outline" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button type="submit" onClick={handleSubmit} disabled={isSubmitting}>
            {isSubmitting ? "Saving..." : "Save Room"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
