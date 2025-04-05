package fr.cytech.projetdevwebbackend.announcements.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.cytech.projetdevwebbackend.announcements.model.Announcement;
import fr.cytech.projetdevwebbackend.announcements.model.projections.AnnouncementSearchProjection;
import fr.cytech.projetdevwebbackend.users.model.User;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByPoster(User poster);

    @Query("SELECT a.id as id, a.title as title FROM Announcement a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY a.updatedAt DESC")
    List<AnnouncementSearchProjection> findByTitleContaining(@Param("searchTerm") String searchTerm);

    @Query("SELECT a.id as id, a.title as title FROM Announcement a WHERE " +
            "a.tags LIKE %:searchTerm% ORDER BY a.updatedAt DESC")
    List<AnnouncementSearchProjection> findByTagContaining(@Param("searchTerm") String searchTerm);
}
