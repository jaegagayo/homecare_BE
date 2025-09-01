package jaega.homecare.domain.review.service.query;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.review.dto.res.*;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.review.mapper.ReviewMapper;
import jaega.homecare.domain.review.repository.ReviewQueryRepository;
import jaega.homecare.domain.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final CaregiverQueryService caregiverQueryService;
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewMapper reviewMapper;

    // ServiceMatch ID로 리뷰 조회
    public GetReviewResponse getReviewByServiceMatch(UUID serviceMatchId) {
        Review review = reviewRepository.findByServiceMatch_ServiceMatchId(serviceMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 serviceMatchId로 리뷰를 찾을 수 없습니다: " + serviceMatchId));
        return reviewMapper.toGetResponse(review);
    }

    public List<ConsumerReviewResponse> getWrittenReviews(UUID consumerId) {
        List<Review> reviews = reviewQueryRepository.findWrittenReviewsByConsumer(consumerId);
        return reviewMapper.toConsumerReviewResponse(reviews);
    }

    public CaregiverReviewSummaryResponse getReviewsForCaregiver(UUID caregiverId) {
        List<Review> reviews = reviewRepository.findByServiceMatch_Caregiver_CaregiverId(caregiverId);

        double averageScore = reviews.stream()
                .mapToDouble(Review::getReviewScore)
                .average()
                .orElse(0.0);

        List<CaregiverReviewItem> reviewItems = reviews.stream()
                .map(reviewMapper::toCaregiverReviewItem)
                .toList();

        return new CaregiverReviewSummaryResponse(averageScore, reviewItems);
    }

    public CaregiverReviewDetailResponse getReviewDetail(UUID reviewId) {
        Review review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        return reviewMapper.toDetailResponse(review);
    }

    public List<CaregiverReviewItem> getReviewByCaregiver(UUID caregiverId){
        Caregiver caregiver = caregiverQueryService.getCaregiver(caregiverId);
        List<Review> reviews = reviewRepository.findByServiceMatch_Caregiver(caregiver);
        return reviews.stream()
                .map(review -> new CaregiverReviewItem(
                        review.getReviewId(),
                        review.getServiceMatch().getServiceRequest().getConsumer().getUser().getName(),
                        review.getReviewContent(),
                        review.getReviewScore(),
                        review.getCreatedAt()
                ))
                .toList();
    }
}