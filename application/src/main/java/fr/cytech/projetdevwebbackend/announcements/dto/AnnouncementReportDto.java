package fr.cytech.projetdevwebbackend.announcements.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for announcement reports
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementReportDto {
    @NotNull
    private Long announcementId;
    @NotBlank
    private String reason;
}
