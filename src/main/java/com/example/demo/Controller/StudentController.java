package com.example.demo.Controller;

import com.example.demo.Entity.Student;
import com.example.demo.Repository.StudentRepository;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController()
public class StudentController {
    private StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    //create student in db
    @PostMapping("/api/students/new")
    public ResponseEntity<Student> saveStudent(@RequestBody Student student){
        return new ResponseEntity<Student>(studentRepository.save(student), HttpStatus.CREATED);
    }

    //get all students
    @GetMapping("/api/students/all")
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    //get student with {id}
    @GetMapping("/api/students/student/{id}")
    public Student getOneWithId(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        return studentRepository.findById(id).orElseThrow(() -> new ChangeSetPersister.NotFoundException());
    }

    /*
    If id is exist then replace with new data
        then create new student with all data
     */
    @PutMapping("/api/students/student/{id}")
    public Student replaceStudent(@RequestBody Student newStudent, @PathVariable Long id){
        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(newStudent.getName());
                    student.setEmail(newStudent.getEmail());
                    return studentRepository.save(student);
                })
                .orElseGet(() ->{
                    newStudent.setId(id);
                    return studentRepository.save(newStudent);
                });
    }

    /*
    Delete student with ID
     */
    @DeleteMapping("/api/students/student/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id){
        if (studentRepository.findById(id).isPresent()){
            studentRepository.deleteById(id);
            return new ResponseEntity<>(studentRepository.getById(id), HttpStatus.ACCEPTED);
        } else {
            System.out.println("Student with this id:"+ id +" Does not exist");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // TODO: Create normal responce for delete student and responce for error
    }

}
