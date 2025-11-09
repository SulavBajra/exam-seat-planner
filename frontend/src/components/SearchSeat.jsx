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
  const [examId, setExamId] = useState("");
  const [seat, setSeat] = useState([]);
  const [error, setError] = useState("");

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
    fetchProgramCode();
  }, []);

  async function fetchProgramCode() {
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      if (!response.ok) throw new Error("Error fetching program name");
      setAvailablePrograms(await response.json());
    } catch (error) {
      console.error("Cannot fetch program name", error);
      setError("Failed to load programs");
    } finally {
      setLoading(false);
    }
  }

  async function handleSearch(e) {
    e.preventDefault();

    if (!selectedProgram || !selectedSemester || !rollNumber || !examId) {
      setError("Please fill out all fields");
      return;
    }

    setError("");
    setSearchLoading(true);

    try {
   const response = await fetch(`http://localhost:8081/api/seating/search?examId=${examId}&programCode=${selectedProgram}&semester=${selectedSemester}&roll=${rollNumber}`);
      if (!response.ok) throw new Error("Student not found");
      setSeat(await response.json());
    } catch (error) {
      console.error("Error searching seat:", error);
      setError(error.message || "Failed to find seat information");
      setSeat([]);
    } finally {
      setSearchLoading(false);
    }
  }

  const clearForm = () => {
    setSelectedProgram("");
    setSelectedSemester("");
    setRoll("");
    setExamId("");
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
              <Select
                value={selectedProgram}
                onValueChange={setSelectedProgram}
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Select your program" />
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
                        <SelectItem
                          key={pro.programCode}
                          value={pro.programCode}
                        >
                          {pro.programName}
                        </SelectItem>
                      ))
                    )}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>Semester</Label>
              <Select
                value={selectedSemester}
                onValueChange={setSelectedSemester}
              >
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

            <div className="space-y-2">
              <Label>Roll Number</Label>
              <Input
                type="number"
                placeholder="Enter your roll number"
                value={rollNumber}
                onChange={(e) => setRoll(e.target.value)}
              />
            </div>

            <div className="space-y-2">
              <Label>Exam ID</Label>
              <Input
                type="number"
                placeholder="Enter exam ID"
                value={examId}
                onChange={(e) => setExamId(e.target.value)}
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
              <Button
                type="button"
                variant="outline"
                onClick={clearForm}
                disabled={searchLoading}
              >
                Clear
              </Button>
            </div>
          </form>

          {/* Seat Info */}
            {seat && seat.seats && (
            <div className="mt-6 border-t pt-4 text-sm text-gray-700">
                <h2 className="font-semibold text-center mb-3">
                Seat Details Found
                </h2>
                <p className="mb-2">
                <span className="font-medium">Room:</span> {seat.roomNo}
                </p>

                <div className="grid gap-2">
                {seat.seats.map((row, rowIndex) => (
                    <div key={rowIndex} className="flex gap-2">
                    {row.map((seatObj, colIndex) => (
                        <div
                        key={colIndex}
                        className="border border-gray-300 rounded p-2 text-center w-16"
                        >
                        <p className="text-xs font-medium">
                            {seatObj.programCode}
                        </p>
                        <p className="text-xs">
                            Sem: {semesters[seatObj.semester - 1]}
                        </p>
                        <p className="text-xs">Roll: {seatObj.roll}</p>
                        <p className="text-xs">Column{seatObj.row}</p>
                        <p className="text-xs">Row{seatObj.column}</p>
                        </div>
                    ))}
                    </div>
                ))}
                </div>
            </div>
            )}

        </CardContent>
      </Card>
    </div>
  );
}
