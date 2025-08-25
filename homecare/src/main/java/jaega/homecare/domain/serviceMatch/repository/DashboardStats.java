package jaega.homecare.domain.serviceMatch.repository;

public record DashboardStats(
        Long totalCaregivers,
        Long assignedCaregivers,
        Long waitingApplicants,
        Long unassignedCaregivers
) {}
