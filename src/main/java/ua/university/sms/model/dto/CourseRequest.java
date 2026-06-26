package ua.university.sms.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {
    @NotBlank(message = "Course name is mandatory")
    private String name;

    @NotNull(message = "Course credits is mandatory")
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    private String description;

    @NotNull(message = "Teacher ID is mandatory")
    private Long teacherId;
}
