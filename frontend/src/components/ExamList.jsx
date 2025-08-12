import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";

export default function ExamList() {
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchExams = async () => {
      try {
        const response = await fetch("http://localhost:8081/api/exams");
        if (!response.ok)
          throw new Error(`HTTP error! status: ${response.status}`);
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

  if (loading) return <div>Loading exams...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="p-4">
      <div className="mb-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">Exam Seating Plans</h1>
        <Button onClick={() => navigate("/choose-seat-plan")}>
          Create Seat Plan
        </Button>
      </div>
      <div className="space-y-2">
        {exams.length > 0 ? (
          exams.map((exam) => (
            <Button
              asChild
              key={exam.id}
              className="w-full justify-start"
              variant="outline"
            >
              <Link to={`/seatGrid/${exam.id}`}>
                {exam.name || `Exam ${exam.id}`} -{" "}
                {new Date(exam.date).toLocaleDateString()}
              </Link>
            </Button>
          ))
        ) : (
          <p>No exams available</p>
        )}
      </div>
    </div>
  );
}
