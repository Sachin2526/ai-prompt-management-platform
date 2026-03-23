package com.pmp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "test_results", indexes = @Index(name = "idx_test_pv_id", columnList = "prompt_version_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_version_id", nullable = false)
    private PromptVersion promptVersion;

    @Convert(converter = com.pmp.util.MapStringObjectConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> inputs;

    @Column(columnDefinition = "TEXT")
    private String outputs;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime executedAt;
}
