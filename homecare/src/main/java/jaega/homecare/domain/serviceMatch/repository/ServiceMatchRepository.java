package jaega.homecare.domain.serviceMatch.repository;

import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ServiceMatchRepository extends JpaRepository<ServiceMatch, Long> {
    Optional<ServiceMatch> findByServiceMatchId(UUID serviceMatchId);

    // 전체 매칭 수
    @Query("SELECT COUNT(sm) FROM ServiceMatch sm WHERE sm.caregiver.caregiverId = :caregiverId")
    Long countByCaregiverId(@Param("caregiverId") UUID caregiverId);

    // CANCELLED 매칭 수
    @Query("SELECT COUNT(sm) FROM ServiceMatch sm WHERE sm.caregiver.caregiverId = :caregiverId AND sm.matchStatus = 'CANCELLED'")
    Long countCancelledByCaregiverId(@Param("caregiverId") UUID caregiverId);
}
