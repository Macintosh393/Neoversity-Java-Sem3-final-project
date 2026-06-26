package ua.university.sms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.GradeUpdateRequest;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.entity.Teacher;
import ua.university.sms.model.enums.Grade;
import ua.university.sms.model.enums.StudentStatus;
import ua.university.sms.model.enums.TeacherPosition;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.StudentRepository;
import ua.university.sms.repository.TeacherRepository;
import java.time.LocalDate;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EnrollmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        Teacher teacher = Teacher.builder()
                .firstName("Professor")
                .lastName("X")
                .email("prof.x@mail.edu")
                .dateOfBirth(LocalDate.of(1970, 1, 1))
                .position(TeacherPosition.PROFESSOR)
                .build();
        teacher = teacherRepository.save(teacher);

        course = Course.builder()
                .name("Roman History")
                .credits(3)
                .description("Intro to Roman history")
                .teacher(teacher)
                .build();
        course = courseRepository.save(course);

        student = Student.builder()
                .firstName("Logan")
                .lastName("Paul")
                .email("logan@mail.com")
                .enrollmentYear(2024)
                .status(StudentStatus.ACTIVE)
                .build();
        student = studentRepository.save(student);
    }

    @Test
    void testEnrollStudentAndGradeAndPay() throws Exception {
        EnrollmentRequest enrollRequest = EnrollmentRequest.builder()
                .studentId(student.getId())
                .courseId(course.getId())
                .semester("Fall")
                .year(2024)
                .build();

        String responseContent = mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(enrollRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentName", is("Logan Paul")))
                .andExpect(jsonPath("$.courseName", is("Roman History")))
                .andExpect(jsonPath("$.grade", is("NA")))
                .andExpect(jsonPath("$.paid", is(false)))
                .andReturn().getResponse().getContentAsString();

        Long enrollmentId = objectMapper.readTree(responseContent).get("id").asLong();

        GradeUpdateRequest gradeRequest = GradeUpdateRequest.builder()
                .grade(Grade.A)
                .build();

        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/grade")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade", is("A")));

        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/paid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid", is(true)));
    }
}
