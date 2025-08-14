import React, { useEffect, useState } from "react";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { toast } from "sonner";
import { RefreshCw, Download } from "lucide-react";
import { useNavigate } from "react-router-dom";
import * as XLSX from "xlsx";

export default function StudentList() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [isClearing, setIsClearing] = useState(false);
  const navigate = useNavigate();

  const fetchStudents = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/students`);
      if (!response.ok) {
        throw new Error(`Failed to load students. Status: ${response.status}`);
      }
      const data = await response.json();
      setStudents(data);
    } catch (error) {
      console.error("Error fetching students:", error);
      setError(error.message);
      toast.error(`Error fetching students: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  const handleClear = async () => {
    setIsClearing(true);
    try {
      const response = await fetch(`http://localhost:8081/api/students/clear`, {
        method: "DELETE",
      });
      if (!response.ok) {
        throw new Error(`Failed to clear students: ${response.status}`);
      }
      toast({
        title: "Success",
        description: "All student data has been cleared",
      });
      fetchStudents(); // Refresh the list after clearing
    } catch (error) {
      console.error("Error clearing Student Data: ", error);
      toast.error(`Error clearing data: ${error.message}`);
    } finally {
      setIsClearing(false);
      setIsConfirmOpen(false);
    }
  };

  const handleExportExcel = () => {
    if (students.length === 0) {
      toast.warning("No data to export");
      return;
    }

    try {
      // Prepare the worksheet
      const worksheet = XLSX.utils.json_to_sheet(
        students.map((student) => ({
          "Program Name": student.programName,
          "Student ID": student.studentId,
          Semester: student.semester,
          Roll: student.roll,
        }))
      );

      // Create a new workbook
      const workbook = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(workbook, worksheet, "Students");

      // Generate the Excel file
      XLSX.writeFile(workbook, "students_data.xlsx", { compression: true });

      toast.success("Excel file downloaded successfully");
    } catch (error) {
      console.error("Error exporting to Excel:", error);
      toast({
        variant: "destructive",
        title: "Error exporting data",
        description: error.message,
      });
    }
  };

  if (loading && !isClearing) {
    return (
      <Card className="w-full max-w-4xl mx-auto mt-8">
        <CardHeader>
          <CardTitle>Loading Students...</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {[...Array(5)].map((_, i) => (
              <Skeleton key={i} className="h-12 w-full" />
            ))}
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className="w-full max-w-4xl mx-auto mt-8">
        <CardHeader>
          <CardTitle>Error</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-red-500">{error}</p>
          <Button variant="outline" className="mt-4" onClick={fetchStudents}>
            <RefreshCw className="mr-2 h-4 w-4" />
            Retry
          </Button>
        </CardContent>
      </Card>
    );
  }

  return (
    <>
      <Card className="w-full max-w-4xl mx-auto mt-8">
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>Student List</CardTitle>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={fetchStudents}
              disabled={loading}
            >
              <RefreshCw
                className={`mr-2 h-4 w-4 ${loading ? "animate-spin" : ""}`}
              />
              Refresh
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={handleExportExcel}
              disabled={loading || students.length === 0}
            >
              <Download className="mr-2 h-4 w-4" />
              Export Excel
            </Button>
            <Button
              variant="destructive"
              size="sm"
              onClick={() => setIsConfirmOpen(true)}
              disabled={isClearing}
            >
              {isClearing ? "Clearing..." : "Clear Data"}
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Program Name</TableHead>
                <TableHead>Student Id</TableHead>
                <TableHead>Semester</TableHead>
                <TableHead>Roll</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {students.length > 0 ? (
                students.map((student) => (
                  <TableRow key={student.id}>
                    <TableCell className="font-medium">
                      {student.programName}
                    </TableCell>
                    <TableCell>{student.studentId}</TableCell>
                    <TableCell>{student.semester}</TableCell>
                    <TableCell>{student.roll}</TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={4} className="text-center h-24">
                    No students found
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      <AlertDialog open={isConfirmOpen} onOpenChange={setIsConfirmOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete all
              student data.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleClear}
              disabled={isClearing}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {isClearing ? "Clearing..." : "Clear Data"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}
