package fr.cytech.projetdevwebbackend.announcements.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for announcement ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementIdDto {
    @NotNull
    private Long id;
}
