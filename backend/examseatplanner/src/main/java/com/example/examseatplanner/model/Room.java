    package com.example.examseatplanner.model;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;

    import java.util.ArrayList;
    import java.util.List;

    @Entity
    public class Room{
        @Id
        private Integer roomNo;

        private int numRow;

        private int roomColumn =3;

        private int seatsPerBench = 2;

        @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<Seat> seats = new ArrayList<>();

        @ManyToMany(mappedBy = "rooms")
        private List<Exam> exams;


        public Room() {
        }

        public Room(Integer roomNo, int numRow, int seatsPerBench, int room_column) {
            this.roomNo = roomNo;
            this.numRow = numRow;
            setSeatsPerBench(seatsPerBench);
            setRoomColumn(room_column);
        }

        public void createRoom() {
            seats.clear();
            for (int row = 0; row < numRow; row++) {
                for (int bench = 0; bench < roomColumn; bench++) {
                    for (int seatPos = 0; seatPos < seatsPerBench; seatPos++) {
                        Seat seat = new Seat(row, bench, seatPos, this, null);
                        // Optionally set seatSide if relevant
                        seat.setSeatSide(seatPos);
                        seats.add(seat);
                    }
                }
            }
        }

        public List<Seat> getSeats() {
            return seats;
        }

        public void setSeats(List<Seat> seats) {
            this.seats = seats;
        }

        public int getRoomColumn() {
            return roomColumn;
        }

        public void setSeatsPerBench(int seatsPerBench){
            this.seatsPerBench = (seatsPerBench > 0) ? seatsPerBench : 2;
        }

        public int getSeatsPerBench() {
            return seatsPerBench;
        }

        public Integer getRoomNo() {
            return roomNo;
        }

        public void setRoomNo(Integer roomNo) {
            this.roomNo = roomNo;
        }

        public void setRoomColumn(int roomColumn){
            this.roomColumn = (roomColumn > 0) ? roomColumn : 3;
        }

        public int getROomColumn() {
            return roomColumn;
        }

        public int getNumRow() {
            return numRow;
        }

        public void setNumRow(int numRow) {
            this.numRow = numRow;
        }

        // Dynamically calculated seating capacity based on rows, columns, and seats per bench
        public int getSeatingCapacity() {
            return numRow * roomColumn * seatsPerBench;
        }

        @Override
        public String toString() {
            return "Room{" +
                    "roomNo=" + roomNo +
                    ", numRow=" + numRow +
                    ", NUMCOLUMN=" + roomColumn +
                    ", seatingCapacity=" + getSeatingCapacity() +
                    '}';
        }
    }
