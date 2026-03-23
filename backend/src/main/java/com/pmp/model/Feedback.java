package com.pmp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback", indexes = @Index(name = "idx_feedback_pv_id", columnList = "prompt_version_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id", nullable = false)
    private PromptVersion promptVersion;

    @Column(nullable = false)
    private Integer ratingQuality; // 1-5 stars

    @Column(nullable = false)
    private Integer ratingAccuracy; // 1-5 stars

    @Column(nullable = false)
    private Integer ratingUsefulness; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String comments;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
