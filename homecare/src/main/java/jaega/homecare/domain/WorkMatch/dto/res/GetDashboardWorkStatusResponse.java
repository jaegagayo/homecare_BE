package jaega.homecare.domain.WorkMatch.dto.res;

import java.util.List;

public record GetDashboardWorkStatusResponse(
        long workingToday,               // 오늘 근무자
        long unassignedCaregivers,       // 미배정 보호사
        long waitingApplicants,          // 신청자(배정 대기)
        List<WorkPlaceDistribution> distribution // 근무지별 분포
) {}