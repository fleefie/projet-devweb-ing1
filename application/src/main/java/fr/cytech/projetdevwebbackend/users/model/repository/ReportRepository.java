package fr.cytech.projetdevwebbackend.users.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import fr.cytech.projetdevwebbackend.users.model.Report;
import fr.cytech.projetdevwebbackend.users.model.User;
import fr.cytech.projetdevwebbackend.users.model.projections.ReportProjection;

/**
 * Repository for managing Report entities.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * Finds all reports made by a specific user.
     */
    List<Report> findByReporter(User reporter);

    /**
     * Finds all reports received by a specific user.
     */
    List<Report> findByReported(User reported);

    /**
     * Finds all reports with projection for REST API.
     */
    @Query("SELECT r.id as id, reporter.username as reporterUsername, reported.username as reportedUsername, r.reason as reason, r.reportDate as reportDate "
            +
            "FROM Report r JOIN r.reporter reporter JOIN r.reported reported " +
            "WHERE reporter.id = :userId")
    List<ReportProjection> findReportsMadeByUserProjected(@Param("userId") Long userId);

    /**
     * Finds all reports received with projection for REST API.
     */
    @Query("SELECT r.id as id, reporter.username as reporterUsername, reported.username as reportedUsername, r.reason as reason, r.reportDate as reportDate "
            +
            "FROM Report r JOIN r.reporter reporter JOIN r.reported reported " +
            "WHERE reported.id = :userId")
    List<ReportProjection> findReportsReceivedByUserProjected(@Param("userId") Long userId);
}
