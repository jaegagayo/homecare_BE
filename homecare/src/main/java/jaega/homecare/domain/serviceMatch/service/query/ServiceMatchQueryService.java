package jaega.homecare.domain.serviceMatch.service.query;

import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesByMonth;
import jaega.homecare.domain.serviceMatch.dto.res.*;
import jaega.homecare.domain.serviceMatch.entity.ServiceMatch;
import jaega.homecare.domain.serviceMatch.mapper.ServiceMatchMapper;
import jaega.homecare.domain.serviceMatch.repository.DashboardStats;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchQueryRepository;
import jaega.homecare.domain.serviceMatch.repository.ServiceMatchRepository;
import jaega.homecare.domain.center.dto.res.GetCaregiverMatchesResponse;
import jaega.homecare.domain.settlement.dto.res.GetDashboardWorkStatusResponse;
import jaega.homecare.domain.settlement.dto.res.WorkPlaceDistribution;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ServiceMatchQueryService {

    private final ServiceMatchRepository serviceMatchRepository;
    private final ServiceMatchQueryRepository serviceMatchQueryRepository;
    private final ServiceMatchMapper serviceMatchMapper;

    // 도메인 조회
    public ServiceMatch getServiceMatch(UUID serviceMatchId){
        return serviceMatchRepository.findByServiceMatchId(serviceMatchId)
                .orElseThrow(() -> new EntityNotFoundException("해당 serviceMatchId로 서비스 매칭 결과를 찾을 수 없습니다."));
    }

    // UUID 기반 매칭 결과 조회
    public GetServiceMatchByUUID getMatchesByUUID(UUID serviceMatchId){
        ServiceMatch serviceMatch = getServiceMatch(serviceMatchId);
        return serviceMatchMapper.toGetResponseByUUID(serviceMatch);
    }

    /**
     *
     * Center
     *
     */

    // Center 기반 매칭 결과 조회, CaregiverCenter
    public List<GetServiceMatchByCenterResponse> getMatchesByCenter(UUID centerId) {
        return serviceMatchQueryRepository.findMatchesByCenterId(centerId);
    }

    public List<GetCaregiverMatchesResponse> getMatchesByCaregiver(UUID caregiverId){
        return serviceMatchQueryRepository.findByCaregiverId(caregiverId);
    }

    // 특정 년도, 월(필수), 일(선택) 의 요양보호사 매칭 스케줄 조회
    public List<GetCaregiverMatchesByMonth> getMatchesByMonth(UUID centerId, int year, int month, Integer day) {
        return serviceMatchQueryRepository.findMatchesByMonth(centerId, year, month, day);
    }

    // 대시보드 매칭 스케줄 조회
    public GetDashboardWorkStatusResponse getDashboardWorkStatus(UUID centerId) {
        LocalDate today = LocalDate.now();

        DashboardStats dashboardStats = serviceMatchQueryRepository.getDashboardStatus(centerId, today);
        List<WorkPlaceDistribution> distributions = serviceMatchQueryRepository.getWorkPlaceDistributionByServiceType(centerId);

        return new GetDashboardWorkStatusResponse(
                dashboardStats,
                distributions
        );
    }

    /**
     *
     * Consumer
     *
     */

    public List<ConsumerScheduleResponse> getConsumerSchedule(UUID consumerId, LocalDate today){
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return serviceMatchQueryRepository.findConsumerWeeklySchedule(consumerId, weekStart, weekEnd);
    }

    public ConsumerScheduleDetailResponse getConsumerScheduleDetail(UUID serviceMatchId){
        return serviceMatchQueryRepository.findConsumerScheduleDetail(serviceMatchId);
    }

    public ConsumerNextScheduleResponse getConsumerNextSchedule(UUID consumerId){
        return serviceMatchQueryRepository.findConsumerNextSchedule(consumerId);
    }

    public List<GetScheduleWithoutReviewResponse> getScheduleWithoutReview(UUID consumerId){
        return serviceMatchQueryRepository.findCompletedScheduleWithoutReview(consumerId);
    }


    /**
     *
     * Caregiver
     *
     */

    public List<CaregiverScheduleResponse> getCaregiverSchedule(UUID caregiverId, LocalDate today){
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return serviceMatchQueryRepository.findCaregiverWeeklySchedule(caregiverId, weekStart, weekEnd);
    }

    public CaregiverScheduleDetailResponse getCaregiverScheduleDetail(UUID serviceMatchId){
        return serviceMatchQueryRepository.findCaregiverScheduleDetail(serviceMatchId);
    }


}
