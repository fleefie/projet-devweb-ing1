package fr.cytech.projetdevwebbackend.announcements.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.cytech.projetdevwebbackend.announcements.model.Announcement;
import fr.cytech.projetdevwebbackend.announcements.model.AnnouncementReport;
import fr.cytech.projetdevwebbackend.announcements.model.projections.AnnouncementReportProjection;
import fr.cytech.projetdevwebbackend.users.model.User;

@Repository
public interface AnnouncementReportRepository extends JpaRepository<AnnouncementReport, Long> {
    List<AnnouncementReport> findByAnnouncement(Announcement announcement);

    List<AnnouncementReport> findByReporter(User reporter);

    @Query("SELECT r.id as id, u.username as reporterUsername, a.id as announcementId, a.title as announcementTitle, " +
            "r.reason as reason, r.reportDate as reportDate " + // Changed from reportedAt to reportDate
            "FROM AnnouncementReport r JOIN r.reporter u JOIN r.announcement a " +
            "WHERE u.id = :userId")
    List<AnnouncementReportProjection> findReportsByUserProjected(@Param("userId") Long userId);

    @Query("SELECT r.id as id, u.username as reporterUsername, a.id as announcementId, a.title as announcementTitle, " +
            "r.reason as reason, r.reportDate as reportDate " + // Changed from reportedAt to reportDate
            "FROM AnnouncementReport r JOIN r.reporter u JOIN r.announcement a " +
            "WHERE a.id = :announcementId")
    List<AnnouncementReportProjection> findReportsByAnnouncementProjected(@Param("announcementId") Long announcementId);

}
