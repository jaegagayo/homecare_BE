package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiver.entity.CaregiverStatus;

public record GetCaregiverByCaregiverStatusResponse(
        String caregiverName,
        CaregiverStatus status
){
}
