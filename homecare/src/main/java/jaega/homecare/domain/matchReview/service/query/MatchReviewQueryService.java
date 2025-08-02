package jaega.homecare.domain.matchReview.service.query;

import jaega.homecare.domain.matchReview.dto.res.GetMatchReviewResponse;
import jaega.homecare.domain.matchReview.entity.MatchReview;
import jaega.homecare.domain.matchReview.mapper.MatchReviewMapper;
import jaega.homecare.domain.matchReview.repository.MatchReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchReviewQueryService {

    private final MatchReviewRepository matchReviewRepository;
    private final MatchReviewMapper matchReviewMapper;

    // ServiceMatch ID로 리뷰 조회
    public GetMatchReviewResponse getMatchReviewByServiceMatchId(UUID serviceMatchId) {
        MatchReview matchReview = matchReviewRepository.findByServiceMatch_ServiceMatchId(serviceMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 serviceMatchId로 리뷰를 찾을 수 없습니다: " + serviceMatchId));
        return matchReviewMapper.toGetResponse(matchReview);
    }
}