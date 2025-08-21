package jaega.homecare.domain.matchReview.service.query;

import jaega.homecare.domain.matchReview.dto.res.GetReviewResponse;
import jaega.homecare.domain.matchReview.entity.Review;
import jaega.homecare.domain.matchReview.mapper.ReviewMapper;
import jaega.homecare.domain.matchReview.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    // ServiceMatch ID로 리뷰 조회
    public GetReviewResponse getReviewByServiceMatch(UUID serviceMatchId) {
        Review review = reviewRepository.findByServiceMatch_ServiceMatchId(serviceMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 serviceMatchId로 리뷰를 찾을 수 없습니다: " + serviceMatchId));
        return reviewMapper.toGetResponse(review);
    }
}