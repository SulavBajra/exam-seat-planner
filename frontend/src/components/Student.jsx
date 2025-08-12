import React, { useState, useRef } from "react";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import { Button } from "@/components/ui/button";

export default function Student() {
  const fileInputRef = useRef(null);
  const [message, setMessage] = useState({ text: "", type: "" });
  const [isUploading, setIsUploading] = useState(false);
  const [validationResults, setValidationResults] = useState([]);
  const [fileInfo, setFileInfo] = useState(null);

  // Helper function to validate numeric range
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

    for (const [index, student] of jsonData.entries()) {
      const validation = validateStudentData(student);
      const programCode = validation.isValid
        ? await getProgramCode(student.Program)
        : null;

      const result = {
        ...student,
        rowNumber: index + 2, // +2 for header row and 1-based index
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

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(
          errorData.message || `Server error: ${response.status}`
        );
      }

      setMessage({
        text: `Successfully uploaded ${uploadData.length} students!`,
        type: "success",
      });
    } catch (error) {
      console.error("Upload error:", error);
      setMessage({
        text: error.message || "Failed to process file",
        type: "error",
      });
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <div className="flex flex-col gap-4 p-4 max-w-5xl mx-auto">
      <h2 className="text-xl font-bold mb-4">Student Data Management</h2>

      {fileInfo && (
        <div className="p-3 bg-blue-50 rounded-md">
          <p>
            <strong>File:</strong> {fileInfo.name}
          </p>
          <p>
            <strong>Size:</strong> {fileInfo.size}
          </p>
          <p>
            <strong>Last Modified:</strong> {fileInfo.lastModified}
          </p>
        </div>
      )}

      <div className="flex gap-4">
        <Button
          variant="outline"
          onClick={handleDownloadTemplate}
          disabled={isUploading}
        >
          Download Excel Template
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
          variant="outline"
          onClick={() => fileInputRef.current?.click()}
          disabled={isUploading}
        >
          {isUploading ? "Uploading..." : "Upload Excel"}
        </Button>
      </div>

      {message.text && (
        <div
          className={`p-3 rounded-md ${
            message.type === "success"
              ? "bg-green-100 text-green-800"
              : "bg-red-100 text-red-800"
          }`}
        >
          {message.text}
        </div>
      )}

      {validationResults.length > 0 && (
        <div className="mt-4 p-4 bg-gray-50 rounded-md">
          <h3 className="font-medium mb-2">
            Validation Results (
            {validationResults.filter((r) => r.isValid).length} valid /{" "}
            {validationResults.length} total)
          </h3>
          <div className="max-h-96 overflow-y-auto">
            <table className="w-full border-collapse">
              <thead>
                <tr className="bg-gray-200">
                  <th className="p-2 border text-left">Row</th>
                  <th className="p-2 border text-left">Program</th>
                  <th className="p-2 border text-left">Semester</th>
                  <th className="p-2 border text-left">Roll</th>
                  <th className="p-2 border text-left">Status</th>
                  <th className="p-2 border text-left">Errors</th>
                </tr>
              </thead>
              <tbody>
                {validationResults.map((result, index) => (
                  <tr
                    key={index}
                    className={`${
                      result.isValid ? "bg-white" : "bg-red-50"
                    } hover:bg-gray-100`}
                  >
                    <td className="p-2 border">{result.rowNumber}</td>
                    <td className="p-2 border">
                      {result.Program}
                      {result.programCode && (
                        <div className="text-xs text-gray-500">
                          Code: {result.programCode}
                        </div>
                      )}
                    </td>
                    <td className="p-2 border">{result.Semester}</td>
                    <td className="p-2 border">{result.Roll}</td>
                    <td className="p-2 border">
                      {result.isValid ? (
                        <span className="text-green-600">✓ Valid</span>
                      ) : (
                        <span className="text-red-600">✗ Invalid</span>
                      )}
                    </td>
                    <td className="p-2 border text-red-600">
                      {result.errors?.join(", ")}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      <div className="mt-6 p-4 bg-gray-50 rounded-md">
        <h3 className="font-medium mb-2">Expected Data Format:</h3>
        <ul className="list-disc pl-5 space-y-1">
          <li>
            <strong>Program:</strong> Full program name (must exist in system)
          </li>
          <li>
            <strong>Semester:</strong> Number between 1-8 (inclusive)
          </li>
          <li>
            <strong>Roll:</strong> Positive integer
          </li>
        </ul>
      </div>
    </div>
  );
}
