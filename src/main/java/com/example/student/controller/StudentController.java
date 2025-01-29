package com.example.student.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.student.model.Student;
import com.example.student.model.StudentRepository;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")

public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @GetMapping("/students")
    private ResponseEntity<List<Student>> getAllStudents() {

        try {
            var students = new ArrayList<Student>();
            studentRepository.findAll().forEach(students::add);

            if (students.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            } else {
                return new ResponseEntity<>(students, HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/students/{id}")
    private ResponseEntity<Object> deleteStudentById(@PathVariable long id) {

        try {

            var existingStudent = studentRepository.findById(id);
            if (existingStudent.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }
            studentRepository.deleteById(id);
            return new ResponseEntity<>(new Object() {
                public final String message = "Student deleted successfully";
            }, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students/{id}")
    private ResponseEntity<Student> getStudentByID(@PathVariable long id) {
        try {

            var existingStudent = studentRepository.findById(id);
            if (existingStudent.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }
            return new ResponseEntity<>(existingStudent.get(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/students/{id}")
    private ResponseEntity<Object> updateStudentbyID(@PathVariable long id, @Valid @RequestBody Student studentDetails,
            BindingResult result) {
                StringBuilder errorMessages = new StringBuilder();

        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            });
            

            return new ResponseEntity<>(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }

        try {

            var existingStudent = studentRepository.findById(id);
            if (existingStudent.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            }
            var studentToUpdate = existingStudent.get();
            studentToUpdate.setName(studentDetails.getName());
            studentToUpdate.setEmail(studentDetails.getEmail());
            studentToUpdate.setDepartment(studentDetails.getDepartment());

            studentRepository.save(studentToUpdate);

            return new ResponseEntity<>(new Object() {
                public final String message = "Student updated successfully";
                public final Student student = studentToUpdate;
            }, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/students")
    private ResponseEntity<Object> addAStudent(@Valid @RequestBody Student studentDetails, BindingResult result) {
        StringBuilder errorMessages = new StringBuilder();

        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> {
                errorMessages.append(error.getDefaultMessage()).append("; ");
            });
            

            return new ResponseEntity<>(errorMessages.toString(), HttpStatus.BAD_REQUEST);
        }

        try {

            var studentToAdd = new Student(studentDetails.getName(), studentDetails.getEmail(),
                    studentDetails.getDepartment());
            studentRepository.save(studentToAdd);

            return new ResponseEntity<>(new Object() {
                public final String message = "Student added successfully";
                public final Student student = studentToAdd;
            }, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
