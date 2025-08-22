package jaega.homecare.domain.review.service.command;

import jaega.homecare.domain.review.dto.req.CreateReviewRequest;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.review.mapper.ReviewMapper;
import jaega.homecare.domain.review.repository.ReviewRepository;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final ReviewMapper reviewMapper;

    // TODO : JWT 인증 로직 구현 시 직접 consumerId에서 가져오는 게 아닌 인증값에서 가져오도록 할 것 !
    // TODO : 추가로, Exception 또한 JWT 에서 하기 !!
    public UUID createReview(CreateReviewRequest request) throws AccessDeniedException {
        // 연관된 엔티티들 조회
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(request.serviceMatchId());

        UUID verificationConsumerId = serviceMatch.getServiceRequest().getConsumer().getConsumerId();
        System.out.println(verificationConsumerId+"123");
        System.out.println(request.consumerId()+"456");
        if(!request.consumerId().equals(verificationConsumerId)){
            throw new AccessDeniedException("본인의 매칭에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        // 엔티티 생성
        Review review = reviewMapper.toEntity(request, serviceMatch);
        review.initializeReview(UUID.randomUUID());

        // 저장
        Review savedReview = reviewRepository.save(review);
        return savedReview.getReviewId();
    }
}