package ua.university.sms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.repository.StudentRepository;
import ua.university.sms.service.StudentService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        Student student = Student.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .enrollmentYear(request.getEnrollmentYear())
                .status(request.getStatus())
                .build();
        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getAllStudents(StudentStatus status, Integer year, Pageable pageable) {
        Page<Student> studentPage;
        if (status != null && year != null) {
            studentPage = studentRepository.findByStatusAndEnrollmentYear(status, year, pageable);
        } else if (status != null) {
            studentPage = studentRepository.findByStatus(status, pageable);
        } else if (year != null) {
            studentPage = studentRepository.findByEnrollmentYear(year, pageable);
        } else {
            studentPage = studentRepository.findAll(pageable);
        }
        return studentPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = getStudentOrThrow(id);
        return mapToResponse(student);
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = getStudentOrThrow(id);
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setEnrollmentYear(request.getEnrollmentYear());
        student.setStatus(request.getStatus());
        Student updatedStudent = studentRepository.save(student);
        return mapToResponse(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentOrThrow(id);
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> searchStudents(String query, Pageable pageable) {
        return studentRepository.searchStudents(query, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsWithUnpaidCourses() {
        return studentRepository.findStudentsWithUnpaidCourses().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getTopStudents(int limit) {
        return studentRepository.findAll().stream()
                .sorted((s1, s2) -> Double.compare(calculateGpa(s2), calculateGpa(s1)))
                .limit(limit)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TranscriptResponse getStudentTranscript(Long studentId) {
        Student student = getStudentOrThrow(studentId);
        Double gpa = calculateGpa(student);

        List<TranscriptResponse.TranscriptEntry> entries = student.getEnrollments().stream()
                .map(e -> TranscriptResponse.TranscriptEntry.builder()
                        .courseId(e.getCourse().getId())
                        .courseName(e.getCourse().getName())
                        .credits(e.getCourse().getCredits())
                        .semester(e.getSemester())
                        .year(e.getYear())
                        .grade(e.getGrade())
                        .paid(e.isPaid())
                        .build())
                .collect(Collectors.toList());

        return TranscriptResponse.builder()
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .studentEmail(student.getEmail())
                .gpa(gpa)
                .courses(entries)
                .build();
    }

    private Student getStudentOrThrow(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }

    private Double calculateGpa(Student student) {
        if (student.getEnrollments() == null || student.getEnrollments().isEmpty()) {
            return 0.0;
        }

        List<Enrollment> gradedEnrollments = student.getEnrollments().stream()
                .filter(e -> e.getGrade() != null && e.getGrade().isCountedInGpa())
                .collect(Collectors.toList());

        if (gradedEnrollments.isEmpty()) {
            return 0.0;
        }

        double totalWeightedPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment enrollment : gradedEnrollments) {
            int credits = enrollment.getCourse().getCredits();
            double points = enrollment.getGrade().getPoints();
            totalWeightedPoints += points * credits;
            totalCredits += credits;
        }

        if (totalCredits == 0) {
            return 0.0;
        }

        double gpa = totalWeightedPoints / totalCredits;
        return Math.round(gpa * 100.0) / 100.0;
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .enrollmentYear(student.getEnrollmentYear())
                .status(student.getStatus())
                .build();
    }
}
