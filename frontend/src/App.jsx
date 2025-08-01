import { useState } from "react";
import "./App.css";
import Navigation from "./components/Navigation";
import Dashboard from "./components/Dashboard";
import ExamsManagement from "./components/ExamsManagement";
import RoomsManagement from "./components/RoomsManagement";
import StudentsManagement from "./components/StudentsManagement";

// function App() {
//   const [count, setCount] = useState(0);

//   return (
//     <>
//       <Button />
//     </>
//   );
// }

const ExamSeatPlannerApp = () => {
  const [activeTab, setActiveTab] = useState("dashboard");
  
  const renderContent = () => {
    switch (activeTab) {
      case "dashboard":
        return <Dashboard />;
      case "exams":
        return <ExamsManagement />;
      case "rooms":
        return <RoomsManagement />;
      case "students":
        return <StudentsManagement />;
      case "reports":
        return <Reports />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navigation activeTab={activeTab} setActiveTab={setActiveTab} />
      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="px-4 py-6 sm:px-0">{renderContent()}</div>
      </main>
    </div>
  );
};

export default ExamSeatPlannerApp;
