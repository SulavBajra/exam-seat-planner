import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import {
  CalendarDays,
  DoorOpen,
  GraduationCap,
  Loader2,
  ArrowLeft,
  ArrowRight,
} from "lucide-react";
import { toast } from "sonner";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export function ScheduleExamForm({ onSubmit, onCancel }) {
  const [currentStep, setCurrentStep] = useState(1);
  const [rooms, setRooms] = useState([]);
  const [availablePrograms, setAvailablePrograms] = useState([]);
  const [bookedRooms, setBookedRooms] = useState([]);
  const [isLoading, setIsLoading] = useState({
    rooms: false,
    programs: false,
    submitting: false,
  });
  const [error, setError] = useState({
    rooms: null,
    programs: null,
  });

  const [formData, setFormData] = useState({
    startDate: "",
    endDate: "",
    programSemesters: [],
    rooms: [],
  });

  const semesters = [
    "FIRST",
    "SECOND",
    "THIRD",
    "FOURTH",
    "FIFTH",
    "SIXTH",
    "SEVENTH",
    "EIGHTH",
  ];

  const steps = [
    { id: 1, title: "Date Selection", description: "Choose exam date" },
    {
      id: 2,
      title: "Programs & Semesters",
      description: "Select programs and semesters",
    },
    { id: 3, title: "Room Selection", description: "Choose examination rooms" },
    { id: 4, title: "Review & Submit", description: "Review before submitting" },
  ];


  useEffect(() => {
    fetchPrograms();
    fetchRooms();
  },[]);

  useEffect(() => {
  fetchBookedRooms(formData.startDate, formData.endDate);
}, [formData.startDate, formData.endDate]);

  async function fetchRooms() {
    setIsLoading((p) => ({ ...p, rooms: true }));
    try {
      const res = await fetch("http://localhost:8081/api/rooms");
      if (!res.ok) throw new Error("Failed to fetch rooms");
      setRooms(await res.json());
    } catch (err) {
      setError((p) => ({ ...p, rooms: err.message }));
      toast.error("Failed to load rooms");
    } finally {
      setIsLoading((p) => ({ ...p, rooms: false }));
    }
  }

  async function fetchPrograms() {
    setIsLoading((p) => ({ ...p, programs: true }));
    try {
      const res = await fetch("http://localhost:8081/api/programs");
      if (!res.ok) throw new Error("Failed to fetch programs");
      setAvailablePrograms(await res.json());
    } catch (err) {
      setError((p) => ({ ...p, programs: err.message }));
      toast.error("Failed to load programs ",error);
    } finally {
      setIsLoading((p) => ({ ...p, programs: false }));
    }
  }

  async function fetchBookedRooms(startDate, endDate) {
    if (!startDate || !endDate) return; 

    try {
      const res = await fetch(
        `http://localhost:8081/api/exams/booked-rooms?startDate=${startDate}&endDate=${endDate}`
      );

      if (!res.ok) throw new Error("Failed to fetch booked rooms");

      const data = await res.json();
      setBookedRooms(data);
    } catch (error) {
      console.error(error);
      toast.error(`Failed to load booked rooms: ${error.message}`);
    }
  }

  const getTodayDate = () => new Date().toISOString().split("T")[0];

  const nextStep = () => {
   if (currentStep === 1) {
    if (!formData.startDate || !formData.endDate) {
    toast.error("Please select both start and end dates");
    return;
    }
     if (formData.endDate < formData.startDate) {
    toast.error("End date cannot be before start date");
    return;
    }
  }
    if (currentStep === 2 && formData.programSemesters.length === 0)
      return toast.error("Please select at least one program and semester");
    if (currentStep === 3 && formData.rooms.length === 0)
      return toast.error("Please select at least one room");

    setCurrentStep((s) => Math.min(s + 1, steps.length));
  };

  const prevStep = () => setCurrentStep((s) => Math.max(s - 1, 1));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading((p) => ({ ...p, submitting: true }));

    const payload = {
      startDate: formData.startDate,
      endDate: formData.endDate,
      programSemesters: formData.programSemesters.map(({ programCode, semester }) => ({
        programCode,
        semester: semesters.indexOf(semester) + 1,
      })),
      roomNumbers: formData.rooms.map((r) => r.roomNo),
    };

    if (!formData.startDate || !formData.endDate) {
      toast.error("Please select both start and end dates");
      setIsLoading((prev) => ({ ...prev, submitting: false }));
      return;
    }

    if (formData.endDate < formData.startDate) {
      toast.error("End date cannot be before start date");
      setIsLoading((prev) => ({ ...prev, submitting: false }));
      return;
    }


    try {
      await onSubmit(payload);
    } finally {
      setIsLoading((p) => ({ ...p, submitting: false }));
    }
  };


  const StepIndicator = () => (
    <div className="flex justify-center items-center gap-3 mb-8">
      {steps.map((step, i) => (
        <div key={step.id} className="flex items-center">
          <div
            className={`w-8 h-8 flex items-center justify-center rounded-full text-sm font-medium border transition-colors ${
              currentStep >= step.id
                ? "bg-primary text-primary-foreground border-primary"
                : "bg-muted text-muted-foreground"
            }`}
          >
            {step.id}
          </div>
          {i < steps.length - 1 && (
            <div
              className={`w-10 h-0.5 ${
                currentStep > step.id ? "bg-primary" : "bg-muted"
              }`}
            />
          )}
        </div>
      ))}
    </div>
  );


  const renderStep = () => {
    switch (currentStep) {
    case 1:
  return (
    <Card className="shadow-md border-gray-200">
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-lg">
          <CalendarDays className="h-5 w-5 text-primary" />
          Select Exam Dates
        </CardTitle>
        <CardDescription>
          Choose the start and end dates for the examination period
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          {/* Start Date */}
          <div className="flex flex-col">
            <Label htmlFor="startDate" className="font-medium text-sm">
              Start Date
            </Label>
            <Input
              type="date"
              id="startDate"
              name="startDate"
              value={formData.startDate}
              onChange={(e) =>
                setFormData({ ...formData, startDate: e.target.value })
              }
              required
              min={getTodayDate()}
              className="mt-1 border rounded-md px-3 py-2 focus:ring-2 focus:ring-primary focus:border-primary"
            />
          </div>

          {/* End Date */}
          <div className="flex flex-col">
            <Label htmlFor="endDate" className="font-medium text-sm">
              End Date
            </Label>
            <Input
              type="date"
              id="endDate"
              name="endDate"
              value={formData.endDate}
              onChange={(e) =>
                setFormData({ ...formData, endDate: e.target.value })
              }
              required
              min={formData.startDate || getTodayDate()}
              className="mt-1 border rounded-md px-3 py-2 focus:ring-2 focus:ring-primary focus:border-primary"
            />
          </div>
        </div>

        <p className="text-sm text-muted-foreground">
          The end date must be the same or after the start date.
        </p>
      </CardContent>
    </Card>
  );

      case 2:
        return (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <GraduationCap className="h-5 w-5" /> Select Programs & Semesters
              </CardTitle>
              <CardDescription>
                Choose which programs and semesters will take the exam
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading.programs ? (
                <div className="flex justify-center items-center py-6">
                  <Loader2 className="h-5 w-5 animate-spin mr-2" /> Loading...
                </div>
              ) : (
                <ScrollArea className="h-[400px] rounded-md border">
                  <div className="p-4 space-y-6">
                    {availablePrograms.map((program) => (
                      <div key={program.programCode} className="space-y-2">
                        <div className="flex items-center gap-2">
                          <span className="font-medium">{program.programName}</span>
                          <Badge variant="outline">{program.programCode}</Badge>
                        </div>
                        <div className="grid grid-cols-2 md:grid-cols-4 gap-2">
                          {semesters.map((sem) => (
                            <Label
                              key={sem}
                              className="flex items-center gap-2 text-sm border rounded-md p-2 hover:bg-accent/40"
                            >
                              <Checkbox
                                checked={formData.programSemesters.some(
                                  (ps) =>
                                    ps.programCode === program.programCode &&
                                    ps.semester === sem
                                )}
                                onCheckedChange={(checked) => {
                                  setFormData((prev) => ({
                                    ...prev,
                                    programSemesters: checked
                                      ? [
                                          ...prev.programSemesters,
                                          {
                                            programCode: program.programCode,
                                            semester: sem,
                                          },
                                        ]
                                      : prev.programSemesters.filter(
                                          (ps) =>
                                            !(
                                              ps.programCode ===
                                                program.programCode &&
                                              ps.semester === sem
                                            )
                                        ),
                                  }));
                                }}
                              />
                              {sem}
                            </Label>
                          ))}
                        </div>
                      </div>
                    ))}
                  </div>
                </ScrollArea>
              )}
            </CardContent>
          </Card>
        );

      case 3:
        return (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-lg">
                <DoorOpen className="h-5 w-5" /> Select Examination Rooms
              </CardTitle>
              <CardDescription>
                Choose available rooms for the examination
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading.rooms ? (
                <div className="flex justify-center items-center py-6">
                  <Loader2 className="h-5 w-5 animate-spin mr-2" /> Loading...
                </div>
              ) : (
                <ScrollArea className="h-[400px] rounded-md border">
                  <div className="p-4 space-y-3">
                    {rooms.map((room) => {
                      const isBooked = bookedRooms.includes(room.roomNo);
                      return (
                        <Label
                          key={room.roomNo}
                          className={`flex justify-between items-center gap-3 p-3 border rounded-lg hover:bg-accent/40 ${
                            isBooked ? "opacity-50 cursor-not-allowed" : ""
                          }`}
                        >
                          <Checkbox
                            checked={formData.rooms.some(
                              (r) => r.roomNo === room.roomNo
                            )}
                            disabled={isBooked}
                            onCheckedChange={(checked) =>
                              setFormData((prev) => ({
                                ...prev,
                                rooms: checked
                                  ? [...prev.rooms, room]
                                  : prev.rooms.filter(
                                      (r) => r.roomNo !== room.roomNo
                                    ),
                              }))
                            }
                          />
                          <div className="flex justify-between w-full">
                            <span className="font-medium">
                              Room {room.roomNo}
                            </span>
                            <Badge variant="outline">
                              {room.seatingCapacity} seats
                            </Badge>
                          </div>
                        </Label>
                      );
                    })}
                  </div>
                </ScrollArea>
              )}
            </CardContent>
          </Card>
        );

      case 4:
        return (
          <Card>
            <CardHeader>
              <CardTitle className="text-lg font-semibold">
                Review & Confirm
              </CardTitle>
              <CardDescription>
                Please review all selections before submitting
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between items-center p-3 bg-muted rounded-md">
                <span className="font-medium">Exam Duration:</span>
                <Badge variant="secondary">
                  {formData.startDate} → {formData.endDate}
                </Badge>
              </div>
              <div>
                <h4 className="font-medium mb-2">Programs & Semesters</h4>
                <div className="space-y-1 text-sm">
                  {formData.programSemesters.map((p, i) => (
                    <div
                      key={i}
                      className="flex justify-between border rounded-md p-2"
                    >
                      <span>{p.programCode}</span>
                      <Badge variant="outline">{p.semester}</Badge>
                    </div>
                  ))}
                </div>
              </div>

              <div>
                <h4 className="font-medium mb-2">Selected Rooms</h4>
                <div className="space-y-1 text-sm">
                  {formData.rooms.map((room, i) => (
                    <div
                      key={i}
                      className="flex justify-between border rounded-md p-2"
                    >
                      <span>Room {room.roomNo}</span>
                      <Badge variant="outline">
                        {room.seatingCapacity} seats
                      </Badge>
                    </div>
                  ))}
                </div>
              </div>
            </CardContent>
          </Card>
        );

      default:
        return null;
    }
  };


  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6">
      <StepIndicator />
      <div className="flex-1 overflow-y-auto">{renderStep()}</div>

      <div className="flex justify-between border-t pt-4">
        <Button
          type="button"
          variant="outline"
          disabled={currentStep === 1 || isLoading.submitting}
          onClick={prevStep}
        >
          <ArrowLeft className="h-4 w-4 mr-2" /> Back
        </Button>

        <div className="flex gap-2">
          <Button
            type="button"
            variant="outline"
            onClick={onCancel}
            disabled={isLoading.submitting}
          >
            Cancel
          </Button>
          {currentStep < steps.length ? (
            <Button type="button" onClick={nextStep}>
              Next <ArrowRight className="h-4 w-4 ml-2" />
            </Button>
          ) : (
            <Button type="submit" disabled={isLoading.submitting}>
              {isLoading.submitting ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin mr-2" /> Scheduling...
                </>
              ) : (
                "Confirm & Schedule"
              )}
            </Button>
          )}
        </div>
      </div>
    </form>
  );
}
