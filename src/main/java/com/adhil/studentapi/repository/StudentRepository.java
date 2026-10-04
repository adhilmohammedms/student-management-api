package com.adhil.studentapi.repository;

import com.adhil.studentapi.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findByDepartmentIgnoreCase(String department, Pageable pageable);

    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Student> findByDepartmentIgnoreCaseAndNameContainingIgnoreCase(String department, String name,
                                                                        Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
