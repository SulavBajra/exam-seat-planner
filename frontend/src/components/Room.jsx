import React, { useEffect, useState } from "react";
import AddRoomForm from "@/components/AddRoomForm";
import EditRoomForm from "@/components/EditRoomForm";
import ViewRoom from "@/components/ViewRoom";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent, CardFooter } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { DoorOpen, Plus, Pencil } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { toast } from "sonner";

export default function Room() {
  const [rooms, setRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddRoom, setShowAddRoom] = useState(false);

  const [selectedRoom, setSelectedRoom] = useState(null);
  const [roomDetails, setRoomDetails] = useState(null);
  const [detailsLoading, setDetailsLoading] = useState(false);

  const [viewRoomMode, setViewRoomMode] = useState(false);
  const [editRoomMode, setEditRoomMode] = useState(false);

  const [deleteConfirmation, setDeleteConfirmation] = useState({
    isOpen: false,
    roomNo: null,
    isBooked: false,
  });

  // Fetch rooms
  useEffect(() => {
    const fetchRooms = async () => {
      setLoading(true);
      setError(null);
      try {
        const response = await fetch("http://localhost:8081/api/rooms");
        if (!response.ok) throw new Error(`Failed to fetch rooms: ${response.status}`);
        const data = await response.json();
        setRooms(data);
      } catch (err) {
        console.error(err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchRooms();
  }, []);

  // Common function to fetch room details
  const fetchRoomDetails = async (roomNo) => {
    setDetailsLoading(true);
    try {
      const response = await fetch(`http://localhost:8081/api/rooms/${roomNo}`);
      if (!response.ok) throw new Error("Failed to fetch room details");
      const data = await response.json();
      setRoomDetails(data);
      setSelectedRoom(roomNo);
      return data;
    } catch (err) {
      console.error(err);
      toast.error("Failed to load room details",{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
      return null;
    } finally {
      setDetailsLoading(false);
    }
  };

  // Handlers
  const handleViewRoom = async (roomNo) => {
    const data = await fetchRoomDetails(roomNo);
    if (data) setViewRoomMode(true);
  };

  const handleEditRoom = async (roomNo) => {
    try {
      const bookedRes = await fetch(`http://localhost:8081/api/rooms/booked/${roomNo}`);
      if (!bookedRes.ok) throw new Error("Failed to check room booking status");
      const isBooked = await bookedRes.json();
      if (isBooked) {
        toast.error(`Room ${roomNo} is booked and cannot be edited`,{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
        return;
      }

      const data = await fetchRoomDetails(roomNo);
      if (data) setEditRoomMode(true);
    } catch (err) {
      console.error(err);
      toast.error(err.message,{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    }
  };

  const handleDeleteRoom = async (roomNo) => {
    try {
      const bookedRes = await fetch(`http://localhost:8081/api/rooms/booked/${roomNo}`);
      if (!bookedRes.ok) throw new Error("Failed to check room booking status");
      const isBooked = await bookedRes.json();

      setDeleteConfirmation({ isOpen: true, roomNo, isBooked });
    } catch (err) {
      console.error(err);
      toast.error(err.message,{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    }
  };

  const confirmDeleteRoom = async () => {
    const { roomNo } = deleteConfirmation;
    try {
      const response = await fetch(`http://localhost:8081/api/rooms/${roomNo}`, { method: "DELETE" });
      if (!response.ok) throw new Error("Failed to delete room");
      setRooms((prev) => prev.filter((r) => r.roomNo !== roomNo));
      toast.success(`Room ${roomNo} deleted successfully`);
    } catch (err) {
      console.error(err);
      toast.error(err.message);
    } finally {
      setDeleteConfirmation({ isOpen: false, roomNo: null, isBooked: false });
      setSelectedRoom(null);
      setRoomDetails(null);
      setViewRoomMode(false);
      setEditRoomMode(false);
    }
  };

  const cancelDeleteRoom = () => setDeleteConfirmation({ isOpen: false, roomNo: null, isBooked: false });

  return (
    <div className="p-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Room Management</h1>
          <p className="text-muted-foreground">View and manage all available examination rooms</p>
        </div>
        <Button onClick={() => setShowAddRoom(true)} className="gap-2">
          <Plus className="h-4 w-4" />
          Add Room
        </Button>
      </div>

      {/* Error */}
      {error && <div className="mb-6 p-4 bg-red-50 text-red-600 rounded-lg">{error}</div>}

      {/* Rooms Grid */}
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
            <Card key={room.roomNo} className="hover:shadow-md transition-shadow">
              <CardHeader className="pb-3 flex items-center gap-3">
                <div className="p-2 bg-blue-50 rounded-full">
                  <DoorOpen className="h-5 w-5 text-blue-600" />
                </div>
                <CardTitle>Room {room.roomNo}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">Capacity</span>
                  <Badge variant="secondary">{room.seatingCapacity} seats</Badge>
                </div>
              </CardContent>
              <CardFooter className="flex justify-between">
                <Button variant="outline" size="sm" onClick={() => handleViewRoom(room.roomNo)}>
                  View Layout
                </Button>
                <Button variant="ghost" size="sm" className="text-red-500 hover:bg-red-50" onClick={() => handleDeleteRoom(room.roomNo)}>
                  Remove
                </Button>
                <Button variant="outline" size="sm" onClick={() => handleEditRoom(room.roomNo)}>
                  <Pencil className="h-4 w-4" />
                </Button>
              </CardFooter>
            </Card>
          ))}

          {/* Add Room Card */}
          <Card className="border-dashed hover:border-primary cursor-pointer transition-colors" onClick={() => setShowAddRoom(true)}>
            <div className="h-full flex flex-col items-center justify-center p-6 text-center">
              <Plus className="h-8 w-8 text-muted-foreground mb-2" />
              <h3 className="font-medium">Add New Room</h3>
              <p className="text-sm text-muted-foreground mt-1">Click to create a new examination room</p>
            </div>
          </Card>
        </div>
      )}

      {/* Add Room Form */}
      {showAddRoom && <AddRoomForm onClose={() => setShowAddRoom(false)} onRoomAdded={(newRoom) => setRooms((prev) => [...prev, newRoom])} />}

      {/* Edit Room Modal */}
      <Dialog open={editRoomMode} onOpenChange={() => setEditRoomMode(false)}>
        <DialogContent className="sm:max-w-[500px] rounded-lg">
          <DialogHeader>
            <DialogTitle>Edit Room {selectedRoom}</DialogTitle>
            <DialogDescription>Update room details below. Seating layout will auto-adjust.</DialogDescription>
          </DialogHeader>
          {!detailsLoading && roomDetails && (
            <EditRoomForm
              roomDetails={roomDetails}
              onClose={() => setEditRoomMode(false)}
              onRoomUpdated={(updatedRoom) =>
                setRooms((prev) => prev.map((r) => r.roomNo === updatedRoom.roomNo ? updatedRoom : r))
              }
            />
          )}
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation */}
      <Dialog open={deleteConfirmation.isOpen} onOpenChange={(open) => { if (!open) cancelDeleteRoom(); }}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm Deletion</DialogTitle>
            <DialogDescription>
              {deleteConfirmation.isBooked ? (
                <div className="text-red-600 font-medium py-2">
                  Cannot remove Room {deleteConfirmation.roomNo} because it is assigned to an exam.
                </div>
              ) : (
                <div>Are you sure you want to remove Room {deleteConfirmation.roomNo}? This action cannot be undone.</div>
              )}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            {deleteConfirmation.isBooked ? (
              <Button onClick={cancelDeleteRoom}>OK</Button>
            ) : (
              <>
                <Button variant="outline" onClick={cancelDeleteRoom}>Cancel</Button>
                <Button variant="destructive" onClick={confirmDeleteRoom}>Delete</Button>
              </>
            )}
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* View Room Layout */}
      <Dialog open={viewRoomMode} onOpenChange={() => setViewRoomMode(false)}>
        <DialogContent className="sm:max-w-md rounded-lg">
          <DialogHeader>
            <DialogTitle>Room Layout {selectedRoom}</DialogTitle>
            <DialogDescription>View the seating arrangement of the room</DialogDescription>
          </DialogHeader>
          {!detailsLoading && roomDetails && <ViewRoom roomDetails={roomDetails} />}
          <DialogFooter>
            <Button onClick={() => setViewRoomMode(false)}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
