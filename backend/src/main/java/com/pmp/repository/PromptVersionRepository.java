package com.pmp.repository;

import com.pmp.model.PromptVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromptVersionRepository extends JpaRepository<PromptVersion, Long> {

    List<PromptVersion> findByPromptIdOrderByVersionNumberDesc(Long promptId);

    Optional<PromptVersion> findTopByPromptIdOrderByVersionNumberDesc(Long promptId);

    Optional<PromptVersion> findByPromptIdAndVersionNumber(Long promptId, Integer versionNumber);
}
