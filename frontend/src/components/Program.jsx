import React, { useEffect, useState } from "react";

export default function Program() {
  const [programs, setPrograms] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPrograms();
  }, []);

  async function fetchPrograms() {
    try {
      const response = await fetch("http://localhost:8081/api/programs");
      const data = await response.json();
      setPrograms(data);
    } catch (error) {
      console.error("Error fetching programs:", error);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <h2 className="text-xl font-bold mb-4">Total Programs Available</h2>

      {loading ? (
        <div>Loading...</div>
      ) : (
        <div className="flex flex-wrap gap-4">
          {programs.map((program) => (
            <div
              key={program.id}
              className="p-5 border rounded-2xl shadow hover:shadow-lg transition w-64"
            >
              <div className="flex flex-col gap-2">
                <div className="font-semibold text-lg">
                  {program.programName}
                </div>
                <div className="text-gray-600">
                  Program Code: {program.programCode}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
