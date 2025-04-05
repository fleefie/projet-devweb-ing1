package fr.cytech.projetdevwebbackend.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user report creation.
 */
@Data
@NoArgsConstructor
public class UserReportDto {
    @NotNull(message = "Reported username cannot be null")
    @NotBlank(message = "Reported username cannot be blank")
    private String reportedUsername;

    @NotNull(message = "Reason cannot be null")
    @NotBlank(message = "Reason cannot be blank")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
