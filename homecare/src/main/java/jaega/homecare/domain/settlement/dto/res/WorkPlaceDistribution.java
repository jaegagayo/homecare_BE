package jaega.homecare.domain.settlement.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

public record WorkPlaceDistribution (
        ServiceType serviceType,
        Long count,
        Double percent
) {}
