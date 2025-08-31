package jaega.homecare.domain.settlement.service.query;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.settlement.entity.Settlement;
import jaega.homecare.domain.settlement.mapper.SettlementMapper;
import jaega.homecare.domain.settlement.repository.SettlementQueryRepository;
import jaega.homecare.domain.settlement.repository.SettlementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementQueryService {

    private final SettlementRepository settlementRepository;
    private final SettlementQueryRepository settlementQueryRepository;
    private final SettlementMapper settlementMapper;

    // 엔티티 조회용
    public Settlement getSettlement(UUID settlementId){
        return settlementRepository.findBySettlementId(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 settlementId로 정산 내역을 찾을 수 없습니다."));
    }

    // 프론트엔드 GET 조회
    public GetSettlementResponse findSettlement(UUID settlementId){
        Settlement settlement = getSettlement(settlementId);
        return settlementMapper.toGetResponse(settlement);
    }


    /**
     *
     * Center
     */

    // 센터의 총 정산 금액 및 정산 상태 통계 조회
    public GetSettlementSummaryResponse getSettlementSummary(UUID centerId){
        return settlementQueryRepository.getSettlementCenterSummary(centerId);
    }

    // 요양 보호사 별 정산 내역 조회
    public List<GetSettlementByCaregiverResponse> getSettlementByCaregiver(
            UUID centerId,
            UUID caregiverId,
            MatchStatus status,
            LocalDate date
    ) {
        return settlementQueryRepository.getSettlementByCaregiver(centerId, caregiverId, status, date);
    }

    // 요양보호사 총 정산 금액 및 정산 상태 통계 조회
    public GetSettlementSummaryByCaregiverResponse getCaregiverSettlementSummary(UUID caregiverId){
        return settlementQueryRepository.getCaregiverSettlementSummary(caregiverId);
    }


    // 최근 6개월 간 정산 내역 조회
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId) {
        return settlementQueryRepository.getMonthlyPaidSettlements(centerId, 6); // 이번 달 포함 6개월
    }

    // 최근 7일간 미정산 된 건수 조회
    public List<GetDailyUnsettledResponse> getWeeklyUnsettledCount(UUID centerId) {
        return settlementQueryRepository.getDailyUnsettledCount(centerId);
    }

    // 정산 상태 기반 정산 내역 조회
    public List<GetSettlementByPaid> getSettlementByPaid(UUID centerId, Boolean isPaid){
        return settlementQueryRepository.findSettlementByPaid(centerId, isPaid);
    }

    // (대시보드) 정산 현황 조회
    public GetDashboardSettlementResponse getSettlementStatus(UUID centerId) {

        BigDecimal totalSettledAmount = settlementQueryRepository.getTotalSettledAmountThisMonth(centerId);
        if (totalSettledAmount == null) {
            totalSettledAmount = BigDecimal.ZERO;
        }
        Long unsettledCount = settlementQueryRepository.countUnsettled(centerId);
        if (unsettledCount == null) {
            unsettledCount = 0L;
        }

        // TODO: 부정행위 알림 건수 조회 메서드 추가 시 적용할 것!
        Long fraudAlertsCount = 0L;

        return new GetDashboardSettlementResponse(totalSettledAmount, unsettledCount, fraudAlertsCount);
    }
}
