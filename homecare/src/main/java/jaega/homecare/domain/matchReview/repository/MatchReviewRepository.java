package jaega.homecare.domain.matchReview.repository;

import jaega.homecare.domain.matchReview.entity.MatchReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MatchReviewRepository extends JpaRepository<MatchReview, Long> {
    Optional<MatchReview> findByServiceMatch_ServiceMatchId(UUID serviceMatchId);
}