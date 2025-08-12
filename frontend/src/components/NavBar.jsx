import React from "react";
import { Button } from "@/components/ui/button";
import { Link } from "react-router-dom";

export default function NavBar() {
  return (
    <nav className="flex gap-4 p-4 bg-slate-50 border shadow-sm rounded-lg">
      <Button asChild variant="ghost">
        <Link to="/exam">Exams</Link>
      </Button>
      <Button asChild variant="ghost">
        <Link to="/program">Programs</Link>
      </Button>
      <Button asChild variant="ghost">
        <Link to="/student">Students</Link>
      </Button>
      <Button asChild variant="ghost">
        <Link to="/room">Rooms</Link>
      </Button>
      <Button asChild variant="ghost">
        <Link to="/examList">Seat Plans</Link>
      </Button>
    </nav>
  );
}
