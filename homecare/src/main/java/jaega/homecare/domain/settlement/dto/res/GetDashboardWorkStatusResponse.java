package jaega.homecare.domain.settlement.dto.res;

import jaega.homecare.domain.serviceMatch.repository.DashboardStats;

import java.util.List;

public record GetDashboardWorkStatusResponse(
        DashboardStats dashboardStats,
        List<WorkPlaceDistribution> distribution // 근무지별 분포
) {}