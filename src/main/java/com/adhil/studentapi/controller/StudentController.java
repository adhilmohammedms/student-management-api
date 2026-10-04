package com.adhil.studentapi.controller;

import com.adhil.studentapi.dto.PageResponse;
import com.adhil.studentapi.dto.StudentRequest;
import com.adhil.studentapi.model.Student;
import com.adhil.studentapi.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    /** GET /api/students?department=CSE&name=anu&page=0&size=10&sort=cgpa,desc */
    @GetMapping
    public PageResponse<Student> list(
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "name", required = false) String name,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return PageResponse.from(service.search(department, name, pageable));
    }

    @GetMapping("/{id}")
    public Student get(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody StudentRequest request) {
        Student saved = service.create(request);
        return ResponseEntity.created(URI.create("/api/students/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable("id") Long id, @Valid @RequestBody StudentRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
