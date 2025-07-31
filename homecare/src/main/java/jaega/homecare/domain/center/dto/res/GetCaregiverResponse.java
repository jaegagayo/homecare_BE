package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiver.entity.ServiceType;

import java.util.Set;
import java.util.UUID;

public record GetCaregiverResponse(
        UUID caregiverId,
        String name,
        String phone,
        Set<ServiceType> serviceTypes
) { }
