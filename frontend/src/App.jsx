import AppRoutes from "@/routes/AppRoutes";
import NavBar from "@/components/NavBar";

function App() {
  return (
    <div className="flex flex-col p-6 justify-center items-center gap-10">
      <div className="flex items-center justify-center">
        <NavBar />
      </div>
      <div className="">
        <AppRoutes />
      </div>
    </div>
  );
}

export default App;
