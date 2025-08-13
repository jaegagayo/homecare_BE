package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.users.entity.ServiceType;

import java.util.Set;

public record GetCaregiverProfileResponse(
        String caregiverName,
        String email,
        String birthDate,
        String phone,
        String address,
        Set<ServiceType> serviceTypes

) {
}
