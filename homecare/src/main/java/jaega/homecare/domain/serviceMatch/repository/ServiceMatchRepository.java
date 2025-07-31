package jaega.homecare.domain.serviceMatch.repository;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceMatchRepository extends JpaRepository<ServiceMatch, Long> {
}
