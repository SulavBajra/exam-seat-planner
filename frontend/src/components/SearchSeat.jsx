import React, { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Loader2, Search } from "lucide-react";

export default function SearchSeat() {
  const [availablePrograms, setAvailablePrograms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchLoading, setSearchLoading] = useState(false);
  const [selectedProgram, setSelectedProgram] = useState("");
  const [selectedSemester, setSelectedSemester] = useState("");
  const [rollNumber, setRoll] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [seat, setSeat] = useState(null);
  const [error, setError] = useState("");

  const getTodayDate = () => new Date().toISOString().split("T")[0];


  const semesters = [
    "FIRST", "SECOND", "THIRD", "FOURTH",
    "FIFTH", "SIXTH", "SEVENTH", "EIGHTH"
  ];

  useEffect(() => {
    fetchProgramCode();
  }, []);

  async function fetchProgramCode() {
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      if (!response.ok) throw new Error("Error fetching program name");
      setAvailablePrograms(await response.json());
    } catch (err) {
      console.error(err);
      setError("Failed to load programs");
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(e) {
    e.preventDefault();

    if (!selectedProgram || !selectedSemester || !rollNumber  || !startDate || !endDate) {
      setError("Please fill out all fields");
      return;
    }

    setError("");
    setSearchLoading(true);

    try {
      const response = await fetch(
        `http://localhost:8081/api/seating/search?startDate=${startDate}&endDate=${endDate}&programCode=${selectedProgram}&semester=${selectedSemester}&roll=${rollNumber}`
      );
      if (!response.ok) throw new Error("Student seat not found");
      setSeat(await response.json());
    } catch (err) {
      console.error(err);
      setError(err.message || "Failed to find seat information");
      setSeat(null);
    } finally {
      setSearchLoading(false);
    }
  }

  const clearForm = () => {
    setSelectedProgram("");
    setSelectedSemester("");
    setRoll("");
    setStartDate("");
    setEndDate("");
    setSeat(null);
    setError("");
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <Card className="w-full max-w-lg border border-gray-200 shadow-sm">
        <CardHeader>
          <CardTitle className="text-center text-lg font-semibold text-gray-700">
            Search Examination Seat
          </CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSearch} className="space-y-5">
            {/* Program */}
            <div className="space-y-2">
              <Label>Program</Label>
             <Select value={selectedProgram} onValueChange={setSelectedProgram}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select your program">
                    {selectedProgram
                      ? availablePrograms.find(p => p.programCode.toString() === selectedProgram)?.programName
                      : ""}
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    <SelectLabel>Programs</SelectLabel>
                    {loading ? (
                      <div className="flex items-center justify-center py-3 text-sm text-gray-500">
                        <Loader2 className="h-4 w-4 animate-spin mr-2" />
                        Loading...
                      </div>
                    ) : (
                      availablePrograms.map((pro) => (
                        <SelectItem key={pro.programCode} value={pro.programCode.toString()}>
                          {pro.programName}
                        </SelectItem>
                      ))
                    )}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>

            {/* Semester */}
            <div className="space-y-2">
              <Label>Semester</Label>
              <Select value={selectedSemester} onValueChange={setSelectedSemester}>
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select semester" />
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    {semesters.map((sem, idx) => (
                      <SelectItem key={sem} value={(idx + 1).toString()}>
                        {sem}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>

            {/* Roll Number */}
            <div className="space-y-2">
              <Label>Roll Number</Label>
              <Input
                type="number"
                placeholder="Enter your roll number"
                min = "1"
                value={rollNumber}
                onChange={(e) => setRoll(e.target.value)}
              />
            </div>

            {/* Exam ID */}
            <div className="flex flex-col">
             <Label htmlFor="startDate" className="font-medium text-sm">
              Start Date
            </Label>
            <Input
              type="date"
              id="startDate"
              name="startDate"
              value={startDate}
              onChange={(e) =>
                setStartDate(e.target.value )
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
              value={endDate}
              onChange={(e) =>
                setEndDate(e.target.value)
              }
              required
              min={startDate || getTodayDate()}
              className="mt-1 border rounded-md px-3 py-2 focus:ring-2 focus:ring-primary focus:border-primary"
            />
          </div>

            {error && (
              <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-md p-2">
                {error}
              </p>
            )}

            <div className="flex gap-3 pt-2">
              <Button
                type="submit"
                disabled={searchLoading}
                className="flex-1 bg-gray-700 hover:bg-gray-800 text-white"
              >
                {searchLoading ? (
                  <>
                    <Loader2 className="h-4 w-4 animate-spin mr-2" />
                    Searching...
                  </>
                ) : (
                  <>
                    <Search className="h-4 w-4 mr-2" />
                    Search
                  </>
                )}
              </Button>
              <Button type="button" variant="outline" onClick={clearForm} disabled={searchLoading}>
                Clear
              </Button>
            </div>
          </form>

          {/* Seat Info */}
          {seat && (
            <div className="mt-6 border-t pt-4 text-sm text-gray-700">
              <h2 className="font-semibold text-center mb-3">Seat Details Found</h2>
              <p><span className="font-medium">Student is in Room:</span>{seat.roomNo}</p>
              <p><span className="font-medium">Student is in Row number</span>{seat.rowNumber}</p>
              <p><span className="font-medium">Student is in Column number</span>{seat.columnNumber}</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
