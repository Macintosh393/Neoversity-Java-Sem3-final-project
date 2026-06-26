package ua.university.sms.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.university.sms.exception.BadRequestException;
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
import ua.university.sms.service.impl.EnrollmentServiceImpl;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    void testEnrollStudentSuccess() {
        Student student = Student.builder().id(1L).firstName("John").lastName("Doe").build();
        Course course = Course.builder().id(10L).name("Math").credits(4).build();

        EnrollmentRequest request = EnrollmentRequest.builder()
                .studentId(1L)
                .courseId(10L)
                .semester("Fall")
                .year(2023)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(1L, 10L, "Fall", 2023)).thenReturn(false);

        Enrollment savedEnrollment = Enrollment.builder()
                .id(100L)
                .student(student)
                .course(course)
                .semester("Fall")
                .year(2023)
                .grade(Grade.NA)
                .paid(false)
                .build();

        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

        EnrollmentResponse response = enrollmentService.enrollStudent(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(Grade.NA, response.getGrade());
        assertFalse(response.isPaid());
    }

    @Test
    void testEnrollStudentDuplicateThrowsBadRequest() {
        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(10L).build();

        EnrollmentRequest request = EnrollmentRequest.builder()
                .studentId(1L)
                .courseId(10L)
                .semester("Fall")
                .year(2023)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndSemesterAndYear(1L, 10L, "Fall", 2023)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> enrollmentService.enrollStudent(request));
    }

    @Test
    void testCalculateAverageGpaForCourse() {
        Course course = Course.builder().id(10L).name("Math").credits(4).build();

        Enrollment e1 = Enrollment.builder().id(1L).course(course).grade(Grade.A).build();
        Enrollment e2 = Enrollment.builder().id(2L).course(course).grade(Grade.B).build();
        Enrollment e3 = Enrollment.builder().id(3L).course(course).grade(Grade.NA).build();

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByCourseId(10L)).thenReturn(List.of(e1, e2, e3));

        CourseAverageGpaResponse response = enrollmentService.getAverageGpa(10L, null);

        assertNotNull(response);
        assertEquals(3.5, response.getAverageGpa());
    }
}
