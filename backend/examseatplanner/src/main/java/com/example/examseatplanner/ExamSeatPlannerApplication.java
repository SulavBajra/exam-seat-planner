package com.example.examseatplanner;

import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.StudentRepository;
import com.example.examseatplanner.repository.SubjectRepository;
import com.example.examseatplanner.service.StudentService;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SpringBootApplication
public class ExamSeatPlannerApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExamSeatPlannerApplication.class, args);
	}
}
