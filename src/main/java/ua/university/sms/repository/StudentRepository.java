package ua.university.sms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.university.sms.model.entity.Student;
import ua.university.sms.model.enums.StudentStatus;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
    Page<Student> findByEnrollmentYear(Integer enrollmentYear, Pageable pageable);
    Page<Student> findByStatusAndEnrollmentYear(StudentStatus status, Integer enrollmentYear, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Student> searchStudents(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT e.student FROM Enrollment e WHERE e.paid = false")
    List<Student> findStudentsWithUnpaidCourses();
}
