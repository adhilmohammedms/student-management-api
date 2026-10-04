package com.adhil.studentapi.service;

import com.adhil.studentapi.dto.StudentRequest;
import com.adhil.studentapi.exception.DuplicateResourceException;
import com.adhil.studentapi.exception.ResourceNotFoundException;
import com.adhil.studentapi.model.Student;
import com.adhil.studentapi.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<Student> search(String department, String name, Pageable pageable) {
        boolean hasDepartment = StringUtils.hasText(department);
        boolean hasName = StringUtils.hasText(name);

        if (hasDepartment && hasName) {
            return repository.findByDepartmentIgnoreCaseAndNameContainingIgnoreCase(
                    department.trim(), name.trim(), pageable);
        }
        if (hasDepartment) {
            return repository.findByDepartmentIgnoreCase(department.trim(), pageable);
        }
        if (hasName) {
            return repository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Student getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id " + id + " not found"));
    }

    @Transactional
    public Student create(StudentRequest request) {
        String email = normaliseEmail(request.email());
        if (repository.existsByEmail(email)) {
            throw new DuplicateResourceException("A student with email " + email + " already exists");
        }
        Student student = new Student();
        copyFields(request, student);
        return repository.save(student);
    }

    @Transactional
    public Student update(Long id, StudentRequest request) {
        Student student = getById(id);
        String email = normaliseEmail(request.email());
        if (repository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("A student with email " + email + " already exists");
        }
        copyFields(request, student);
        return repository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Student with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    private static void copyFields(StudentRequest request, Student student) {
        student.setName(request.name().trim());
        student.setEmail(normaliseEmail(request.email()));
        student.setDepartment(request.department().trim().toUpperCase(Locale.ROOT));
        student.setSemester(request.semester());
        student.setCgpa(request.cgpa());
    }

    private static String normaliseEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
