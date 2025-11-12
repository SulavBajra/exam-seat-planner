import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";

export default function EditRoomForm({ roomDetails, onClose, onRoomUpdated }) {
  const [formData, setFormData] = useState({
    roomNo: roomDetails.roomNo,
    // seatingCapacity: roomDetails.seatingCapacity,
    numRow: roomDetails.numRow,
    seatsPerBench: roomDetails.seatsPerBench,
    roomColumn: roomDetails.roomColumn,
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value === "" ? "" : Number(value),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8081/api/rooms/${formData.roomNo}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        }
      );

      if (!response.ok) throw new Error("Failed to update room");

      const updatedRoom = await response.json();
      toast.success(`Room ${formData.roomNo} updated successfully`);
      onRoomUpdated(updatedRoom);
      onClose();
    } catch (error) {
      console.error(error);
      toast.error("Failed to update room");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 py-4">
      <div>
        <Label htmlFor="roomNo">Room Number</Label>
        <Input
          id="roomNo"
          name="roomNo"
          type="number"
          value={formData.roomNo}
          disabled
          className="bg-gray-100"
        />
      </div>

      {/* <div>
        <Label htmlFor="seatingCapacity">Seating Capacity</Label>
        <Input
          id="seatingCapacity"
          name="seatingCapacity"
          type="number"
          min="1"
          value={formData.seatingCapacity}
          onChange={handleChange}
          required
        />
      </div> */}

      <div className="grid grid-cols-2 gap-4">
        <div>
          <Label htmlFor="numRow">Number of Rows</Label>
          <Input
            id="numRow"
            name="numRow"
            type="number"
            min="1"
            value={formData.numRow}
            onChange={handleChange}
            required
          />
        </div>
        <div>
          <Label htmlFor="roomColumn">Number of Columns</Label>
          <Input
            id="roomColumn"
            name="roomColumn"
            type="number"
            min="1"
            value={formData.roomColumn}
            onChange={handleChange}
            required
          />
        </div>
      </div>

      <div>
        <Label htmlFor="seatsPerBench">Seats per Bench</Label>
        <Input
          id="seatsPerBench"
          name="seatsPerBench"
          type="number"
          min="1"
          value={formData.seatsPerBench}
          onChange={handleChange}
          required
        />
      </div>

      <div className="flex justify-end gap-3 pt-4">
        <Button variant="outline" type="button" onClick={onClose}>
          Cancel
        </Button>
        <Button type="submit" disabled={loading}>
          {loading ? "Updating..." : "Update Room"}
        </Button>
      </div>
    </form>
  );
}
