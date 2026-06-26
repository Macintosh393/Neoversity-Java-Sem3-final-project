package ua.university.sms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.university.sms.model.entity.Enrollment;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findBySemester(String semester);
    List<Enrollment> findByCourseIdAndSemester(Long courseId, String semester);
    boolean existsByStudentIdAndCourseIdAndSemesterAndYear(Long studentId, Long courseId, String semester, Integer year);
}
