package fr.cytech.projetdevwebbackend.users.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity representing a report made by one user against another.
 * Stores the reporter, reported user, reason, and timestamp.
 */
@Entity
@Table(name = "user_reports", uniqueConstraints = @UniqueConstraint(columnNames = { "reporter_id", "reported_id" }))
@Getter
@Setter
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Column(nullable = false)
    private String reason;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate = LocalDateTime.now();

    public Report(@NonNull User reporter, @NonNull User reported, @NonNull String reason) {
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
    }
}
