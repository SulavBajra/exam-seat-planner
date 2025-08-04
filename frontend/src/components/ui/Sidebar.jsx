import React from "react";
import { Button } from "@/components/ui/button.jsx";
import { Link, useNavigate } from "react-router-dom";

export default function Sidebar() {
  const navigate = useNavigate();

  return (
    <div className="flex flex-col p-4 gap-4 shadow-lg border-r h-screen">
      <Button asChild variant="ghost" onClick={() => navigate("/programs")}>
        <Link to="/programs">Programs</Link>
      </Button>
      <Button asChild variant="ghost">
        <Link to="/students">Students</Link>
      </Button>
      <Button asChild variant="ghost">
        <Link to="/exams">Exams</Link>
      </Button>
      <Button asChild variant="ghost" onClick={() => navigate("/seatplans")}>
        <Link to="/seatplans">Seat Plan</Link>
      </Button>
      <Button asChild variant="ghost" onClick={() => navigate("/rooms")}>
        <Link to="/rooms">Rooms</Link>
      </Button>
      <Button asChild variant="ghost" onClick={() => navigate("/subject")}>
        <Link to="/subject">Subject</Link>
      </Button>
    </div>
  );
}
