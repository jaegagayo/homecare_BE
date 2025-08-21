package jaega.homecare.domain.workLog.service.query;

import jaega.homecare.domain.workLog.dto.res.*;
import jaega.homecare.domain.workLog.dto.res.GetDashboardSettlementResponse;
import jaega.homecare.domain.workLog.entity.WorkLog;
import jaega.homecare.domain.workLog.entity.WorkStatus;
import jaega.homecare.domain.workLog.mapper.WorkLogMapper;
import jaega.homecare.domain.workLog.repository.WorkLogRepository;
import jaega.homecare.domain.workLog.repository.WorkLogQueryRepository;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkLogQueryService {

    private final WorkLogQueryRepository workLogQueryRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final WorkLogRepository workLogRepository;
    private final WorkLogMapper workLogMapper;

    // 프론트엔드 GET 조회
    public GetWorkLogResponse findWorkLog(UUID workLogId){
        WorkLog workLog = getWorkLog(workLogId);
        return workLogMapper.toGetResponse(workLog);
    }

    // 엔티티 조회용
    public WorkLog getWorkLog(UUID workLogId){
        return workLogRepository.findByWorkLogId(workLogId)
                .orElseThrow(() -> new EntityNotFoundException("해당 workLogId로 근무 기록을 찾을 수 없습니다."));
    }

    public List<GetCaregiverMatchesResponse> getWorkLogByCaregiver(UUID caregiverId){
        return serviceMatchQueryRepository.findByCaregiverId(caregiverId);
    }

    public List<GetWorkLogByPaid> getWorkLogByPaid(UUID centerId, Boolean isPaid){
        return workLogQueryRepository.findWorkLogByPaid(centerId, isPaid);
    }

    public List<GetWorkLogByDateResponse> getWorkLogByDate(UUID centerId, LocalDate date) {
        return workLogQueryRepository.findWorkLogByDate(centerId, date);
    }

    public List<GetCaregiverMatchesByMonth> getWorkLogByMonth(UUID centerId, int year, int month, Integer day) {
        return workLogQueryRepository.findWorkLogByMonth(centerId, year, month, day);
    }

    public GetDashboardWorkStatusResponse getDashboardWorkStatus(UUID centerId) {
        Long workingToday = workLogQueryRepository.countCaregiversWorkingToday(centerId);
        Long unassigned = workLogQueryRepository.countUnassignedCaregiversToday(centerId);
        Long waiting = workLogQueryRepository.countWaitingApplicants(centerId);
        List<WorkPlaceDistribution> distribution = workLogQueryRepository.getWorkPlaceDistributionByServiceType(centerId);

        return new GetDashboardWorkStatusResponse(
                workingToday,
                unassigned,
                waiting,
                distribution
        );
    }

    public GetDashboardSettlementResponse getSettlementStatus(UUID centerId) {

        BigDecimal totalSettledAmount = workLogQueryRepository.getTotalSettledAmountThisMonth(centerId);
        if (totalSettledAmount == null) {
            totalSettledAmount = BigDecimal.ZERO;
        }
        Long unsettledCount = workLogQueryRepository.countUnsettled(centerId);
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
        return workLogQueryRepository.getSettlementCenterSummary(centerId);
    }

    // 센터의 요양보호사 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkList(
            UUID centerId,
            WorkStatus status,
            Integer year,
            Integer month
    ) {
        return workLogQueryRepository.getCaregiverWorkListByCenter(centerId, status, year, month);
    }

    // 요양보호사 개별 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCaregiver(
            UUID caregiverId,
            WorkStatus status,
            Integer year,
            Integer month
    ) {
        return workLogQueryRepository.getCaregiverWorkListByCaregiver(caregiverId, status, year, month);
    }

    // 최근 6개월 간 정산 내역 조회
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId) {
        return workLogQueryRepository.getMonthlyPaidSettlements(centerId, 6); // 이번 달 포함 6개월
    }

    // 최근 7일간 미정산 된 건수 조회
    public List<GetDailyUnsettledResponse> getDailyUnsettledCount(UUID centerId) {
        return workLogQueryRepository.getDailyUnsettledCount(centerId);
    }

    // 요양 보호사의 정산 내역 조회
    public GetCaregiverSettlementSummaryResponse getCaregiverSettlementSummary(UUID caregiverId){
        return workLogQueryRepository.getCaregiverSettlementSummary(caregiverId);
    }


}


