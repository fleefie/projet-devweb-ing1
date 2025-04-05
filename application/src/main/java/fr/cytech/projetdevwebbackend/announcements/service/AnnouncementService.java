package fr.cytech.projetdevwebbackend.announcements.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fr.cytech.projetdevwebbackend.announcements.model.Announcement;
import fr.cytech.projetdevwebbackend.announcements.model.projections.AnnouncementSearchProjection;
import fr.cytech.projetdevwebbackend.announcements.model.repository.AnnouncementRepository;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.repository.RoleRepository;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Announcement createAnnouncement(String title, String body, List<String> tags, User poster,
            Set<String> roleRestrictionNames, boolean isPublic) {
        Set<Role> roleRestrictions = roleRestrictionNames != null
                ? roleRestrictionNames.stream()
                        .map(roleName -> roleRepository.findByName(roleName)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Role not found: " + roleName)))
                        .collect(Collectors.toSet())
                : Set.of();

        LocalDateTime now = LocalDateTime.now();

        Announcement announcement = Announcement.builder()
                .title(title)
                .body(body)
                .poster(poster)
                .tags(tags != null ? String.join(",", tags) : "")
                .createdAt(now)
                .updatedAt(now)
                .roleRestrictions(roleRestrictions)
                .isPublic(isPublic)
                .build();

        return announcementRepository.save(announcement);
    }

    @Transactional(readOnly = true)
    public Announcement getAnnouncementById(Long id, User currentUser) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));

        if (!announcement.isVisibleToUser(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to view this announcement");
        }

        return announcement;
    }

    @Transactional(readOnly = true)
    public List<AnnouncementSearchProjection> searchAnnouncements(String searchTerm, User currentUser) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }

        String term = searchTerm.trim();

        // Search in title
        List<AnnouncementSearchProjection> titleResults = announcementRepository.findByTitleContaining(term);

        // Search in tags
        List<AnnouncementSearchProjection> tagResults = announcementRepository.findByTagContaining(term);

        // Combine results, removing duplicates (titles have priority)
        Set<Long> foundIds = titleResults.stream()
                .map(AnnouncementSearchProjection::getId)
                .collect(Collectors.toSet());

        List<AnnouncementSearchProjection> combinedResults = Stream.concat(
                titleResults.stream(),
                tagResults.stream().filter(result -> !foundIds.contains(result.getId()))).collect(Collectors.toList());

        return combinedResults;
    }

    @Transactional
    public Announcement updateAnnouncement(Long id, String title, String body, List<String> tags, User currentUser,
            Set<String> roleRestrictionNames, boolean isPublic) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));

        // Only the poster or an admin can update the announcement
        if (!announcement.getPoster().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to update this announcement");
        }

        if (title != null)
            announcement.setTitle(title);
        if (body != null)
            announcement.setBody(body);
        if (tags != null)
            announcement.setTagsList(tags);

        if (roleRestrictionNames != null) {
            Set<Role> roleRestrictions = roleRestrictionNames.stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            announcement.setRoleRestrictions(roleRestrictions);
        }

        announcement.setPublic(isPublic);
        announcement.setUpdatedAt(LocalDateTime.now());

        return announcementRepository.save(announcement);
    }

    @Transactional
    public void deleteAnnouncement(Long id, User currentUser) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));

        // Only the poster or an admin can delete the announcement
        if (!announcement.getPoster().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to delete this announcement");
        }

        announcementRepository.delete(announcement);
    }
}
