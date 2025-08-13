package jaega.homecare.domain.caregiver.service.query;

import jaega.homecare.domain.caregiver.dto.res.GetDashboardPopularResponse;
import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.entity.CaregiverStatus;
import jaega.homecare.domain.caregiver.mapper.CaregiverMapper;
import jaega.homecare.domain.caregiver.repository.CaregiverQueryRepository;
import jaega.homecare.domain.caregiver.repository.CaregiverRepository;
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
    private final CaregiverQueryRepository caregiverQueryRepository;
    private final CaregiverRepository caregiverRepository;
    private final CaregiverMapper caregiverMapper;

    public List<GetCaregiverResponse> getAllCaregiversByCenter(UUID centerId) {
        return caregiverQueryRepository.findAllByCenterId(centerId);
    }

    public Caregiver getCaregiver(UUID caregiverId){
        return caregiverRepository.findByCaregiverId(caregiverId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 요양보호사입니다."));
    }

    public List<GetCaregiverByCaregiverStatusResponse> getCaregiverByWorkStatus(UUID centerId, CaregiverStatus status){
        return caregiverQueryRepository.findCaregiverByCaregiverStatus(centerId, status);
    }

    public List<GetCaregiverByServiceTypeResponse> getCaregiverByServiceType(UUID centerId, Set<ServiceType> serviceTypes){
        return caregiverQueryRepository.findCaregiverByServiceTypes(centerId, serviceTypes);
    }

    public GetCaregiverProfileResponse getCaregiverProfile(UUID caregiverId){
        Caregiver caregiver = getCaregiver(caregiverId);
        return caregiverMapper.toGetCaregiverProfile(caregiver);
    }


    public GetDashboardPopularResponse getCaregiverStats(UUID centerId) {
        Center center = centerQueryService.getCenterByUUID(centerId);

        Long total = caregiverRepository.countByCenter(center);
        Long active = caregiverRepository.countByCenterAndStatus(center, CaregiverStatus.ACTIVE);
        Long inactive = caregiverRepository.countByCenterAndStatus(center, CaregiverStatus.INACTIVE);
        Long resigned = caregiverRepository.countByCenterAndStatus(center, CaregiverStatus.RESIGNED);
        Long newThisMonth = caregiverQueryRepository.countNewCaregiversThisMonth(center);

        return new GetDashboardPopularResponse(total, active, inactive, resigned, newThisMonth);
    }

}
