package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {
    private final ExamRepository examRepository;

    @Autowired
    public ExamService(ExamRepository examRepository){
        this.examRepository = examRepository;
    }

    public List<Exam> findByDate(String Date){
        return examRepository.findByDate(Date);
    }

    public ExamRequestDTO createExam(ExamRequestDTO dto){
        return new ExamRequestDTO(dto.subjectCode(), dto.date(), dto.time(),dto.studentIds(),dto.roomIds());
    }


}
