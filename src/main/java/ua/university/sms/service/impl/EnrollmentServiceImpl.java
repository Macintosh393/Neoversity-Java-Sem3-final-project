package ua.university.sms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.BadRequestException;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.model.dto.CourseAverageGpaResponse;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.EnrollmentRepository;
import ua.university.sms.repository.StudentRepository;
import ua.university.sms.service.EnrollmentService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        boolean exists = enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(
                request.getStudentId(), request.getCourseId(), request.getSemester(), request.getYear()
        );

        if (exists) {
            throw new BadRequestException("Student is already enrolled in this course for the specified semester and year");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .semester(request.getSemester())
                .year(request.getYear())
                .grade(Grade.NA)
                .paid(false)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(savedEnrollment);
    }

    @Override
    public EnrollmentResponse updateGrade(Long enrollmentId, Grade grade) {
        Enrollment enrollment = getEnrollmentOrThrow(enrollmentId);
        enrollment.setGrade(grade);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(updatedEnrollment);
    }

    @Override
    public EnrollmentResponse markAsPaid(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentOrThrow(enrollmentId);
        enrollment.setPaid(true);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(updatedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseAverageGpaResponse getAverageGpa(Long courseId, String semester) {
        List<Enrollment> enrollments;
        String courseName = "All Courses";

        if (courseId != null && semester != null) {
            enrollments = enrollmentRepository.findByCourseIdAndSemester(courseId, semester);
            courseName = courseRepository.findById(courseId)
                    .map(Course::getName)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        } else if (courseId != null) {
            enrollments = enrollmentRepository.findByCourseId(courseId);
            courseName = courseRepository.findById(courseId)
                    .map(Course::getName)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        } else if (semester != null) {
            enrollments = enrollmentRepository.findBySemester(semester);
        } else {
            enrollments = enrollmentRepository.findAll();
        }

        List<Enrollment> graded = enrollments.stream()
                .filter(e -> e.getGrade() != null && e.getGrade().isCountedInGpa())
                .collect(Collectors.toList());

        double averageGpa = 0.0;
        if (!graded.isEmpty()) {
            double totalWeightedPoints = 0.0;
            int totalCredits = 0;
            for (Enrollment e : graded) {
                int credits = e.getCourse().getCredits();
                totalWeightedPoints += e.getGrade().getPoints() * credits;
                totalCredits += credits;
            }
            if (totalCredits > 0) {
                averageGpa = totalWeightedPoints / totalCredits;
                averageGpa = Math.round(averageGpa * 100.0) / 100.0;
            }
        }

        return CourseAverageGpaResponse.builder()
                .courseId(courseId)
                .courseName(courseName)
                .semester(semester)
                .averageGpa(averageGpa)
                .build();
    }

    private Enrollment getEnrollmentOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName())
                .courseId(enrollment.getCourse().getId())
                .courseName(enrollment.getCourse().getName())
                .semester(enrollment.getSemester())
                .year(enrollment.getYear())
                .paid(enrollment.isPaid())
                .grade(enrollment.getGrade())
                .build();
    }
}
