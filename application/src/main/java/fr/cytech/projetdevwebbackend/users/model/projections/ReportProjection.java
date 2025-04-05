package fr.cytech.projetdevwebbackend.users.model.projections;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Projection interface for Report entities.
 * Used for serializing reports to JSON in a simplified format
 * containing just usernames and reason.
 */
public interface ReportProjection {
    Long getId();

    String getReporterUsername();

    String getReportedUsername();

    String getReason();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime getReportDate();
}
