import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { ScheduleExamForm } from "@/components/ScheduleExamForm";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

export default function Exam() {
  const [exams, setExams] = useState([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  useEffect(() => {
    fetchExams();
  }, []);

  async function fetchExams() {
    try {
      const response = await fetch("http://localhost:8081/api/exams");
      const data = await response.json();
      setExams(data);
    } catch (error) {
      console.error(error);
    }
  }

  const handleScheduleExam = async (formData) => {
    try {
      const response = await fetch("http://localhost:8081/api/exams", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(formData),
      });
      if (response.ok) {
        fetchExams(); // Refresh the list
        setIsDialogOpen(false); // Close the dialog
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleDeleteExam = async (examId) => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/exams/${examId}`,
        {
          method: "DELETE",
        }
      );
      if (response.ok) {
        fetchExams();
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="p-6">
      <Button onClick={() => setIsDialogOpen(true)}>Schedule Exams</Button>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[625px]">
          <DialogHeader>
            <DialogTitle>Schedule New Exam</DialogTitle>
          </DialogHeader>
          <ScheduleExamForm
            onSubmit={handleScheduleExam}
            onCancel={() => setIsDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      <h1 className="text-2xl font-bold my-6">All Scheduled Exams</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {exams.map((exam) => (
          <div
            key={exam.id}
            className="bg-white rounded-lg shadow-md overflow-hidden border border-gray-200"
          >
            <div className="p-4 bg-blue-600 text-white">
              <h2 className="text-xl font-semibold">Exam Schedule</h2>
              <p className="text-sm">{exam.date}</p>
            </div>

            <div className="p-4">
              <h3 className="font-medium text-gray-700 mb-2">Programs:</h3>
              <ul className="space-y-1 mb-4">
                {exam.programs.map((program) => (
                  <li
                    key={program.programCode}
                    className="flex justify-between"
                  >
                    <span>{program.programName}</span>
                    <span className="text-gray-500">
                      ({program.programCode})
                    </span>
                  </li>
                ))}
              </ul>

              <h3 className="font-medium text-gray-700 mb-2">Rooms:</h3>
              <ul className="space-y-2">
                {exam.rooms.map((room) => (
                  <li
                    key={room.roomNo}
                    className="flex justify-between items-center"
                  >
                    <div>
                      <span className="font-medium">Room {room.roomNo}</span>
                    </div>
                    <span className="bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-0.5 rounded">
                      Capacity: {room.seatingCapacity}
                    </span>
                  </li>
                ))}
              </ul>

              <div className="mt-4 flex justify-end">
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => handleDeleteExam(exam.id)}
                >
                  Delete
                </Button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
