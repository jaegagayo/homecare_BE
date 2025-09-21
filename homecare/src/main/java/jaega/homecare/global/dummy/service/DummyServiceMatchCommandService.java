package jaega.homecare.global.dummy.service;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.review.entity.Review;
import jaega.homecare.domain.review.repository.ReviewRepository;
import jaega.homecare.domain.serviceMatch.dto.req.CreateServiceMatchRequest;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.command.ServiceMatchCommandService;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequest;
import jaega.homecare.domain.serviceRequest.entity.ServiceRequestStatus;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.service.command.SettlementCommandService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DummyServiceMatchCommandService {

    private final ServiceMatchQueryService serviceMatchQueryService;
    private final ServiceMatchCommandService serviceMatchCommandService;
    private final ReviewRepository reviewRepository;
    private final SettlementCommandService settlementCommandService;
    private final Random random = new Random();

    protected void createDummyServiceMatchForCaregiver(CaregiverCenter caregiverCenter,
                                                    ServiceRequest serviceRequest,
                                                    LocalDate requestedDate,
                                                    LocalTime startTime,
                                                    LocalTime endTime,
                                                    double distanceLog) {

        UUID caregiverId = caregiverCenter.getCaregiver().getCaregiverId();

        // ServiceMatch 생성
        CreateServiceMatchRequest matchRequest = new CreateServiceMatchRequest(
                serviceRequest.getServiceRequestId(),
                caregiverId,
                startTime,
                endTime,
                requestedDate
        );
        UUID serviceMatchId = serviceMatchCommandService.createServiceMatch(matchRequest);

        // ServiceMatch 상태 업데이트
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(serviceMatchId);
        MatchStatus matchStatus = requestedDate.isAfter(LocalDate.now()) ? MatchStatus.CONFIRMED : MatchStatus.COMPLETED;
        serviceMatch.changeMatchStatus(matchStatus);

        // 리뷰 생성 (COMPLETED인 경우만)
        if (matchStatus == MatchStatus.COMPLETED) {
            Review review = Review.builder()
                    .reviewId(UUID.randomUUID())
                    .serviceMatch(serviceMatch)
                    .reviewScore(4.5 + (0.5 * random.nextDouble()))
                    .reviewContent("더미 리뷰 내용입니다.")
                    .build();
            reviewRepository.save(review);
        }

        // ServiceRequest 상태 동기화
        serviceRequest.changeRequestStatus(ServiceRequestStatus.ASSIGNED);

        // 정산 생성
        CreateSettlementRequest settlementRequest = new CreateSettlementRequest(
                caregiverCenter.getCaregiverCenterId(),
                serviceMatchId,
                distanceLog
        );
        settlementCommandService.createSettlement(settlementRequest);
    }
}
