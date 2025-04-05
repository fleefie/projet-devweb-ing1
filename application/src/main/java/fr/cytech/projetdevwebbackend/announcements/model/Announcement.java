package fr.cytech.projetdevwebbackend.announcements.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.cytech.projetdevwebbackend.jpa.annotation.JsonColumn;
import fr.cytech.projetdevwebbackend.users.model.Role;
import fr.cytech.projetdevwebbackend.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "announcements")
@EqualsAndHashCode(of = { "id" })
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id", nullable = false)
    private User poster;

    @Builder.Default
    @JsonColumn
    private String tags = "";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "announcement_role_restrictions", joinColumns = @JoinColumn(name = "announcement_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roleRestrictions = new HashSet<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean isPublic = true;

    public boolean isVisibleToUser(User user) {
        // Poster can always see their own announcement
        if (user != null && user.getId().equals(poster.getId())) {
            return true;
        }

        // If no role restrictions and public, anyone can see
        if (isPublic && (roleRestrictions == null || roleRestrictions.isEmpty())) {
            return true;
        }

        // Otherwise, check if user has any required role
        return user != null && roleRestrictions.stream()
                .anyMatch(role -> user.getRoles().contains(role));
    }

    /**
     * Gets the tags as a List of Strings.
     * 
     * @return List of tag strings
     */
    public List<String> getTagsList() {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(tags.split(","));
    }

    /**
     * Sets tags from a List of Strings.
     * 
     * @param tagsList List of tag strings
     */
    public void setTagsList(List<String> tagsList) {
        if (tagsList == null || tagsList.isEmpty()) {
            this.tags = "";
        } else {
            this.tags = String.join(",", tagsList);
        }
    }
}
