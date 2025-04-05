package fr.cytech.projetdevwebbackend.announcements.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDto {
    private Long id;
    private String title;
    private String body;
    private String posterUsername;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> roleRestrictions;
}
