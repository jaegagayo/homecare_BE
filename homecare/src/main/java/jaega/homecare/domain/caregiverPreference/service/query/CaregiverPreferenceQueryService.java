package jaega.homecare.domain.caregiverPreference.service.query;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiverPreference.dto.res.GetCaregiverPreferenceResponse;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.mapper.CaregiverPreferenceMapper;
import jaega.homecare.domain.caregiverPreference.repository.CaregiverPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverPreferenceQueryService {
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;
    private final CaregiverPreferenceMapper caregiverPreferenceMapper;
    private final CaregiverQueryService caregiverQueryService;

    public GetCaregiverPreferenceResponse getCaregiverPreferenceByCaregiver(UUID caregiverId){
        Caregiver caregiver = caregiverQueryService.getCaregiver(caregiverId);
        CaregiverPreference caregiverPreference = caregiverPreferenceRepository.findByCaregiver(caregiver);
        return caregiverPreferenceMapper.toGetResponse(caregiverPreference);
    }


}
