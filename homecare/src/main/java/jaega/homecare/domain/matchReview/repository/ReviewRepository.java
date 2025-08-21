package jaega.homecare.domain.matchReview.repository;

import jaega.homecare.domain.matchReview.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByServiceMatch_ServiceMatchId(UUID serviceMatchId);
}