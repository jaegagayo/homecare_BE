package jaega.homecare.domain.settlement.service.query;

import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
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
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
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

    public List<GetSettlementByPaid> getSettlementByPaid(UUID centerId, Boolean isPaid){
        return settlementQueryRepository.findSettlementByPaid(centerId, isPaid);
    }

    public List<GetSettlementByDateResponse> getSettlementByDate(UUID centerId, LocalDate date) {
        return settlementQueryRepository.findSettlementByDate(centerId, date);
    }

    public List<GetCaregiverMatchesByMonth> getSettlementByMonth(UUID centerId, int year, int month, Integer day) {
        return settlementQueryRepository.findSettlementByMonth(centerId, year, month, day);
    }

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

    /**
     *  정산 페이지
     */

    // 정산 금액, 정산 건수 통계 조회
    public GetSettlementCenterSummaryResponse getSettlementSummary(UUID centerId){
        return settlementQueryRepository.getSettlementCenterSummary(centerId);
    }

    // 센터의 요양보호사 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkList(
            UUID centerId,
            MatchStatus status,
            Integer year,
            Integer month
    ) {
        return settlementQueryRepository.getCaregiverWorkListByCenter(centerId, status, year, month);
    }

    // 요양보호사 개별 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCaregiver(
            UUID caregiverId,
            MatchStatus status,
            Integer year,
            Integer month
    ) {
        return settlementQueryRepository.getCaregiverWorkListByCaregiver(caregiverId, status, year, month);
    }

    // 최근 6개월 간 정산 내역 조회
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId) {
        return settlementQueryRepository.getMonthlyPaidSettlements(centerId, 6); // 이번 달 포함 6개월
    }

    // 최근 7일간 미정산 된 건수 조회
    public List<GetDailyUnsettledResponse> getDailyUnsettledCount(UUID centerId) {
        return settlementQueryRepository.getDailyUnsettledCount(centerId);
    }

    // 요양 보호사의 정산 내역 조회
    public GetCaregiverSettlementSummaryResponse getCaregiverSettlementSummary(UUID caregiverId){
        return settlementQueryRepository.getCaregiverSettlementSummary(caregiverId);
    }
}
