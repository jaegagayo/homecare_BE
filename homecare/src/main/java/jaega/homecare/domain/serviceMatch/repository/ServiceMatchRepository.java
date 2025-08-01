package jaega.homecare.domain.serviceMatch.repository;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceMatchRepository extends JpaRepository<ServiceMatch, Long> {
    Optional<ServiceMatch> findByServiceMatchId(UUID serviceMatchId);
}
