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

        @Transient
        private final int ROOM_COLUMN =3;

        @Transient
        public static final int SEATS_PER_BENCH = 2;

        @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<Seat> seats = new ArrayList<>();


        public Room() {
        }

        public Room(Integer roomNo, int numRow) {
            this.roomNo = roomNo;
            this.numRow = numRow;
        }

        public void createRoom() {
            seats.clear();
            int dummyCounter = 1;
            for (int row = 0; row < numRow; row++) {
                for (int bench = 0; bench < ROOM_COLUMN; bench++) {
                    for (int seatPos = 0; seatPos < SEATS_PER_BENCH; seatPos++) {
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

        public int getROOM_COLUMN() {
            return ROOM_COLUMN;
        }

        public static int getSeatsPerBench() {
            return SEATS_PER_BENCH;
        }

        public Integer getRoomNo() {
            return roomNo;
        }

        public void setRoomNo(Integer roomNo) {
            this.roomNo = roomNo;
        }

        public int getNumColumn() {
            return ROOM_COLUMN;
        }

        public int getNumRow() {
            return numRow;
        }

        public void setNumRow(int numRow) {
            this.numRow = numRow;
        }

        // Dynamically calculated seating capacity based on rows, columns, and seats per bench
        public int getSeatingCapacity() {
            return numRow * ROOM_COLUMN * SEATS_PER_BENCH;
        }

        @Override
        public String toString() {
            return "Room{" +
                    "roomNo=" + roomNo +
                    ", numRow=" + numRow +
                    ", NUMCOLUMN=" + ROOM_COLUMN +
                    ", seatingCapacity=" + getSeatingCapacity() +
                    '}';
        }
    }
