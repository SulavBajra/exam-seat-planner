import React from "react";
import { Button } from "@/components/ui/button";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogTrigger,
} from "@/components/ui/dialog";
import ExamForm from "@/components/ui/ExamForm";

export default function Exam() {
  const [dialogOpen, setDialogOpen] = useState(false);
  const [exams, setExams] = useState([]);

  useEffect(() => {
    fetchExams();
  }, []);

  async function fetchExams() {
    try {
      const response = await fetch("http://localhost:8081/api/exams");
      const data = await response.json();
      setExams(data);
    } catch (error) {
      console.error("Error fetching exams:", error);
    }
  }

  const handleSuccess = (newExam) => {
    setExams((prev) => [...prev, newExam]);
    setDialogOpen(false);
  };

  return (
    <>
      {/* <div>
        <AvailableStudents />
      </div> */}
      <div className="flex flex-row gap-6">
        <div>
          <h1 className="p-1">Scheduled Exams</h1>
          <div className="p-2">
            <ul>
              {exams.map((exam) => (
                <li key={exam.id}>
                  {exam.date} - {exam.subjectName}
                </li>
              ))}
            </ul>
          </div>
        </div>
        <div>
          <Dialog>
            <DialogTrigger asChild>
              <Button variant="outline" className="">
                Schedule Exam
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Adding New Exam</DialogTitle>
                <DialogDescription>
                  Fill in the details of the new exam.
                </DialogDescription>
              </DialogHeader>
              <ExamForm onSuccess={handleSuccess} />
            </DialogContent>
          </Dialog>
        </div>
      </div>
    </>
  );
}
