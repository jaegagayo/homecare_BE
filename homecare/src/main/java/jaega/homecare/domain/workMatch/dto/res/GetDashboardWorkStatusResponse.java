package jaega.homecare.domain.workMatch.dto.res;

import java.util.List;

public record GetDashboardWorkStatusResponse(
        Long workingToday,               // 오늘 근무자
        Long unassignedCaregivers,       // 미배정 보호사
        Long waitingApplicants,          // 신청자(배정 대기)
        List<WorkPlaceDistribution> distribution // 근무지별 분포
) {}