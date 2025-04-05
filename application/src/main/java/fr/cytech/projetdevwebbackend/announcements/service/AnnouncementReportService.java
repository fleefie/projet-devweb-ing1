package fr.cytech.projetdevwebbackend.announcements.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import fr.cytech.projetdevwebbackend.announcements.model.Announcement;
import fr.cytech.projetdevwebbackend.announcements.model.AnnouncementReport;
import fr.cytech.projetdevwebbackend.announcements.model.repository.AnnouncementReportRepository;
import fr.cytech.projetdevwebbackend.announcements.model.repository.AnnouncementRepository;
import fr.cytech.projetdevwebbackend.users.model.User;

@Service
public class AnnouncementReportService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementReportRepository reportRepository;

    @Transactional
    public AnnouncementReport reportAnnouncement(Long announcementId, String reason, User reporter) {
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));

        // Check if the announcement is visible to the user
        if (!announcement.isVisibleToUser(reporter)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to view this announcement");
        }

        AnnouncementReport report = AnnouncementReport.builder()
                .announcement(announcement)
                .reporter(reporter)
                .reason(reason)
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementReport> getReportsForAnnouncement(Long announcementId, User admin) {
        // Only admin can see reports
        if (!admin.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can view reports");
        }

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Announcement not found"));

        return reportRepository.findByAnnouncement(announcement);
    }
}
