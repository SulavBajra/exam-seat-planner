import React, { useEffect, useState } from "react";
import AddRoomForm from "@/components/AddRoomForm";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";

import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { DoorOpen, Plus, Pencil } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { toast } from "sonner";

export default function Room() {
  const [rooms, setRooms] = useState([]);
  const [showAddRoom, setShowAddRoom] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [roomDetails, setRoomDetails] = useState(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [deleteConfirmation, setDeleteConfirmation] = useState({
    isOpen: false,
    roomNo: null,
    isBooked: false,
  });

  useEffect(() => {
    const fetchRooms = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch(`http://localhost:8081/api/rooms`);
        if (!response.ok) {
          throw new Error(`Failed to fetch rooms: ${response.status}`);
        }
        const data = await response.json();
        setRooms(data);
      } catch (error) {
        console.error("Error fetching rooms:", error);
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };
    fetchRooms();
  }, []);

  // Add room handlers
  const handleAddRoom = () => setShowAddRoom(true);
  const handleRoomAdded = (newRoom) => {
    setRooms((prev) => [...prev, newRoom]);
    setShowAddRoom(false);
  };

  const handleCloseDetails = () => {
    setSelectedRoom(null);
    setRoomDetails(null);
  };

  const fetchRoomDetails = async (roomNo) => {
    setDetailsLoading(true);
    try {
      const response = await fetch(`http://localhost:8081/api/rooms/${roomNo}`);
      if (!response.ok) throw new Error(`Failed to fetch room details`);
      const data = await response.json();
      setRoomDetails(data);
      setSelectedRoom(roomNo);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load room details");
    } finally {
      setDetailsLoading(false);
    }
  };

  const handleEditRoomClick = async (roomNo) => {
    try {
      const bookedRes = await fetch(
        `http://localhost:8081/api/rooms/booked/${roomNo}`
      );
      if (!bookedRes.ok) throw new Error("Room is booked");
      const isBooked = await bookedRes.json();

      if (isBooked) {
        toast.error(`Room ${roomNo} is booked and cannot be edited`);
        return;
      }

      const res = await fetch(`http://localhost:8081/api/rooms/${roomNo}`);
      if (!res.ok) throw new Error("Failed to fetch room details");
      const data = await res.json();
      setRoomDetails(data);
      setSelectedRoom(roomNo);
    } catch (error) {
      console.error(error);
      toast.error(error.message);
    }
  };

  const handleRemoveRoomClick = async (roomNo) => {
    try {
      const bookedRes = await fetch(
        `http://localhost:8081/api/rooms/booked/${roomNo}`
      );
      if (!bookedRes.ok) throw new Error("Room already booked");
      const isBooked = await bookedRes.json();

      setDeleteConfirmation({
        isOpen: true,
        roomNo,
        isBooked: isBooked,
      });
    } catch (error) {
      console.error("Error checking room status:", error);
      toast.error(error.message);
    }
  };

  const confirmDeleteRoom = async () => {
    const { roomNo } = deleteConfirmation;
    try {
      const response = await fetch(
        `http://localhost:8081/api/rooms/${roomNo}`,
        { method: "DELETE" }
      );
      if (!response.ok) throw new Error(`Failed to delete room`);
      setRooms((prev) => prev.filter((room) => room.roomNo !== roomNo));
      if (selectedRoom === roomNo) handleCloseDetails();
      toast.success(`Room ${roomNo} deleted successfully`);
    } catch (error) {
      console.error(error);
      toast.error(`Failed to delete Room ${roomNo}`);
    } finally {
      setDeleteConfirmation({ isOpen: false, roomNo: null, isBooked: false });
    }
  };

  const cancelDeleteRoom = () =>
    setDeleteConfirmation({ isOpen: false, roomNo: null, isBooked: false });

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Room Management</h1>
          <p className="text-muted-foreground">
            View and manage all available examination rooms
          </p>
        </div>
        <Button onClick={handleAddRoom} className="gap-2">
          <Plus className="h-4 w-4" />
          Add Room
        </Button>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 text-red-600 rounded-lg">{error}</div>
      )}

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(3)].map((_, i) => (
            <Card key={i}>
              <CardHeader>
                <Skeleton className="h-6 w-3/4" />
              </CardHeader>
              <CardContent>
                <Skeleton className="h-4 w-full" />
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {rooms.map((room) => (
            <Card
              key={room.roomNo}
              className="hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => fetchRoomDetails(room.roomNo)}
            >
              <CardHeader className="pb-3">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-blue-50 rounded-full">
                    <DoorOpen className="h-5 w-5 text-blue-600" />
                  </div>
                  <CardTitle>Room {room.roomNo}</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Capacity</span>
                  <Badge variant="secondary">{room.seatingCapacity} seats</Badge>
                </div>
              </CardContent>
              <CardFooter className="flex justify-between">
                <Button variant="outline" size="sm">
                  View Details
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  className="text-red-500 hover:bg-red-50"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemoveRoomClick(room.roomNo);
                  }}
                >
                  Remove
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleEditRoomClick(room.roomNo);
                  }}
                >
                  <Pencil className="h-4 w-4" />
                </Button>
              </CardFooter>
            </Card>
          ))}

          <Card
            className="border-dashed hover:border-primary cursor-pointer transition-colors"
            onClick={handleAddRoom}
          >
            <div className="h-full flex flex-col items-center justify-center p-6 text-center">
              <Plus className="h-8 w-8 text-muted-foreground mb-2" />
              <h3 className="font-medium">Add New Room</h3>
              <p className="text-sm text-muted-foreground mt-1">
                Click to create a new examination room
              </p>
            </div>
          </Card>
        </div>
      )}

      {/* Add Room Form */}
      {showAddRoom && (
        <AddRoomForm onClose={() => setShowAddRoom(false)} onRoomAdded={handleRoomAdded} />
      )}

          {/* Edit Room Modal */}
      <Dialog open={!!selectedRoom} onOpenChange={handleCloseDetails}>
        <DialogContent className="sm:max-w-[500px] rounded-lg">
          <DialogHeader className="pb-2">
            <DialogTitle className="flex items-center gap-2 text-xl">
              <DoorOpen className="h-6 w-6 text-blue-600" />
              Edit Room {selectedRoom}
            </DialogTitle>
            <DialogDescription className="pt-2">
              Update the room details below. Seating layout will auto-adjust.
            </DialogDescription>
          </DialogHeader>

          {detailsLoading ? (
            <div className="space-y-4 mt-4">
              <Skeleton className="h-6 w-3/4" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-1/2" />
            </div>
          ) : roomDetails ? (
            <form
              className="space-y-4 py-2"
              onSubmit={async (e) => {
                e.preventDefault();
                try {
                  const response = await fetch(
                    `http://localhost:8081/api/rooms/${selectedRoom}`,
                    {
                      method: "PUT",
                      headers: { "Content-Type": "application/json" },
                      body: JSON.stringify(roomDetails),
                    }
                  );
                  if (!response.ok) throw new Error("Failed to update room");
                  const updated = await response.json();
                  setRooms((prev) =>
                    prev.map((room) =>
                      room.roomNo === updated.roomNo ? updated : room
                    )
                  );
                  toast.success(`Room ${updated.roomNo} updated`);
                  handleCloseDetails();
                } catch (err) {
                  toast.error(err.message);
                }
              }}
            >
              {/* Seating Capacity */}
              <div className="space-y-2">
                <Label htmlFor="seatingCapacity" className="text-sm font-medium">
                  Seating Capacity *
                </Label>
                <Input
                  id="seatingCapacity"
                  type="number"
                  min="1"
                  value={roomDetails.seatingCapacity || ""}
                  onChange={(e) =>
                    setRoomDetails({
                      ...roomDetails,
                      seatingCapacity: parseInt(e.target.value) || 1,
                    })
                  }
                  required
                  className="h-11"
                />
              </div>

              {/* Advanced Toggle */}
              <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg border">
                <Label htmlFor="advanced" className="text-sm font-medium cursor-pointer">
                  Advanced Configuration
                </Label>
                <Switch
                  id="advanced"
                  checked={roomDetails.isAdvanced || false}
                  onCheckedChange={(val) =>
                    setRoomDetails({ ...roomDetails, isAdvanced: val })
                  }
                />
              </div>

              {/* Advanced Fields */}
              {roomDetails.isAdvanced && (
                <Card className="bg-blue-50 border-blue-200">
                  <CardContent className="p-4 space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="roomColumn" className="text-sm font-medium">
                        Number of Columns
                      </Label>
                      <Input
                        id="roomColumn"
                        type="number"
                        min="1"
                        value={roomDetails.roomColumn || ""}
                        onChange={(e) =>
                          setRoomDetails({
                            ...roomDetails,
                            roomColumn: parseInt(e.target.value) || 1,
                          })
                        }
                        required
                        className="h-11"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="seatsPerBench" className="text-sm font-medium">
                        Seats per Bench
                      </Label>
                      <Input
                        id="seatsPerBench"
                        type="number"
                        min="1"
                        value={roomDetails.seatsPerBench || ""}
                        onChange={(e) =>
                          setRoomDetails({
                            ...roomDetails,
                            seatsPerBench: parseInt(e.target.value) || 1,
                          })
                        }
                        required
                        className="h-11"
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="numRows" className="text-sm font-medium">
                        Number of Rows (auto-calculated)
                      </Label>
                      <Input
                        id="numRows"
                        type="number"
                        min="1"
                        value={Math.ceil(
                          (parseInt(roomDetails.seatingCapacity) || 1) /
                            ((parseInt(roomDetails.roomColumn) || 1) *
                              (parseInt(roomDetails.seatsPerBench) || 1))
                        )}
                        className="h-11"
                        readOnly
                      />
                    </div>
                  </CardContent>
                </Card>
              )}

              {/* Footer */}
              <DialogFooter className="pt-4 gap-2 sm:gap-0">
                <Button
                  variant="outline"
                  type="button"
                  onClick={handleCloseDetails}
                  className="h-11"
                >
                  Cancel
                </Button>
                <Button type="submit" className="h-11 bg-blue-600 hover:bg-blue-700">
                  Save Room
                </Button>
              </DialogFooter>
            </form>
          ) : (
            <div className="text-center py-4 text-gray-500">No details available</div>
          )}
        </DialogContent>
      </Dialog>

      <Dialog
        open={deleteConfirmation.isOpen}
        onOpenChange={(open) => {
          if (!open) cancelDeleteRoom();
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm Deletion</DialogTitle>
            <DialogDescription>
              {deleteConfirmation.isBooked ? (
                <div className="text-red-600 font-medium py-2">
                  Cannot remove Room {deleteConfirmation.roomNo} because it is
                  assigned to an exam.
                </div>
              ) : (
                <div>
                  Are you sure you want to remove Room {deleteConfirmation.roomNo}? This
                  action cannot be undone.
                </div>
              )}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            {deleteConfirmation.isBooked ? (
              <Button onClick={cancelDeleteRoom}>OK</Button>
            ) : (
              <>
                <Button variant="outline" onClick={cancelDeleteRoom}>
                  Cancel
                </Button>
                <Button variant="destructive" onClick={confirmDeleteRoom}>
                  Delete
                </Button>
              </>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
