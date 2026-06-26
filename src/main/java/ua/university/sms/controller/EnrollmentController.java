package ua.university.sms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.university.sms.model.dto.CourseAverageGpaResponse;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.dto.GradeUpdateRequest;
import ua.university.sms.service.EnrollmentService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Enrollments & Grades Management", description = "Endpoints for enrolling students to courses, updating grades, recording payments, and fetching GPA stats")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/enrollments")
    @Operation(summary = "Create a new enrollment for a student into a course")
    public ResponseEntity<EnrollmentResponse> enrollStudent(@Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = enrollmentService.enrollStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/enrollments/{id}/grade")
    @Operation(summary = "Set or update grade for a specific enrollment")
    public ResponseEntity<EnrollmentResponse> updateGrade(
            @PathVariable Long id,
            @Valid @RequestBody GradeUpdateRequest request) {
        return ResponseEntity.ok(enrollmentService.updateGrade(id, request.getGrade()));
    }

    @PutMapping("/enrollments/{id}/paid")
    @Operation(summary = "Mark an enrollment course as paid")
    public ResponseEntity<EnrollmentResponse> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.markAsPaid(id));
    }

    @GetMapping("/reports/average-gpa")
    @Operation(summary = "Get average GPA for a specific course or semester")
    public ResponseEntity<CourseAverageGpaResponse> getAverageGpa(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String semester) {
        return ResponseEntity.ok(enrollmentService.getAverageGpa(courseId, semester));
    }
}
