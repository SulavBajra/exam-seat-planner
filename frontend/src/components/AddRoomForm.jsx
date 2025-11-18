import React, { useState, useMemo } from "react";
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
  const [numRows, setNumRows] = useState("");
  const [isAdvanced, setIsAdvanced] = useState(false);
  const [numColumns, setNumColumns] = useState("");
  const [seatsPerBench, setSeatsPerBench] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  // default values for non-advanced mode
  const defaultColumns = 3;
  const defaultSeatsPerBench = 2;

  // compute capacity dynamically
  const seatingCapacity = useMemo(() => {
    const cols = isAdvanced ? parseInt(numColumns) || 0 : defaultColumns;
    const seats = isAdvanced ? parseInt(seatsPerBench) || 0 : defaultSeatsPerBench;
    const rows = parseInt(numRows) || 0;
    return rows > 0 && cols > 0 && seats > 0 ? rows * cols * seats : 0;
  }, [numRows, numColumns, seatsPerBench, isAdvanced]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    const newRoom = {
      roomNo: parseInt(roomNo),
      numRow: parseInt(numRows),
      roomColumn: isAdvanced ? parseInt(numColumns) : defaultColumns,
      seatsPerBench: isAdvanced ? parseInt(seatsPerBench) : defaultSeatsPerBench,
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
            Enter the room details below. Seating capacity will be calculated automatically.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4 py-2">
          {/* Room Number */}
          <div className="space-y-2">
            <Label htmlFor="roomNo">Room Number *</Label>
            <Input
              id="roomNo"
              value={roomNo}
              onChange={(e) => setRoomNo(e.target.value)}
              placeholder="e.g., 101"
              required
              className="h-11"
            />
          </div>

          {/* Number of Rows */}
          <div className="space-y-2">
            <Label htmlFor="numRows">Number of Rows *</Label>
            <Input
              id="numRows"
              type="number"
              min="1"
              value={numRows}
              onChange={(e) => setNumRows(e.target.value)}
              placeholder="e.g., 5"
              required
              className="h-11"
            />
          </div>

          {/* Advanced toggle */}
          <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border">
            <div className="flex items-center gap-2">
              <Label htmlFor="advanced">Advanced Configuration</Label>
              <div className="relative group">
                <Info className="h-4 w-4 text-gray-400" />
                <div className="absolute hidden group-hover:block bottom-full left-1/2 transform -translate-x-1/2 mb-2 w-48 p-2 bg-gray-800 text-white text-xs rounded shadow-lg">
                  Customize columns and bench layout for specific rooms
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
                  <Label htmlFor="numColumns">Number of Columns</Label>
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
                  <Label htmlFor="seatsPerBench">Seats per Bench</Label>
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

          {/* Computed Seating Capacity */}
          <div className="space-y-2">
            <Label htmlFor="capacity">Calculated Seating Capacity</Label>
            <Input
              id="capacity"
              type="number"
              value={seatingCapacity}
              readOnly
              className="h-11 bg-gray-100"
            />
          </div>

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
              disabled={isSubmitting || !roomNo || !numRows}
              className="h-11 bg-black hover:bg-green-700"
            >
              {isSubmitting ? "Saving..." : "Save Room"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
