package com.mdnurakmal.studentattendance;

import com.mdnurakmal.studentattendance.model.Student;
import com.mdnurakmal.studentattendance.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")

public class StudentResource {
    private final StudentService studentService;

    public StudentResource(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents(){
        List<Student> students = studentService.findAllStudents();
        return new ResponseEntity<List<Student>>(students, HttpStatus.OK);
    }
    @GetMapping("/find/{id}")
    public ResponseEntity<Student> getAllStudents(@PathVariable("id") Long id){
        Student student = studentService.findStudentById(id);
        return new ResponseEntity<Student>(student, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Student> addStudent (@RequestBody Student student){
        Student newStudent = studentService.AddStudent(student);
        return new ResponseEntity<Student>(newStudent, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Student> updateStudent (@RequestBody Student student){
        Student updateStudent = studentService.AddStudent(student);
        return new ResponseEntity<Student>(updateStudent, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStudent (@PathVariable("id") Long  student){
        studentService.deleteStudent(student);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
