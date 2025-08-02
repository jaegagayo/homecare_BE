package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.util.Set;
import java.util.UUID;

public record GetCaregiverResponse(
        UUID caregiverId,
        String name,
        String phone,
        Set<ServiceType> serviceTypes,
        CaregiverStatus status
) { }
