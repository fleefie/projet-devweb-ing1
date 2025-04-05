package fr.cytech.projetdevwebbackend.announcements.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for announcement search
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementSearchDto {
    @NotBlank
    private String searchTerm;
}
