package jaega.homecare.domain.WorkMatch.service.query;

import jaega.homecare.domain.WorkMatch.dto.res.*;
import jaega.homecare.domain.WorkMatch.entity.WorkMatch;
import jaega.homecare.domain.WorkMatch.entity.WorkStatus;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchQueryRepository;
import jaega.homecare.domain.WorkMatch.repository.WorkMatchRepository;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkMatchQueryService {

    private final WorkMatchRepository workMatchRepository;
    private final WorkMatchQueryRepository workMatchQueryRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;

    public WorkMatch getWorkMatch(UUID workMatchId){
        return workMatchRepository.findByWorkMatchId(workMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 workMatchId로 근무 매칭 정보를 찾을 수 없습니다."));
    }

    public List<GetCaregiverMatchesResponse> getWorkMatchesByCaregiver(UUID caregiverId){
        return serviceMatchQueryRepository.findByCaregiverId(caregiverId);
    }

    public List<GetCaregiverMatchesByMonth> getWorkMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        return workMatchQueryRepository.findWorkMatchesByMonth(centerId, year, month, day);
    }

    public GetDashboardWorkStatusResponse getDashboardWorkStatus(UUID centerId) {
        long workingToday = workMatchQueryRepository.countCaregiversWorkingToday(centerId);
        long unassigned = workMatchQueryRepository.countUnassignedCaregiversToday(centerId);
        long waiting = workMatchQueryRepository.countWaitingApplicants(centerId);
        List<WorkPlaceDistribution> distribution = workMatchQueryRepository.getWorkPlaceDistributionByServiceType(centerId);

        return new GetDashboardWorkStatusResponse(
                workingToday,
                unassigned,
                waiting,
                distribution
        );
    }

    // 정산 페이지

    // 정산 금액, 정산 건수 통계 조회
    public GetSettlementSummaryResponse getSettlementSummary(UUID centerId){
        return workMatchQueryRepository.getSettlementSummary(centerId);
    }

    // 요양보호사 정산 내역 목록 조회
    public List<GetCaregiverWorkResponse> getCaregiverWorkList(
            UUID centerId,
            WorkStatus status,
            int year,
            int month
    ) {
        return workMatchQueryRepository.getCaregiverWorkList(centerId, status, year, month);
    }

    /**
     * 최근 6개월 총 정산 금액
     */
    public List<GetMonthlyPaymentResponse> getMonthlyPaidSettlements(UUID centerId) {
        return workMatchQueryRepository.getMonthlyPaidSettlements(centerId, 6); // 이번 달 포함 6개월
    }

    /**
     * 최근 7일 미정산 건수
     */
    public List<GetDailyUnsettledResponse> getDailyUnsettledCount(UUID centerId) {
        return workMatchQueryRepository.getDailyUnsettledCount(centerId);
    }
}


