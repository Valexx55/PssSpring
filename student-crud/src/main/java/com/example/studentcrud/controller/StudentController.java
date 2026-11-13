
package com.example.studentcrud.controller;

import com.example.studentcrud.model.Student;
import com.example.studentcrud.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {

    @Autowired
    private StudentRepository repository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute Student student, Model model) {
        repository.save(student);
        model.addAttribute("message", "Student Added!");
        return "index";
    }

    @PostMapping("/delete")
    public String deleteStudent(@RequestParam String rollNo, Model model) {
        repository.deleteById(rollNo);
        model.addAttribute("message", "Student Deleted!");
        return "index";
    }

    @GetMapping("/display")
    public String displayAll(Model model) {
        model.addAttribute("students", repository.findAll());
        return "display";
    }

    @GetMapping("/display/{rollNo}")
    public String displayById(@PathVariable String rollNo, Model model) {
        model.addAttribute("student", repository.findById(rollNo).orElse(null));
        return "displayOne";
    }
}
