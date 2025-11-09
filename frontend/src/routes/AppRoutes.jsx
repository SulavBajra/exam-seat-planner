import { Routes, Route } from "react-router-dom";
import Layout from "@/components/Layout";
import Student from "@/components/Student";
import Program from "@/components/Program";
import Exam from "@/components/Exam";
import ExamList from "@/components/ExamList";
import Room from "@/components/Room";
import SeatingArrangement from "@/components/SeatingArrangement";
import StudentList from "@/components/StudentList";
import Login from "@/components/Login";
import SearchSeat from "@/components/SearchSeat";

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
        <Route path="studentList" element={<StudentList />} />
        <Route
          path="seatingArrangement/:examId"
          element={<SeatingArrangement />}
        />
        <Route path="searchSeat" element={<SearchSeat />} />
      </Route>
      
      <Route path="/login" element={<Login />} />
    </Routes>
  );
}
