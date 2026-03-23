package com.pmp.repository;

import com.pmp.model.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByPromptVersionIdOrderByExecutedAtDesc(Long promptVersionId);
}
