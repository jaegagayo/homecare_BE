package jaega.homecare.domain.settlement.service.query;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementQueryService {

    private final CaregiverCenterRepository caregiverCenterRepository;
    private final SettlementRepository settlementRepository;
    private final SettlementQueryRepository settlementQueryRepository;
    private final SettlementMapper settlementMapper;

    // 엔티티 조회용
    public Settlement getSettlement(UUID settlementId){
        return settlementRepository.findBySettlementId(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("해당 settlementId로 정산 내역을 찾을 수 없습니다."));
    }

    /**
     *
     * Caregiver
     */

    public GetCaregiverSettlementStatsResponse getSettlementStatsByCaregiver(UUID caregiverId) {
        // caregiverId 기준으로 settlement 모두 조회
        List<Settlement> settlements = settlementRepository.findByCaregiverCenter_Caregiver_CaregiverId(caregiverId);

        if (settlements.isEmpty()) {
            return new GetCaregiverSettlementStatsResponse(
                    BigDecimal.ZERO, 0L, 0.0, 0L, 0L
            );
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        long totalMinutes = 0L;
        double totalDistance = 0.0;
        long completed = 0L;
        long pending = 0L;

        for (Settlement settlement : settlements) {
            // 금액
            if (settlement.getSettlementAmount() != null) {
                totalAmount = totalAmount.add(settlement.getSettlementAmount());
            }

            // 근무시간 (분 단위)
            if (settlement.getServiceMatch() != null &&
                    settlement.getServiceMatch().getServiceStartTime() != null &&
                    settlement.getServiceMatch().getServiceEndTime() != null) {

                long minutes = java.time.Duration.between(
                        settlement.getServiceMatch().getServiceStartTime(),
                        settlement.getServiceMatch().getServiceEndTime()
                ).toMinutes();
                totalMinutes += minutes;
            }

            // 이동거리
            if (settlement.getDistanceLog() != null) {
                totalDistance += settlement.getDistanceLog();
            }

            // 상태
            if (settlement.isPaid()) {
                completed++;
            } else {
                pending++;
            }
        }

        return new GetCaregiverSettlementStatsResponse(
                totalAmount,
                totalMinutes,
                totalDistance,
                completed,
                pending
        );
    }


    public List<GetCaregiverCenterSettlementResponse> getSettlementHistoryByCaregiver(UUID caregiverId) {
        // 1. caregiverId로 소속 기관 조회
        List<CaregiverCenter> caregiverCenters = caregiverCenterRepository.findByCaregiver_CaregiverId(caregiverId);

        if (caregiverCenters.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 각 기관별로 Settlement 조회 후 그룹핑
        return caregiverCenters.stream()
                .map(caregiverCenter -> {
                    List<Settlement> settlements = settlementRepository.findByCaregiverCenter(caregiverCenter);

                    List<GetSettlementByCaregiverResponse> settlementDtos = settlementMapper.toDtoList(settlements);

                    return new GetCaregiverCenterSettlementResponse(
                            caregiverCenter.getCenter().getName(),
                            settlementDtos
                    );
                })
                .toList();
    }

    /**
     *
     * Center
     */

    // 센터의 정산 내역 조회
    public List<GetSettlementResponse> getCenterSettlement(UUID centerId, MatchStatus status, LocalDate date) {
        return settlementQueryRepository.getCenterSettlement(centerId, null, status, date);
    }

    // 센터의 총 정산 금액 및 정산 상태 통계 조회
    public GetSettlementSummaryResponse getCenterSettlementSummary(UUID centerId){
        return settlementQueryRepository.getCenterSettlementSummary(centerId);
    }

    // 요양 보호사 별 정산 내역 조회
    public List<GetSettlementResponse> getSettlementByCaregiver(
            UUID centerId,
            UUID caregiverId,
            MatchStatus status,
            LocalDate date
    ) {
        return settlementQueryRepository.getCenterSettlement(centerId, caregiverId, status, date);
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
