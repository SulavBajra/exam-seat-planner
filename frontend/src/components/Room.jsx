import React, { useEffect, useState } from "react";
import AddRoomForm from "@/components/AddRoomForm";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { DoorOpen, Plus, X } from "lucide-react";
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

  const handleAddRoom = () => {
    setShowAddRoom(true);
  };

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
      if (!response.ok) {
        throw new Error(`Failed to fetch room details: ${response.status}`);
      }
      const data = await response.json();
      setRoomDetails(data);
      setSelectedRoom(roomNo);
    } catch (error) {
      console.error("Error fetching room details:", error);
      toast.error("Failed to load room details");
    } finally {
      setDetailsLoading(false);
    }
  };

  const handleRemoveRoomClick = async (roomNo) => {
    try {
      const bookedRes = await fetch(
        `http://localhost:8081/api/exams/${roomNo}/is-booked`
      );
      if (!bookedRes.ok) throw new Error("Failed to check room status");
      const isBooked = await bookedRes.json();

      if (isBooked) {
        setDeleteConfirmation({
          isOpen: true,
          roomNo,
          isBooked: true,
        });
      } else {
        setDeleteConfirmation({
          isOpen: true,
          roomNo,
          isBooked: false,
        });
      }
    } catch (error) {
      console.error("Error checking room status:", error);
      toast.error("Failed to check room status");
    }
  };

  const confirmDeleteRoom = async () => {
    const { roomNo } = deleteConfirmation;
    try {
      const response = await fetch(
        `http://localhost:8081/api/rooms/${roomNo}`,
        {
          method: "DELETE",
        }
      );

      if (!response.ok)
        throw new Error(`Failed to delete room: ${response.status}`);

      setRooms((prev) => prev.filter((room) => room.roomNo !== roomNo));

      if (selectedRoom === roomNo) handleCloseDetails();
      toast.success(`Room ${roomNo} deleted successfully`);
    } catch (error) {
      console.error("Error deleting room:", error);
      toast.error(`Failed to delete Room ${roomNo}`);
    } finally {
      setDeleteConfirmation({
        isOpen: false,
        roomNo: null,
        isBooked: false,
      });
    }
  };

  const cancelDeleteRoom = () => {
    setDeleteConfirmation({
      isOpen: false,
      roomNo: null,
      isBooked: false,
    });
  };

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
        <div className="mb-6 p-4 bg-red-50 text-red-600 rounded-lg">
          {error}
        </div>
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
                  <span className="text-sm text-muted-foreground">
                    Capacity
                  </span>
                  <Badge variant="secondary">
                    {room.seatingCapacity} seats
                  </Badge>
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

      {showAddRoom && (
        <AddRoomForm
          onClose={() => setShowAddRoom(false)}
          onRoomAdded={handleRoomAdded}
        />
      )}

      <Dialog open={!!selectedRoom} onOpenChange={handleCloseDetails}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="text-xl font-bold flex items-center gap-2">
              <DoorOpen className="h-5 w-5 text-blue-600" />
              Room {selectedRoom} Details
            </DialogTitle>
            <DialogDescription>
              Overview of room capacity and seating arrangement
            </DialogDescription>
          </DialogHeader>

          {detailsLoading ? (
            <div className="space-y-4 mt-4">
              <Skeleton className="h-6 w-3/4" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-1/2" />
            </div>
          ) : roomDetails ? (
            <div className="mt-4 space-y-3">
              {[
                { label: "Room Number", value: roomDetails.roomNo },
                {
                  label: "Student Capacity",
                  value: roomDetails.seatingCapacity + " students",
                },
                { label: "Rows", value: roomDetails.numRow + " rows" },
                {
                  label: "Seats Per Bench",
                  value: roomDetails.seatsPerBench + " students",
                },
                { label: "Room Column", value: roomDetails.roomColumn },
              ].map((item) => (
                <div
                  key={item.label}
                  className="flex justify-between items-center p-3 bg-gray-50 rounded-md border border-gray-200"
                >
                  <span className="text-gray-700 font-medium">
                    {item.label}
                  </span>
                  <Badge variant="secondary">{item.value}</Badge>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-4 text-gray-500">
              No details available
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
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
                  Are you sure you want to remove Room{" "}
                  {deleteConfirmation.roomNo}? This action cannot be undone.
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
