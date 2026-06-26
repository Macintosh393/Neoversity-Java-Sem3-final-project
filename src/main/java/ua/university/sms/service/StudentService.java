package ua.university.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.enums.StudentStatus;
import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    Page<StudentResponse> getAllStudents(StudentStatus status, Integer year, Pageable pageable);
    StudentResponse getStudentById(Long id);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
    Page<StudentResponse> searchStudents(String query, Pageable pageable);
    List<StudentResponse> getStudentsWithUnpaidCourses();
    List<StudentResponse> getTopStudents(int limit);
    TranscriptResponse getStudentTranscript(Long studentId);
}
