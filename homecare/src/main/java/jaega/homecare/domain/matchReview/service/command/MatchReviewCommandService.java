package jaega.homecare.domain.matchReview.service.command;

import jaega.homecare.domain.matchReview.dto.req.CreateMatchReviewRequest;
import jaega.homecare.domain.matchReview.entity.MatchReview;
import jaega.homecare.domain.matchReview.mapper.MatchReviewMapper;
import jaega.homecare.domain.matchReview.repository.MatchReviewRepository;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.users.entity.User;
import jaega.homecare.domain.users.service.query.UserQueryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchReviewCommandService {

    private final MatchReviewRepository matchReviewRepository;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final UserQueryService userQueryService;
    private final MatchReviewMapper matchReviewMapper;

    public UUID createMatchReview(CreateMatchReviewRequest request) {
        // 연관된 엔티티들 조회
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(request.serviceMatchId());
        User writer = userQueryService.getUser(request.writerId());

        // 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 엔티티 생성
        MatchReview matchReview = matchReviewMapper.toEntity(request, serviceMatch, writer, now, now);
        matchReview.setMatchReviewId(UUID.randomUUID());

        // 저장
        MatchReview savedReview = matchReviewRepository.save(matchReview);
        return savedReview.getMatchReviewId();
    }
}