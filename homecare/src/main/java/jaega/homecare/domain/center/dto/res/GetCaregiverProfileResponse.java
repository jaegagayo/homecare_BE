package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.util.Set;

public record GetCaregiverProfileResponse(
        String caregiverName,
        String email,
        String birthDate,
        String phone,
        String address,
        CaregiverStatus status,
        Set<ServiceType> serviceTypes

) {
}
