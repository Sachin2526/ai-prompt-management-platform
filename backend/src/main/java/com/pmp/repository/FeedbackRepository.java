package com.pmp.repository;

import com.pmp.model.Feedback;
import com.pmp.dto.FeedbackStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    @Query("SELECT new com.pmp.dto.FeedbackStats(" +
           "AVG(f.ratingQuality), " +
           "AVG(f.ratingAccuracy), " +
           "AVG(f.ratingUsefulness), " +
           "COUNT(f)) " +
           "FROM Feedback f WHERE f.promptVersion.id = :versionId")
    FeedbackStats getStatsForVersion(@Param("versionId") Long versionId);
}
