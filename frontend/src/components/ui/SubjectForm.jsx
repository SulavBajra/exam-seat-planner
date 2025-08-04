import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";

export default function SubjectForm({ onSuccess }) {
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");

  const [formData, setFormData] = useState({
    programCode: "",
    semester: "",
    subjectCode: "",
    subjectName: "",
  });

  const [programs, setPrograms] = useState([]);

  useEffect(() => {
    fetchPrograms();
  }, []);

  async function fetchPrograms() {
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      if (!response.ok) throw new Error("Failed to fetch programs");
      const data = await response.json();
      setPrograms(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
      setMessage("Failed to load programs");
    } finally {
      setLoading(false);
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    // Clear error for this field when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({
        ...prev,
        [name]: "",
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    if (!formData.programCode) {
      newErrors.programCode = "Program is required";
    }
    if (!formData.semester) {
      newErrors.semester = "Semester is required";
    }
    if (!formData.subjectCode) {
      newErrors.subjectCode = "Subject code is required";
    } else if (isNaN(formData.subjectCode) || formData.subjectCode <= 0) {
      newErrors.subjectCode = "Subject code must be a positive number";
    }
    if (!formData.subjectName.trim()) {
      newErrors.subjectName = "Subject name is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const resetForm = () => {
    setFormData({
      programCode: "",
      semester: "",
      subjectCode: "",
      subjectName: "",
    });
    setErrors({});
    setMessage("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    setMessage("");

    const newSubject = {
      subjectCode: parseInt(formData.subjectCode),
      semester: parseInt(formData.semester),
      subjectName: formData.subjectName.trim(),
      programCode: parseInt(formData.programCode),
    };

    try {
      const response = await fetch("http://localhost:8081/api/subjects", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newSubject),
      });

      const responseData = await response.json();

      if (!response.ok) {
        if (responseData.errors) {
          setErrors(responseData.errors);
        } else {
          setMessage(responseData.message || "Failed to add subject");
        }
        return;
      }

      setMessage("Subject added successfully!");
      resetForm();

      // Call parent success callback if provided
      if (onSuccess) {
        onSuccess(responseData);
      }
    } catch (error) {
      console.error("Error adding subject:", error);
      setMessage("Network error. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div className="text-center">Loading programs...</div>;
  }

  return (
    <div>
      {message && (
        <div
          className={`p-2 mb-4 rounded ${
            message.includes("success")
              ? "bg-green-100 text-green-700 border border-green-300"
              : "bg-red-100 text-red-700 border border-red-300"
          }`}
        ></div>
      )}

      <form className="flex flex-col gap-4" onSubmit={handleSubmit}>
        <div className="flex flex-col gap-1">
          <label htmlFor="programCode" className="font-medium">
            Program *
          </label>
          <select
            id="programCode"
            name="programCode"
            className={`bg-black border rounded-md p-2 ${
              errors.programCode ? "border-red-500" : "border-gray-300"
            }`}
            value={formData.programCode}
            onChange={handleInputChange}
          >
            <option value="">Select a program</option>
            {programs.map((program) => (
              <option key={program.programCode} value={program.programCode}>
                {program.programCode} - {program.programName}
              </option>
            ))}
          </select>
          {errors.programCode && (
            <span className="text-red-500 text-sm">{errors.programCode}</span>
          )}
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="semester" className="font-medium">
            Semester *
          </label>
          <select
            id="semester"
            name="semester"
            className={`bg-black border rounded-md p-2 w-35 ${
              errors.semester ? "border-red-500" : "border-gray-300"
            }`}
            value={formData.semester}
            onChange={handleInputChange}
          >
            <option value="">Select semester</option>
            {[1, 2, 3, 4, 5, 6, 7, 8].map((sem) => (
              <option key={sem} value={sem}>
                {sem}
              </option>
            ))}
          </select>
          {errors.semester && (
            <span className="text-red-500 text-sm">{errors.semester}</span>
          )}
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="subjectCode" className="font-medium">
            Subject Code *
          </label>
          <input
            type="number"
            id="subjectCode"
            name="subjectCode"
            className={`bg-black border rounded-md p-2 ${
              errors.subjectCode ? "border-red-500" : "border-gray-300"
            }`}
            value={formData.subjectCode}
            onChange={handleInputChange}
            placeholder="Enter subject code"
          />
          {errors.subjectCode && (
            <span className="text-red-500 text-sm">{errors.subjectCode}</span>
          )}
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="subjectName" className="font-medium">
            Subject Name *
          </label>
          <input
            type="text"
            id="subjectName"
            name="subjectName"
            className={`bg-black border rounded-md p-2 ${
              errors.subjectName ? "border-red-500" : "border-gray-300"
            }`}
            value={formData.subjectName}
            onChange={handleInputChange}
            placeholder="Enter subject name"
          />
          {errors.subjectName && (
            <span className="text-red-500 text-sm">{errors.subjectName}</span>
          )}
        </div>

        <div className="flex gap-2">
          <Button
            type="submit"
            disabled={submitting}
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md disabled:opacity-50"
          >
            {submitting ? "Adding..." : "Add Subject"}
          </Button>

          <Button
            type="button"
            variant="outline"
            onClick={resetForm}
            disabled={submitting}
            className="px-4 py-2 rounded-md"
          >
            Reset
          </Button>
        </div>
      </form>
    </div>
  );
}
