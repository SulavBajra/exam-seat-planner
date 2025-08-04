import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { ThemeProvider } from "@/components/ui/theme-provider";
import ThemeToggle from "@/components/ui/theme-toggle";
import { BrowserRouter as Router } from "react-router-dom";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <Router>
      <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
        <div className="h-screen w-20%">
          <App />
        </div>
      </ThemeProvider>
    </Router>
  </StrictMode>
);
