package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;

public record GetCaregiverByCaregiverStatusResponse(
        String caregiverName,
        CaregiverStatus status
){
}
