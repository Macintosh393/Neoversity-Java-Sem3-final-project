package ua.university.sms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.university.sms.model.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByTeacherId(Long teacherId, Pageable pageable);
    Page<Course> findByCredits(Integer credits, Pageable pageable);
    Page<Course> findByTeacherIdAndCredits(Long teacherId, Integer credits, Pageable pageable);
}
