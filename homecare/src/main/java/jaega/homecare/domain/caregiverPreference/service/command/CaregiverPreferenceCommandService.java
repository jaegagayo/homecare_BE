package jaega.homecare.domain.caregiverPreference.service.command;

import jaega.homecare.domain.caregiver.entity.Caregiver;
import jaega.homecare.domain.caregiver.service.query.CaregiverQueryService;
import jaega.homecare.domain.caregiverPreference.dto.req.CreateCaregiverPreferenceRequest;
import jaega.homecare.domain.caregiverPreference.entity.CaregiverPreference;
import jaega.homecare.domain.caregiverPreference.mapper.CaregiverPreferenceMapper;
import jaega.homecare.domain.caregiverPreference.repository.CaregiverPreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaregiverPreferenceCommandService {
    private final CaregiverPreferenceRepository caregiverPreferenceRepository;
    private final CaregiverPreferenceMapper caregiverPreferenceMapper;
    private final CaregiverQueryService caregiverQueryService;

    public void createCaregiverPreference(CreateCaregiverPreferenceRequest request, UUID caregiverId){
        Caregiver caregiver = caregiverQueryService.getCaregiver(caregiverId);
        CaregiverPreference caregiverPreference = caregiverPreferenceMapper.toEntity(request, caregiver);
        caregiverPreference.initializeCaregiverPreference(UUID.randomUUID());
        caregiverPreferenceRepository.save(caregiverPreference);
    }
}
