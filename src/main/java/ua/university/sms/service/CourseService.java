package ua.university.sms.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.university.sms.model.dto.CourseRequest;
import ua.university.sms.model.dto.CourseResponse;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    Page<CourseResponse> getAllCourses(Long teacherId, Integer credits, Pageable pageable);
    CourseResponse getCourseById(Long id);
    CourseResponse updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);
}
