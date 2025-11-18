import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { GraduationCap, Plus, Pencil, Trash2, Eye } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { toast } from "sonner";

export default function Program() {
  const [programs, setPrograms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newProgram, setNewProgram] = useState({
    programName: "",
    programCode: "",
  });
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const [programDetails, setProgramDetails] = useState(null);
  const [detailsOpen, setDetailsOpen] = useState(false);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [editProgram, setEditProgram] = useState(null);
  const [deleteConfirmation, setDeleteConfirmation] = useState({
    isOpen: false,
    programCode: null,
  });

  useEffect(() => {
    fetchPrograms();
  }, []);

  async function fetchPrograms() {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      if (!response.ok)
        throw new Error(`Failed to fetch programs: ${response.status}`);
      const data = await response.json();
      setPrograms(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
      setError(error.message);
      toast.error("Failed to load programs");
    } finally {
      setLoading(false);
    }
  }

  const fetchProgramDetails = async (programCode) => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/programs/${programCode}`
      );
      if (!response.ok)
        throw new Error(`Failed to fetch details: ${response.status}`);
      const data = await response.json();
      setProgramDetails(data);
      setDetailsOpen(true);
    } catch (error) {
      console.error("Error fetching details:", error);
      toast.error("Failed to load details",{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    }
  };

  const handleAddProgram = async () => {
    try {
      const response = await fetch("http://localhost:8081/api/programs", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newProgram),
      });
      if (!response.ok) throw new Error(`Failed to add program`);
      const added = await response.json();
      setPrograms([...programs, added]);
      setNewProgram({ programName: "", programCode: "" });
      setIsDialogOpen(false);
      toast.success("Program added successfully");
    } catch (error) {
      toast.error(error.message,{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    }
  };

  const handleEditClick = (program) => {
    setEditProgram({ ...program });
    setEditDialogOpen(true);
  };

  const handleUpdateProgram = async () => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/programs/${editProgram.programCode}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(editProgram),
        }
      );
      if (!response.ok) throw new Error("Failed to update program");
      toast.success("Program updated successfully");
      setEditDialogOpen(false);
      fetchPrograms();
    } catch (error) {
      toast.error(error.message,{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    }
  };

  const handleDelete = async (programCode) => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/programs/${programCode}`,
        { method: "DELETE" }
      );
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to delete program");
      }
      setPrograms(programs.filter((p) => p.programCode !== programCode));
      setDeleteConfirmation({ isOpen: false, programCode: null });
      toast.success("Program deleted");
    } catch (error) {
      toast.error(error.message,{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">
            Academic Programs
          </h1>
          <p className="text-muted-foreground">
            All available programs in the institution
          </p>
        </div>

        {/* Add Program */}
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button className="gap-2">
              <Plus className="h-4 w-4" />
              Add Program
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[425px]">
            <DialogHeader>
              <DialogTitle className="flex items-center gap-2">
                <GraduationCap className="h-5 w-5" />
                Add New Program
              </DialogTitle>
            </DialogHeader>
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="programName" className="text-right">
                  Name
                </Label>
                <Input
                  id="programName"
                  value={newProgram.programName}
                  onChange={(e) =>
                    setNewProgram({
                      ...newProgram,
                      programName: e.target.value,
                    })
                  }
                  className="col-span-3"
                  placeholder="e.g., Computer Science"
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="programCode" className="text-right">
                  Code
                </Label>
                <Input
                  id="programCode"
                  value={newProgram.programCode}
                  onChange={(e) =>
                    setNewProgram({
                      ...newProgram,
                      programCode: e.target.value,
                    })
                  }
                  className="col-span-3"
                  placeholder="e.g., CS101"
                />
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                Cancel
              </Button>
              <Button onClick={handleAddProgram}>Add</Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* Error */}
      {error && (
        <div className="mb-6 p-4 bg-red-50 text-red-600 rounded-lg">
          {error}
          <Button variant="ghost" size="sm" onClick={fetchPrograms}>
            Retry
          </Button>
        </div>
      )}

      {/* Program Grid */}
      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(6)].map((_, i) => (
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
      ) : programs.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {programs.map((program) => (
            <Card
              key={program.programCode}
              className="hover:shadow-md transition-shadow"
            >
              <CardHeader className="pb-3">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-blue-50 rounded-full">
                    <GraduationCap className="h-5 w-5 text-blue-600" />
                  </div>
                  <CardTitle>{program.programName}</CardTitle>
                </div>
              </CardHeader>
              <CardContent>
                <Badge variant="outline">Code: {program.programCode}</Badge>
              </CardContent>
              <CardFooter className="flex justify-between">
                <Button
                  variant="outline"
                  size="sm"
                  className="gap-1"
                  onClick={() => fetchProgramDetails(program.programCode)}
                >
                  <Eye className="h-4 w-4" /> View
                </Button>
                <div className="flex gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => handleEditClick(program)}
                  >
                    <Pencil className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="text-red-500 hover:bg-red-50"
                    onClick={() =>
                      setDeleteConfirmation({
                        isOpen: true,
                        programCode: program.programCode,
                      })
                    }
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </CardFooter>
            </Card>
          ))}
        </div>
      ) : (
        <p className="text-center text-gray-500">No programs found.</p>
      )}

      {/* View Details Dialog */}
      <Dialog open={detailsOpen} onOpenChange={setDetailsOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Program Details</DialogTitle>
          </DialogHeader>
          {programDetails ? (
            <div>
              <p>
                <strong>Name:</strong> {programDetails.programName}
              </p>
              <p>
                <strong>Code:</strong> {programDetails.programCode}
              </p>
            </div>
          ) : (
            <p>Loading...</p>
          )}
        </DialogContent>
      </Dialog>

      {/* Edit Dialog */}
      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit Program</DialogTitle>
          </DialogHeader>
          {editProgram && (
            <div className="grid gap-4 py-4">
              <div className="grid grid-cols-4 items-center gap-4">
                <Label>Name</Label>
                <Input
                  className="col-span-3"
                  value={editProgram.programName}
                  onChange={(e) =>
                    setEditProgram({
                      ...editProgram,
                      programName: e.target.value,
                    })
                  }
                />
              </div>
              <div className="grid grid-cols-4 items-center gap-4">
                <Label>Code</Label>
                <Input
                  className="col-span-3"
                  value={editProgram.programCode}
                  disabled
                />
              </div>
            </div>
          )}
          <div className="flex justify-end gap-2">
            <Button variant="outline" onClick={() => setEditDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleUpdateProgram}>Update</Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation */}
      <Dialog
        open={deleteConfirmation.isOpen}
        onOpenChange={(o) =>
          setDeleteConfirmation({ ...deleteConfirmation, isOpen: o })
        }
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Confirm Delete</DialogTitle>
          </DialogHeader>
          <p>
            Are you sure you want to delete{" "}
            <b>{deleteConfirmation.programCode}</b>? This cannot be undone.
          </p>
          <div className="flex justify-end gap-2 mt-4">
            <Button
              variant="outline"
              onClick={() =>
                setDeleteConfirmation({ isOpen: false, programCode: null })
              }
            >
              Cancel
            </Button>
            <Button
              variant="destructive"
              onClick={() => handleDelete(deleteConfirmation.programCode)}
            >
              Delete
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
