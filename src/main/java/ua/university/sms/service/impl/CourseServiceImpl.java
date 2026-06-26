package ua.university.sms.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.university.sms.exception.ResourceNotFoundException;
import ua.university.sms.model.dto.CourseRequest;
import ua.university.sms.model.dto.CourseResponse;
import ua.university.sms.model.entity.Course;
import ua.university.sms.model.entity.Teacher;
import ua.university.sms.repository.CourseRepository;
import ua.university.sms.repository.TeacherRepository;
import ua.university.sms.service.CourseService;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.getTeacherId()));

        Course course = Course.builder()
                .name(request.getName())
                .credits(request.getCredits())
                .description(request.getDescription())
                .teacher(teacher)
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllCourses(Long teacherId, Integer credits, Pageable pageable) {
        Page<Course> coursesPage;
        if (teacherId != null && credits != null) {
            coursesPage = courseRepository.findByTeacherIdAndCredits(teacherId, credits, pageable);
        } else if (teacherId != null) {
            coursesPage = courseRepository.findByTeacherId(teacherId, pageable);
        } else if (credits != null) {
            coursesPage = courseRepository.findByCredits(credits, pageable);
        } else {
            coursesPage = courseRepository.findAll(pageable);
        }
        return coursesPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = getCourseOrThrow(id);
        return mapToResponse(course);
    }

    @Override
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = getCourseOrThrow(id);
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id: " + request.getTeacherId()));

        course.setName(request.getName());
        course.setCredits(request.getCredits());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);

        Course updatedCourse = courseRepository.save(course);
        return mapToResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = getCourseOrThrow(id);
        courseRepository.delete(course);
    }

    private Course getCourseOrThrow(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private CourseResponse mapToResponse(Course course) {
        String teacherName = null;
        Long teacherId = null;
        if (course.getTeacher() != null) {
            teacherId = course.getTeacher().getId();
            teacherName = course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName();
        }
        return CourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .credits(course.getCredits())
                .description(course.getDescription())
                .teacherId(teacherId)
                .teacherName(teacherName)
                .build();
    }
}
