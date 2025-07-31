package jaega.homecare.domain.WorkMatch.repository;

import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkMatchRepository extends JpaRepository<WorkMatch, Long> {
}
