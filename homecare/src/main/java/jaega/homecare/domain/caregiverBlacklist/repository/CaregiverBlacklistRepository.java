package jaega.homecare.domain.caregiverBlacklist.repository;

import jaega.homecare.domain.caregiverBlacklist.entity.CaregiverBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CaregiverBlacklistRepository extends JpaRepository<CaregiverBlacklist, Long> {
    List<CaregiverBlacklist> findByReporter_UserId(UUID reporterId);
}