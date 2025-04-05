package fr.cytech.projetdevwebbackend.announcements.model.projections;

import java.time.LocalDateTime;

/**
 * Projection interface for announcement reports
 */
public interface AnnouncementReportProjection {
    Long getId();

    String getReporterUsername();

    Long getAnnouncementId();

    String getAnnouncementTitle();

    String getReason();

    LocalDateTime getReportDate();
}
