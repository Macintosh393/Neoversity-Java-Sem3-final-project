package ua.university.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.service.StudentService;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students Management", description = "Endpoints for managing students, search, filters, transcript generation, and top student lists")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get list of all students (with filtering, pagination, and sorting)")
    public ResponseEntity<Page<StudentResponse>> getAllStudents(
            @RequestParam(required = false) StudentStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(studentService.getAllStudents(status, year, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get data of a specific student")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student data")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search student by part of name or email")
    public ResponseEntity<Page<StudentResponse>> searchStudents(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(studentService.searchStudents(q, pageable));
    }

    @GetMapping("/unpaid")
    @Operation(summary = "Get list of students who have at least one unpaid course")
    public ResponseEntity<List<StudentResponse>> getStudentsWithUnpaidCourses() {
        return ResponseEntity.ok(studentService.getStudentsWithUnpaidCourses());
    }

    @GetMapping("/top")
    @Operation(summary = "Get top N students sorted by GPA descending")
    public ResponseEntity<List<StudentResponse>> getTopStudents(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(studentService.getTopStudents(limit));
    }

    @GetMapping("/{id}/transcript")
    @Operation(summary = "Get detailed transcript of student with GPA calculation")
    public ResponseEntity<TranscriptResponse> getStudentTranscript(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentTranscript(id));
    }
}
