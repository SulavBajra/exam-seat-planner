import React, { useState } from "react";

export function ProgramForm({ onSuccess }) {
  const [programCode, setProgramCode] = useState("");
  const [programName, setProgramName] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8081/api/programs/save", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          programCode: programCode.trim(),
          programName: programName.trim(),
        }),
      });

      if (!response.ok) {
        const errData = await response.json();
        throw new Error(errData.message || "Failed to add program");
      }

      const newProgram = await response.json();

      if (onSuccess) {
        onSuccess(newProgram);
      }

      setProgramCode("");
      setProgramName("");
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-2 mt-4">
      <label htmlFor="programCode">Program Code</label>
      <input
        type="text"
        id="programCode"
        name="programCode"
        value={programCode}
        onChange={(e) => setProgramCode(e.target.value)}
        required
        className="border p-1"
      />

      <label htmlFor="programName">Program Name</label>
      <input
        type="text"
        id="programName"
        name="programName"
        value={programName}
        onChange={(e) => setProgramName(e.target.value)}
        required
        className="border p-1"
      />

      {error && <p className="text-red-500">{error}</p>}

      <button
        type="submit"
        disabled={submitting}
        className="bg-indigo-600 text-white mt-2 py-1 px-3 rounded disabled:opacity-50"
      >
        {submitting ? "Adding..." : "Add Program"}
      </button>
    </form>
  );
}
