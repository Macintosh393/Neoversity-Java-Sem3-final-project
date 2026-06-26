package ua.university.sms.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.university.sms.model.enums.Grade;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeUpdateRequest {
    @NotNull(message = "Grade is mandatory")
    private Grade grade;
}
