package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.users.entity.ServiceType;

import java.util.Set;

public record GetCaregiverByServiceTypeResponse(
        String caregiverName,
        Set<ServiceType> serviceTypes
) {
}
