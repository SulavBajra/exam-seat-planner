import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";

export function ScheduleExamForm({ onSubmit, onCancel }) {
  const [rooms, setRooms] = useState([]);
  const [availablePrograms, setAvailablePrograms] = useState([]);
  const [isLoading, setIsLoading] = useState({
    rooms: false,
    programs: false,
  });
  const [exams, setExams] = useState([
    { date: "", programSemesters: [], rooms: [] },
  ]);

  const [error, setError] = useState({
    rooms: null,
    programs: null,
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

  const semesterMap = {
    FIRST: 1,
    SECOND: 2,
    THIRD: 3,
    FOURTH: 4,
    FIFTH: 5,
    SIXTH: 6,
    SEVENTH: 7,
    EIGHTH: 8,
  };

  useEffect(() => {
    fetchRooms();
    fetchPrograms();
  }, []); // Empty dependency array to run only once

  async function fetchRooms() {
    setIsLoading((prev) => ({ ...prev, rooms: true }));
    setError((prev) => ({ ...prev, rooms: null }));

    try {
      const response = await fetch("http://localhost:8081/api/rooms");
      if (!response.ok) {
        throw new Error(`Failed to fetch rooms: ${response.status}`);
      }
      const data = await response.json();
      setRooms(data);
    } catch (error) {
      console.error(error);
      setError((prev) => ({ ...prev, rooms: error.message }));
      toast({
        title: "Error",
        description: "Failed to load rooms",
        variant: "destructive",
      });
    } finally {
      setIsLoading((prev) => ({ ...prev, rooms: false }));
    }
  }

  async function fetchPrograms() {
    setIsLoading((prev) => ({ ...prev, programs: true }));
    setError((prev) => ({ ...prev, programs: null }));

    try {
      const response = await fetch("http://localhost:8081/api/programs");
      if (!response.ok) {
        throw new Error(`Failed to fetch programs: ${response.status}`);
      }
      const data = await response.json();
      setAvailablePrograms(data);
    } catch (error) {
      console.error(error);
      setError((prev) => ({ ...prev, programs: error.message }));
      toast({
        title: "Error",
        description: "Failed to load programs",
        variant: "destructive",
      });
    } finally {
      setIsLoading((prev) => ({ ...prev, programs: false }));
    }
  }

  const [formData, setFormData] = useState({
    date: "",
    programSemesters: [],
    rooms: [],
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleProgramSemesterChange = (programCode, semester, isChecked) => {
    setFormData((prev) => {
      const newProgramSemesters = isChecked
        ? [...prev.programSemesters, { programCode, semester }]
        : prev.programSemesters.filter(
            (ps) =>
              !(ps.programCode === programCode && ps.semester === semester)
          );

      return { ...prev, programSemesters: newProgramSemesters };
    });
  };

  const handleRoomChange = (room, isChecked) => {
    setFormData((prev) => {
      const newRooms = isChecked
        ? [...prev.rooms, room]
        : prev.rooms.filter((r) => r.roomNo !== room.roomNo);

      return { ...prev, rooms: newRooms };
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (formData.programSemesters.length === 0) {
      toast({
        title: "Validation Error",
        description: "Please select at least one program and semester",
        variant: "destructive",
      });
      return;
    }

    if (formData.rooms.length === 0) {
      toast({
        title: "Validation Error",
        description: "Please select at least one room",
        variant: "destructive",
      });
      return;
    }

    const payload = {
      date: formData.date,
      programSemesters: formData.programSemesters.map(
        ({ programCode, semester }) => ({
          programCode,
          semester: semesterMap[semester],
        })
      ),
      roomNumbers: formData.rooms.map((r) => r.roomNo),
    };

    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="date">Exam Date</Label>
        <Input
          type="date"
          id="date"
          name="date"
          value={formData.date}
          onChange={handleInputChange}
          required
          aria-required="true"
          min={new Date().toISOString().split("T")[0]} // Prevent past dates
        />
      </div>

      <div>
        <Label>Programs & Semesters</Label>
        {isLoading.programs ? (
          <div className="p-4 text-center">Loading programs...</div>
        ) : error.programs ? (
          <div className="p-4 text-center text-red-500">{error.programs}</div>
        ) : (
          <div className="space-y-4 max-h-60 overflow-auto border p-2 rounded">
            {availablePrograms.map((program) => (
              <div key={program.programCode}>
                <div className="font-semibold">{program.programName}</div>
                <div className="flex flex-wrap gap-2 mt-1">
                  {semesters.map((sem) => {
                    const id = `ps-${program.programCode}-${sem}`;
                    const checked = formData.programSemesters.some(
                      (ps) =>
                        ps.programCode === program.programCode &&
                        ps.semester === sem
                    );

                    return (
                      <div key={sem} className="flex items-center space-x-1">
                        <Checkbox
                          id={id}
                          checked={checked}
                          onCheckedChange={(checked) =>
                            handleProgramSemesterChange(
                              program.programCode,
                              sem,
                              checked
                            )
                          }
                        />
                        <Label htmlFor={id}>{sem}</Label>
                      </div>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="space-y-2">
        <Label>Rooms</Label>
        {isLoading.rooms ? (
          <div className="p-4 text-center">Loading rooms...</div>
        ) : error.rooms ? (
          <div className="p-4 text-center text-red-500">{error.rooms}</div>
        ) : (
          <div className="space-y-2 max-h-40 overflow-auto border p-2 rounded">
            {rooms.map((room) => {
              const id = `room-${room.roomNo}`;
              const checked = formData.rooms.some(
                (r) => r.roomNo === room.roomNo
              );

              return (
                <div key={room.roomNo} className="flex items-center space-x-2">
                  <Checkbox
                    id={id}
                    checked={checked}
                    onCheckedChange={(checked) =>
                      handleRoomChange(room, checked)
                    }
                  />
                  <Label htmlFor={id}>
                    Room {room.roomNo} (Capacity: {room.seatingCapacity})
                  </Label>
                </div>
              );
            })}
          </div>
        )}
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          disabled={isLoading.rooms || isLoading.programs}
        >
          Cancel
        </Button>
        <Button type="submit" disabled={isLoading.rooms || isLoading.programs}>
          {isLoading.rooms || isLoading.programs
            ? "Loading..."
            : "Schedule Exam"}
        </Button>
      </div>
    </form>
  );
}
