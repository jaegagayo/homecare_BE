package jaega.homecare.domain.caregiver.service.query;

import jaega.homecare.domain.caregiver.dto.res.GetCaregiverVerifiedStatusResponse;
import jaega.homecare.domain.caregiver.dto.res.GetDashboardPopularResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiverCenter.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.caregiver.repository.CaregiverQueryRepository;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.service.query.CaregiverPreferenceQueryService;
import jaega.homecare.domain.center.dto.res.GetCaregiverByCaregiverStatusResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverByServiceTypeResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverProfileResponse;
import jaega.homecare.domain.center.dto.res.GetCaregiverResponse;
import jaega.homecare.domain.center.entity.Center;
import jaega.homecare.domain.center.service.query.CenterQueryService;
import jaega.homecare.domain.users.entity.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverQueryService {

    private final CenterQueryService centerQueryService;
    private final CaregiverPreferenceQueryService caregiverPreferenceQueryService;
    private final CaregiverQueryRepository caregiverQueryRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverMapper caregiverMapper;

    public Caregiver getCaregiver(UUID caregiverId){
        return caregiverRepository.findByCaregiverId(caregiverId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요양보호사입니다."));
    }

    /**
     *
     * Caregiver
     *
     */

    public GetCaregiverVerifiedStatusResponse getCaregiverVerifiedStatus(UUID caregiverId){
        Caregiver caregiver = getCaregiver(caregiverId);
        return caregiverMapper.toGetCaregiverVerifiedStatus(caregiver);
    }


    /**
     *
     * Center
     *
     */

    public List<GetCaregiverResponse> getAllCaregiversByCenter(UUID centerId) {
        return caregiverQueryRepository.findAllByCenterId(centerId);
    }

    public List<GetCaregiverByCaregiverStatusResponse> getCaregiverByWorkStatus(UUID centerId, CaregiverStatus status){
        return caregiverQueryRepository.findCaregiverByCaregiverStatus(centerId, status);
    }

    public List<GetCaregiverByServiceTypeResponse> getCaregiverByServiceType(UUID centerId, Set<ServiceType> serviceTypes){
        return caregiverQueryRepository.findCaregiverByServiceTypes(centerId, serviceTypes);
    }

    public GetCaregiverProfileResponse getCaregiverProfile(UUID caregiverId){
        Caregiver caregiver = getCaregiver(caregiverId);
        CaregiverPreference caregiverPreference = caregiverPreferenceQueryService.findCaregiverPreferenceByCaregiver(caregiverId);
        return caregiverMapper.toGetCaregiverProfile(caregiver, caregiverPreference);
    }

    public GetDashboardPopularResponse getCaregiverStatus(UUID centerId) {
        Center center = centerQueryService.findCenterByUUID(centerId);

        Long total = caregiverQueryRepository.countByCenterId(center.getCenterId());
        Long active = caregiverQueryRepository.countByCenterAndStatus(center.getCenterId(), CaregiverStatus.ACTIVE);
        Long inactive = caregiverQueryRepository.countByCenterAndStatus(center.getCenterId(), CaregiverStatus.INACTIVE);
        Long resigned = caregiverQueryRepository.countByCenterAndStatus(center.getCenterId(), CaregiverStatus.RESIGNED);
        Long newThisMonth = caregiverQueryRepository.countNewCaregiversThisMonth(center);

        return new GetDashboardPopularResponse(total, active, inactive, resigned, newThisMonth);
    }

}
