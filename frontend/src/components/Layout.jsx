import React from "react";
import { Outlet } from "react-router-dom";
import NavBar from "@/components/NavBar";

export default function Layout() {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="sticky top-0 z-10 bg-white shadow-sm">
        {/* <NavBar /> */}
      </header>
      <main className="flex-grow p-4 container mx-auto">
        <Outlet />
      </main>
    </div>
  );
}
