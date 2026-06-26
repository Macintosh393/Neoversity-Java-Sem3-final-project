package ua.university.sms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.university.sms.model.enums.Grade;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TranscriptResponse {
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Double gpa;
    private List<TranscriptEntry> courses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TranscriptEntry {
        private Long courseId;
        private String courseName;
        private Integer credits;
        private String semester;
        private Integer year;
        private Grade grade;
        private boolean paid;
    }
}
