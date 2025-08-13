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
import { DoorOpen, Plus } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";

export default function Room() {
  const [rooms, setRooms] = useState([]);
  const [showAddRoom, setShowAddRoom] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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
              className="hover:shadow-md transition-shadow"
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
    </div>
  );
}
