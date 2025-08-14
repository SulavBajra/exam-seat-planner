import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import { Book, Users, LayoutGrid, Map } from "lucide-react"; // Icons

export default function Dashboard() {
  const navigate = useNavigate();

  const cards = [
    {
      title: "Exams",
      description: "View and manage exams",
      icon: <Book className="w-6 h-6" />,
      path: "/examList",
    },
    {
      title: "Students",
      description: "Manage students",
      icon: <Users className="w-6 h-6" />,
      path: "/studentList",
    },
    {
      title: "Programs",
      description: "View and manage programs",
      icon: <LayoutGrid className="w-6 h-6" />,
      path: "/program",
    },
    {
      title: "Seat Planning",
      description: "Plan seating for exams",
      icon: <Map className="w-6 h-6" />,
      path: "/choose-seat-plan",
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {cards.map((card) => (
        <Card key={card.title} className="hover:shadow-lg transition">
          <CardHeader className="flex items-center gap-3">
            {card.icon}
            <CardTitle>{card.title}</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-4">
            <p className="text-sm text-muted-foreground">{card.description}</p>
            <Button onClick={() => navigate(card.path)}>Go</Button>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
