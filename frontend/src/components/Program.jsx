import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { GraduationCap, Plus } from "lucide-react";
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

  const handleAddProgram = async () => {
    try {
      const response = await fetch("http://localhost:8081/api/programs", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newProgram),
      });

      if (!response.ok)
        throw new Error(`Failed to add program: ${response.status}`);

      const addedProgram = await response.json();
      setPrograms([...programs, addedProgram]);
      setIsDialogOpen(false);
      setNewProgram({ programName: "", programCode: "" });
      toast.success("Program added successfully");
    } catch (error) {
      console.error("Error adding program:", error);
      toast.error(error.message);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">
            Academic Programs
          </h1>
          <p className="text-muted-foreground">
            All available programs in the institution
          </p>
        </div>

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
              <Button onClick={handleAddProgram}>Add Program</Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 text-red-600 rounded-lg">
          {error}
          <Button
            variant="ghost"
            size="sm"
            onClick={fetchPrograms}
            className="mt-2"
          >
            Retry
          </Button>
        </div>
      )}

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
              key={program.id}
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
                <div className="flex items-center gap-2 text-sm">
                  <Badge variant="outline">Code: {program.programCode}</Badge>
                </div>
              </CardContent>
              <CardFooter className="flex justify-end">
                <Button variant="outline" size="sm">
                  View Details
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle>No Programs Found</CardTitle>
            <p className="text-muted-foreground">
              There are no programs currently registered in the system
            </p>
          </CardHeader>
          <CardContent>
            <Button onClick={() => setIsDialogOpen(true)} className="gap-2">
              <Plus className="h-4 w-4" />
              Add Program
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
