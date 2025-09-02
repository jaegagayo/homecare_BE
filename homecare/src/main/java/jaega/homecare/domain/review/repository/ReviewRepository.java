package jaega.homecare.domain.review.repository;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByServiceMatch_ServiceMatchId(UUID serviceMatchId);

    List<Review> findByServiceMatch_Caregiver_CaregiverId(UUID caregiverId);

    Optional<Review> findByReviewId(UUID reviewId);

    List<Review> findByServiceMatch_Caregiver(Caregiver caregiver);

    @Query("SELECT COALESCE(AVG(r.reviewScore), 0) FROM Review r " +
            "WHERE r.serviceMatch.caregiver.caregiverId = :caregiverId")
    Double findAverageScoreByCaregiverId(@Param("caregiverId") UUID caregiverId);
}