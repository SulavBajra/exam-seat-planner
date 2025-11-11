import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {ScheduleExamForm} from "@/components/ScheduleExamForm";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  CalendarDays,
  School,
  DoorOpen,
  Trash2,
  PlusCircle,
  ChevronRight,
  Users,
  BookOpen,
  Calendar,
  Hash,
  User,
  Building,
} from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { format } from "date-fns";
import { Separator } from "@/components/ui/separator";


export default function Exam() {
  const [exams, setExams] = useState([]);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedExam, setSelectedExam] = useState(null);
  const [detailsDialogOpen, setDetailsDialogOpen] = useState(false);
  const [examStudents, setExamStudents] = useState({});

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
      const studentCounts = {};
      for (const exam of data) {
        try {
          const examResponse = await fetch(
            `http://localhost:8081/api/exams/students/${exam.id}`
          );
          
          if (examResponse.ok) {
            const students = await examResponse.json();
            studentCounts[exam.id] = students;
          } else {
            console.error(
              `Failed to fetch students for exam ${exam.id}: ${examResponse.status}`
            );
            studentCounts[exam.id] = 0;
          }
        } catch (error) {
          console.error(`Error fetching students for exam ${exam.id}:`, error);
          studentCounts[exam.id] = 0;
        }
      }
      setExamStudents(studentCounts);
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
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to create exam");
      }

      const createdExam = await response.json();

      try {
        const seatResponse = await fetch(
          `http://localhost:8081/api/seating/generate/${createdExam.id}`,
          { method: "POST" }
        );
        if (!seatResponse.ok) {
          console.error(
            `Seat plan generation failed for exam ${createdExam.id}`
          );
        } else {
          console.log(`Seat plan generated for exam ${createdExam.id}`);
        }
      } catch (seatError) {
        console.error("Error generating seat plan:", seatError);
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

  const handleViewDetails = (exam) => {
    setSelectedExam(exam);
    setDetailsDialogOpen(true);
  };

  const formatDate = (dateString) => {
    try {
      return format(new Date(dateString), "PPPP");
    } catch {
      return dateString;
    }
  };

  const formatDateRange = (startDate, endDate) => {
  try {
    const start = format(new Date(startDate), "MMM dd, yyyy");
    const end = format(new Date(endDate), "MMM dd, yyyy");
    return `${start} - ${end}`;
  } catch {
    return `${startDate} - ${endDate}`;
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
            <DialogDescription>
              Fill in the details below to create a new exam schedule.
            </DialogDescription>
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
                      {formatDateRange(exam.startDate, exam.endDate)}
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
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <BookOpen className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm font-medium">Programs</span>
                  </div>
                  <Badge variant="secondary">
                    {exam.programSemesters.length}
                  </Badge>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <DoorOpen className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm font-medium">Rooms</span>
                  </div>
                  <Badge variant="secondary">{exam.roomNames.length}</Badge>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Users className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm font-medium">Total Students</span>
                  </div>
                  <Badge variant="outline">{examStudents[exam.id] || 0}</Badge>
                </div>
              </CardContent>
              <CardFooter>
                <Button
                  variant="outline"
                  className="w-full group-hover:bg-accent"
                  onClick={() => handleViewDetails(exam)}
                >
                  View Details <ChevronRight className="ml-2 h-4 w-4" />
                </Button>
              </CardFooter>
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

      <Dialog open={detailsDialogOpen} onOpenChange={setDetailsDialogOpen}>
        <DialogContent className="sm:max-w-[700px] max-h-[80vh] overflow-y-auto">
          <DialogHeader className="pb-4">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-blue-100 rounded-full">
                <Calendar className="h-6 w-6 text-blue-600" />
              </div>
              <div>
                <DialogTitle className="text-2xl">
                  Exam Schedule Details
                </DialogTitle>
                <DialogDescription className="text-base">
                  {selectedExam && formatDate(selectedExam.date)}
                </DialogDescription>
              </div>
            </div>
          </DialogHeader>

          {selectedExam && (
            <div className="space-y-6">
              {/* Summary Section */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 p-4 bg-muted/30 rounded-lg">
                <div className="text-center">
                  <div className="flex justify-center mb-2">
                    <BookOpen className="h-5 w-5 text-blue-600" />
                  </div>
                  <div className="text-2xl font-bold">
                    {selectedExam.programSemesters.length}
                  </div>
                  <div className="text-sm text-muted-foreground">Programs</div>
                </div>
                <div className="text-center">
                  <div className="flex justify-center mb-2">
                    <Building className="h-5 w-5 text-green-600" />
                  </div>
                  <div className="text-2xl font-bold">
                    {selectedExam.roomNames.length}
                  </div>
                  <div className="text-sm text-muted-foreground">Rooms</div>
                </div>
                <div className="text-center">
                  <div className="flex justify-center mb-2">
                    <User className="h-5 w-5 text-purple-600" />
                  </div>
                  <div className="text-2xl font-bold">
                    {examStudents[selectedExam.id] || 0}
                  </div>
                  <div className="text-sm text-muted-foreground">Students</div>
                </div>
              </div>

              <Separator />

              {/* Programs Section */}
              <div>
                <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                  <School className="h-5 w-5 text-blue-600" />
                  Programs Information
                </h3>
                <div className="space-y-3">
                  {selectedExam.programSemesters.map((program) => (
                    <div
                      key={`${program.programCode}-${program.semester}`}
                      className="p-4 border rounded-lg hover:bg-muted/50 transition-colors"
                    >
                      <div className="flex justify-between items-start">
                        <div className="space-y-1">
                          <div className="font-medium flex items-center gap-2">
                            <Hash className="h-4 w-4 text-muted-foreground" />
                            {program.programName}
                          </div>
                          <div className="text-sm text-muted-foreground">
                            Code: {program.programCode} | Semester:{" "}
                            {program.semesterName}
                          </div>
                        </div>
                        <Badge variant="outline" className="ml-2">
                          {program.studentCount} students
                        </Badge>
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <Separator />

              {/* Rooms Section */}
              <div>
                <h3 className="text-lg font-semibold mb-4 flex items-center gap-2">
                  <DoorOpen className="h-5 w-5 text-green-600" />
                  Assigned Rooms
                </h3>
                <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                  {selectedExam.roomNames.map((room) => (
                    <div
                      key={room}
                      className="p-3 border rounded-lg text-center hover:bg-muted/50 transition-colors"
                    >
                      <div className="font-medium">Room {room}</div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Total Students Summary */}
              <div className="p-4 bg-primary/5 border border-primary/20 rounded-lg">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Users className="h-6 w-6 text-primary" />
                    <div>
                      <div className="font-semibold">Total Students</div>
                      <div className="text-sm text-muted-foreground">
                        Across all programs
                      </div>
                    </div>
                  </div>
                  <Badge variant="secondary" className="text-lg px-3 py-1">
                    {examStudents[selectedExam.id] || 0} students
                  </Badge>
                </div>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
