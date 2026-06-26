package ua.university.sms.service;

import ua.university.sms.model.dto.CourseAverageGpaResponse;
import ua.university.sms.model.dto.EnrollmentRequest;
import ua.university.sms.model.dto.EnrollmentResponse;
import ua.university.sms.model.enums.Grade;

public interface EnrollmentService {
    EnrollmentResponse enrollStudent(EnrollmentRequest request);
    EnrollmentResponse updateGrade(Long enrollmentId, Grade grade);
    EnrollmentResponse markAsPaid(Long enrollmentId);
    CourseAverageGpaResponse getAverageGpa(Long courseId, String semester);
}
