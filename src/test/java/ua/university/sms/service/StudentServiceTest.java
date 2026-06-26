package ua.university.sms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.university.sms.model.dto.StudentRequest;
import ua.university.sms.model.dto.StudentResponse;
import ua.university.sms.model.dto.TranscriptResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Enrollment;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.repository.StudentRepository;
import ua.university.sms.service.impl.StudentServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        student = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .enrollmentYear(2023)
                .status(StudentStatus.ACTIVE)
                .enrollments(new ArrayList<>())
                .build();
    }

    @Test
    void testCreateStudent() {
        StudentRequest request = StudentRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .enrollmentYear(2023)
                .status(StudentStatus.ACTIVE)
                .build();

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        StudentResponse response = studentService.createStudent(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void testCalculateGpaWithMixedGrades() {
        Course math = Course.builder().id(1L).name("Math").credits(4).build();
        Course physics = Course.builder().id(2L).name("Physics").credits(3).build();

        Enrollment e1 = Enrollment.builder().student(student).course(math).grade(Grade.A).paid(true).semester("S1").year(2023).build();
        Enrollment e2 = Enrollment.builder().student(student).course(physics).grade(Grade.C).paid(true).semester("S1").year(2023).build();

        student.getEnrollments().add(e1);
        student.getEnrollments().add(e2);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        TranscriptResponse transcript = studentService.getStudentTranscript(1L);

        assertNotNull(transcript);
        assertEquals(3.14, transcript.getGpa());
        assertEquals(2, transcript.getCourses().size());
    }

    @Test
    void testCalculateGpaWithNaGradesOnly() {
        Course math = Course.builder().id(1L).name("Math").credits(4).build();
        Enrollment e1 = Enrollment.builder().student(student).course(math).grade(Grade.NA).paid(false).semester("S1").year(2023).build();

        student.getEnrollments().add(e1);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        TranscriptResponse transcript = studentService.getStudentTranscript(1L);

        assertNotNull(transcript);
        assertEquals(0.0, transcript.getGpa());
    }

    @Test
    void testGetTopStudents() {
        Student s1 = Student.builder().id(1L).firstName("Alice").lastName("Smith").enrollments(new ArrayList<>()).build();
        Student s2 = Student.builder().id(2L).firstName("Bob").lastName("Jones").enrollments(new ArrayList<>()).build();

        Course c1 = Course.builder().credits(3).build();
        s1.getEnrollments().add(Enrollment.builder().student(s1).course(c1).grade(Grade.A).build());
        s2.getEnrollments().add(Enrollment.builder().student(s2).course(c1).grade(Grade.B).build());

        when(studentRepository.findAll()).thenReturn(List.of(s2, s1));

        List<StudentResponse> top = studentService.getTopStudents(5);

        assertNotNull(top);
        assertEquals(2, top.size());
        assertEquals("Alice", top.get(0).getFirstName());
    }
}
