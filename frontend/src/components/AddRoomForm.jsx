import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { DoorOpen, Info } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Switch } from "@/components/ui/switch";
import { Card, CardContent } from "@/components/ui/card";

export default function AddRoomForm({ onClose, onRoomAdded }) {
  const [roomNo, setRoomNo] = useState("");
  const [seatingCapacity, setSeatingCapacity] = useState("");
  const [isAdvanced, setIsAdvanced] = useState(false);
  const [numColumns, setNumColumns] = useState("");
  const [seatsPerBench, setSeatsPerBench] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    const newRoom = {
      roomNo: parseInt(roomNo),
      seatingCapacity: parseInt(seatingCapacity),
      numRow: isAdvanced
        ? Math.ceil(
            parseInt(seatingCapacity) /
              (parseInt(numColumns) * parseInt(seatsPerBench))
          )
        : Math.ceil(parseInt(seatingCapacity) / (3 * 2)),
      roomColumn: isAdvanced ? parseInt(numColumns) : 3,
      seatsPerBench: isAdvanced ? parseInt(seatsPerBench) : 2,
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
      <DialogContent className="sm:max-w-[500px] rounded-lg">
        <DialogHeader className="pb-2">
          <DialogTitle className="flex items-center gap-2 text-xl">
            <DoorOpen className="h-6 w-6 text-blue-600" />
            Add New Room
          </DialogTitle>
          <DialogDescription className="pt-2">
            Enter the room details below. The seating capacity should be
            divisible by the bench configuration for optimal layout.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4 py-2">
          {/* Room Number */}
          <div className="space-y-2">
            <Label htmlFor="roomNo" className="text-sm font-medium">
              Room Number *
            </Label>
            <Input
              id="roomNo"
              value={roomNo}
              onChange={(e) => setRoomNo(e.target.value)}
              placeholder="e.g., 101"
              required
              className="h-11"
            />
          </div>

          {/* Seating Capacity */}
          <div className="space-y-2">
            <Label htmlFor="capacity" className="text-sm font-medium">
              Seating Capacity *
            </Label>
            <Input
              id="capacity"
              type="number"
              min="1"
              value={seatingCapacity}
              onChange={(e) => setSeatingCapacity(e.target.value)}
              placeholder="e.g., 30"
              required
              className="h-11"
            />
          </div>

          {/* Advanced toggle */}
          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border">
            <div className="flex items-center gap-2">
              <Label
                htmlFor="advanced"
                className="text-sm font-medium cursor-pointer"
              >
                Advanced Configuration
              </Label>
              <div className="relative group">
                <Info className="h-4 w-4 text-gray-400" />
                <div className="absolute hidden group-hover:block bottom-full left-1/2 transform -translate-x-1/2 mb-2 w-48 p-2 bg-gray-800 text-white text-xs rounded shadow-lg">
                  Customize column and bench layout for specialized room
                  configurations
                </div>
              </div>
            </div>
            <Switch
              id="advanced"
              checked={isAdvanced}
              onCheckedChange={() => setIsAdvanced(!isAdvanced)}
            />
          </div>

          {isAdvanced && (
            <Card className="bg-blue-50 border-blue-200">
              <CardContent className="p-4 space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="numColumns" className="text-sm font-medium">
                    Number of Columns
                  </Label>
                  <Input
                    id="numColumns"
                    type="number"
                    min="1"
                    value={numColumns}
                    onChange={(e) => setNumColumns(e.target.value)}
                    placeholder="e.g., 5"
                    required={isAdvanced}
                    className="h-11"
                  />
                </div>

                <div className="space-y-2">
                  <Label
                    htmlFor="seatsPerBench"
                    className="text-sm font-medium"
                  >
                    Seats per Bench
                  </Label>
                  <Input
                    id="seatsPerBench"
                    type="number"
                    min="1"
                    value={seatsPerBench}
                    onChange={(e) => setSeatsPerBench(e.target.value)}
                    placeholder="e.g., 2"
                    required={isAdvanced}
                    className="h-11"
                  />
                </div>
              </CardContent>
            </Card>
          )}

          {error && (
            <div className="p-3 text-sm text-red-600 bg-red-50 rounded-md border border-red-200">
              {error}
            </div>
          )}

          <DialogFooter className="pt-4 gap-2 sm:gap-0">
            <Button
              variant="outline"
              onClick={onClose}
              disabled={isSubmitting}
              type="button"
              className="h-11"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={isSubmitting || !roomNo || !seatingCapacity}
              className="h-11 bg-blue-600 hover:bg-blue-700"
            >
              {isSubmitting ? "Saving..." : "Save Room"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
