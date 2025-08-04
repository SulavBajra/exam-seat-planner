import React from "react";
import { Routes, Route } from "react-router-dom";
import Programs from "@/components/pages/programs";
import Students from "@/components/pages/Student";
import Exam from "@/components/pages/Exam";
import SeatPlan from "@/components/pages/SeatPlan";
import Rooms from "@/components/pages/Rooms";
import Subject from "@/components/pages/Subject";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/programs" element={<Programs />} />
      <Route path="/students" element={<Students />} />
      <Route path="/exams" element={<Exam />} />
      <Route path="/seatplans" element={<SeatPlan />} />
      <Route path="/rooms" element={<Rooms />} />
      <Route path="/subject" element={<Subject />} />
    </Routes>
  );
}
