import React from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import SeatPlanForm from "@/components/ui/SeatPlanForm";

export default function SeatPlan() {
  const [seats, setSeats] = React.useState([]);
  const [loading, setLoading] = React.useState(true);
  //   useEffect(() => {
  //     async function fetchSeatPlans() {
  //       try {
  //         const response = await fetch("http://localhost:8081/api/seats");
  //         const data = await response.json();
  //         setSeats(data);
  //       } catch (error) {
  //         console.error("Error fetching seats:", error);
  //       } finally {
  //         setLoading(false);
  //       }
  //     }

  //     // fetchSeatPlans();
  //   }, []);

  return (
    <>
      <div className="flex flex-col gap-4">
        <div>SeatPlan</div>
        <Dialog>
          <DialogTrigger asChild>
            <Button variant="outline" className="w-40">
              Create Seat Plan
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Seat Plan</DialogTitle>
            </DialogHeader>
            <SeatPlanForm />
          </DialogContent>
        </Dialog>
        <div>
          {seats.map((seat) => (
            <div key={seat.id}>{seat.studentId}</div>
          ))}
        </div>
      </div>
    </>
  );
}
