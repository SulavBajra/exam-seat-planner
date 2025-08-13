import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { ScheduleExamForm } from "@/components/ScheduleExamForm";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  CalendarDays,
  School,
  DoorOpen,
  Trash2,
  PlusCircle,
  ChevronRight,
} from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { format } from "date-fns";

export default function Exam() {
  const [exams, setExams] = useState([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchExams();
  }, []);

  async function fetchExams() {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch("http://localhost:8081/api/exams");
      if (!response.ok) {
        throw new Error(`Failed to fetch exams: ${response.status}`);
      }
      const data = await response.json();
      setExams(data);
    } catch (error) {
      console.error(error);
      setError(error.message);
    } finally {
      setLoading(false);
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

      if (!response.ok) {
        throw new Error(`Failed to schedule exam: ${response.status}`);
      }

      await fetchExams();
      setIsDialogOpen(false);
    } catch (error) {
      console.error(error);
      setError(error.message);
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

      if (!response.ok) {
        throw new Error(`Failed to delete exam: ${response.status}`);
      }

      await fetchExams();
    } catch (error) {
      console.error(error);
      setError(error.message);
    }
  };

  const formatDate = (dateString) => {
    try {
      return format(new Date(dateString), "PP");
    } catch {
      return dateString;
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Exam Schedules</h1>
          <p className="text-muted-foreground">
            Manage all scheduled exams and seating arrangements
          </p>
        </div>
        <Button
          onClick={() => setIsDialogOpen(true)}
          className="gap-2 w-full sm:w-auto"
        >
          <PlusCircle className="h-4 w-4" />
          Schedule Exam
        </Button>
      </div>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[625px]">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <CalendarDays className="h-5 w-5" />
              Schedule New Exam
            </DialogTitle>
          </DialogHeader>
          <ScheduleExamForm
            onSubmit={handleScheduleExam}
            onCancel={() => setIsDialogOpen(false)}
          />
        </DialogContent>
      </Dialog>

      {error && (
        <Alert variant="destructive" className="mb-6">
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {loading ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[...Array(3)].map((_, i) => (
            <Card key={i}>
              <CardHeader>
                <Skeleton className="h-6 w-3/4" />
                <Skeleton className="h-4 w-1/2 mt-2" />
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Skeleton className="h-4 w-1/4" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-3/4" />
                </div>
                <div className="space-y-2">
                  <Skeleton className="h-4 w-1/4" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-3/4" />
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : exams.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {exams.map((exam) => (
            <Card
              key={exam.id}
              className="hover:shadow-md transition-shadow group"
            >
              <CardHeader className="pb-3">
                <div className="flex justify-between items-start">
                  <div>
                    <div className="flex items-center gap-2">
                      <div className="p-2 rounded-lg bg-blue-50 text-blue-600">
                        <CalendarDays className="h-5 w-5" />
                      </div>
                      <CardTitle className="text-lg">Exam Schedule</CardTitle>
                    </div>
                    <CardDescription className="mt-2 font-medium text-foreground">
                      {formatDate(exam.date)}
                    </CardDescription>
                  </div>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-8 w-8 text-muted-foreground hover:text-red-500 hover:bg-red-50"
                    onClick={() => handleDeleteExam(exam.id)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <div className="flex items-center gap-2 text-sm font-medium mb-2 text-muted-foreground">
                    <School className="h-4 w-4" />
                    Programs
                  </div>
                  <ul className="space-y-3">
                    {exam.programs.map((program) => (
                      <li
                        key={program.programCode}
                        className="flex items-center justify-between"
                      >
                        <span className="font-medium">
                          {program.programName}
                        </span>
                        <Badge variant="outline" className="font-normal">
                          {program.programCode}
                        </Badge>
                      </li>
                    ))}
                  </ul>
                </div>

                <div>
                  <div className="flex items-center gap-2 text-sm font-medium mb-2 text-muted-foreground">
                    <DoorOpen className="h-4 w-4" />
                    Rooms
                  </div>
                  <ul className="space-y-3">
                    {exam.rooms.map((room) => (
                      <li
                        key={room.roomNo}
                        className="flex items-center justify-between"
                      >
                        <span className="font-medium">Room {room.roomNo}</span>
                        <Badge variant="secondary">
                          {room.seatingCapacity} seats
                        </Badge>
                      </li>
                    ))}
                  </ul>
                </div>
              </CardContent>
              <div className="px-6 pb-4">
                <Button
                  variant="outline"
                  className="w-full group-hover:bg-accent"
                >
                  View Details <ChevronRight className="ml-2 h-4 w-4" />
                </Button>
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <Card className="text-center">
          <CardHeader>
            <CardTitle>No Exams Scheduled</CardTitle>
            <CardDescription>
              There are no exams scheduled yet. Create your first exam schedule.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => setIsDialogOpen(true)} className="gap-2">
              <PlusCircle className="h-4 w-4" />
              Schedule Exam
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
