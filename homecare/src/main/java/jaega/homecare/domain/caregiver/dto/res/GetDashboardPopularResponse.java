package jaega.homecare.domain.caregiver.dto.res;

public record GetDashboardPopularResponse(
        Long total,
        Long active,
        Long inactive,
        Long resigned,
        Long newsThisMonth
) {
}
