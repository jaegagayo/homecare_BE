package jaega.homecare.domain.settlement.service.command;

import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.service.query.CaregiverCenterQueryService;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.service.query.ServiceMatchQueryService;
import jaega.homecare.domain.settlement.entity.Settlement;
import jaega.homecare.domain.settlement.mapper.SettlementMapper;
import jaega.homecare.domain.settlement.repository.SettlementRepository;
import jaega.homecare.domain.settlement.dto.req.CreateSettlementRequest;
import jaega.homecare.domain.settlement.service.query.SettlementQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementCommandService {
    private final SettlementRepository settlementRepository;
    private final CaregiverCenterQueryService caregiverCenterQueryService;
    private final ServiceMatchQueryService serviceMatchQueryService;
    private final SettlementQueryService settlementQueryService;
    private final SettlementMapper settlementMapper;

    /**
     * 요양보호사가 정산 내역을 관리할 센터 선정 후 정산 기록 생성
     */
    public UUID createSettlement(CreateSettlementRequest request){
        CaregiverCenter caregiverCenter = caregiverCenterQueryService.getCaregiverCenter(request.caregiverCenterId());
        ServiceMatch serviceMatch = serviceMatchQueryService.getServiceMatch(request.serviceMatchId());

        BigDecimal settlementAmount = calculateSettlementAmount(serviceMatch.getServiceStartTime(), serviceMatch.getServiceEndTime(), request.distanceLog());

        Settlement settlement = settlementMapper.toEntity(request, serviceMatch, caregiverCenter, settlementAmount);
        settlement.initializeSettlement(UUID.randomUUID());
        settlementRepository.save(settlement);

        return settlement.getSettlementId();
    }

    /**
     * 정산 금액 계산 메서드
     */
    public BigDecimal calculateSettlementAmount(LocalTime start, LocalTime end, Double distanceKm) {
        BigDecimal hourlyRate = new BigDecimal("15000");  // 시간당 단가
        BigDecimal distanceRate = new BigDecimal("500");  // 거리당 단가

        // 근무 시간 계산 (분 단위 -> 시간 단위로 변환)
        long minutes = Duration.between(start, end).toMinutes();
        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        // 거리
        BigDecimal distance = BigDecimal.valueOf(distanceKm != null ? distanceKm : 0);

        // 금액 계산
        BigDecimal amount = hourlyRate.multiply(hours).add(distanceRate.multiply(distance));
        return amount.setScale(0, RoundingMode.HALF_UP);  // 반올림, 소수점 없애기
    }

    /**
     * 정산 여부 변경
     */

    public void changePaidStatus(UUID settlementId) {
        Settlement settlement = settlementQueryService.getSettlement(settlementId);
        settlement.changePaidStatus();
    }
}
