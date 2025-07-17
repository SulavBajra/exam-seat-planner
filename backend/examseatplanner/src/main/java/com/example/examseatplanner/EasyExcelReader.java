//package com.example.examseatplanner;
//
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.read.listener.ReadListener;
//import com.example.examseatplanner.model.Student;
//
//import java.io.File;
//
//public class EasyExcelReader {
//    public static void main(String[] args) {
//        String fileName = "example.xlsx";
//
//        EasyExcel.read(new File(fileName), Student.class,
//                new YourDataListener()).sheet().doRead();
//    }
//}
//
//abstract class YourDataListener implements ReadListener<Student> {
//    @Override
//    public void invoke(Student data, AnalysisContext context) {
//        // Process each row
//        System.out.println(data);
//    }
//
//    @Override
//    public void doAfterAllAnalysed(AnalysisContext context) {
//        // All data read
//    }
//}