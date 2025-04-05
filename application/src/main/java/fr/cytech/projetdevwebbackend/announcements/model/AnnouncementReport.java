package fr.cytech.projetdevwebbackend.announcements.model;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import fr.cytech.projetdevwebbackend.users.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "announcement_reports", uniqueConstraints = @UniqueConstraint(columnNames = { "reporter_id",
        "announcement_id" }))
public class AnnouncementReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    @JsonBackReference
    private Announcement announcement;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Builder.Default
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate = LocalDateTime.now();

    public AnnouncementReport(@NonNull User reporter, @NonNull Announcement announcement, @NonNull String reason) {
        this.reporter = reporter;
        this.announcement = announcement;
        this.reason = reason;
    }
}
