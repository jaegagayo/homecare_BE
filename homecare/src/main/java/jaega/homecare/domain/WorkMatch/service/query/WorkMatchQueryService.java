package jaega.homecare.domain.WorkMatch.service.query;

import jaega.homecare.domain.WorkMatch.dto.res.*;
import jaega.homecare.domain.WorkMatch.dto.res.GetDashboardSettlementResponse;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.WorkMatch.mapper.WorkMatchMapper;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchQueryRepository;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchRepository;
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
public class WorkMatchQueryService {

    private final WorkMatchQueryRepository workMatchQueryRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final WorkMatchRepository workMatchRepository;
    private final WorkMatchMapper workMatchMapper;

    // 프론트엔드 GET 조회
    public GetWorkMatchResponse findWorkMatch(UUID workMatchId){
        WorkMatch workMatch = getWorkMatch(workMatchId);
        return workMatchMapper.toGetResponse(workMatch);
    }

    // 엔티티 조회용
    public WorkMatch getWorkMatch(UUID workMatchId){
        return workMatchRepository.findByWorkMatchId(workMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 workMatchId로 근무 기록을 찾을 수 없습니다."));
    }

    public List<GetCaregiverMatchesResponse> getWorkMatchesByCaregiver(UUID caregiverId){
        return serviceMatchQueryRepository.findByCaregiverId(caregiverId);
    }

    public List<GetWorkMatchByDateResponse> getWorkMatchByDate(UUID centerId, LocalDate date) {
        return workMatchQueryRepository.findWorkMatchByDate(centerId, date);
    }

    public List<GetCaregiverMatchesByMonth> getWorkMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        return workMatchQueryRepository.findWorkMatchesByMonth(centerId, year, month, day);
    }

    public GetDashboardWorkStatusResponse getDashboardWorkStatus(UUID centerId) {
        Long workingToday = workMatchQueryRepository.countCaregiversWorkingToday(centerId);
        Long unassigned = workMatchQueryRepository.countUnassignedCaregiversToday(centerId);
        Long waiting = workMatchQueryRepository.countWaitingApplicants(centerId);
        List<WorkPlaceDistribution> distribution = workMatchQueryRepository.getWorkPlaceDistributionByServiceType(centerId);

        return new GetDashboardWorkStatusResponse(
                workingToday,
                unassigned,
                waiting,
                distribution
        );
    }

    public List<GetWorkMatchByPaid> getWorkMatchByPaid(UUID centerId, Boolean isPaid){
        return workMatchQueryRepository.findWorkMatchByPaid(centerId, isPaid);
    }

    public GetDashboardSettlementResponse getSettlementStatus(UUID centerId) {

        BigDecimal totalSettledAmount = workMatchQueryRepository.getTotalSettledAmountThisMonth(centerId);
        if (totalSettledAmount == null) {
            totalSettledAmount = BigDecimal.ZERO;
        }
        Long unsettledCount = workMatchQueryRepository.countUnsettled(centerId);
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
        return workMatchQueryRepository.getSettlementCenterSummary(centerId);
    }

    // 센터의 요양보호사 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkList(
            UUID centerId,
            WorkStatus status,
            Integer year,
            Integer month
    ) {
        return workMatchQueryRepository.getCaregiverWorkListByCenter(centerId, status, year, month);
    }

    // 요양보호사 개별 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkListByCaregiver(
            UUID caregiverId,
            WorkStatus status,
            Integer year,
            Integer month
    ) {
        return workMatchQueryRepository.getCaregiverWorkListByCaregiver(caregiverId, status, year, month);
    }

    // 최근 6개월 간 정산 내역 조회
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId) {
        return workMatchQueryRepository.getMonthlyPaidSettlements(centerId, 6); // 이번 달 포함 6개월
    }

    // 최근 7일간 미정산 된 건수 조회
    public List<GetDailyUnsettledResponse> getDailyUnsettledCount(UUID centerId) {
        return workMatchQueryRepository.getDailyUnsettledCount(centerId);
    }

    // 요양 보호사의 정산 내역 조회
    public GetCaregiverSettlementSummaryResponse getCaregiverSettlementSummary(UUID caregiverId){
        return workMatchQueryRepository.getCaregiverSettlementSummary(caregiverId);
    }


}


