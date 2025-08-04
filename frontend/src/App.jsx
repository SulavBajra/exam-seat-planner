import Sidebar from "@/components/ui/Sidebar";
import AppRoutes from "@/routes/AppRoutes";

function App() {
  return (
    <div className="flex h-screen">
      <Sidebar />
      <div className="flex-1 p-6">
        <AppRoutes />
      </div>
    </div>
  );
}

export default App;
