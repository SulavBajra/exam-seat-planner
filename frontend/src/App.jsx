import AppRoutes from "@/routes/AppRoutes";
import NavBar from "@/components/NavBar";
import { Toaster } from "@/components/ui/sonner";

function App() {
  return (
    <div className="min-h-screen w-full">
      <div className="flex flex-col min-h-screen">
        {/* Header/Navigation */}
        <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
          <div className="container flex h-16 items-center justify-between px-4">
            <h1 className="text-lg font-semibold">Exam Seating Planner</h1>
            <NavBar />
          </div>
        </header>

        {/* Main Content */}
        <main className="flex-1 container py-6 px-4 md:px-6">
          <AppRoutes />
        </main>

        {/* Footer */}
        <footer className="py-6 border-t">
          <div className="container flex flex-col items-center justify-between gap-4 md:flex-row px-4"></div>
        </footer>
      </div>

      {/* Toast Notifications */}
      <Toaster />
    </div>
  );
}

export default App;
