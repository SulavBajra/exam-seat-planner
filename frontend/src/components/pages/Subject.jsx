import React from "react";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import SubjectForm from "@/components/ui/SubjectForm";

export default function Subject() {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [subjects, setSubjects] = useState([]);

  const handleSuccess = (newSubject) => {
    setSubjects((prev) => [...prev, newSubject]);
    setDialogOpen(false);
  };

  return (
    <>
      <div className="flex flex-col gap-4 p-2">
        <div>
          <h1 className="p-1">Subject Management</h1>
        </div>
        <div>
          <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogTrigger
              className="shadow-xl border hover:bg-indigo-100 pr-2 pl-2"
              asChild
            >
              <Button variant="outline">Add Subject</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Adding New Subject</DialogTitle>
                <DialogDescription>
                  Fill in the details of the new subject.
                </DialogDescription>
              </DialogHeader>
              <SubjectForm onSuccess={handleSuccess} />
            </DialogContent>
          </Dialog>
        </div>
      </div>
    </>
  );
}
