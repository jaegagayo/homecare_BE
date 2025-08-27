package jaega.homecare.domain.review.service.query;

import jaega.homecare.domain.review.dto.res.ConsumerReviewResponse;
import jaega.homecare.domain.review.dto.res.GetReviewResponse;
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
}