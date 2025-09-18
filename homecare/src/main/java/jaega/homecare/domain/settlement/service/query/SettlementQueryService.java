package jaega.homecare.domain.settlement.service.query;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverCenter;
import jaega.homecare.domain.caregiverCenter.repository.CaregiverCenterRepository;
import jaega.homecare.domain.serviceMatch.entity.MatchStatus;
import jaega.homecare.domain.settlement.dto.res.*;
import jaega.homecare.domain.settlement.entity.Settlement;
import jaega.homecare.domain.settlement.mapper.SettlementMapper;
import jaega.homecare.domain.settlement.repository.SettlementQueryRepository;
import jaega.homecare.domain.settlement.repository.SettlementRepository;
import jaega.homecare.domain.users.entity.DateFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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


    public List<GetCaregiverSettlementResponse> getSettlementHistoryByCaregiver(
            UUID caregiverId,
            DateFilter dateFilter,
            Boolean isPaidFilter
    ) {
        List<CaregiverCenter> caregiverCenters =
                caregiverCenterRepository.findByCaregiver_CaregiverId(caregiverId);

        if (caregiverCenters.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDate now = LocalDate.now();
        final LocalDate startDate;
        final LocalDate endDate;

        switch (dateFilter) {
            case THIS_WEEK -> {
                startDate = now.with(DayOfWeek.MONDAY);
                endDate = now.with(DayOfWeek.SUNDAY);
            }
            case THIS_MONTH -> {
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
            }
            case LAST_MONTH -> {
                LocalDate lastMonth = now.minusMonths(1);
                startDate = lastMonth.withDayOfMonth(1);
                endDate = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
            }
            default -> {
                startDate = null;
                endDate = null;
            }
        }

        return caregiverCenters.stream()
                .map(caregiverCenter -> {
                    // QueryDSL로 필터링된 Settlement 조회
                    List<Settlement> settlements = settlementQueryRepository
                            .findByCaregiverCenterWithFilters(caregiverCenter, startDate, endDate, isPaidFilter);

                    // 디테일 매핑
                    List<GetCaregiverSettlementDetailDto> details = settlements.stream()
                            .map(s -> new GetCaregiverSettlementDetailDto(
                                    s.getServiceMatch().getServiceDate(),
                                    s.getServiceMatch().getServiceStartTime(),
                                    s.getServiceMatch().getServiceEndTime(),
                                    s.getDistanceLog(),
                                    s.getSettlementAmount(),
                                    s.isPaid()
                            ))
                            .collect(Collectors.toList());

                    // 총합 계산
                    Integer totalCount = details.size();
                    Long totalHours = details.stream()
                            .mapToLong(d -> Duration.between(d.serviceStartTime(), d.serviceEndTime()).toHours())
                            .sum();
                    Long totalDistanceLog = details.stream()
                            .mapToLong(d -> d.distanceLog() != null ? d.distanceLog().longValue() : 0L)
                            .sum();
                    BigDecimal totalSettlementAmount = details.stream()
                            .map(GetCaregiverSettlementDetailDto::settlementAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new GetCaregiverSettlementResponse(
                            caregiverCenter.getCenter().getName(),
                            totalCount,
                            totalHours,
                            totalDistanceLog,
                            totalSettlementAmount,
                            details
                    );
                })
                .collect(Collectors.toList());
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
