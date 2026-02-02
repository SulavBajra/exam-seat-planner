# Exam Seat Planner

A comprehensive exam seat planning system built with **Spring Boot**, **PostgreSQL**, and **React** that intelligently allocates students to exam seats while ensuring proper separation between different programs.

## Features

- **Smart Seat Allocation**: Automatically assigns students to exam seats with intelligent program separation
- **3D Room Visualization**: Supports multiple room sides (left, center, right) for comprehensive seat planning
- **Program Management**: Handle multiple academic programs and semesters
- **Student Management**: Upload and manage student data via Excel files
- **Room Configuration**: Flexible room setup with customizable rows, columns, and capacity
- **Validation System**: Built-in rule validation to ensure proper seat allocation
- **Export Functionality**: Export seat allocations to Excel and PDF formats
- **Real-time Statistics**: Track occupancy rates, program distribution, and allocation metrics

## Architecture

### Backend
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **API**: RESTful endpoints
- **File Processing**: Excel upload/download support

### Frontend
- **Framework**: React 19 with Vite
- **UI Components**: Radix UI + Tailwind CSS
- **Routing**: React Router DOM v7
- **Forms**: React Hook Form with Zod validation
- **Styling**: Tailwind CSS v4 with custom themes
- **Export**: ExcelJS, jsPDF, html2canvas

## Getting Started

### Prerequisites

- Java 21 or higher
- Node.js 18 or higher
- PostgreSQL 14 or higher
- Maven

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/SulavBajra/exam-seat-planner.git
   cd exam-seat-planner
   ```

2. **Configure Database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE exam_seat_planner;
   ```
   
   Update `backend/examseatplanner/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/exam_seat_planner
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build and run the backend**
   ```bash
   cd backend/examseatplanner
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:5173`

## Project Structure

```
exam-seat-planner/
├── backend/
│   └── examseatplanner/
│       ├── src/main/java/com/example/examseatplanner/
│       │   ├── config/          # Configuration (CORS, Security, Database)
│       │   ├── controller/      # REST API controllers
│       │   ├── dto/             # Data Transfer Objects
│       │   ├── mapper/          # Entity-DTO mappers
│       │   ├── model/           # JPA entities (Student, Exam, Room, Program)
│       │   ├── repository/      # Spring Data JPA repositories
│       │   ├── service/         # Business logic layer
│       │   └── exception/       # Custom exceptions
│       ├── src/main/resources/
│       │   └── application.properties
│       └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/          # Reusable React components
│   │   ├── pages/               # Page components
│   │   ├── lib/                 # Utilities and helpers
│   │   └── App.jsx              # Main application component
│   ├── public/                  # Static assets
│   ├── package.json
│   └── vite.config.js
├── studentDat.xlsx              # Sample student data
└── README.md
```

## API Endpoints

### Students
- `GET /api/students` - Get all students
- `GET /api/students/{studentId}` - Get student by ID
- `POST /api/students` - Create a new student
- `POST /api/students/bulk` - Bulk create students
- `POST /api/students/upload-excel` - Upload students from Excel
- `PUT /api/students/{studentId}` - Update student
- `DELETE /api/students/{studentId}` - Delete student
- `DELETE /api/students/clear` - Delete all students
- `GET /api/students/semester/{semester}` - Get students by semester
- `GET /api/students/program/{programCode}/semester/{semester}` - Get students by program and semester

### Exams
- `GET /api/exams` - Get all exams
- `GET /api/exams/{examId}` - Get exam by ID
- `POST /api/exams` - Create a new exam
- `PUT /api/exams/{examId}` - Update exam
- `DELETE /api/exams/{examId}` - Delete exam
- `GET /api/exams/{examId}/data` - Get exam data including students and rooms
- `GET /api/exams/booked-rooms?startDate={date}&endDate={date}` - Get booked rooms by date range

### Rooms
- `GET /api/rooms` - Get all rooms
- `GET /api/rooms/{roomNo}` - Get room by number
- `POST /api/rooms` - Create a new room
- `PUT /api/rooms/{roomNo}` - Update room
- `DELETE /api/rooms/{roomNo}` - Delete room
- `GET /api/rooms/capacity/{minCapacity}` - Get rooms with minimum capacity

### Programs
- `GET /api/programs` - Get all programs
- `GET /api/programs/{programCode}` - Get program by code
- `POST /api/programs` - Create a new program
- `PUT /api/programs/{programCode}` - Update program
- `DELETE /api/programs/{programCode}` - Delete program

### Seating Plans
- `POST /api/seating-plans/allocate/{examId}` - Allocate seats for exam
- `GET /api/seating-plans/exam/{examId}` - Get seating plan for exam
- `DELETE /api/seating-plans/exam/{examId}` - Delete seating plan

## Data Models

### Student
- Program Code
- Semester (1-8)
- Roll Number
- Unique Student ID (generated)

### Exam
- Start Date
- End Date
- Programs (with semesters)
- Rooms
- Seating Plan

### Room
- Room Number
- Number of Rows
- Number of Columns
- Seats per Bench
- Total Capacity (calculated)

### Program
- Program Code
- Program Name

## Tech Stack

### Backend
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Data persistence
- **Spring Security** - Security configuration
- **PostgreSQL** - Database
- **Maven** - Build tool
- **Hibernate** - ORM
- **Apache POI** - Excel file processing

### Frontend
- **React 19** - UI library
- **Vite 7** - Build tool
- **Tailwind CSS 4** - Styling
- **Radix UI** - Component library
- **React Router DOM 7** - Routing
- **React Hook Form** - Form management
- **Zod** - Schema validation
- **ExcelJS** - Excel generation
- **jsPDF** - PDF generation
- **html2canvas** - HTML to canvas conversion

## Development

### Backend Development

Run with hot reload:
```bash
cd backend/examseatplanner
mvn spring-boot:run
```
Run tests:
```bash
mvn test
```

### Frontend Development

Run development server:
```bash
cd frontend
npm run dev
```

Build for production:
```bash
npm run build
```

## Configuration

### CORS Configuration
The backend is configured to accept requests from `http://localhost:5173` by default. Update `WebConfig.java` to change allowed origins.

### Database Configuration
Update `application.properties` with your PostgreSQL credentials and connection details.

### Security Configuration
Currently configured for development with all endpoints open. Update `SecurityConfig.java` for production security requirements.

## Sample Data

A sample Excel file (`studentDat.xlsx`) is included in the root directory with the following format:

| Program Code | Semester | Roll |
|--------------|----------|------|
| 1001         | 3        | 1    |
| 1001         | 3        | 2    |


