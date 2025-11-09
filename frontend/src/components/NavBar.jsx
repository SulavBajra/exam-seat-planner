import React from "react";
import { Button } from "@/components/ui/button";
import { Link, useLocation } from "react-router-dom";
import {
  CalendarDays,
  School,
  Users,
  DoorOpen,
  LayoutGrid,
  Search,
} from "lucide-react";
import { cn } from "@/lib/utils";

export default function NavBar() {
  const location = useLocation();

  const navItems = [
    {
      path: "/exam",
      label: "Exams",
      icon: <CalendarDays className="h-4 w-4" />,
    },
    {
      path: "/program",
      label: "Programs",
      icon: <School className="h-4 w-4" />,
    },
    {
      path: "/student",
      label: "Students",
      icon: <Users className="h-4 w-4" />,
    },
    {
      path: "/searchSeat",
      label: "Search Seat",
      icon: <Search className="h-4 w-4" />,
    },
    {
      path: "/room",
      label: "Rooms",
      icon: <DoorOpen className="h-4 w-4" />,
    },
    {
      path: "/examList",
      label: "Seat Plans",
      icon: <LayoutGrid className="h-4 w-4" />,
    },
  ];

  return (
    <nav className="flex items-center gap-1 p-2 bg-background border rounded-lg shadow-sm">
      {navItems.map((item) => (
        <Button
          key={item.path}
          asChild
          variant="ghost"
          size="sm"
          className={cn(
            "flex items-center gap-2 rounded-md",
            location.pathname.startsWith(item.path)
              ? "bg-accent text-accent-foreground"
              : "hover:bg-muted/50"
          )}
        >
          <Link to={item.path}>
            {item.icon}
            <span>{item.label}</span>
          </Link>
        </Button>
      ))}
    </nav>
  );
}
