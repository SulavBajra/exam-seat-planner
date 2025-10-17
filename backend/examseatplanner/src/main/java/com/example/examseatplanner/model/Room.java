    package com.example.examseatplanner.model;

    import jakarta.persistence.*;

    import java.util.List;

    @Entity
    public class Room{
        @Id
        private Integer roomNo;

        private int numRow;

        private int roomColumn =3;

        private int seatsPerBench = 2;

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
