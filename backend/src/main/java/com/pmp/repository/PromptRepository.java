package com.pmp.repository;

import com.pmp.model.Prompt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromptRepository extends JpaRepository<Prompt, Long> {

    @Query("""
            SELECT DISTINCT p
            FROM Prompt p
            LEFT JOIN p.tags t
            WHERE (:title = '' OR LOWER(p.title) LIKE CONCAT('%', :title, '%'))
              AND (:author = '' OR LOWER(p.author) LIKE CONCAT('%', :author, '%'))
              AND (:tag = '' OR LOWER(t) LIKE CONCAT('%', :tag, '%'))
            """)
    Page<Prompt> searchPromptsRefined(
            @Param("title") String title,
            @Param("author") String author,
            @Param("tag") String tag,
            Pageable pageable);

    @Query("""
            SELECT p
            FROM Prompt p
            WHERE LOWER(p.title) LIKE CONCAT('%', :query, '%')
               OR LOWER(p.description) LIKE CONCAT('%', :query, '%')
            """)
    Page<Prompt> searchPrompts(@Param("query") String query, Pageable pageable);
}