import React, { useState, useRef } from "react";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import { Download, Upload, Check, X, FileText, Users } from "lucide-react";
import { Progress } from "@/components/ui/progress";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

export default function Student() {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);
  const [message, setMessage] = useState({ text: "", type: "" });
  const [isUploading, setIsUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [validationResults, setValidationResults] = useState([]);
  const [fileInfo, setFileInfo] = useState(null);

  const validateRange = (value, min, max) => {
    const num = Number(value);
    return !isNaN(num) && num >= min && num <= max;
  };

  const handleDownloadTemplate = () => {
    const wsData = [
      ["Program", "Semester", "Roll"],
      ["Bachelor in Information Management", "1", "1"],
      ["Bachelor in Business Administration", "2", "2"],
      ["Bachelor in Computer Science", "3", "3"],
    ];

    const ws = XLSX.utils.aoa_to_sheet(wsData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Students");

    const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
    const file = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(file, "Student_Template.xlsx");
  };

  const validateStudentData = (student) => {
    const errors = [];

    if (
      !student.Program ||
      typeof student.Program !== "string" ||
      student.Program.trim() === ""
    ) {
      errors.push("Program name is required");
    }

    if (!validateRange(student.Semester, 1, 8)) {
      errors.push("Semester must be between 1-8");
    }

    if (!validateRange(student.Roll, 1, Infinity)) {
      errors.push("Roll must be a positive number");
    }

    return {
      isValid: errors.length === 0,
      errors,
    };
  };

  const getProgramCode = async (programName) => {
    try {
      const response = await fetch(
        `http://localhost:8081/api/programs/search/code?name=${encodeURIComponent(
          programName
        )}`
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const programCodes = await response.json();
      return programCodes.length > 0 ? programCodes[0] : null;
    } catch (error) {
      console.error("Error fetching program code:", error);
      return null;
    }
  };

  const processExcelData = async (jsonData) => {
    const processedData = [];
    let hasErrors = false;
    const totalRecords = jsonData.length;

    for (const [index, student] of jsonData.entries()) {
      setUploadProgress(Math.floor((index / totalRecords) * 100));

      const validation = validateStudentData(student);
      const programCode = validation.isValid
        ? await getProgramCode(student.Program)
        : null;

      const result = {
        ...student,
        rowNumber: index + 2,
        programCode,
        isValid: validation.isValid && programCode !== null,
        errors: validation.errors,
      };

      if (!result.isValid) hasErrors = true;
      processedData.push(result);
    }

    return { processedData, hasErrors };
  };

  const handleUploadExcel = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setIsUploading(true);
    setUploadProgress(0);
    setMessage({ text: "", type: "" });
    setValidationResults([]);
    setFileInfo({
      name: file.name,
      size: (file.size / 1024).toFixed(2) + " KB",
      lastModified: new Date(file.lastModified).toLocaleString(),
    });

    try {
      const data = await new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (evt) => resolve(new Uint8Array(evt.target.result));
        reader.onerror = reject;
        reader.readAsArrayBuffer(file);
      });

      const workbook = XLSX.read(data, { type: "array" });
      const sheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[sheetName];
      const jsonData = XLSX.utils.sheet_to_json(worksheet);

      const { processedData, hasErrors } = await processExcelData(jsonData);
      setValidationResults(processedData);

      if (hasErrors) {
        throw new Error(
          "Some records contain errors. Please fix them before uploading."
        );
      }

      const uploadData = processedData.map((student) => ({
        programCode: student.programCode,
        semester: Number(student.Semester),
        roll: Number(student.Roll),
      }));

      const response = await fetch("http://localhost:8081/api/students/bulk", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(uploadData),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || `Server error: ${response.status}`);
      }

      setValidationResults((prev) =>
        prev.map((item) => {
          const match = result.details?.find(
            (r) =>
              r.programCode === item.programCode &&
              r.semester === Number(item.Semester) &&
              r.roll === Number(item.Roll)
          );
          return match
            ? {
                ...item,
                backendStatus: match.status,
                backendMessage: match.message,
              }
            : item;
        })
      );

      const successCount = result.summary?.success || 0;
      const errorCount = result.summary?.error || 0;

      setMessage({
        text: `${successCount} students added, ${errorCount} failed.`,
        type: "success",
      });

      toast.success(
        `${successCount} students added, ${errorCount} failed to upload`,{
        style:{
          border: "1px solid green",
          color: "green"
        }
      }
      );
    } catch (error) {
      console.error("Upload error:", error);
      setMessage({
        text: error.message || "Failed to process file",
        type: "error",
      });
      toast.error(error.message || "Failed to process file",{
        style:{
          border: "1px solid red",
          color: "red"
        }
      });
    } finally {
      setIsUploading(false);
      setUploadProgress(100);
      setTimeout(() => setUploadProgress(0), 2000);
    }
  };

  const validCount = validationResults.filter((r) => r.isValid).length;
  const invalidCount = validationResults.length - validCount;

  return (
    <div className="flex flex-col gap-4 p-4 max-w-7xl mx-auto h-full">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold tracking-tight flex items-center gap-2">
            <Users className="h-6 w-6" />
            Student Management
          </h1>
          <p className="text-muted-foreground">
            Upload and manage student records in bulk
          </p>
        </div>
        <Button
          variant="outline"
          className="gap-2 bg-black text-white"
          onClick={() => navigate("/studentList")}
        >
          View Student List
        </Button>
      </div>

      <div className="grid gap-4 flex-1 overflow-hidden">
        {/* Upload Card */}
        <Card className="flex-1">
          <CardHeader>
            <CardTitle>Upload Student Data</CardTitle>
            <CardDescription>
              Upload an Excel file with student records
            </CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4">
            {fileInfo && (
              <div className="flex items-center gap-3 p-3 border rounded-lg">
                <FileText className="h-5 w-5 text-blue-500" />
                <div>
                  <p className="font-medium">{fileInfo.name}</p>
                  <p className="text-sm text-muted-foreground">
                    {fileInfo.size} • {fileInfo.lastModified}
                  </p>
                </div>
              </div>
            )}

            <div className="flex flex-wrap gap-2">
              <Button
                variant="outline"
                onClick={handleDownloadTemplate}
                disabled={isUploading}
                className="gap-2"
              >
                <Download className="h-4 w-4" />
                Download Template
              </Button>

              <input
                ref={fileInputRef}
                type="file"
                accept=".xlsx,.xls"
                onChange={handleUploadExcel}
                className="hidden"
                disabled={isUploading}
              />
              <Button
                onClick={() => fileInputRef.current?.click()}
                disabled={isUploading}
                className="gap-2"
              >
                <Upload className="h-4 w-4" />
                {isUploading ? "Uploading..." : "Upload Excel"}
              </Button>
            </div>

            {isUploading && (
              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span>Processing file...</span>
                  <span>{uploadProgress}%</span>
                </div>
                <Progress value={uploadProgress} className="h-2" />
              </div>
            )}
          </CardContent>
        </Card>

        {/* Messages */}
        {message.text && (
          <Alert
            variant={message.type === "success" ? "default" : "destructive"}
          >
            {message.type === "success" ? (
              <Check className="h-4 w-4" />
            ) : (
              <X className="h-4 w-4" />
            )}
            <AlertDescription>{message.text}</AlertDescription>
          </Alert>
        )}

        {/* Validation Results */}
        {validationResults.length > 0 && (
          <Card className="flex-1 overflow-hidden">
            <CardHeader>
              <div className="flex justify-between items-center">
                <CardTitle>Validation Results</CardTitle>
                <div className="flex gap-2">
                  <Badge variant="success" className="gap-1">
                    <Check className="h-3 w-3" /> {validCount} Valid
                  </Badge>
                  {invalidCount > 0 && (
                    <Badge variant="destructive" className="gap-1">
                      <X className="h-3 w-3" /> {invalidCount} Invalid
                    </Badge>
                  )}
                  {validationResults.some((r) => r.backendStatus)}
                </div>
              </div>
            </CardHeader>
            <CardContent className="p-0">
              <div className="overflow-auto max-h-[400px]">
                <Table>
                  <TableHeader className="sticky top-0 bg-background">
                    <TableRow>
                      <TableHead className="w-[80px]">Row</TableHead>
                      <TableHead>Program</TableHead>
                      <TableHead>Semester</TableHead>
                      <TableHead>Roll</TableHead>
                      <TableHead>Status</TableHead>
                      <TableHead>Error</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {validationResults.map((result, index) => (
                      <TableRow
                        key={index}
                        className={
                          !result.isValid ? "bg-red-50 hover:bg-red-100" : ""
                        }
                      >
                        <TableCell className="font-medium">
                          {result.rowNumber}
                        </TableCell>
                        <TableCell>
                          <div className="font-medium">{result.Program}</div>
                          {result.programCode && (
                            <div className="text-xs text-muted-foreground">
                              Code: {result.programCode}
                            </div>
                          )}
                        </TableCell>
                        <TableCell>{result.Semester}</TableCell>
                        <TableCell>{result.Roll}</TableCell>
                        <TableCell>
                          {result.isValid ? (
                            <Badge variant="success" className="gap-1">
                              <Check className="h-3 w-3" /> Valid
                            </Badge>
                          ) : (
                            <Badge variant="destructive" className="gap-1">
                              <X className="h-3 w-3" /> Invalid
                            </Badge>
                          )}
                        </TableCell>
                        {/* <TableCell className="text-red-600">
                          {result.errors?.join(", ")}
                        </TableCell> */}
                        <TableCell>
                          {result.backendStatus ? (
                            result.backendStatus === "success" ? (
                              <Badge variant="success" className="gap-1">
                                <Check className="h-3 w-3" />{" "}
                                {result.backendMessage}
                              </Badge>
                            ) : (
                              <Badge variant="destructive" className="gap-1">
                                <X className="h-3 w-3" />{" "}
                                {result.backendMessage}
                              </Badge>
                            )
                          ) : (
                            <Badge variant="outline">Pending</Badge>
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Requirements Card */}
        <Card>
          <CardHeader>
            <CardTitle>Data Format Requirements</CardTitle>
          </CardHeader>
          <CardContent className="grid gap-2">
            <div className="flex items-start gap-3">
              <div className="mt-1 w-2 h-2 rounded-full bg-primary" />
              <div>
                <span className="font-medium">Program:</span> Full program name
                (must exist in system, check program page for valid names)
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="mt-1 w-2 h-2 rounded-full bg-primary" />
              <div>
                <span className="font-medium">Semester:</span> Number between
                1-8 (inclusive)
              </div>
            </div>
            <div className="flex items-start gap-3">
              <div className="mt-1 w-2 h-2 rounded-full bg-primary" />
              <div>
                <span className="font-medium">Roll:</span> Positive integer
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
