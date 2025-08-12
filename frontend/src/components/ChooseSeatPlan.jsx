import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";

export default function ChooseSeatPlan() {
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchExams = async () => {
      try {
        const response = await fetch("http://localhost:8081/api/exams");
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setExams(data);
      } catch (error) {
        console.error("Error fetching exams:", error);
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchExams();
  }, []);

  async function createSeatPlan(examId) {
    try {
      const response = await fetch(
        `http://localhost:8081/api/seat-allocation/exam/${examId}/allocate`,
        { method: "POST" }
      );
      if (!response.ok) {
        throw new Error(`Failed to create seat plan: ${response.status}`);
      }
      // You can handle the response here if needed
    } catch (error) {
      console.error(error);
    }
  }

  if (loading) return <div className="text-center py-8">Loading exams...</div>;
  if (error)
    return <div className="text-center py-8 text-red-500">Error: {error}</div>;
  if (exams.length === 0)
    return <div className="text-center py-8">No exams found</div>;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Available Exams</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {exams.map((exam) => (
          <Card key={exam.id} className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <CardTitle>{exam.name || `Exam #${exam.id}`}</CardTitle>
              <CardDescription>
                {new Date(exam.date).toLocaleDateString()}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                <p className="font-medium">Programs:</p>
                <ul className="list-disc list-inside ml-4">
                  {exam.programSemesters?.map((ps, idx) => (
                    <li key={idx}>
                      {ps.program.programName} ({ps.semester})
                    </li>
                  ))}
                </ul>
                <p>
                  <span className="font-medium">Rooms:</span>{" "}
                  {exam.rooms?.length} assigned
                </p>
              </div>
              <Button
                className="mt-4 w-full"
                onClick={() => createSeatPlan(exam.id)}
              >
                Choose Seat Plan
              </Button>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
