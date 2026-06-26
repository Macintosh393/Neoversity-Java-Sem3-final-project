package ua.university.sms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAverageGpaResponse {
    private Long courseId;
    private String courseName;
    private String semester;
    private Double averageGpa;
}
