package jaega.homecare.domain.center.dto.res;

import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;

public record GetCaregiverByStatusResponse(
        String caregiverName,
        CaregiverStatus caregiverStatus
){
}
