import { Routes, Route } from "react-router-dom";
import Layout from "@/components/Layout";
import Student from "@/components/Student";
import Program from "@/components/Program";
import Exam from "@/components/Exam";
import ExamList from "@/components/ExamList";
import SeatGrid from "@/components/SeatGrid";
import Room from "@/components/Room";
import SeatingArrangement from "@/components/SeatingArrangement";
import ChooseSeatPlan from "@/components/ChooseSeatPlan";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<ExamList />} />
        <Route path="student" element={<Student />} />
        <Route path="program" element={<Program />} />
        <Route path="exam" element={<Exam />} />
        <Route path="examList" element={<ExamList />} />
        <Route path="room" element={<Room />} />
        <Route path="seatGrid/:examId" element={<SeatGrid />} />
        <Route path="/choose-seat-plan" element={<ChooseSeatPlan />} />
        <Route
          path="seatingArrangement/:examId"
          element={<SeatingArrangement />}
        />
      </Route>
    </Routes>
  );
}
