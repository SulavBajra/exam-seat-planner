import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { useEffect, useState } from "react";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { CalendarDays, DoorOpen, GraduationCap, Loader2 } from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";

export function ScheduleExamForm({ onSubmit, onCancel }) {
  const [rooms, setRooms] = useState([]);
  const [availablePrograms, setAvailablePrograms] = useState([]);
  const [isLoading, setIsLoading] = useState({
    rooms: false,
    programs: false,
    submitting: false,
  });
  const [formData, setFormData] = useState({
    date: "",
    programSemesters: [],
    rooms: [],
  });
  const [error, setError] = useState({
    rooms: null,
    programs: null,
  });
  const [bookedRooms, setBookedRooms] = useState([]);

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

  useEffect(() => {
    fetchRooms();
    fetchPrograms();
  }, []);

  useEffect(() => {
    if (!formData.date) return;

    async function fetchBookedRooms() {
      try {
        const res = await fetch(
          `http://localhost:8081/api/exams/booked-rooms?date=${formData.date}`
        );
        if (!res.ok) throw new Error("Failed to fetch booked rooms");
        const data = await res.json();
        setBookedRooms(data);
      } catch (err) {
        console.error(err);
        toast.error("Failed to fetch already booked rooms");
      }
    }

    fetchBookedRooms();
  }, [formData.date]);

  const getTodayDate = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  };

  async function fetchRooms() {
    setIsLoading((prev) => ({ ...prev, rooms: true }));
    setError((prev) => ({ ...prev, rooms: null }));
    try {
      const response = await fetch("http://localhost:8081/api/rooms");
      if (!response.ok)
        throw new Error(`Failed to fetch rooms: ${response.status}`);
      setRooms(await response.json());
    } catch (error) {
      setError((prev) => ({ ...prev, rooms: error.message }));
      toast.error("Failed to load rooms");
    } finally {
      setIsLoading((prev) => ({ ...prev, rooms: false }));
    }
  }

  async function fetchPrograms() {
    setIsLoading((prev) => ({ ...prev, programs: true }));
    setError((prev) => ({ ...prev, programs: null }));
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      if (!response.ok)
        throw new Error(`Failed to fetch programs: ${response.status}`);
      setAvailablePrograms(await response.json());
    } catch (error) {
      setError((prev) => ({ ...prev, programs: error.message }));
      toast.error("Failed to load programs");
    } finally {
      setIsLoading((prev) => ({ ...prev, programs: false }));
    }
  }

  const handleDateChange = (e) => {
    const selectedDate = e.target.value;
    const today = getTodayDate();
    if (selectedDate < today) return; // ignore past date
    setFormData({ ...formData, date: selectedDate });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading((prev) => ({ ...prev, submitting: true }));

    const today = getTodayDate();
    if (formData.date < today) {
      toast.error("Please select a future date");
      setIsLoading((prev) => ({ ...prev, submitting: false }));
      return;
    }

    if (formData.programSemesters.length === 0) {
      toast.error("Please select at least one program and semester");
      setIsLoading((prev) => ({ ...prev, submitting: false }));
      return;
    }

    if (formData.rooms.length === 0) {
      toast.error("Please select at least one room");
      setIsLoading((prev) => ({ ...prev, submitting: false }));
      return;
    }

    const payload = {
      date: formData.date,
      programSemesters: formData.programSemesters.map(
        ({ programCode, semester }) => ({
          programCode,
          semester: semesters.indexOf(semester) + 1,
        })
      ),
      roomNumbers: formData.rooms.map((r) => r.roomNo),
    };

    try {
      await onSubmit(payload);
    } finally {
      setIsLoading((prev) => ({ ...prev, submitting: false }));
    }
  };

  const selectedProgramsCount = new Set(
    formData.programSemesters.map((ps) => ps.programCode)
  ).size;
  const selectedRoomsCount = formData.rooms.length;

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-4 h-full">
      {/* Scrollable Content */}
      <div className="flex-1 overflow-y-auto space-y-4 pr-2">
        {/* Date Picker */}
        <div className="space-y-2">
          <Label htmlFor="date" className="flex items-center gap-2">
            <CalendarDays className="h-4 w-4" />
            Exam Date
          </Label>
          <Input
            type="date"
            id="date"
            name="date"
            value={formData.date}
            onChange={handleDateChange}
            required
            min={getTodayDate()}
          />
          <p className="text-sm text-muted-foreground">
            Please select a future date
          </p>
        </div>

        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label className="flex items-center gap-2">
              <GraduationCap className="h-4 w-4" />
              Programs
            </Label>
            {selectedProgramsCount > 0 && (
              <Badge variant="secondary">
                {selectedProgramsCount} selected
              </Badge>
            )}
          </div>

          {isLoading.programs ? (
            <div className="flex items-center justify-center p-4">
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Loading programs...
            </div>
          ) : error.programs ? (
            <div className="rounded-md border border-red-200 bg-red-50 p-3 text-red-600 text-sm">
              {error.programs}
              <Button
                variant="ghost"
                size="sm"
                onClick={fetchPrograms}
                className="mt-2 h-8"
              >
                Retry
              </Button>
            </div>
          ) : (
            <ScrollArea className="h-[200px] rounded-md border">
              <div className="p-3 space-y-4">
                {availablePrograms.map((program) => (
                  <div key={program.programCode} className="space-y-2">
                    <div className="flex items-center gap-2">
                      <h4 className="font-medium">{program.programName}</h4>
                      <Badge variant="outline">{program.programCode}</Badge>
                    </div>
                    <div className="grid grid-cols-2 gap-2">
                      {semesters.map((sem) => (
                        <Label
                          key={sem}
                          className="flex items-center gap-2 font-normal"
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
        </div>

        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <Label className="flex items-center gap-2">
              <DoorOpen className="h-4 w-4" />
              Rooms
            </Label>
            {selectedRoomsCount > 0 && (
              <Badge variant="secondary">{selectedRoomsCount} selected</Badge>
            )}
          </div>

          {isLoading.rooms ? (
            <div className="flex items-center justify-center p-4">
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Loading rooms...
            </div>
          ) : error.rooms ? (
            <div className="rounded-md border border-red-200 bg-red-50 p-3 text-red-600 text-sm">
              {error.rooms}
              <Button
                variant="ghost"
                size="sm"
                onClick={fetchRooms}
                className="mt-2 h-8"
              >
                Retry
              </Button>
            </div>
          ) : (
            <ScrollArea className="h-[180px] rounded-md border">
              <div className="p-3 space-y-2">
                {rooms.map((room) => {
                  const isBooked = bookedRooms.includes(room.roomNo); // check if room is already booked

                  return (
                    <Label
                      key={room.roomNo}
                      className={`flex items-center gap-3 p-2 hover:bg-accent rounded ${
                        isBooked ? "opacity-50 cursor-not-allowed" : ""
                      }`}
                    >
                      <Checkbox
                        checked={formData.rooms.some(
                          (r) => r.roomNo === room.roomNo
                        )}
                        disabled={isBooked} // disable checkbox if booked
                        onCheckedChange={(checked) => {
                          if (isBooked) return;
                          setFormData((prev) => ({
                            ...prev,
                            rooms: checked
                              ? [...prev.rooms, room]
                              : prev.rooms.filter(
                                  (r) => r.roomNo !== room.roomNo
                                ),
                          }));
                        }}
                      />
                      <div className="flex-1 flex justify-between items-center">
                        <span
                          title={isBooked ? "This room is already booked" : ""}
                        >
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
        </div>
      </div>

      <div className="sticky bottom-0 bg-background pt-4 pb-2 border-t">
        <div className="flex justify-end gap-2">
          <Button
            type="button"
            variant="outline"
            onClick={onCancel}
            disabled={isLoading.submitting}
          >
            Cancel
          </Button>
          <Button type="submit" disabled={isLoading.submitting}>
            {isLoading.submitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Scheduling...
              </>
            ) : (
              "Schedule Exam"
            )}
          </Button>
        </div>
      </div>
    </form>
  );
}
