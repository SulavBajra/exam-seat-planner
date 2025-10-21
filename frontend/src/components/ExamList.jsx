import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { CalendarDays, PlusCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";

export default function ExamList() {
  const [exams, setExams] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchExams = async () => {
      try {
        const response = await fetch("http://localhost:8081/api/exams");
        if (!response.ok) {
          throw new Error(`Failed to load exams. Status: ${response.status}`);
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

  const formatDate = (dateString) => {
    const options = { year: "numeric", month: "long", day: "numeric" };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  if (error) {
    return (
      <div className="p-4">
        <Card className="border-destructive">
          <CardHeader>
            <CardTitle>Error Loading Exams</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => window.location.reload()}>Retry</Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="p-4 max-w-4xl mx-auto">
      <div className="mb-6 flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">
            Exam Seating Plans
          </h1>
          <p className="text-muted-foreground">
            View and manage all exam seating arrangements
          </p>
        </div>
      </div>

      {loading ? (
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => (
            <Skeleton key={i} className="h-20 w-full rounded-lg" />
          ))}
        </div>
      ) : exams.length > 0 ? (
        <div className="grid gap-4">
          {exams.map((exam) => (
            <Card key={exam.id} className="hover:shadow-md transition-shadow">
              <Link to={`/seatingArrangement/${exam.id}`} className="block">
                <CardHeader className="flex flex-row items-center justify-between pb-2">
                  <div>
                    <CardTitle className="text-lg">
                      {exam.name || `Exam #${exam.id}`}
                    </CardTitle>
                    <CardDescription className="flex items-center gap-2 mt-1">
                      <CalendarDays className="h-4 w-4" />
                      {formatDate(exam.date)}
                    </CardDescription>
                  </div>
                  <Badge variant="secondary" className="ml-2">
                    {exam.programSemesters?.length || 0} Semesters
                  </Badge>
                </CardHeader>
              </Link>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle>No Exams Found</CardTitle>
            <CardDescription>
              There are no exams scheduled yet. Create your first seating plan.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => navigate("/choose-seat-plan")}>
              Create New Exam Plan
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
