import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { ProgramForm } from "@/components/ui/ProgramForm";

export default function Programs() {
  const [programs, setPrograms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [dialogOpen, setDialogOpen] = useState(false);
  useEffect(() => {
    fetchPrograms();
  }, []);

  async function fetchPrograms() {
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      const data = await response.json();
      setPrograms(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
    } finally {
      setLoading(false);
    }
  }

  const handleSuccess = (newProgram) => {
    setPrograms((prev) => [...prev, newProgram]);
    setDialogOpen(false);
  };

  if (loading) return <p>Loading programs...</p>;

  return (
    <div className="flex flex-col gap-6">
      <div className="flex gap-10">
        <h1 className="text-xl font-bold">Program List</h1>
        <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
          <DialogTrigger
            className="shadow-xl border hover:bg-indigo-100 pr-2 pl-2"
            asChild
          >
            <Button variant="outline">Add Program</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Adding New Program</DialogTitle>
              <DialogDescription>
                Fill in the details of the new program.
              </DialogDescription>
            </DialogHeader>
            <ProgramForm onSuccess={handleSuccess} />
          </DialogContent>
        </Dialog>
      </div>
      <div>
        <table className="min-w-full border-collapse">
          <thead>
            <tr>
              <th className="border-b p-2 text-left">Program Code</th>
              <th className="border-b p-2 text-left">Program Name</th>
            </tr>
          </thead>
          <tbody>
            {programs.map((program) => (
              <tr key={program.programId}>
                <td className="border-b p-2">{program.programCode}</td>
                <td className="border-b p-2">{program.programName}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
